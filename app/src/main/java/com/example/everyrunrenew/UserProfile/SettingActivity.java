
package com.example.everyrunrenew.UserProfile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.everyrunrenew.R;
import com.example.everyrunrenew.UserInfo.LoginActivity;
import com.example.everyrunrenew.UserInfo.ResetPass.FromLoginFindPassActivity;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName(); // log
    private UserSharedPreference preferenceHelper;
    private ActivitySettingBinding binding;
    Context context;
    String option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        binding.txResetpass.setOnClickListener(this);
        binding.btnLogout.setOnClickListener(this);
        // shared 객체 생성
        preferenceHelper = new UserSharedPreference(this);
        binding.txHealth.setOnClickListener(this);
        binding.switchview.setOnClickListener(this);

        setUI();
    }

    private void setUI() {
        // shared에서 날씨옵션 on인지 off인지 확인하고 세팅해야함.
        option = preferenceHelper.getWeather();
        Log.d(TAG, "setUI: option =" + option);
        // option on인지 off인지 확인 가능
        if(option.equals("on")){
            // 옵션이 on 이라면
            binding.switchview.setChecked(true);
        }else{
            // 옵션이 off라면
            binding.switchview.setChecked(false);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_logout:


                BuildDiaglog();

                break;

            case R.id.tx_resetpass:

                startActivity(new Intent(SettingActivity.this, FromLoginFindPassActivity.class));
                // shared 정보 삭제해야함.
                finish();

                break;

            case R.id.tx_health:
                startActivity(new Intent(SettingActivity.this, HealthInfoActivity.class));
                break;
                
            case R.id.switchview:
                ClickWeatherOption();
                break;
        }
    }

    // 날씨 옵션 클릭했을 때 메서드
    private void ClickWeatherOption() {
        if(binding.switchview.isChecked())
        {
            // 스위치 on 
            Log.d(TAG, "ClickWeatherOption: 스위치 on");
            preferenceHelper.putWeather("on");
            // shared 저장 , 변경까지 확인
        }else{
            // 스위치 off
            Log.d(TAG, "ClickWeatherOption: 스위치 off");
            preferenceHelper.putWeather("off");
        }
    }

    private void BuildDiaglog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preferenceHelper.clear(); // shared 정보 삭제

                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                // shared 정보 삭제해야함.
                finish();
            }
        });
        builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
}