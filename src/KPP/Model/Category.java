package KPP.Model;

import org.apache.commons.lang3.StringUtils;

public class Category {
    private String label;
    private String value;

    public static Category create(String name)
    {
        Category category = new Category();
        category.label = StringUtils.capitalize(name);
        category.value = name.toLowerCase();

        return category;
    }

    public String getLabel()
    {
        return label != null ? label : value;
    }

    public String getValue()
    {
        return value != null ? value : label;
    }

    public String toString()
    {
        return getLabel();
    }
}
