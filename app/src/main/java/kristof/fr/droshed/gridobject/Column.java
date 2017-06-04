package kristof.fr.droshed.gridobject;

import java.util.List;

/**
 * Created by kristof
 * on 5/31/17.
 */

public class Column {

    private String value;

    public Column(String value, int index) {
        this.value = value;
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    private int index;





}
