package kristof.fr.droshed.Explorer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kristof
 * on 4/23/17.
 */

public abstract class ItemExplorer implements Parcelable{

    protected String type;
    protected String name;
    protected int layoutID;

    public ItemExplorer(String type, String name, int layoutID) {
        this.type = type;
        this.name = name;
        this.layoutID = layoutID;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return toString();
    }

    public int getLayoutID() {
        return layoutID;
    }

    @Override
    public abstract String toString();

    protected ItemExplorer(Parcel in) {
        this.type = in.readString();
        this.name = in.readString();
        this.layoutID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(name);
        dest.writeInt(layoutID);
    }

}