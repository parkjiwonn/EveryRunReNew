package com.example.everyrunrenew.UserInfo.ResisterStep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.everyrunrenew.MainActivity;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.UserInfoData;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.databinding.ActivityStep4Binding;
import com.example.everyrunrenew.databinding.ActivityStep5Binding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Step5Activity extends AppCompatActivity  implements View.OnClickListener{

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityStep5Binding binding;
    Context context;

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private UserSharedPreference preferenceHelper;
    Dialog dialog;

    // db 로 보낼값
    String user_email;
    String user_pass;
    String user_nick;
    int pickerHeight; // 키 값
    int pickerWeight; // 체중 값

    double bmiResult; // 비만도 계산
    String result2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // binding 객체 생성하기
        binding = ActivityStep5Binding.inflate(getLayoutInflater());
        // 해당 액티비티에 맞는 레이아웃 불러오기
        setContentView(binding.getRoot());
        context = getApplicationContext();

        //step3 액티비티에서 보낸 유저 email, pass.
        user_email = getIntent().getStringExtra("email");
        user_pass = getIntent().getStringExtra("pass");
        user_nick = getIntent().getStringExtra("nick");
        Log.d(TAG, "onCreate: email, pass, nick =" + user_email +" , "+ user_pass + " ," + user_nick);
        // email, pass 잘 받아오는 거 확인함.

        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        preferenceHelper = new UserSharedPreference(this);

        binding.btnSave.setOnClickListener(this);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.numberpicker_dialog);

        binding.txHeight.setOnClickListener(this);
        binding.txWeight.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.tx_height:
                ShowNumberPickerDialog(0);
                break;

            case R.id.tx_weight:
                ShowNumberPickerDialog(1);
                break;

            case R.id.btn_save:

                Log.d(TAG, "onClick: SaveUserInfo 데이터들 = " + user_email + user_nick + user_pass + pickerHeight + pickerWeight);
                SaveUserInfo(user_email, user_nick, user_pass, pickerHeight, pickerWeight);

                break;
        }

    }

    // 유저 정보 저장하는 메서드
    private void SaveUserInfo(String user_email, String user_id, String user_pass, int user_height, int user_weight) {

        Log.d(TAG, "SaveUserInfo: user_height, user_weight = "+user_height + user_weight);

        retrofitInterface.SaveUserInfo(user_email,user_pass,user_id, user_height, user_weight).enqueue(new Callback<UserInfoData>() {
            @Override
            public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {

                // 응답이 성공적인지 아닌지 먼저 구분하기.
                if(response.isSuccessful())
                {
                    // 서버에서 온 응답 DTO로 받기
                    UserInfoData result = response.body();
                    String status = result.getStatus();

                    // 가입 성공인지 아닌지 확인하기
                    if(status.equals("true"))
                    {
                        Log.d(TAG, "onResponse: 가입 성공");
                        /**
                         * 가입 성공이면 메인 액티비티로 이동하게 하기
                         * Shared에 회원가입한 유저의 정보 저장하기
                         * 회원가입하고 이후에 앱 사용할때 자동으로 로그인 되게 하기**/

                        //shared에 유저 이메일, 닉네임 저장하기
                        SaveShared(user_email,user_id, user_height, user_weight);

                        Intent i = new Intent(Step5Activity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        // 그 전에 쌓인 스택들 모두 종료시켜야 한다.


                    }else{
                        // mysql 쿼리문이 제대로 안돌았다.
                        Log.d(TAG, "onResponse: 가입 실패");
                    }

                }else {
                    Log.d(TAG, "onResponse: 응답 안됨. ");
                }


            }

            // 쉐어드에 유저 이메일, 닉네임 저장하는 메서드
            private void SaveShared(String user_email, String user_id, int user_height, int user_weight) {
                String height = String.valueOf(user_height);
                String weight = String.valueOf(user_weight);
                preferenceHelper.putIsLogin(true);
                preferenceHelper.putEmail(user_email);
                preferenceHelper.putNick(user_id);
                preferenceHelper.putHEIGHT(height);
                preferenceHelper.putWEIGHT(weight);
                preferenceHelper.putPROFILE("basic");
            }

            @Override
            public void onFailure(Call<UserInfoData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }



    private void ShowNumberPickerDialog(int i) {
        dialog.show();
        NumberPicker numberPicker = dialog.findViewById(R.id.numberpicker);
        Button btn_infosave = dialog.findViewById(R.id.btn_infosave);
        TextView tx_set = dialog.findViewById(R.id.tx_set);
        TextView tx_title = dialog.findViewById(R.id.tx_title);
        // i = 0 이라면 키 선택
        if(i == 0){
            tx_set.setText("cm");
            tx_title.setText("신장");
            numberPicker.setMaxValue(302);
            numberPicker.setMinValue(61);
            numberPicker.setValue(160);

        }else{
            // i = 1 이라면 몸무게 선택
            tx_set.setText("kg");
            tx_title.setText("체중");
            numberPicker.setMaxValue(227);
            numberPicker.setMinValue(13);
            numberPicker.setValue(50);
        }

        // numberpicker에서 값 변화할때 리스너 메서드
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                Log.d(TAG, "onValueChange: selected =" + newValue);

                if(i == 0){
                    // 키
                    pickerHeight = newValue;
                }else{
                    // 체중
                    pickerWeight = newValue;
                }
            }
        });

        // dialog에서 완료 버튼 클릭 리스너 메서드
        btn_infosave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if(i == 0){
                    // 신장 세팅
                    binding.txHeight.setText(String.valueOf(pickerHeight));
                    setBMI();
                }else{
                    // 체중 세팅
                    binding.txWeight.setText(String.valueOf(pickerWeight));
                    setBMI();
                }
            }
        });

    }

    // BMI 계산
    private void setBMI() {
        bmiResult =  Double.parseDouble(String.valueOf(pickerWeight)) / ((Double.parseDouble(String.valueOf(pickerHeight)) / 100) * (Double.parseDouble(String.valueOf(pickerHeight)) / 100));

        if (bmiResult < 20) {
            result2 = "저체중";
        } else if (bmiResult <= 24 && bmiResult > 20) {
            result2 = "정상체중";
        } else if (bmiResult <= 30 && bmiResult > 24) {
            result2 = "과체중";
        } else {
            result2 = "비만";
        }

        String BMI = String.format("%.1f", bmiResult);
        binding.txBmi.setText("현재 체질량 지수는 "+BMI+"이며 현재 "+ result2+"입니다." );
    }
}