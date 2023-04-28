package com.example.everyrunrenew.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedPreference {
    private final String INTRO = "intro";
    private final String EMAIL = "email"; // 유저 이메일
    private final String NICK = "nick"; // 유저 닉네임
    private final String PROFILE = "profile"; // 유저 프로필 사진
    private final String HEIGHT = "height"; // 유저 키 정보
    private final String WEIGHT = "weight"; // 유저 몸무게 정보
    private final String WEATHER = "weather"; // 날씨 옵션 on/off 확인


    private SharedPreferences app_prefs;
    // 변수 선언
    private Context context;



    public UserSharedPreference(Context context)
    {
        app_prefs = context.getSharedPreferences("shared", 0);
        // 초기화
        this.context = context;
    }


    public void putIsLogin(boolean loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putBoolean(INTRO, loginOrOut);
        edit.apply();
        // 저장
    }
    public void putEmail(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(EMAIL, loginOrOut);
        edit.apply();
    }

    public String getEmail()
    {
        return app_prefs.getString(EMAIL, "");
    }

    public void putNick(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(NICK, loginOrOut);
        edit.apply();
    }

    public String getNick()
    {
        return app_prefs.getString(NICK, "");
        // 불러오기.
    }

    public void putPROFILE(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PROFILE, loginOrOut);
        edit.apply();
    }

    public String getPROFILE() {
        return app_prefs.getString(PROFILE, "");
    }

    public void putHEIGHT(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(HEIGHT, loginOrOut);
        edit.apply();
    }

    public String getHEIGHT()
    {
        return app_prefs.getString(HEIGHT, "");
    }

    public void putWEIGHT(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(WEIGHT, loginOrOut);
        edit.apply();
    }

    public String getWEIGHT()
    {
        return app_prefs.getString(WEIGHT, "");
    }

    public void clear(){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.clear();
        edit.commit();
    }

    public void putWeather(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(WEATHER, loginOrOut);
        edit.apply();
    }

    public String getWeather()
    {
        return app_prefs.getString(WEATHER, "");
    }

}
