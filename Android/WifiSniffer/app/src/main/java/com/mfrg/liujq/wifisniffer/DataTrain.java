package com.mfrg.liujq.wifisniffer;

import com.google.gson.Gson;

import java.util.ArrayList;

public class DataTrain {
    private String id;
    private ArrayList<WifiDataNetwork> data;

    public DataTrain(String id, ArrayList<WifiDataNetwork> data) {
        this.id = id;
        this.data = data;
    }

    public String getJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
}