package KPP.Model;

import KPP.Event.CatalogEvent;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import javafx.scene.Node;
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

    public Catalog bind(Node broker) {
        broker.addEventHandler(CatalogEvent.DIRECTORY, event -> {
            load(event.getDirectory());
            broker.fireEvent(new CatalogEvent(CatalogEvent.UPDATE, event));
        });

        broker.addEventHandler(CatalogEvent.TOGGLE, event -> {
            for (Part part : event.getParts()) part.toggle();
            broker.fireEvent(new CatalogEvent(CatalogEvent.UPDATE, event));
        });

        broker.addEventHandler(CatalogEvent.APPLY, event -> {
            for (Part part : parts) part.apply();
            broker.fireEvent(new CatalogEvent(CatalogEvent.UPDATE, event));
        });

        broker.addEventHandler(CatalogEvent.REVERT, event -> {
            for (Part part : parts) part.revert();
            broker.fireEvent(new CatalogEvent(CatalogEvent.UPDATE, event));
        });

        broker.addEventHandler(CatalogEvent.EXPORT, event -> {
            try (
                    FileWriter writer = new FileWriter(event.getFile())
            ) {
                writer.write(new Gson().toJson(Json.create(this)));
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        broker.addEventHandler(CatalogEvent.RESOLVE, event -> {
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
            broker.fireEvent(new CatalogEvent(CatalogEvent.UPDATE, event));
        });

        broker.addEventHandler(CatalogEvent.IMPORT, event -> {
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

            broker.fireEvent(new CatalogEvent(CatalogEvent.UPDATE, event));
        });

        return this;
    }

    public Catalog load(Directory dir) {
        parts.clear();

        for (File file : FileUtils.listFiles(dir, Part.FILE_FILTER, Part.DIRECTORY_FILTER)) {
            Part part = Part.create(file);
            if (null == part) continue;
            parts.add(part);

            if (index.contains(part.getName())) {
                duplicates.add(part.getName());
                continue;
            }

            index.add(part.getName());
        }

        return this;
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
}
