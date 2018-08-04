package com.mfrg.liujq.wifisniffer;

import com.google.gson.Gson;

import java.util.ArrayList;

public class DataPredict {
    private String uid;
    private ArrayList<WifiDataNetwork> data;

    public DataPredict(String uid, ArrayList<WifiDataNetwork> data) {
        this.uid = uid;
        this.data = data;
    }

    public String getJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
}