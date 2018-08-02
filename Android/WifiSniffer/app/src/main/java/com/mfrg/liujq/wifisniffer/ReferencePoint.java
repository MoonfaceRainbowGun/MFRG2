package com.mfrg.liujq.wifisniffer;

import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import java.util.ArrayList;

public class ReferencePoint {
    private Date createdAt = new Date();
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
//        JsonObject rpJSON = new JsonObject();
//        String data = gson.toJson(this.data);
//
//        rpJSON.addProperty("x", this.x);
//        rpJSON.addProperty("y", this.y);
//        rpJSON.addProperty("data", data);

        return rpJSON;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<WifiDataNetwork> getWifi() {
        return data;
    }

    public void setWifi(ArrayList<WifiDataNetwork> wifi) {
        this.data = data;
    }
}