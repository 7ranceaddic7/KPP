package KPP.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Part {
    static final IOFileFilter DIRECTORY_FILTER = TrueFileFilter.INSTANCE;
    static final IOFileFilter FILE_FILTER = new IOFileFilter() {
        public boolean accept(File file) {
            return file.getName().matches(".+\\.cfg(\\.purged)?$");
        }

        public boolean accept(File file, String s) {
            return true;
        }
    };

    private Hashtable<String, String> values;
    private StringProperty name = new SimpleStringProperty("unknown");
    private StringProperty title = new SimpleStringProperty("unknown");
    private StringProperty category = new SimpleStringProperty("unknown");
    private boolean state = true;

    static Part create(File file) {
        Hashtable<String, String> values = new Hashtable<>();
        values.put("category", "none");
        values.put("type", "unknown");
        values.put("file", file.getAbsolutePath());

        try (
                BufferedReader reader = new BufferedReader(new FileReader(file))
        ) {
            Pattern valuePattern = Pattern.compile(".*autoloc.+?=(.+)$", Pattern.CASE_INSENSITIVE);
            Pattern linePattern = Pattern.compile("^\\s*(.+?)\\s*=\\s*(.+)$");
            String line = reader.readLine();
            Integer depth = 0;

            while (null != line) {
                if (line.contains("PART")) values.put("type", "part");
                if (line.contains("{")) depth++;
                if (line.contains("}")) depth--;

                if (1 == depth) {
                    Matcher lineMatcher = linePattern.matcher(line);

                    if (lineMatcher.matches()) {
                        String value = lineMatcher.group(2).trim();
                        String key = lineMatcher.group(1).trim().toLowerCase();

                        if (!key.substring(0, 2).equals("//")) {
                            Matcher valueMatcher = valuePattern.matcher(value);
                            values.put(key, valueMatcher.matches() ? valueMatcher.group(1).trim() : value);
                        }
                    }
                }

                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (values.get("type").equals("part")) {
            Part part = new Part();
            part.values = values;
            part.state = values.get("file").endsWith(".cfg");
            part.category.set(StringUtils.capitalize(values.get("category")));
            part.title.set(values.get("title"));
            part.name.set(values.get("name"));

            return part;
        }

        return null;
    }

    public String getCategory() {
        return category.get();
    }

    public String getName() {
        return name.get();
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getTitle() {
        return title.get();
    }

    public ArrayList<Property> getProperties() {
        ArrayList<Property> properties = new ArrayList<>();

        for (Map.Entry<String, String> entry : values.entrySet()) {
            properties.add(Property.create(entry));
        }

        properties.sort(Comparator.comparing(Property::getKey));

        return properties;
    }

    public boolean isActual() {
        return (state && values.get("file").endsWith(".cfg")) || (!state && values.get("file").endsWith(".purged"));
    }

    public boolean isState(boolean state) {
        return this.state == state;
    }

    public boolean apply() {
        return apply(false);
    }

    public boolean apply(boolean force) {
        if (!isActual()) {
            File from = new File(values.get("file"));
            File to = new File(state ? values.get("file").replace(".purged", "") : values.get("file") + ".purged");

            if (force && to.exists()) {
                to.delete();
            }

            if (from.renameTo(to)) {
                values.put("file", to.getAbsolutePath());
                return true;
            }

            return false;
        }

        return true;
    }

    public boolean revert() {
        if (!isActual()) {
            state = values.get("file").endsWith(".cfg");
        }

        return true;
    }

    public boolean matches(Category category) {
        if (null == category || category.getValue().equals("any")) return true;
        return getCategory().toLowerCase().equals(category.getValue());
    }

    public boolean matches(String pattern) {
        if (null == pattern) return true;
        String needle = pattern.toLowerCase();
        String haystack = (getTitle() + " " + getName()).toLowerCase();
        return haystack.contains(needle);
    }

    public void toggle() {
        this.state = !this.state;
    }
}
