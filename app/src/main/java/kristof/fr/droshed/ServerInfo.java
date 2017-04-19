package kristof.fr.droshed;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kristof
 * on 4/13/17.
 */

public class ServerInfo implements Serializable {

    private int port;
    private String credentials;
    private String address;
    private ArrayList<CustomItem> datalist = new ArrayList<>();

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
