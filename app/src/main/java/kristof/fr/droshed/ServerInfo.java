package kristof.fr.droshed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by kristof
 * on 4/13/17.
 */

public class ServerInfo implements Parcelable {

    private int port;
    private String credentials;
    private String address;
    private ArrayList<CustomItem> datalist = new ArrayList<>();

    protected ServerInfo(Parcel in) {
        port = in.readInt();
        credentials = in.readString();
        address = in.readString();
        ArrayList<CustomItem> list = new ArrayList<>();
        in.readList(list,CustomItem.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(port);
        dest.writeString(credentials);
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

    public ServerInfo(int port, String credentials, String address) {
        this.port = port;
        this.credentials = credentials;
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public String getCredentials() {
        return credentials;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(address).append(":").append(port);
        return sb.toString();
    }


}
