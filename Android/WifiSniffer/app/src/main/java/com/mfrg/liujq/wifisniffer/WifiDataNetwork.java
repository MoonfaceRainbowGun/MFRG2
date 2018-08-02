package com.mfrg.liujq.wifisniffer;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

public class WifiDataNetwork implements Comparable<WifiDataNetwork>, Parcelable {
    private String bssid;
    private String ssid;
    private int level;

    public WifiDataNetwork(ScanResult result) {
        bssid = result.BSSID;
        ssid = result.SSID;
        level = result.level;
    }

    public WifiDataNetwork(Parcel in) {
        bssid = in.readString();
        ssid = in.readString();
        level = in.readInt();
    }

    public static final Creator<WifiDataNetwork> CREATOR = new Creator<WifiDataNetwork>() {
        public WifiDataNetwork createFromParcel(Parcel in) {
            return new WifiDataNetwork(in);
        }

        public WifiDataNetwork[] newArray(int size) {
            return new WifiDataNetwork[size];
        }
    };

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int compareTo(WifiDataNetwork another) {
        return another.level - this.level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bssid);
        dest.writeString(ssid);
        dest.writeInt(level);
    }

    @Override
    public String toString() {
        return ssid + " addr:" + bssid + " lev:" + level;
    }
}
