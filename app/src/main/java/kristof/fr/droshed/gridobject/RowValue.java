package kristof.fr.droshed.gridobject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by kristof
 * on 6/4/17.
 */

public class RowValue implements Parcelable {

    public RowValue(int index, ArrayList<Column> arrayList) {
        this.index = index;
        this.arrayList = arrayList;
    }

    protected RowValue(Parcel in) {
        index = in.readInt();
        arrayList = in.createTypedArrayList(Column.CREATOR);
    }

    public static final Creator<RowValue> CREATOR = new Creator<RowValue>() {
        @Override
        public RowValue createFromParcel(Parcel in) {
            return new RowValue(in);
        }

        @Override
        public RowValue[] newArray(int size) {
            return new RowValue[size];
        }
    };

    public int getIndex() {
        return index;
    }

    private int index;

    public ArrayList<Column> getArrayList() {
        return arrayList;
    }

    private ArrayList<Column> arrayList;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeTypedList(arrayList);
    }
}
