package com.example.everyrunrenew.Record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.everyrunrenew.Community.CommunityActivity;
import com.example.everyrunrenew.MainActivity;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.FinalRunningData;
import com.example.everyrunrenew.RetrofitData.UserInfoData;
import com.example.everyrunrenew.Running.HardCord;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.UserProfile.HealthInfoActivity;
import com.example.everyrunrenew.UserProfile.SettingActivity;
import com.example.everyrunrenew.UserProfile.UserProfileActivity;
import com.example.everyrunrenew.databinding.ActivityCommunityBinding;
import com.example.everyrunrenew.databinding.ActivityRecord2Binding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.naver.maps.geometry.LatLng;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityRecord2Binding binding;
    Context context;
    TextView tx_nick;
    ImageView headerprofile;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private UserSharedPreference preferenceHelper;
    String user_email;

    RecyclerView recyclerView = null;
    RecordAdapter recordAdapter = null;
    ArrayList<FinalRunningData> finalRunningData ;

    Gson gson = new Gson();
    SharedPreferences preferences;



    int month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecord2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = getApplicationContext();
        preferenceHelper = new UserSharedPreference(this);
        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        recyclerView = findViewById(R.id.running_rv);
        finalRunningData = new ArrayList<>();

        ArrayList<HardCord> hardCordArrayList = new ArrayList<>();
        hardCordArrayList.add(new HardCord("2023.02.01","수요일 오전 러닝", "3.34km","34:35","04'55''"));
        hardCordArrayList.add(new HardCord("2023.02.02","목요일 오후 러닝", "4.54km","54:35","06'55''"));
        hardCordArrayList.add(new HardCord("2023.02.03","금요일 오전 러닝", "5.54km","01:30:35","05'35''"));

        recordAdapter = new RecordAdapter(hardCordArrayList,getApplicationContext());
        recyclerView.setAdapter(recordAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        setUI();
    }

    private void setUI() {

        // 러닝 분석 세팅하기
        // 총 이동거리, 총 소요시간, 평균 페이스
        binding.txDistance.setText("13.42km");
        binding.txTime.setText("02:59:45");
        binding.txPace.setText("5'48''");



        user_email = preferenceHelper.getEmail();

        Log.d(TAG, "setUI: email = " + user_email);

        // inflateheaderview 사용해서 코드상에서 네비게이션 뷰에 헤더 추가하기
        View headerview = binding.navigationView.inflateHeaderView(R.layout.header_layout);
        tx_nick = headerview.findViewById(R.id.tx_nick); // 헤더뷰에서 유저 닉네임 선언해주기
        headerprofile = headerview.findViewById(R.id.img_profile); // 헤더뷰에 프로필 이미지 선언해주기

        // 사이드 메뉴 세팅
        retrofitInterface.SetUserInfo(user_email).enqueue(new Callback<UserInfoData>() {
            @Override
            public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                Log.d(TAG, "setUI: 2");
                UserInfoData result = response.body();
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "setUI: 3");
                    Log.d(TAG, "onResponse: result=" + result);
                    String status = result.getStatus();
                    Log.d(TAG, "setUI: 4");
                    Log.d(TAG, "onResponse: result=" + result);

                    if (status.equals("true")) {
                        // header에서 사용자 프로필 사진 세팅하기
                        if (result.getUser_photo().equals("basic")) {
                            // 기본 이미지로 세팅해주기
                            headerprofile.setImageResource(R.drawable.user_img);

                        } else {
                            // 사용자가 설정한 이미지 UserProfileImg
                            String url = "http://3.36.174.137/UserProfileImg/" + result.getUser_photo();
                            // glide로 이미지 세팅해주기
                            Glide.with(RecordActivity.this).load(url).into(headerprofile);
                        }

                        // header에서 사용자 닉네임 세팅하기
                        tx_nick.setText(result.getUser_name());
                    }
                } else {
                    Log.e(TAG, "onResponse: 응답 실패");
                }

            }

            @Override
            public void onFailure(Call<UserInfoData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });


        headerprofile.setOnClickListener(new View.OnClickListener() { // 프로필 이미지 클릭 리스너
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 이미지 뷰 선택");
                // 헤더에서 유저 이미지 선택하면 유저 프로필로 이동시키기
                startActivity(new Intent(RecordActivity.this, UserProfileActivity.class));
                finish();

            }
        });


        //==========================drawer layout 설정========================================
        setSupportActionBar(binding.toolbar); // 툴바 세팅 먼저 해야함.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바 타이틀 비활성화 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        //==========================drawer layout 설정========================================

        // navigation view 헤더 뷰에 접근하기


        // navigation view 버튼 선택 리스너
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    // 홈 화면으로 이동
                    case R.id.nav_home:
                        item.setChecked(true);
                        binding.drawerLayout.closeDrawers();
                        startActivity(new Intent(RecordActivity.this, MainActivity.class));
                        return true;

                    // 운동 기록 화면으로 이동
                    case R.id.nav_record:
                        item.setChecked(true);
                        binding.drawerLayout.closeDrawers();
                        startActivity(new Intent(RecordActivity.this, RecordActivity.class));
                        return true;

                    // 커뮤니티로 이동
                    case R.id.nav_community:
                        item.setChecked(true);
                        binding.drawerLayout.closeDrawers();
                        startActivity(new Intent(RecordActivity.this, CommunityActivity.class));
                        return true;
