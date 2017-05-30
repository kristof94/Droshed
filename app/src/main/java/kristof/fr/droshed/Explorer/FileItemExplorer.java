package kristof.fr.droshed.Explorer;

import android.os.Parcel;

/**
 * Created by kristof
 * on 4/23/17.
 */

public class FileItemExplorer extends ItemExplorer{

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    private int version;

    public FileItemExplorer(String type, String name,int id,String path,int version) {
        super(type, name,id,path);
        this.version = version;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected FileItemExplorer(Parcel in) {
        super(in);
        version = in.readInt();
    }

    public static final Creator<FileItemExplorer> CREATOR = new Creator<FileItemExplorer>() {
        @Override
        public FileItemExplorer createFromParcel(Parcel in) {
            return new FileItemExplorer(in);
        }

        @Override
        public FileItemExplorer[] newArray(int size) {
            return new FileItemExplorer[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(version);
    }
}
