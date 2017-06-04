package kristof.fr.droshed.gridobject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kristof
 * on 5/31/17.
 */

public class Row {
    private String name;
    private String type;
    private String value;

    public Row(String name, String type,String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Row(String name, String type) {
        this(name,type,null);
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
