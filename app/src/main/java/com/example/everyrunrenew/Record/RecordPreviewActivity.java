package com.example.everyrunrenew.Record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.example.everyrunrenew.MainActivity;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Running.RunningData;
import com.example.everyrunrenew.Running.RunningRecordActivity;
import com.example.everyrunrenew.Running.RunningRecordService;
import com.example.everyrunrenew.databinding.ActivityRecordPreviewBinding;
import com.example.everyrunrenew.databinding.ActivityRunningRecordBinding;


import com.naver.maps.geometry.LatLng;

import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordPreviewActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityRecordPreviewBinding binding;
    Context context;

    ArrayList<RunningData> runningData ;
    List<LatLng> locationArray;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;

    int i;

    double startlat;
    double startlng;
    double finishlat;
    double finishlng;

    //액티비티에서 바인드할 서비스의 레퍼런스를 저장할 변수
    private RunningRecordService mService;
    //bound 상태를 저장할 변수
    private boolean mBound;
    Handler handler;

    String StartTime;
    String FinishTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        // 러닝 데이터 객체 생성해주기
        runningData = new ArrayList<>();
        locationArray = new ArrayList<>();
        // runningrecord 액티비티에서 intent로 넘어온 데이터 객체
        runningData = (ArrayList<RunningData>) getIntent().getSerializableExtra("runningdata");
        // 가장 최근에 들어온 데이터를 세팅해줘야 한다.
        i = runningData.size() -1;
        locationArray = runningData.get(i).getLocationarray();
        Log.d(TAG, "onCreate: runningdata =" + runningData);

        StartTime = getIntent().getStringExtra("StartTime");
        Log.d(TAG, "onCreate: StartTime=" + StartTime);

        // 가장 최근에 들어온 거리 array에서 시작 위치와 마지막 위치 위도 경로 받아오기
        startlat = locationArray.get(0).latitude;
        startlng = locationArray.get(0).longitude;
        finishlat = locationArray.get(locationArray.size()-1).latitude;
        finishlng = locationArray.get(locationArray.size()-1).longitude;

        Log.d(TAG, "onCreate: 시작 위경도:"+startlat + "," + startlng);
        Log.d(TAG, "onCreate: 끝 위경도:"+finishlat + "," + finishlng);

        setUI();
        binding.btnRestart.setOnClickListener(this); // 재시작
        binding.btnStop.setOnClickListener(this); // 종료 버튼
    }

    private void setUI() {
        // intent로 넘어온 데이터 객체 세팅해주기
        binding.txDistance.setText(runningData.get(i).getDistance());
        binding.txTime.setText(runningData.get(i).getTime());
        //binding.txAltitude.setText(runningData.get(i).getAltitude());
        binding.txPace.setText(SetAvgPace(runningData.get(i).getAllPaceDoublelist()));
        binding.txKcal.setText(runningData.get(i).getKcal());

        // 지도 세팅해줘야함.

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

        // 칼로리 계산해줘야 함.
        //setKCAL();

        // 물음표 (닉네임 설정방법 설명 말풍선)
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


    }

    private String SetAvgPace(ArrayList<Double> allPaceDoublelist) {
        String AvgFace;
        double d;
        double sum = 0.0; // 합계

        for(double num : allPaceDoublelist){
            sum += num;
        }

        d = sum / allPaceDoublelist.toArray().length;
        AvgFace = String.format("%.2f", d);
        Log.d(TAG, "getAvgFace: AvgFace = "+ AvgFace);
        int minute = (int)d;
        String StrMinute = String.valueOf(minute);
        String StrSecond = AvgFace.substring(AvgFace.lastIndexOf(".") +1);
        AvgFace = StrMinute+"'"+StrSecond+"''";

        return AvgFace;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_restart:
                setResult(RESULT_OK, new Intent(RecordPreviewActivity.this, RunningRecordActivity.class));
                finish();
                break;

            case R.id.btn_stop:
                // 러닝 종료 버튼 클릭
                // 다이얼로그 보이기
                AlertDialog.Builder dig_finish = new AlertDialog.Builder(RecordPreviewActivity.this);
                dig_finish.setMessage("러닝을 종료하시겠습니까?");
                dig_finish.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dig_finish.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        stopService(RunningRecordActivity.startServiceIntent);

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat simpleDate;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            simpleDate = new SimpleDateFormat("hh:mm");
                            FinishTime = simpleDate.format(date);
                            Log.d(TAG, "setUI: 종료 시간 =" + FinishTime);
                        }

                        // 인텐트로 받은 러닝 데이터를 final 액티비티로 전달시키기
                        Intent intent = new Intent(RecordPreviewActivity.this, RecordFinalActivity.class);
                        // intent에 담아서 보내야 할 것
                        // 이동거리(km), 평균페이스, 시간, 칼로리, 고도, 지도(이동경로)
                        intent.putExtra("runningdata", runningData);
                        // 시작 시간과 종료 시간을 같이 보내야 한다.
                        intent.putExtra("StartTime", StartTime);
                        intent.putExtra("FinishTime", FinishTime);

                        Log.d(TAG, "onClick: runningdata="+ runningData);
                        startActivity(intent);
                    }
                });
                dig_finish.show();
                break;

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
        startMarker.setIconTintColor(Color.GREEN);



        // 끝 마커
        Marker finishMarker = new Marker();
        finishMarker.setPosition(new LatLng(finishlat, finishlng));
        finishMarker.setMap(naverMap);
        finishMarker.setWidth(60);
        finishMarker.setHeight(70);
        finishMarker.setIcon(OverlayImage.fromResource(R.drawable.finish));


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