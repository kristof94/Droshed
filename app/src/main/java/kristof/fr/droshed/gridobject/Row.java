package kristof.fr.droshed.gridobject;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kristof
 * on 5/31/17.
 */

public class Row implements Parcelable {
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

    public static final Creator<Row> CREATOR = new Creator<Row>() {
        @Override
        public Row createFromParcel(Parcel in) {
            return new Row(in);
        }

        @Override
        public Row[] newArray(int size) {
            return new Row[size];
        }
    };

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Row(Parcel in) {
        name = in.readString();
        type = in.readString();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(value);
    }
}
