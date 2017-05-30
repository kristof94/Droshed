package kristof.fr.droshed.Explorer;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * Created by kristof
 * on 4/23/17.
 */

public class FolderItemExplorer extends ItemExplorer {

    public void setItemExplorerList(ArrayList<ItemExplorer> itemExplorerList) {
        this.itemExplorerList = itemExplorerList;
    }

    private ArrayList<ItemExplorer> itemExplorerList;

    public FolderItemExplorer(String type, String name,int id,String path,ArrayList<ItemExplorer> list) {
        super(type, name,id,path);
        this.itemExplorerList = list;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        return sb.toString();
    }

    public ArrayList<ItemExplorer> getItemExplorerList() {
        return itemExplorerList;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    protected FolderItemExplorer(Parcel in) {
        super(in);
        itemExplorerList = in.readArrayList(FolderItemExplorer.class.getClassLoader());
    }

    public static final Creator<FolderItemExplorer> CREATOR = new Creator<FolderItemExplorer>() {
        @Override
        public FolderItemExplorer createFromParcel(Parcel in) {
            return new FolderItemExplorer(in);
        }

        @Override
        public FolderItemExplorer[] newArray(int size) {
            return new FolderItemExplorer[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(itemExplorerList);
    }

}
