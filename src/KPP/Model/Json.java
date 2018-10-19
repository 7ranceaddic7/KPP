package KPP.Model;

import java.util.ArrayList;
import java.util.Date;

public class Json {
    private Date date = new Date();
    private ArrayList<String> parts = new ArrayList<>();

    public static Json create(Catalog catalog)
    {
        Json json = new Json();

        for (Part part : catalog.getParts()) {
            if (part.isActual() && part.isState(false)) json.parts.add(part.getName());
        }

        return json;
    }

    public Date getDate()
    {
        return date;
    }

    public ArrayList<String> getParts()
    {
        return parts;
    }
}
