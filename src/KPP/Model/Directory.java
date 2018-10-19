package KPP.Model;

import java.io.File;

public class Directory extends File {
    private String label;

    public static Directory create(File dir) {
        Directory directory = new Directory(dir);

        directory.label = dir.getName();

        return directory;
    }

    public static Directory create(File dir, String label) {
        Directory directory = new Directory(dir);

        directory.label = label;

        return directory;
    }

    public Directory(File dir) {
        super(dir.getAbsolutePath());
    }

    public String getLabel()
    {
        return label;
    }

    public String toString() {
        return getLabel();
    }
}
