package com.example.everyrunrenew.Record;

import java.util.ArrayList;

public class ListData {
    private String km;
    private String face;
    private double doubleface;

    public ListData(String km, String face, double doubleface) {
        this.km = km;
        this.face = face;
        this.doubleface = doubleface;
    }

    public double getDoubleface() {
        return doubleface;
    }

    public void setDoubleface(double doubleface) {
        this.doubleface = doubleface;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }
}
