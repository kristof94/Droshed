package kristof.fr.droshed;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by kristof
 * on 4/13/17.
 */

public class ServerInfo implements Serializable {

    private int port;
    private String credentials;
    private String address;

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
