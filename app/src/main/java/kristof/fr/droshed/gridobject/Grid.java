package kristof.fr.droshed.gridobject;

import android.os.Parcel;
import android.os.Parcelable;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by kristof
 * on 5/31/17.
 */

public class Grid implements Parcelable {

    private ArrayList<Row> rows ;
    private ArrayList<RowValue> rowValues;

    protected Grid(Parcel in) {
        rows = in.createTypedArrayList(Row.CREATOR);
        rowValues = in.createTypedArrayList(RowValue.CREATOR);
        title = in.readString();
    }

    public static final Creator<Grid> CREATOR = new Creator<Grid>() {
        @Override
        public Grid createFromParcel(Parcel in) {
            return new Grid(in);
        }

        @Override
        public Grid[] newArray(int size) {
            return new Grid[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(rows);
        dest.writeTypedList(rowValues);
        dest.writeString(title);
    }
}
