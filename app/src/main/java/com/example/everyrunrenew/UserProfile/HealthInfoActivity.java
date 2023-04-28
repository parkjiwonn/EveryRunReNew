package com.example.everyrunrenew.UserProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.UserInfoData;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.databinding.ActivityHealthInfoBinding;
import com.example.everyrunrenew.databinding.ActivitySettingBinding;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthInfoActivity extends AppCompatActivity  implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityHealthInfoBinding binding;
    Context context;
    Dialog dialog;

    int pickerHeight; // 키 값
    int pickerWeight; // 체중 값

    double bmiResult; // 비만도 계산
    String result2;

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private UserSharedPreference preferenceHelper;

    String user_email;
    int user_height;
    int user_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        preferenceHelper = new UserSharedPreference(this);
        user_email = preferenceHelper.getEmail();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.numberpicker_dialog);

        setUI();

        binding.txHeight.setOnClickListener(this);
        binding.txWeight.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);

    }

    private void setUI() {
        retrofitInterface.SetUserBMI(user_email).enqueue(new Callback<UserInfoData>() {
            @Override
            public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {

                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: response is success");
                    UserInfoData result = response.body();
                    user_height = result.getUser_height();
                    user_weight = result.getUser_weight();

                    binding.txHeight.setText(String.valueOf(user_height));
                    binding.txWeight.setText(String.valueOf(user_weight));

                    setFirstBMI(user_height, user_weight);
                    Log.d(TAG, "onResponse: result = "+result);
                }else{
                    Log.d(TAG, "onResponse: response is fail");
                }
            }

            @Override
            public void onFailure(Call<UserInfoData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    // BMI 세팅해주는 메서드
    private void setFirstBMI(int user_height, int user_weight) {

       // bmiResult =  Double.parseDouble(String.valueOf(user_weight)) / ((Double.parseDouble(String.valueOf(user_height)) / 100) * (Double.parseDouble(String.valueOf(user_height)) / 100));

        bmiResult = Double.parseDouble(String.valueOf(user_weight))  / Double.parseDouble(String.valueOf(user_height))/ Double.parseDouble(String.valueOf(user_height))* 10000;

        if (bmiResult <= 18.5) {
            result2 = "저체중";
        } else if (bmiResult <= 23 && bmiResult > 18.5) {
            result2 = "정상체중";
        } else if (bmiResult <= 25 && bmiResult > 23) {
            result2 = "과체중";
        } else {
            result2 = "비만";
        }

        String BMI = String.format("%.1f", bmiResult);
        binding.txBmi.setText("현재 체질량 지수는 "+BMI+"이며 현재 "+ result2+"입니다." );
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
                // 저장
                UpdateUserBmi();
                break;
        }
    }

    private void UpdateUserBmi() {
        String update_height = binding.txHeight.getText().toString();
        String update_weight = binding.txWeight.getText().toString();

        retrofitInterface.UpdateUserBmi(user_email,update_height, update_weight).enqueue(new Callback<UserInfoData>() {
            @Override
            public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {

                if(response.isSuccessful()){
                    UserInfoData result = response.body();
                    Log.d(TAG, "onResponse: 유저 신체정보 업데이트 후 result = "+result);
                    String status = result.getStatus();
                    
                    if(status.equals("true")){

                        // 업데이트 성공했으니까 toast 메세지 띄워주기
                        Toast.makeText(context, "신체정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();

                    }else{
                        Log.d(TAG, "onResponse: 유저 신체정보 바꾼 후 status is false");
                    }
                }else{
                    Log.d(TAG, "onResponse: response is fail");
                }
            }

            @Override
            public void onFailure(Call<UserInfoData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    // 유저 신체정보 수정하기


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

            // 만약 이미 선택된게 있다면 선택된 값이 세팅되어야 한다.
            if(pickerHeight == 0){
                // 선택안했을 떄
                numberPicker.setValue(user_height);
            }else{
                // 선택했을 떄
                numberPicker.setValue(pickerHeight);
            }


        }else{
            // i = 1 이라면 몸무게 선택
            tx_set.setText("kg");
            tx_title.setText("체중");
            numberPicker.setMaxValue(227);
            numberPicker.setMinValue(13);

            if(pickerWeight == 0){
                // 선택안했을때
                numberPicker.setValue(user_weight);
            }else{
                // 선택했을 때
                numberPicker.setValue(pickerWeight);
            }

        }

        // numberpicker에서 값 변화할때 리스너 메서드
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                Log.d(TAG, "onValueChange: oldValue ="+oldValue);
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
                    Log.d(TAG, "onClick: pickerHeight ="+pickerHeight);

                    if(pickerHeight == 0)
                    {
                        // 선택안했다는 것.
                        binding.txHeight.setText(String.valueOf(user_height));

                    }else{
                        binding.txHeight.setText(String.valueOf(pickerHeight));

                    }

                    setBMI();

                }else{
                    // 체중 세팅
                    Log.d(TAG, "onClick: pickerWeight =" + pickerWeight);

                    if(pickerWeight == 0){
                        //선택안했다는 것
                        binding.txWeight.setText(String.valueOf(user_weight));

                    }else{
                        binding.txWeight.setText(String.valueOf(pickerWeight));
                    }

                    setBMI();
                }
            }
        });

    }

    // BMI 계산
    private void setBMI() {
        bmiResult =  Double.parseDouble(binding.txWeight.getText().toString()) / ((Double.parseDouble(binding.txHeight.getText().toString()) / 100) *
                (Double.parseDouble(binding.txHeight.getText().toString()) / 100));

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