package com.example.everyrunrenew.Running;

import android.os.Parcel;
import android.os.Parcelable;



import com.google.gson.annotations.SerializedName;
import com.naver.maps.geometry.LatLng;


import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class RunningData implements Serializable, Parcelable {

    @SerializedName("distance")
    private String distance;

    @SerializedName("face")
    private String face;

    @SerializedName("time")
    private String time;

    @SerializedName("kcal")
    private String kcal;

    @SerializedName("altitude")
    private String altitude;

    @SerializedName("cadence")
    private String cadence;

    @SerializedName("locationarray")
    private ArrayList<LatLng> locationarray;

    @SerializedName("FacePerKmArraylist")
    private ArrayList<LatLng> FacePerKmArraylist;

    @SerializedName("FaceArraylist")
    private ArrayList<String> FaceArraylist;

    // 구간별 페이스
    @SerializedName("FaceDoublelist")
    private ArrayList<Double> FaceDoublelist;

    @SerializedName("AllPaceDoublelist")
    private ArrayList<Double> AllPaceDoublelist;

    @SerializedName("MaxHeight")
    private String MaxHeight;

    @SerializedName("MinHeight")
    private String MinHeight;

    @SerializedName("MinPace")
    private String MinPace;


    public RunningData(String distance, String face, String time, String kcal, String altitude, ArrayList<LatLng> locationarray, String cadence , ArrayList<LatLng> FacePerKmArraylist, ArrayList<String> FaceArraylist
    , ArrayList<Double> FaceDoublelist , ArrayList<Double> AllPaceDoublelist , String MaxHeight, String MinHeight, String MinPace) {
        this.distance = distance;
        this.face = face;
        this.time = time;
        this.kcal = kcal;
        this.altitude = altitude;
        this.locationarray = locationarray;
        this.cadence = cadence;
        this.FacePerKmArraylist = FacePerKmArraylist;
        this.FaceArraylist = FaceArraylist;
        this.FaceDoublelist = FaceDoublelist;
        this.AllPaceDoublelist = AllPaceDoublelist;
        this.MaxHeight = MaxHeight;
        this.MinHeight = MinHeight;
        this.MinPace = MinPace;

    }

    protected RunningData(Parcel in) {
        distance = in.readString();
        face = in.readString();
        time = in.readString();
        kcal = in.readString();
        altitude = in.readString();
        locationarray = in.createTypedArrayList(LatLng.CREATOR);
        cadence = in.readString();
        FacePerKmArraylist = in.createTypedArrayList(LatLng.CREATOR);
        FaceArraylist = (ArrayList<String>) in.readSerializable();
        FaceDoublelist = (ArrayList<Double>) in.readSerializable();
        AllPaceDoublelist = (ArrayList<Double>) in.readSerializable();
        MaxHeight = in.readString();
        MinHeight = in.readString();
        MinPace = in.readString();
    }

    public static final Creator<RunningData> CREATOR = new Creator<RunningData>() {
        @Override
        public RunningData createFromParcel(Parcel in) {
            return new RunningData(in);
        }

        @Override
        public RunningData[] newArray(int size) {
            return new RunningData[size];
        }
    };

    public ArrayList<Double> getAllPaceDoublelist() {
        return AllPaceDoublelist;
    }

    public void setAllPaceDoublelist(ArrayList<Double> allPaceDoublelist) {
        AllPaceDoublelist = allPaceDoublelist;
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

    public String getMinPace() {
        return MinPace;
    }

    public void setMinPace(String minPace) {
        MinPace = minPace;
    }

    public ArrayList<Double> getFaceDoublelist() {
        return FaceDoublelist;
    }

    public void setFaceDoublelist(ArrayList<Double> faceDoublelist) {
        FaceDoublelist = faceDoublelist;
    }

    public ArrayList<LatLng> getFacePerKmArraylist() {
        return FacePerKmArraylist;
    }

    public void setFacePerKmArraylist(ArrayList<LatLng> facePerKmArraylist) {
        FacePerKmArraylist = facePerKmArraylist;
    }

    public ArrayList<String> getFaceArraylist() {
        return FaceArraylist;
    }

    public void setFaceArraylist(ArrayList<String> faceArraylist) {
        FaceArraylist = faceArraylist;
    }

    public String getCadence() {
        return cadence;
    }

    public void setCadence(String cadence) {
        this.cadence = cadence;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
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

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public ArrayList<LatLng> getLocationarray() {
        return locationarray;
    }

    public void setLocationarray(ArrayList<LatLng> locationarray) {
        this.locationarray = locationarray;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(distance);
        parcel.writeString(face);
        parcel.writeString(time);
        parcel.writeString(kcal);
        parcel.writeString(altitude);
        parcel.writeTypedList(locationarray);
        parcel.writeString(cadence);
        parcel.writeTypedList(FacePerKmArraylist);
        parcel.writeSerializable(FaceArraylist);
        parcel.writeSerializable(FaceDoublelist);
        parcel.writeSerializable(AllPaceDoublelist);
        parcel.writeString(MaxHeight);
        parcel.writeString(MinHeight);
        parcel.writeString(MinPace);
    }


    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +

                "distance=" + distance +
                ", face=" + face +
                ", time=" + time +
                ", kcal=" + kcal +
                ", altitude=" + altitude +
                ", locationarray=" + locationarray +
                ", cadence=" + cadence +
                ", FacePerKmArraylist=" + FacePerKmArraylist +
                ", FaceArraylist=" + FaceArraylist +
                ", FaceDoublelist=" + FaceDoublelist +
                ", AllPaceDoublelist=" + AllPaceDoublelist +
                ", MaxHeight=" + MaxHeight +
                ", MinHeight=" + MinHeight +
                ", MinPace=" + MinPace +

                '}';
    }

}
