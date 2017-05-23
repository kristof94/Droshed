package kristof.fr.droshed.Explorer;

import android.os.Parcel;

/**
 * Created by kristof
 * on 4/23/17.
 */

public class FileItemExplorer extends ItemExplorer{

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public FileItemExplorer(String type, String name,int id,String content) {
        super(type, name,id);
        this.content = content;
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
        content = in.readString();
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
        dest.writeString(content);
    }








}
