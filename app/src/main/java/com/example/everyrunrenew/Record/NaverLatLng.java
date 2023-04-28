package com.example.everyrunrenew.Record;

import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NaverLatLng {

    public static ArrayList<LatLng> locationarray = new ArrayList<>();
    public static ArrayList<LatLng> PaceLocationlist = new ArrayList<>();

    public ArrayList<LatLng> getPaceLocationlist() {
        return PaceLocationlist;
    }

    public void setPaceLocationlist(Double latitude, Double longitude) {
        PaceLocationlist.add(new LatLng(latitude, longitude));
    }

    public void setLocationarray(Double latitude, Double longitude){

        locationarray.add(new LatLng(latitude, longitude));

    }

    public ArrayList<LatLng> getLocationarray(){
        return locationarray;
    }


}
