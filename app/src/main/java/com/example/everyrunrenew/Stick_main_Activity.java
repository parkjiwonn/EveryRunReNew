package com.example.everyrunrenew;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.everyrunrenew.UserInfo.UserSharedPreference;

public class Stick_main_Activity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName(); // log
    String email;
    String nick;
    int type;

    private Stick_shared preferenceHelper;
    Button login;
    EditText et_email;
    EditText et_nick;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_main);
        // (1) 메인 화면에는 로그인을 해야 하나?
        preferenceHelper = new Stick_shared(this);
        et_email = findViewById(R.id.et_email);
        et_nick = findViewById(R.id.et_nick);
        radioGroup = findViewById(R.id.radio);

        // 서비스 돌려서 서버 통신하기
        //

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_blind:
                        // 시각장애인 선택
                        type = 0;
                        break;
                    case R.id.radio_protector:
                        // 보호자 선택
                        type = 1;
                        break;
                }
            }
        });



        // 로그인 버튼을 누르면 유저 정보가 shard에 저장되어야 한다.
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_email.getText().toString();
                nick = et_nick.getText().toString();
                Log.d(TAG, "onClick: email = " + email);
                Log.d(TAG, "onClick: nick = " + nick);
                Log.d(TAG, "onClick: type = " + type);
                preferenceHelper.putIsLogin(true);
                preferenceHelper.putEmail(email);
                preferenceHelper.putNick(nick);
                preferenceHelper.putType(type);

                startActivity(new Intent(Stick_main_Activity.this, Stick_menu_Activity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: 들어옴");
        // shared 확인
        String userEmail = preferenceHelper.getEmail();
        String userNick = preferenceHelper.getNick();
        
        if(userNick != "" && userEmail != ""){
            Log.d(TAG, "onStart: 로그인 되어있음");
            startActivity(new Intent(Stick_main_Activity.this, Stick_menu_Activity.class));
            finish();
            
        }else{
            Log.d(TAG, "onStart: 로그인 안되어 있음.");
        }
    }
}