package com.example.everyrunrenew.RetrofitData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.naver.maps.geometry.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class FinalRunningData implements Serializable, Parcelable{


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

    @SerializedName("Date")
    private String Date;

    @SerializedName("StartTime")
    private String StartTime;

    @SerializedName("FinishTime")
    private String FinishTime;

    @SerializedName("title")
    private String title;

    @SerializedName("User_email")
    private String User_email;

    public FinalRunningData(String distance, String face, String time, String kcal, String altitude, String cadence, ArrayList<LatLng> locationarray,
                            ArrayList<LatLng> facePerKmArraylist, ArrayList<String> faceArraylist, ArrayList<Double> faceDoublelist,
                            ArrayList<Double> allPaceDoublelist, String maxHeight, String minHeight, String minPace, String date,
                            String startTime, String finishTime, String user_email, String title) {
        this.distance = distance;
        this.face = face;
        this.time = time;
        this.kcal = kcal;
        this.altitude = altitude;
        this.cadence = cadence;
        this.locationarray = locationarray;
        this.FacePerKmArraylist = facePerKmArraylist;
        this.FaceArraylist = faceArraylist;
        this.FaceDoublelist = faceDoublelist;
        this.AllPaceDoublelist = allPaceDoublelist;
        this.MaxHeight = maxHeight;
        this.MinHeight = minHeight;
        this.MinPace = minPace;
        this.Date = date;
        this.StartTime = startTime;
        this.FinishTime = finishTime;
        this.User_email = user_email;
        this.title = title;
    }

    protected FinalRunningData(Parcel in) {
        distance = in.readString();
        face = in.readString();
        time = in.readString();
        kcal = in.readString();
        altitude = in.readString();
        cadence = in.readString();
        locationarray = in.createTypedArrayList(LatLng.CREATOR);
        FacePerKmArraylist = in.createTypedArrayList(LatLng.CREATOR);
        FaceArraylist = (ArrayList<String>) in.readSerializable();
        FaceDoublelist = (ArrayList<Double>) in.readSerializable();
        AllPaceDoublelist = (ArrayList<Double>) in.readSerializable();
        MaxHeight = in.readString();
        MinHeight = in.readString();
        MinPace = in.readString();
        Date = in.readString();
        StartTime = in.readString();
        FinishTime = in.readString();
        User_email = in.readString();
        title = in.readString();
    }

    public static final Creator<FinalRunningData> CREATOR = new Creator<FinalRunningData>() {
        @Override
        public FinalRunningData createFromParcel(Parcel in) {
            return new FinalRunningData(in);
        }

        @Override
        public FinalRunningData[] newArray(int size) {
            return new FinalRunningData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCadence() {
        return cadence;
    }

    public void setCadence(String cadence) {
        this.cadence = cadence;
    }

    public ArrayList<LatLng> getLocationarray() {
        return locationarray;
    }

    public void setLocationarray(ArrayList<LatLng> locationarray) {
        this.locationarray = locationarray;
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

    public ArrayList<Double> getFaceDoublelist() {
        return FaceDoublelist;
    }

    public void setFaceDoublelist(ArrayList<Double> faceDoublelist) {
        FaceDoublelist = faceDoublelist;
    }

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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getFinishTime() {
        return FinishTime;
    }

    public void setFinishTime(String finishTime) {
        FinishTime = finishTime;
    }

    public String getUser_email() {
        return User_email;
    }

    public void setUser_email(String user_email) {
        User_email = user_email;
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
                ", Date=" + Date +
                ", User_email=" + User_email +
                ", StartTime=" + StartTime +
                ", FinishTime=" + FinishTime +
                ", title=" + title +

                '}';
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
        parcel.writeString(cadence);
        parcel.writeTypedList(locationarray);
        parcel.writeTypedList(FacePerKmArraylist);
        parcel.writeSerializable(FaceArraylist);
        parcel.writeSerializable(FaceDoublelist);
        parcel.writeSerializable(AllPaceDoublelist);
        parcel.writeString(MaxHeight);
        parcel.writeString(MinHeight);
        parcel.writeString(MinPace);
        parcel.writeString(Date);
        parcel.writeString(StartTime);
        parcel.writeString(FinishTime);
        parcel.writeString(User_email);
        parcel.writeString(title);
    }
}
