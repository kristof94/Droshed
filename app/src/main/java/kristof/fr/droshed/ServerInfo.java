package kristof.fr.droshed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import kristof.fr.droshed.custom.CustomItem;

/**
 * Created by kristof
 * on 4/13/17.
 * ServerInfo class used to save data like credential(user+password),port, etc..
 */

public class ServerInfo implements Parcelable {

    private String authBase64;
    private String address;
    private ArrayList<CustomItem> datalist = new ArrayList<>();

    public ServerInfo(String address, String credentials) {
        this.authBase64 = credentials;
        this.address = address;
    }

    protected ServerInfo(Parcel in) {
        authBase64 = in.readString();
        address = in.readString();
        ArrayList<CustomItem> list = new ArrayList<>();
        in.readList(list,CustomItem.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authBase64);
        dest.writeString(address);
        dest.writeList(datalist);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ServerInfo> CREATOR = new Creator<ServerInfo>() {
        @Override
        public ServerInfo createFromParcel(Parcel in) {
            return new ServerInfo(in);
        }

        @Override
        public ServerInfo[] newArray(int size) {
            return new ServerInfo[size];
        }
    };

    public ArrayList<CustomItem> getDatalist() {
        return datalist;
    }

    public void setDatalist(ArrayList<CustomItem> datalist) {
        this.datalist = datalist;
    }

    public ArrayList<CustomItem> getModelist() {
        return modelist;
    }

    public void setModelist(ArrayList<CustomItem> modelist) {
        this.modelist = modelist;
    }

    private ArrayList<CustomItem> modelist = new ArrayList<>();

    public String getAuthBase64() {
        return authBase64;
    }

    @Override
    public String toString() {
        return  address;
    }


}
