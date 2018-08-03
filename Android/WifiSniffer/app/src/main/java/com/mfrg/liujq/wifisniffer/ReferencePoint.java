package com.mfrg.liujq.wifisniffer;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ReferencePoint {
    private String id;
    private ArrayList<WifiDataNetwork> data;

    public ReferencePoint(String id, ArrayList<WifiDataNetwork> data) {
        this.id = id;
        this.data = data;
    }

    public String getJSON() {
        Gson gson = new Gson();
        String rpJSON = gson.toJson(this);

        return rpJSON;
    }
}