package KPP.Event;

import KPP.Model.Catalog;
import KPP.Model.Category;
import KPP.Model.Directory;
import KPP.Model.Part;
import javafx.event.Event;
import javafx.event.EventType;

import java.io.File;
import java.util.List;

public class CatalogEvent extends Event {
    public static final EventType<CatalogEvent> ROOT = new EventType<>("ROOT");
    public static final EventType<CatalogEvent> APPLY = new EventType<>("APPLY");
    public static final EventType<CatalogEvent> REVERT = new EventType<>("REVERT");
    public static final EventType<CatalogEvent> SEARCH = new EventType<>("SEARCH");
    public static final EventType<CatalogEvent> CATEGORY = new EventType<>("CATEGORY");
    public static final EventType<CatalogEvent> DIRECTORY = new EventType<>("DIRECTORY");
    public static final EventType<CatalogEvent> UPDATE = new EventType<>("UPDATE");
    public static final EventType<CatalogEvent> DISPLAY = new EventType<>("DISPLAY");
    public static final EventType<CatalogEvent> TOGGLE = new EventType<>("TOGGLE");
    public static final EventType<CatalogEvent> IMPORT = new EventType<>("IMPORT");
    public static final EventType<CatalogEvent> EXPORT = new EventType<>("EXPORT");

    private List<Part> parts;
    private Directory directory;
    private Category category;
    private Catalog catalog;
    private String term;
    private File file;

    public CatalogEvent(EventType<CatalogEvent> type)
    {
        super(type);
    }

    public CatalogEvent(EventType<CatalogEvent> type, CatalogEvent event)
    {
        super(type);
        this.directory = event.getDirectory();
        this.category = event.getCategory();
        this.catalog = event.getCatalog();
        this.parts = event.getParts();
        this.term = event.getTerm();
        this.file = event.getFile();
    }

    public Directory getDirectory() {
        return directory;
    }

    public CatalogEvent setDirectory(Directory directory) {
        this.directory = directory;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public CatalogEvent setCategory(Category category) {
        this.category = category;
        return this;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public CatalogEvent setCatalog(Catalog catalog) {
        this.catalog = catalog;
        return this;
    }

    public String getTerm() {
        return term;
    }

    public CatalogEvent setTerm(String term) {
        this.term = term;
        return this;
    }

    public CatalogEvent setParts(List<Part> parts) {
        this.parts = parts;
        return this;
    }

    public List<Part> getParts()
    {
        return parts;
    }

    public File getFile() {
        return file;
    }

    public CatalogEvent setFile(File file) {
        this.file = file;
        return this;
    }

    public CatalogEvent setType(EventType<CatalogEvent> type)
    {
        super.eventType = type;
        return this;
    }
}
