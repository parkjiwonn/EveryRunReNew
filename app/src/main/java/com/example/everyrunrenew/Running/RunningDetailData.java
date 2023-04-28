package com.example.everyrunrenew.Running;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class RunningDetailData implements Serializable {

    @SerializedName("distance")
    private String distance;

    @SerializedName("AllPaceDoublelist")
    private ArrayList<Double> AllPaceDoublelist;

    @SerializedName("MinPace")
    private String MinPace;

    @SerializedName("time")
    private String time;

    @SerializedName("kcal")
    private String kcal;

    @SerializedName("cadence")
    private String cadence;

    @SerializedName("MaxHeight")
    private String MaxHeight;

    @SerializedName("MinHeight")
    private String MinHeight;

    public RunningDetailData(String distance, ArrayList<Double> allPaceDoublelist, String minPace, String time, String kcal, String cadence, String maxHeight, String minHeight) {
        this.distance = distance;
        this.AllPaceDoublelist = allPaceDoublelist;
        this.MinPace = minPace;
        this.time = time;
        this.kcal = kcal;
        this.cadence = cadence;
        this.MaxHeight = maxHeight;
        this.MinHeight = minHeight;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public ArrayList<Double> getAllPaceDoublelist() {
        return AllPaceDoublelist;
    }

    public void setAllPaceDoublelist(ArrayList<Double> allPaceDoublelist) {
        AllPaceDoublelist = allPaceDoublelist;
    }

    public String getMinPace() {
        return MinPace;
    }

    public void setMinPace(String minPace) {
        MinPace = minPace;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public String getCadence() {
        return cadence;
    }

    public void setCadence(String cadence) {
        this.cadence = cadence;
    }

    public String getMaxHeight() {
        return MaxHeight;
    }

    public void setMaxHeight(String maxHeight) {
        MaxHeight = maxHeight;
    }

    public String getMinHeight() {
        return MinHeight;
    }

    public void setMinHeight(String minHeight) {
        MinHeight = minHeight;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +

                "distance=" + distance +
                ", time=" + time +
                ", kcal=" + kcal +
                ", cadence=" + cadence +
                ", AllPaceDoublelist=" + AllPaceDoublelist +
                ", MaxHeight=" + MaxHeight +
                ", MinHeight=" + MinHeight +
                ", MinPace=" + MinPace +

                '}';
    }

}
