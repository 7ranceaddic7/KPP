package KPP.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

public class Property {
    private StringProperty key = new SimpleStringProperty();
    private StringProperty value = new SimpleStringProperty();

    static Property create(Map.Entry<String, String> entry)
    {
        Property property = new Property();

        property.key.set(entry.getKey());
        property.value.set(entry.getValue());

        return property;
    }

    public String getKey()
    {
        return key.get();
    }

    public String getValue()
    {
        return value.get();
    }
}
