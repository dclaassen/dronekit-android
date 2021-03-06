package com.o3dr.services.android.lib.drone.connection;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Base type used to pass the drone connection parameters over ipc.
 */
public class ConnectionParameter implements Parcelable {

    private final int connectionType;
    private final Bundle paramsBundle;
    private final DroneSharePrefs droneSharePrefs;

    public ConnectionParameter(int connectionType, Bundle paramsBundle){
        this.connectionType = connectionType;
        this.paramsBundle = paramsBundle;
        this.droneSharePrefs = null;
    }

    /**
     * @deprecated Use {@link ConnectionParameter#ConnectionParameter(int, Bundle)} instead.
     * @since 2.8.0
     */
    public ConnectionParameter(int connectionType, Bundle paramsBundle, DroneSharePrefs droneSharePrefs){
        this.connectionType = connectionType;
        this.paramsBundle = paramsBundle;
        this.droneSharePrefs = droneSharePrefs;
    }

    public int getConnectionType() {
        return connectionType;
    }

    public Bundle getParamsBundle() {
        return paramsBundle;
    }

    public DroneSharePrefs getDroneSharePrefs() {
        return droneSharePrefs;
    }

    public String getUniqueId(){
        final String uniqueId;
        switch(connectionType){

            case ConnectionType.TYPE_UDP:
                int udpPort = ConnectionType.DEFAULT_UDP_SERVER_PORT;
                if(paramsBundle != null){
                    udpPort = paramsBundle.getInt(ConnectionType.EXTRA_UDP_SERVER_PORT, udpPort);
                }
                uniqueId = "udp:" + udpPort;
                break;

            case ConnectionType.TYPE_BLUETOOTH:
                String btAddress = "";
                if(paramsBundle != null){
                    btAddress = paramsBundle.getString(ConnectionType.EXTRA_BLUETOOTH_ADDRESS, "");
                }

                uniqueId = TextUtils.isEmpty(btAddress) ? "bluetooth" : "bluetooth:" + btAddress;
                break;

            case ConnectionType.TYPE_TCP:
                String tcpIp = "";
                int tcpPort = ConnectionType.DEFAULT_TCP_SERVER_PORT;
                if(paramsBundle != null){
                    tcpIp = paramsBundle.getString(ConnectionType.EXTRA_TCP_SERVER_IP, "");
                    tcpPort = paramsBundle.getInt(ConnectionType.EXTRA_TCP_SERVER_PORT, tcpPort);
                }

                uniqueId = "tcp"  + ":" + tcpIp + ":" + tcpPort;
                break;

            case ConnectionType.TYPE_USB:
                uniqueId = "usb";
                break;

            case ConnectionType.TYPE_SOLO:
                String soloLinkId = "";
                if(paramsBundle != null){
                    soloLinkId = paramsBundle.getString(ConnectionType.EXTRA_SOLO_LINK_ID, "");
                }
                uniqueId = "solo:" + soloLinkId;
                break;

            default:
                uniqueId = "";
                break;
        }

        return uniqueId;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof ConnectionParameter)) return false;

        ConnectionParameter that = (ConnectionParameter) o;
        return getUniqueId().equals(that.getUniqueId());
    }

    @Override
    public int hashCode(){
        return getUniqueId().hashCode();
    }

    @Override
    public String toString() {
        String toString = "ConnectionParameter{" +
                "connectionType=" + connectionType +
                ", paramsBundle=[";

        if (paramsBundle != null && !paramsBundle.isEmpty()) {
            boolean isFirst = true;
            for (String key : paramsBundle.keySet()) {
                if (isFirst)
                    isFirst = false;
                else
                    toString += ", ";

                toString += key + "=" + paramsBundle.get(key);
            }
        }

        toString += "]}";
        return toString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.connectionType);
        dest.writeBundle(paramsBundle);
        dest.writeParcelable(this.droneSharePrefs, 0);
    }

    private ConnectionParameter(Parcel in) {
        this.connectionType = in.readInt();
        paramsBundle = in.readBundle();
        this.droneSharePrefs = in.readParcelable(DroneSharePrefs.class.getClassLoader());
    }

    public static final Creator<ConnectionParameter> CREATOR = new Creator<ConnectionParameter>() {
        public ConnectionParameter createFromParcel(Parcel source) {
            return new ConnectionParameter(source);
        }

        public ConnectionParameter[] newArray(int size) {
            return new ConnectionParameter[size];
        }
    };
}
