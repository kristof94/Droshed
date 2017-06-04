package kristof.fr.droshed.gridobject;

import java.util.ArrayList;

/**
 * Created by kristof
 * on 6/4/17.
 */

public class RowValue {

    public RowValue(int index, ArrayList<Column> arrayList) {
        this.index = index;
        this.arrayList = arrayList;
    }

    public int getIndex() {
        return index;
    }

    private int index;

    public ArrayList<Column> getArrayList() {
        return arrayList;
    }

    private ArrayList<Column> arrayList;


}
