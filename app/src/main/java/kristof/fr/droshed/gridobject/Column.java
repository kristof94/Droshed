package kristof.fr.droshed.gridobject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by kristof
 * on 5/31/17.
 */

public class Column implements Parcelable {

    private String value;

    public Column(String value, int index) {
        this.value = value;
        this.index = index;
    }

    protected Column(Parcel in) {
        value = in.readString();
        index = in.readInt();
    }

    public static final Creator<Column> CREATOR = new Creator<Column>() {
        @Override
        public Column createFromParcel(Parcel in) {
            return new Column(in);
        }

        @Override
        public Column[] newArray(int size) {
            return new Column[size];
        }
    };

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    private int index;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
        dest.writeInt(index);
    }
}
