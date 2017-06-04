package kristof.fr.droshed.gridobject;

import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by kristof
 * on 5/31/17.
 */

public class Grid {

    private ArrayList<Row> rows ;
    private ArrayList<RowValue> rowValues;

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public Document getDocument() {
        return document;
    }

    private Document document;

    public String getTitle() {
        return title;
    }


    public Grid(ArrayList<Row> rows, String title, ArrayList<RowValue> rowValues, Document document) {
        this.rows = rows;
        this.title = title;
        this.document = document;
        this.rowValues = rowValues;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public ArrayList<RowValue> getRowValues() {
        return rowValues;
    }
}
