package com.example.everyrunrenew;

import static com.example.everyrunrenew.TransLocalPoint.TO_GRID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.everyrunrenew.Community.CommunityActivity;
import com.example.everyrunrenew.Record.RecordActivity;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.UserInfoData;
import com.example.everyrunrenew.Running.RunningRecordActivity;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.UserProfile.SettingActivity;
import com.example.everyrunrenew.UserProfile.UserProfileActivity;
import com.example.everyrunrenew.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityMainBinding binding;

    Context context;
    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private UserSharedPreference preferenceHelper;

    String user_email;

    TextView tx_nick;
    ImageView headerprofile;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;

    LocationManager locationManager;
    ArrayList<LatLng> locationArray = new ArrayList<>(); // 유저 위치 arraylist

    double longitude;
    double latitude;

    String bestProvider;
    String provider;
    Location location;

    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    String getTime;

    private TransLocalPoint transLocalPoint;
    TransLocalPoint.LatXLngY tmp;
    JSONObject json = null;

    List<Address> addressList = null;
    String address;
    int weather;
    String temperature;
    String option;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(binding.getRoot());
        context = getApplicationContext();
        preferenceHelper = new UserSharedPreference(this);
        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

//shared 삭제
        SharedPreferences pref = getSharedPreferences("RunningData", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        // 지도 객체 생성
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }

        //getMapAsync 호출해 비동기로 onMapReady 콜백 메서드 호출
        //onMapReady에서 NaverMap 객체를 받음.
        mapFragment.getMapAsync(this);

        //위치를 반환하는 구현체인 FusedLocationSource 생성
        mLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        GetUserLoction();

        // 기상청 격자 좌표 변화
        transLocalPoint = new TransLocalPoint();
        tmp = transLocalPoint.convertGRID_GPS(TO_GRID, latitude, longitude);
        Log.d(TAG, "onCreate: x= "+tmp.x +", y="+tmp.y);


        setUI();

        binding.btnStart.setOnClickListener(this);
        binding.iconWeather1.setOnClickListener(this);


    }

    // 유저 현재 위치 받아오는 메서드
    @SuppressLint("MissingPermission")
    private void GetUserLoction() {

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        // GPS 프로바이더 사용가능여부
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("Main", "isGPSEnabled="+ isGPSEnabled);
        Log.d("Main", "isNetworkEnabled="+ isNetworkEnabled);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.d(TAG, "onLocationChanged: latitude =" + latitude);
                Log.d(TAG, "onLocationChanged: longitude =" + longitude);

            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);

        provider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
        if(lastKnownLocation != null) {
            latitude = lastKnownLocation.getLatitude();
            longitude = lastKnownLocation.getLongitude();
            Log.d(TAG, "GetUserLoction: lastKnownLocation != null");
            Log.d(TAG, "GetUserLoction: latitude =" + latitude);
            Log.d(TAG, "GetUserLoction: longitude =" + longitude);
            Geocoder g = new Geocoder(MainActivity.this);

            // 위도 경도로 주소 변환하기
            try{
                addressList = g.getFromLocation(latitude, longitude, 10);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "GetUserLoction: 입출력 오류");
            }

            if(addressList != null){
                if(addressList.size() == 0){
                    Log.d(TAG, "GetUserLoction: 주소 찾기 오류");
                }else{
                    Log.d(TAG, "GetUserLoction: 찾은 주소 =" + addressList.get(0).toString());
                    Log.d(TAG, "GetUserLoction: 주소 =" + addressList.get(0).getAddressLine(0));
                    address = addressList.get(0).getAddressLine(0);
                }
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUI() {
        user_email = preferenceHelper.getEmail();

        Log.d(TAG, "setUI: email = " + user_email);

        // inflateheaderview 사용해서 코드상에서 네비게이션 뷰에 헤더 추가하기
        View headerview = binding.navigationView.inflateHeaderView(R.layout.header_layout);
        tx_nick = headerview.findViewById(R.id.tx_nick); // 헤더뷰에서 유저 닉네임 선언해주기
        headerprofile = headerview.findViewById(R.id.img_profile); // 헤더뷰에 프로필 이미지 선언해주기

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
                            Glide.with(MainActivity.this).load(url).into(headerprofile);
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
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
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
                        return true;

                    // 운동 기록 화면으로 이동
                    case R.id.nav_record:
                        item.setChecked(true);
                        binding.drawerLayout.closeDrawers();
                        startActivity(new Intent(MainActivity.this, RecordActivity.class));
                        return true;

                    // 커뮤니티로 이동
                    case R.id.nav_community:
                        item.setChecked(true);
                        binding.drawerLayout.closeDrawers();
                        startActivity(new Intent(MainActivity.this, CommunityActivity.class));
                        return true;

                    // 커뮤니티 화면으로 이동
//                    case R.id.nav_callenge:
//                        item.setChecked(true);
//                        binding.drawerLayout.closeDrawers();
//                        startActivity(new Intent(MainActivity.this, ChallengeActivity.class));
//                        return true;

                    // 설정 화면으로 이동
                    case R.id.nav_setting:
                        item.setChecked(true);
                        binding.drawerLayout.closeDrawers();
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        return true;


                }

                return false;
            }
        });

        // 날씨 세팅하는 메서드
        SetWeather();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SetWeather() {
        option = preferenceHelper.getWeather();
        // 날씨 api 결과 받아오기

        // 현재 시간, 날짜 가져오기
        long mNow = System.currentTimeMillis();
        Date mReDate= new Date(mNow);
        SimpleDateFormat mFormatYDM = new SimpleDateFormat("yyyyMMdd"); // 현재 날짜 데이터 폼 년도월일 ex. 20230205
        String formatYDM =mFormatYDM.format(mReDate);
        SimpleDateFormat mFormatTime = new SimpleDateFormat("HH00"); // 현재 시간대 ex. 1100 = 11시 대
        String formatTime = String.valueOf(Integer.parseInt(mFormatTime.format(mReDate))-100); // 현재 시간을 넣었을때 갱신된 정보가 안나오므로 1시간 이전 데이터를 조회

        //공공데이터 기상청 URL 설정.
        String service_key = "0oGVuytM0Ys5btcFxj32tPgfM81G%2BypV7oB5tXJp2OV7r3gwT2bnm55rNb90WgxeN909tr1FwO%2FFIrn5nggjYA%3D%3D";
        String num_of_rows = "10";
        String page_no = "1";
        String date_type = "JSON";
        String base_date = formatYDM; //조회할 날짜
        String base_time = "0600"; //조회할 시간
        Log.d(TAG, "SetWeather: tmp.x ="+tmp.x);
        Log.d(TAG, "SetWeather: tmp.y ="+tmp.y);
        String nx = String.format("%.0f", tmp.x);
        String ny = String.format("%.0f", tmp.y);


        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?" +
                "serviceKey="+service_key+
                "&numOfRows="+num_of_rows+
                "&pageNo="+page_no+
                "&dataType="+date_type+
                "&base_date="+base_date+
                "&base_time="+base_time+
                "&nx="+nx+
                "&ny="+ny;

        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();

        String dust_url = "https://api.odcloud.kr/api/RltmArpltnInforInqireSvrc/v1/getMsrstnAcctoRltmMesureDnsty?numOfRows=1&stationName=동작구&returnType=json&dataTerm=DAILY&ver=1.3&serviceKey=0oGVuytM0Ys5btcFxj32tPgfM81G%2BypV7oB5tXJp2OV7r3gwT2bnm55rNb90WgxeN909tr1FwO%2FFIrn5nggjYA%3D%3D";
        DustApi dustApi = new DustApi(dust_url, null);
        dustApi.execute();

    }

    public class DustApi extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public DustApi(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            Log.d(TAG, "onPostExecute: 미세먼지 결과 =" + s);

            try{
                json = new JSONObject(s);
                String dust_response = json.getString("response");
                Log.d(TAG, "onPostExecute: 미세먼지 response =" + dust_response);
                // response로 부터 body 찾기
                JSONObject jsonObject_1 = new JSONObject(dust_response);
                String body = jsonObject_1.getString("body");
                Log.d(TAG, "onPostExecute: body ="+body);
                // body로 부터 items 찾기
                JSONObject jsonObject_2 = new JSONObject(body);
                String items = jsonObject_2.getString("items");
                Log.d(TAG, "onPostExecute: items="+items);
                //items로 부터 itemlist 받기

                JSONArray jArrObject  = jsonObject_2.getJSONArray("items");

                // itemlist을 돌다가 category가 PTY인 곳에서 멈추기
                for(int i=0; i<jArrObject.length(); i++){
                    jsonObject_2 = jArrObject.getJSONObject(i);
                    // pm10Value = 미세먼지 농도
                    String pm10Value = jsonObject_2.getString("pm10Value");
                    // pm25Value = 초미세먼지 농도
                    String pm25Value = jsonObject_2.getString("pm25Value");
                    // 미세먼지 등급
                    String pm10Grade = jsonObject_2.getString("pm10Grade");
                    // 미세먼지 등급
                    String pm25Grade = jsonObject_2.getString("pm25Grade");

                    Log.d(TAG, "onPostExecute: pm10="+pm10Value);
                    Log.d(TAG, "onPostExecute: pm25="+pm25Value);
                    Log.d(TAG, "onPostExecute: pm10Grade="+pm10Grade);
                    Log.d(TAG, "onPostExecute: pm25Grade="+pm25Grade);

                    // 미세먼지, 초미세먼지 수치 세팅
                    binding.txDustvalue.setText(pm10Value+"㎍/㎥");
                    binding.txChodustvalue.setText(pm25Value+"㎍/㎥");

                    // 미세먼지 농도 측정
                    if(pm10Grade.equals("1")){
                        //좋음
                        binding.imgDust.setImageResource(R.drawable.smile);
                    }else if(pm10Grade.equals("2")){
                        //보통
                        binding.imgDust.setImageResource(R.drawable.normal_smile);
                    }else if(pm10Grade.equals("3")){
                        //나쁨
                        binding.imgDust.setImageResource(R.drawable.sad);
                    }else{
                        //매우 나쁨
                        binding.imgDust.setImageResource(R.drawable.angry);
                    }

                    // 초미세먼지 농도 측정
                    if(pm25Grade.equals("1")){
                        //좋음
                        binding.imgChodust.setImageResource(R.drawable.smile);
                    }else if(pm25Grade.equals("2")){
                        //보통
                        binding.imgChodust.setImageResource(R.drawable.normal_smile);
                    }else if(pm25Grade.equals("3")){
                        //나쁨
                        binding.imgChodust.setImageResource(R.drawable.sad);
                    }else{
                        //매우 나쁨
                        binding.imgChodust.setImageResource(R.drawable.angry);
                    }


                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            Log.d(TAG, "onPostExecute: 날씨 결과 =" + s);

            try{
                json = new JSONObject(s);
                String response = json.getString("response");
                Log.d(TAG, "onPostExecute: 변환된 데이터 값 = "+ response);
                // response로 부터 body 찾기
                JSONObject jsonObject_1 = new JSONObject(response);
                String body = jsonObject_1.getString("body");
                Log.d(TAG, "onPostExecute: body ="+body);
                // body로 부터 items 찾기
                JSONObject jsonObject_2 = new JSONObject(body);
                String items = jsonObject_2.getString("items");
                Log.d(TAG, "onPostExecute: items="+items);
                //items로 부터 itemlist 받기
                JSONObject jsonObject_3 = new JSONObject(items);
                JSONArray jsonArray = jsonObject_3.getJSONArray("item");

                // itemlist을 돌다가 category가 PTY인 곳에서 멈추기
                for(int i=0; i<jsonArray.length(); i++){
                    jsonObject_3 = jsonArray.getJSONObject(i);
                    // obsrValue = 강수량에 다른 날씨
                    String obsrValue = jsonObject_3.getString("obsrValue");
                    // 날씨 카테고리
                    String category = jsonObject_3.getString("category");

                    // 강수량에 따른 날씨 조건문
                    if(category.equals("PTY")){
                        Log.d(TAG, "onPostExecute: PTY 들어옴");
                        if(obsrValue.equals("0")){
                            weather = 0;
                            Log.d(TAG, "onPostExecute: 맑음");
                        }else if(obsrValue.equals("1")){
                            weather =1;
                            Log.d(TAG, "onPostExecute: 비");
                        }else if(obsrValue.equals("2")){
                            weather =2;
                            Log.d(TAG, "onPostExecute: 눈/비");
                        }else if(obsrValue.equals("3")){
                            weather =3;
                            Log.d(TAG, "onPostExecute: 눈");
                        }else if(obsrValue.equals("4")){
                            weather =4;
                            Log.d(TAG, "onPostExecute: 소나기");
                        }else{
                            weather =5;
                            Log.d(TAG, "onPostExecute: 그외");
                        }
                    }

                    if(category.equals("T1H")){
                        // 기온
                        Log.d(TAG, "onPostExecute: obserValue =" + obsrValue);
                        temperature = obsrValue;
                    }
                }

                if (option.equals("on")) {
                    // 날씨 옵션 on 이면
                    // 지도 위에 날씨 띄워줘야 한다.
                    Log.d(TAG, "SetWeather: on");
                    binding.linearWeather.setVisibility(View.VISIBLE);
                    Log.d(TAG, "SetWeather: weather = " + weather);
                    Log.d(TAG, "SetWeather: address=" + address);
                    Log.d(TAG, "SetWeather: temperature =" + temperature);
                } else {
                    // 날씨 옵션 off 이면
                    // 지도 위에 날씨 띄워주면 X
                    Log.d(TAG, "SetWeather: off");
                    binding.linearWeather.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "SetWeather: weather = " + weather);
                    Log.d(TAG, "SetWeather: address=" + address);
                    Log.d(TAG, "SetWeather: temperature =" + temperature);
                }

                // 날씨 UI 설정하기
                setWeatherUI(weather, address, temperature);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setWeatherUI(int weather, String address, String temperature) {
        Log.d(TAG, "setWeatherUI: 들어옴");
        binding.txTemperature.setText(temperature+"º"); // 온도 세팅해주기
        binding.txLocattion.setText(address); // 주소 세팅해주기

        // 날씨에 따른 아이콘 변경
        if(weather ==0 ){
            // 맑음
            binding.iconWeather1.setImageResource(R.drawable.sunny);
            binding.iconWeather2.setImageResource(R.drawable.sunny);
        }else if(weather ==1) {
            // 비
            binding.iconWeather1.setImageResource(R.drawable.rainy_day);
            binding.iconWeather2.setImageResource(R.drawable.rainy_day);

        }else if(weather ==2) {
            // 눈/ 비
            binding.iconWeather1.setImageResource(R.drawable.snow_rain);
            binding.iconWeather2.setImageResource(R.drawable.snow_rain);

        }else if(weather ==3) {
            // 눈
            binding.iconWeather1.setImageResource(R.drawable.snowing);
            binding.iconWeather2.setImageResource(R.drawable.snowing);

        }else if(weather ==4) {
            // 소나기
            binding.iconWeather1.setImageResource(R.drawable.raining);
            binding.iconWeather2.setImageResource(R.drawable.raining);

        }else if(weather ==5) {
            // 그 외
            binding.iconWeather1.setImageResource(R.drawable.clouds);
            binding.iconWeather2.setImageResource(R.drawable.clouds);
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d(TAG, "onMapReady: 들어옴");

        // 지도상에 마커 표시
//        Marker marker = new Marker();
//        marker.setPosition(new LatLng(latitude, longitude));
//        marker.setMap(naverMap);

        Log.d(TAG, "onMapReady: latitude = "+latitude);
        Log.d(TAG, "onMapReady: longitude = "+longitude);
        LatLng mlatlng = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition(mlatlng, 16);
        naverMap.setCameraPosition(cameraPosition);

        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);

        // 권한 확인. 결과는 onRequestPermissionResult 콜백 메서드 호출
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // request code와 권한획득 여부 확인
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

            }
        }
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
    public void onBackPressed() {
        // 기존 뒤로가기 버튼의 기능을 막기위해 주석처리 또는 삭제
        // super.onBackPressed();

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
            // 현재 표시된 Toast 취소
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                finish();
                toast.cancel();
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                // 러닝 시작 버튼
                // 러닝 기록 시작해주는 액티비티로 이동해준다.
                // 현재 날짜 세팅해주기
                // 현재 날짜 세팅해주기
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat simpleDate;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    simpleDate = new SimpleDateFormat("aa hh:mm", Locale.KOREA);
                    getTime = simpleDate.format(date);
                    Log.d(TAG, "setUI: 시작 시간 =" + getTime);
                }

                startActivity(new Intent(MainActivity.this, RunningRecordActivity.class).putExtra("StartTime", getTime));
                // record 액티비티 시작하고 immortal service 켜야함.


                break;

            case R.id.icon_weather1:
                // 선택하면 날씨창 켜지고 다시 선택하면 날씨창 닫히기
                if(binding.linearWeather.getVisibility() == View.VISIBLE){
                    // 날씨 창이 보이는 상태라면
                    Log.d(TAG, "onClick: 날씨창 열린 상태");
                    binding.linearWeather.setVisibility(View.INVISIBLE);
                }else{
                    // 날씨 창이 보이지 않는 상태라면
                    Log.d(TAG, "onClick: 날씨창 닫힌 상태");
                    binding.linearWeather.setVisibility(View.VISIBLE);
                }
                break;
        }
    }



}
