package KPP.Model;

import KPP.Event.CatalogEvent;
import KPP.Service.Broker;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;

public class Catalog {
    private ArrayList<Part> parts = new ArrayList<>();
    private ArrayList<String> index = new ArrayList<>();
    private ArrayList<String> duplicates = new ArrayList<>();

    public Catalog()
    {
        Broker.listen(CatalogEvent.APPLY, this::onApply);
        Broker.listen(CatalogEvent.DIRECTORY, this::onDirectory);
        Broker.listen(CatalogEvent.REVERT, this::onRevert);
        Broker.listen(CatalogEvent.TOGGLE, this::onToggle);
        Broker.listen(CatalogEvent.EXPORT, this::onExport);
        Broker.listen(CatalogEvent.IMPORT, this::onImport);
        Broker.listen(CatalogEvent.RESOLVE, this::onResolve);
    }

    public LinkedList<Category> getCategories() {
        ArrayList<String> values = new ArrayList<>();
        LinkedList<Category> categories = new LinkedList<>();

        for (Part part : parts) {
            String category = part.getCategory().toLowerCase();
            if (values.contains(category)) continue;
            values.add(category);
            categories.add(Category.create(category));
        }

        categories.sort(Comparator.comparing(Category::getLabel));
        categories.push(Category.create("Any"));

        return categories;
    }

    public LinkedList<Directory> getDirectories(File root) {
        LinkedList<Directory> directories = new LinkedList<>();

        for (File dir : Objects.requireNonNull(root.listFiles())) {
            if (!dir.isDirectory()) continue;
            directories.add(Directory.create(dir));
        }

        directories.sort(Comparator.comparing(Directory::getLabel));
        directories.push(Directory.create(root, "Any"));

        return directories;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public boolean isActual() {
        for (Part part : parts) {
            if (!part.isActual()) {
                return false;
            }
        }

        return true;
    }

    public boolean hasDuplicates()
    {
        return !duplicates.isEmpty();
    }

    private Part find(String name, boolean state)
    {
        for (Part part : parts) {
            if (part.getName().equals(name) && part.isState(state)) return part;
        }

        return null;
    }

    private void onApply(CatalogEvent event)
    {
        for (Part part : parts) part.apply();
        Broker.dispatch(new CatalogEvent(CatalogEvent.UPDATE).setCatalog(this));
    }

    private void onExport(CatalogEvent event)
    {
        try (
                FileWriter writer = new FileWriter(event.getFile())
        ) {
            writer.write(new Gson().toJson(Json.create(this)));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onImport(CatalogEvent event)
    {
        try (
                JsonReader reader = new JsonReader(new FileReader(event.getFile()))
        ) {
            Gson gson = new Gson();
            Json json = gson.fromJson(reader, Json.class);

            for (Part part : parts) {
                part.setState(!json.getParts().contains(part.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Broker.dispatch(new CatalogEvent(CatalogEvent.UPDATE).setCatalog(this));
    }

    private void onDirectory(CatalogEvent event)
    {
        parts.clear();
        index.clear();
        duplicates.clear();

        for (File file : FileUtils.listFiles(event.getDirectory(), Part.FILE_FILTER, Part.DIRECTORY_FILTER)) {
            Part part = Part.create(file);
            if (null == part) continue;
            parts.add(part);

            if (index.contains(part.getName())) {
                duplicates.add(part.getName());
                continue;
            }

            index.add(part.getName());
        }

        Broker.dispatch(new CatalogEvent(CatalogEvent.UPDATE).setCatalog(this));
    }

    private void onResolve(CatalogEvent event)
    {
        ArrayList<String> removed = new ArrayList<>();

        for (String duplicate : duplicates) {
            Part active = find(duplicate, true);
            Part purged = find(duplicate, false);
            if (null == active || null == purged) continue;
            removed.add(duplicate);
            parts.remove(purged);
            active.toggle();
            active.apply(true);
        }

        duplicates.removeAll(removed);
        Broker.dispatch(new CatalogEvent(CatalogEvent.UPDATE).setCatalog(this));
    }

    private void onRevert(CatalogEvent event)
    {
        for (Part part : parts) part.revert();
        Broker.dispatch(new CatalogEvent(CatalogEvent.UPDATE).setCatalog(this));
    }

    private void onToggle(CatalogEvent event)
    {
        for (Part part : event.getParts()) part.toggle();
        Broker.dispatch(new CatalogEvent(CatalogEvent.UPDATE).setCatalog(this));
    }
}