//
//                    // 커뮤니티 화면으로 이동
//                    case R.id.nav_callenge:
//                        item.setChecked(true);
//                        binding.drawerLayout.closeDrawers();
//                        startActivity(new Intent(CommunityActivity.this, ChallengeActivity.class));
//                        return true;

                    // 설정 화면으로 이동
                    case R.id.nav_setting:
                        item.setChecked(true);
                        binding.drawerLayout.closeDrawers();
                        startActivity(new Intent(RecordActivity.this, SettingActivity.class));
                        return true;


                }

                return false;
            }
        });

        // 캘린더뷰 설정
//        DateFormat format = new SimpleDateFormat("M", Locale.KOREA);
//        Date date = new Date(binding.calendarView.getDate());
//        String str_month = format.format(date);
//        Log.d(TAG, "setUI: str_month ="+str_month);
//        long now = System.currentTimeMillis();
//        Log.d(TAG, "setUI: now ="+now);
//        Date date = new Date(now);
//        binding.calendarView.setDateSelected(date, true);
        //binding.calendarView.setDateSelected(CalendarDay.from(2022,12,30));

        String[] result = {"2023,02,01","2023,02,02","2023,02,03","2017,06,18"};
        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        binding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Log.d(TAG, "onDateSelected: date = "+date);
            }
        });




        preferences = getSharedPreferences("RunningData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // editor를 선언해주는데 sharedpreference에 null값이여서 오류가 뜬것임.


        //String result = preferences.getString(str_month, "fail");




//        if(result == "fail"){
//            Log.d(TAG, "setUI: 데이터 없음");
//        }else{
//            ArrayList<FinalRunningData> runningData;
//            Type type =new TypeToken<ArrayList<FinalRunningData>>(){}.getType();
//            runningData = gson.fromJson(result,type);
//
//            for(int i =0; i<runningData.size(); i++)
//            {
//                //arrayList의 사이즈만큼 반복문을 돌린다.
//
//                String distance = runningData.get(i).getDistance();
//                String pace = runningData.get(i).getFace();
//                String time = runningData.get(i).getTime();
//                String kcal = runningData.get(i).getKcal();
//                String altitude = runningData.get(i).getAltitude();
//                String cadence = runningData.get(i).getCadence();
//                ArrayList<LatLng> locationarray = runningData.get(i).getLocationarray();
//                ArrayList<LatLng> FacePerKmArraylist = runningData.get(i).getFacePerKmArraylist();
//                ArrayList<String> FaceArraylist = runningData.get(i).getFaceArraylist();
//                ArrayList<Double> FaceDoublelist = runningData.get(i).getFaceDoublelist();
//                ArrayList<Double> AllFaceDoublelist = runningData.get(i).getAllPaceDoublelist();
//                String MaxHeight = runningData.get(i).getMaxHeight();
//                String MinHeight = runningData.get(i).getMinHeight();
//                String MinPace = runningData.get(i).getMinPace();
//                String title = runningData.get(i).getTitle();
//                String getTime = runningData.get(i).getDate();
//                String startTime = runningData.get(i).getStartTime();
//                String finishTime = runningData.get(i).getFinishTime();
//
//                finalRunningData.add(new FinalRunningData(distance, pace,time
//                        ,kcal, altitude, cadence,locationarray,FacePerKmArraylist, FaceArraylist, FaceDoublelist, AllFaceDoublelist, MaxHeight,
//                        MinHeight, MinPace, getTime, startTime,finishTime,user_email, title));
//                // 리사이클러 뷰의 DList에 넣는다.
//
//            }
//
//            recordAdapter.notifyDataSetChanged();
//        }

    }



    // 툴바의 메뉴 버튼 클릭시 - navigation view 나오는 메서드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // 왼쪽 상단 버튼 눌렀을 때
                binding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {

    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1,dayy);
            }



            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            binding.calendarView.addDecorator(new EventDecorator(Color.GREEN, calendarDays,RecordActivity.this));
        }
    }
}
