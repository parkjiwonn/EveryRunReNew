package com.example.everyrunrenew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.everyrunrenew.Community.CommunityActivity;
import com.example.everyrunrenew.UserInfo.LoginActivity;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;

public class SplashActivity extends AppCompatActivity {

    final String TAG = getClass().getName();
    private UserSharedPreference preferenceHelper;

    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.e(TAG, "onCreate: 들어옴" );

        mContext = SplashActivity.this;

        preferenceHelper = new UserSharedPreference(this);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 1000);
    }

    private class splashhandler implements Runnable{
        @Override
        public void run() {

            String userId = preferenceHelper.getEmail();
            String userNick = preferenceHelper.getNick();

            if(userId != "" && userNick != "")
            {
                Log.e(TAG, "run: homeactivity" );
                startActivity(new Intent(getApplication(), MainActivity.class));
                SplashActivity.this.finish();
            }else{
                Log.e(TAG, "run: loginactivity" );
                startActivity(new Intent(getApplication(), LoginActivity.class));
                SplashActivity.this.finish();

            }


        }
    }
}