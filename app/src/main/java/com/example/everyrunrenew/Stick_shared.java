package com.example.everyrunrenew;

import android.content.Context;
import android.content.SharedPreferences;

public class Stick_shared {
    private final String INTRO = "intro";
    private final String EMAIL = "email"; // 유저 이메일
    private final String NICK = "nick"; // 유저 닉네임
    private final String TYPE = "type"; // 유저 닉네임

    private SharedPreferences app_prefs;
    // 변수 선언
    private Context context;

    public Stick_shared(Context context)
    {
        app_prefs = context.getSharedPreferences("stick_shared", 0);
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

    public void putType(int loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putInt(TYPE, loginOrOut);
        edit.apply();
    }

    public int getType()
    {
        return app_prefs.getInt(TYPE, 0);
        // 불러오기.
    }

    public void clear(){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.clear();
        edit.commit();
    }



}
