package kristof.fr.droshed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import kristof.fr.droshed.Explorer.ItemExplorer;

/**
 * Created by kristof
 * on 4/13/17.
 * ServerInfo class used to save data like credential(user+password),port, etc..
 */

public class ServerInfo implements Parcelable {

    private String authBase64;
    private String address;
    private String dataPath;

    public String getDataPath() {
        return dataPath;
    }

    public String getModelPath() {
        return modelPath;
    }

    private String modelPath;

    public ServerInfo(String address, String credentials,String dataPath,String modelPath) {
        this.authBase64 = credentials;
        this.address = address;
        this.dataPath = dataPath;
        this.modelPath = modelPath;
    }

    protected ServerInfo(Parcel in) {
        authBase64 = in.readString();
        address = in.readString();
        dataPath = in.readString();
        modelPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authBase64);
        dest.writeString(address);
        dest.writeString(dataPath);
        dest.writeString(modelPath);
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

    public String getAuthBase64() {
        return authBase64;
    }

    @Override
    public String toString() {
        return  address;
    }


}
