
package com.example.everyrunrenew.Record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.FinalRunningData;
import com.example.everyrunrenew.RetrofitData.UserInfoData;
import com.example.everyrunrenew.Running.RunningData;
import com.example.everyrunrenew.Running.RunningDetailData;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.databinding.ActivityRecordFinalBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordFinalActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityRecordFinalBinding binding;
    Context context;
    ArrayList<ListData> listDataArrayList;
    ListviewAdapter listviewAdapter;

    ArrayList<RunningData> runningData ; // 러닝 데이터
    List<LatLng> locationArray; //지도에 polyline 그려줄 위경도 객체
    ArrayList<LatLng> FacePerKmArraylist;
    ArrayList<String> FaceArraylist;
    ArrayList<Double> FaceDoublelist;
    ArrayList<Double> AllPaceDoublelist;// 모든 페이스 받아옴.
    ArrayList<RunningDetailData> runningDetailData; // 러닝 상세 정보 담는 list
    ArrayList<FinalRunningData> FinalRunningData;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;

    int i;

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private UserSharedPreference preferenceHelper;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;

    double startlat;
    double startlng;
    double finishlat;
    double finishlng;

    String title; // 러닝 기록 제목
    String getTime; // 러닝 기록한 날짜

    String StartTime;
    String FinishTime;

    int finalkm;

    String user_email; // 현재 user_email


    String month;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordFinalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        preferenceHelper = new UserSharedPreference(this);
        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        preferences = getSharedPreferences("RunningData", MODE_PRIVATE);
        // "spotinfo"라는 shared preference 생성하기
        editor = preferences.edit();
        gson = new GsonBuilder().create();

        // user email 세팅해주기
        user_email = preferenceHelper.getEmail();
        Log.d(TAG, "setUI: user_email = " + user_email);



        // 러닝 데이터 객체 생성해주기
        runningData = new ArrayList<>();
        locationArray = new ArrayList<>();
        FacePerKmArraylist = new ArrayList<>();
        FaceArraylist = new ArrayList<>();
        FaceDoublelist = new ArrayList<>();
        runningDetailData = new ArrayList<>();
        FinalRunningData = new ArrayList<>();
        // runningrecord 액티비티에서 intent로 넘어온 데이터 객체
        runningData = (ArrayList<RunningData>) getIntent().getSerializableExtra("runningdata");
        // 가장 최근에 들어온 데이터를 세팅해줘야 한다.
        i = runningData.size() -1;
        locationArray = runningData.get(i).getLocationarray();
        FacePerKmArraylist = runningData.get(i).getFacePerKmArraylist(); // km 당 위경도
        FaceArraylist = runningData.get(i).getFaceArraylist(); // km 당 페이스
        FaceDoublelist = runningData.get(i).getFaceDoublelist(); // km 당 페이스 double 형
        AllPaceDoublelist = runningData.get(i).getAllPaceDoublelist(); // 모든 페이스 double 형


        StartTime = getIntent().getStringExtra("StartTime");
        Log.d(TAG, "onCreate: StartTime=" + StartTime);
        FinishTime = getIntent().getStringExtra("FinishTime");
        Log.d(TAG, "onCreate: StartTime=" + FinishTime);

        Log.d(TAG, "onCreate: runningdata =" + runningData);
        Log.d(TAG, "onCreate: FacePerKmArraylist" + FacePerKmArraylist);
        Log.d(TAG, "onCreate: FaceArraylist=" + FaceArraylist);
        Log.d(TAG, "onCreate: facedoublelist =" + FaceDoublelist);
        Log.d(TAG, "onCreate: AllPaceDoublelist =" + AllPaceDoublelist);

        // 가장 최근에 들어온 거리 array에서 시작 위치와 마지막 위치 위도 경로 받아오기
        startlat = locationArray.get(0).latitude;
        startlng = locationArray.get(0).longitude;
        finishlat = locationArray.get(locationArray.size()-1).latitude;
        finishlng = locationArray.get(locationArray.size()-1).longitude;

        Log.d(TAG, "onCreate: 시작 위경도:"+startlat + "," + startlng);
        Log.d(TAG, "onCreate: 끝 위경도:"+finishlat + "," + finishlng);

        setUI();

        binding.btnDataDetail.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
    }

    private void setUI() {



        // 현재 날짜 세팅해주기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            simpleDate = new SimpleDateFormat("yyyy. MM. dd");
            getTime = simpleDate.format(date);
            Log.d(TAG, "setUI: 현재 날짜 =" + getTime);
            binding.txDate.setText(getTime);
        }

        title = binding.etTitle.getText().toString(); // 러닝 기록 제목 string 변수에 담아주기

        // 러닝 기록 세팅해주기
        binding.txDistance.setText(runningData.get(i).getDistance());
        binding.txPace.setText(runningData.get(i).getFace());
        binding.txTime.setText(runningData.get(i).getTime());
        binding.txKcal.setText(runningData.get(i).getKcal());
        binding.txAltitude.setText(runningData.get(i).getAltitude());
        binding.txCadence.setText(runningData.get(i).getCadence());


        // 지도에 polyline 그려주기
        // 지도 객체 생성
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_view);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.map_view, mapFragment).commit();
        }

        //getMapAsync 호출해 비동기로 onMapReady 콜백 메서드 호출
        //onMapReady에서 NaverMap 객체를 받음.
        mapFragment.getMapAsync(this);

        //위치를 반환하는 구현체인 FusedLocationSource 생성
        mLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        listDataArrayList = new ArrayList<ListData>();


        // for 문 돌면서 listview 채워야한다.
        // 구간 별 페이스 값 넣어주기
        int km=0 ;
        for(int i=0; i<FaceArraylist.size(); i++){
            km++;
            finalkm = km;
            String str_km = String.valueOf(finalkm);
            listDataArrayList.add(new ListData(str_km, FaceArraylist.get(i) , FaceDoublelist.get(i)));

        }

        //===============================총 이동거리 km로 나누고 나머지 이동거리 구하기==============================
        // 최종 거리에서 finalkm 를 뺴주고 맨 마지막의 페이스를 넣어준다.
        double double_km = finalkm;
        double double_distance = Double.parseDouble(runningData.get(i).getDistance());

        // double 형 뺄쌤 안전하게 하기 위함.
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(double_distance));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(double_km));
        BigDecimal left_km = bigDecimal.subtract(bigDecimal2);
        Log.d(TAG, "setUI: 나머지 경로 =" + left_km);
        String left = String.valueOf(left_km);
        int j = AllPaceDoublelist.size()-1;
        double last_pace = AllPaceDoublelist.get(j);
        String last_double_pace = String.valueOf(last_pace);
        int minute = (int)last_pace;
        Log.d(TAG, "onLocationChanged: minute ="+ minute);
        String StrMinute = String.valueOf(minute);
        String second = last_double_pace.substring(last_double_pace.lastIndexOf(".") + 1);
        Log.d(TAG, "onLocationChanged: second = "+second);

        last_double_pace = StrMinute+"'" + second + "''";

        listDataArrayList.add(new ListData(left, last_double_pace ,AllPaceDoublelist.get(j)));
        //===============================총 이동거리 km로 나누고 나머지 이동거리 구하기==============================

        //===============================list view 세팅해주시기==================================================
        listviewAdapter = new ListviewAdapter(this, listDataArrayList);
        binding.recordListview.setAdapter(listviewAdapter);
        setListViewHeightBasedOnChildren(binding.recordListview);
        //===============================list view 세팅해주시기==================================================

        // 물음표 - 페이스 설명
        binding.questionPace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                // 물음표 선택했을 때
                if (isChecked) {
                    binding.txtBanner.setVisibility(View.VISIBLE);
                    binding.txtBannerTail.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onCheckedChanged: 물음표 클릭 보이기");
                }
                // 물음표 먈풍선 닫을 때
                else {
                    binding.txtBanner.setVisibility(View.GONE);
                    binding.txtBannerTail.setVisibility(View.GONE);
                    Log.d(TAG, "onCheckedChanged: 물음표 안보이기");
                }
            }
        });

        // 물음표 - 케이던스 설명
        binding.questionCadence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                // 물음표 선택했을 때
                if (isChecked) {
                    binding.txtBanner2.setVisibility(View.VISIBLE);
                    binding.txtBannerTail2.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onCheckedChanged: 물음표 클릭 보이기");
                }
                // 물음표 먈풍선 닫을 때
                else {
                    binding.txtBanner2.setVisibility(View.GONE);
                    binding.txtBannerTail2.setVisibility(View.GONE);
                    Log.d(TAG, "onCheckedChanged: 물음표 안보이기");
                }
            }
        });

    }

    // listview와 연결되어 있는 adapter의 item의 개수를 통해 listview의 height를 설정해주는 메서드
    private void setListViewHeightBasedOnChildren(ListView recordListview) {
        ListAdapter listAdapter = recordListview.getAdapter();
        if(listAdapter == null){
            return;
        }

        int totalHeight = 0;
        int desireWidth = View.MeasureSpec.makeMeasureSpec(recordListview.getWidth(), View.MeasureSpec.AT_MOST);

        for(int i =0; i< listAdapter.getCount(); i++){
            View listItem = listAdapter.getView(i, null, recordListview);
            listItem.measure(desireWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = recordListview.getLayoutParams();
        params.height = totalHeight + (recordListview.getDividerHeight() * (listAdapter.getCount() -1));
        recordListview.setLayoutParams(params);
        recordListview.requestLayout();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_DataDetail:
                // 러닝 기록 상세 정보
                runningDetailData.add(new RunningDetailData(runningData.get(i).getDistance(), runningData.get(i).getAllPaceDoublelist(), runningData.get(i).getMinPace(), runningData.get(i).getTime()
                        , runningData.get(i).getKcal(), runningData.get(i).getCadence(), runningData.get(i).getMaxHeight(), runningData.get(i).getMinHeight()));
                Intent intent = new Intent(RecordFinalActivity.this, DataDetailActivity.class);
                // intent에 담아서 보내야 할 것
                // 이동거리(km), 평균페이스, 시간, 칼로리, 고도, 지도(이동경로)
                intent.putExtra("runningdetaildata", runningDetailData);
                intent.putExtra("Date", getTime);
                intent.putExtra("StartTime", StartTime);
                intent.putExtra("FinishTime", FinishTime);
                startActivity(intent);

                break;

            case R.id.btn_save:

                // 서버 통신 후 db에 데이터 저장
                SaveRunningDate(runningData, user_email, getTime, StartTime,FinishTime);

                break;
        }
    }

    // 러닝데이터 저장하는 메서드
    private void SaveRunningDate(ArrayList<RunningData> runningData, String user_email, String getTime, String startTime, String finishTime) {
        Log.d(TAG, "SaveRunningDate: runningdata="+runningData+"user_email ="+user_email+"getTime =" + getTime+"starttime="+startTime+"finishtime="+finishTime);
        // 모두 잘 받아와지는 거 확인함.

        String distance = runningData.get(i).getDistance();
        String pace = runningData.get(i).getFace();
        String time = runningData.get(i).getTime();
        String kcal = runningData.get(i).getKcal();
        String altitude = runningData.get(i).getAltitude();
        String cadence = runningData.get(i).getCadence();
        ArrayList<LatLng> locationarray = runningData.get(i).getLocationarray();
        ArrayList<LatLng> FacePerKmArraylist = runningData.get(i).getFacePerKmArraylist();
        ArrayList<String> FaceArraylist = runningData.get(i).getFaceArraylist();
        ArrayList<Double> FaceDoublelist = runningData.get(i).getFaceDoublelist();
        ArrayList<Double> AllFaceDoublelist = runningData.get(i).getAllPaceDoublelist();
        String MaxHeight = runningData.get(i).getMaxHeight();
        String MinHeight = runningData.get(i).getMinHeight();
        String MinPace = runningData.get(i).getMinPace();
        String title = binding.etTitle.getText().toString();

        FinalRunningData finalRunningData = new FinalRunningData(distance, pace,time
        ,kcal, altitude, cadence,locationarray,FacePerKmArraylist, FaceArraylist, FaceDoublelist, AllFaceDoublelist, MaxHeight,
                MinHeight, MinPace, getTime, startTime,finishTime,user_email, title);

        long pre_now = System.currentTimeMillis();
        Date pre_date = new Date(pre_now);
        SimpleDateFormat pre_simpleDate;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            pre_simpleDate = new SimpleDateFormat("M", Locale.KOREA);
            month = pre_simpleDate.format(pre_date);
            Log.d(TAG, "오늘 월 =" + month);
        }

        Log.d(TAG, "SaveRunningDate: month =" + month);
        String shared_data = preferences.getString(month, "");
        // local에 저장하자
        if(shared_data.equals("")){
            String data = gson.toJson(finalRunningData);
            Log.d(TAG, "SaveRunningDate: 아무것도 없음.");

            try {
                JSONObject obj = new JSONObject(data);
                Log.e("obj", obj.toString());
                JSONArray array = new JSONArray();
                array.put(obj);
                Log.e("array", array.toString());
                //json 객체에 java 객체를 문자열로 만든 인자를 넣고 그 json 객체를 json array에 넣는다.

                String result = array.toString();

                //key 값이 유저 아이디가 아니라 달 이어야 함.
                // 현재 날짜 세팅해주기
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat simpleDate;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    simpleDate = new SimpleDateFormat("M", Locale.KOREA);
                    month = simpleDate.format(date);
                    Log.d(TAG, "러닝한 달 =" + month);
                }


                editor.putString(month, result);
                Log.e("result", result);
                editor.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            FinalRunningData = gson.fromJson(shared_data, new TypeToken<ArrayList<FinalRunningData>>(){}.getType());
            Log.d("이전 꺼 FinalRunningData", FinalRunningData.toString());
            FinalRunningData.add(finalRunningData);
            Log.d(TAG, "SaveRunningDate: 이미 데이터 있음 month="+month);
            editor.putString(month, gson.toJson(FinalRunningData));
            editor.commit();
        }


    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // 지도상에 마커 표시
        // 시작 마커
        Marker startMarker = new Marker();
        startMarker.setPosition(new LatLng(startlat, startlng));
        startMarker.setMap(naverMap);
        startMarker.setWidth(60);
        startMarker.setHeight(70);
        startMarker.setIcon(MarkerIcons.BLACK);
        startMarker.setIconTintColor(Color.RED);

        // 끝 마커
        Marker finishMarker = new Marker();
        finishMarker.setPosition(new LatLng(finishlat, finishlng));
        finishMarker.setMap(naverMap);
        finishMarker.setWidth(60);
        finishMarker.setHeight(70);
        startMarker.setIcon(MarkerIcons.BLACK);
        finishMarker.setIconTintColor(Color.GREEN);

        // 정보 창 띄워주기 - km 당
        Log.d(TAG, "onMapReady: FacePerKmArraylist = "+ FacePerKmArraylist);
        int km=0;
        for(int i=0; i<FacePerKmArraylist.size(); i++){
            InfoWindow infoWindow = new InfoWindow(); // 정보창 띄워주기

            // km 증가시키기
            km++;
            int finalKm = km;
            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(context) {
                @NonNull
                @Override
                public CharSequence getText(@NonNull InfoWindow infoWindow) {

                    String str_km = String.valueOf(finalKm);

                    return str_km+"km";
                }
            });

            //km 당 위경도를 세팅해준다.
            infoWindow.setPosition(new LatLng(FacePerKmArraylist.get(i).latitude, FacePerKmArraylist.get(i).longitude));
            //정보창은 항상 열려있다.
            infoWindow.open(naverMap);
        }


        double zoom = 16;
        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);

        PathOverlay path = new PathOverlay();
        // Arrays.asList : Arrays의 private 정적 클래스인 arraylist를 리턴한다.
        // Arrays.asList-> 배열을 리스트로 변환

        path.setCoords(locationArray);
        path.setMap(naverMap);
        path.setOutlineWidth(5);
        path.setColor(Color.parseColor("#6495ED"));

        Log.d(TAG, "onMapReady: locationArray.size =" + locationArray.size());
        Log.d(TAG, "onMapReady: bounds = "+naverMap.getContentBounds());


        double minLat = NaverLatLng.locationarray.get(0).latitude;
        double minLng = NaverLatLng.locationarray.get(0).longitude;
        double maxLat = NaverLatLng.locationarray.get(locationArray.size() -1).latitude;
        double maxLng = NaverLatLng.locationarray.get(locationArray.size() -1).longitude;

        LatLng mlatlng = new LatLng((minLat + maxLat)/2, (minLng +maxLng)/2);
        CameraPosition cameraPosition = new CameraPosition(mlatlng, zoom);

        for(; zoom>= 1.0; zoom -= 0.5)
        {
            cameraPosition = new CameraPosition(mlatlng, zoom);
            naverMap.setCameraPosition(cameraPosition);

            double minLat2 = naverMap.getContentBounds().getSouthWest().latitude;
            double minLng2 = naverMap.getContentBounds().getSouthWest().longitude;
            double maxLat2 = naverMap.getContentBounds().getNorthEast().latitude;
            double maxLng2 = naverMap.getContentBounds().getNorthEast().longitude;

            if(minLat > minLat2 && minLat < maxLat2 && minLng > minLng2 && minLng <maxLng2
                    && maxLat>minLat2 && maxLat < maxLat2 && maxLng > minLng2 && maxLng < maxLng2){
                naverMap.setCameraPosition(cameraPosition);
                return;
            }

            zoom -= 1.0;
        }



    }
}