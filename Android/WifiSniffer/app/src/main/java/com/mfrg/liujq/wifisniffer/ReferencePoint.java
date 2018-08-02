package com.mfrg.liujq.wifisniffer;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ReferencePoint {
    private int x;
    private int y;
    private ArrayList<WifiDataNetwork> data;

    public ReferencePoint(int x, int y, ArrayList<WifiDataNetwork> data) {
        this.x = x;
        this.y = y;
        this.data = data;
    }

    public String getJSON() {
        Gson gson = new Gson();
        String rpJSON = gson.toJson(this);

        return rpJSON;
    }
}