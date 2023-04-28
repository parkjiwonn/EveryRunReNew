package com.example.everyrunrenew.Record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.everyrunrenew.R;
import com.example.everyrunrenew.databinding.ActivityCourseDetailBinding;
import com.example.everyrunrenew.databinding.ActivityRecordFinalBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class CourseDetailActivity extends AppCompatActivity  implements View.OnClickListener, OnMapReadyCallback {

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityCourseDetailBinding binding;
    Context context;

    //선 그래프
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        setUI();

        binding.txPace.setOnClickListener(this);
        binding.txAltitude.setOnClickListener(this);
        binding.txCadence.setOnClickListener(this);
    }

    private void setUI() {
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

        // chart 생성하기
        ArrayList<Entry> entry_chart1 = new ArrayList<>(); // 데이터를 담을 Arraylist

        lineChart = binding.chart;

        LineData chartData = new LineData(); // 차트에 담길 데이터

        entry_chart1.add(new Entry(1, 1)); //entry_chart1에 좌표 데이터를 담는다.
        entry_chart1.add(new Entry(2, 2));
        entry_chart1.add(new Entry(3, 3));
        entry_chart1.add(new Entry(4, 4));
        entry_chart1.add(new Entry(5, 2));
        entry_chart1.add(new Entry(6, 8));


        LineDataSet lineDataSet1 = new LineDataSet(entry_chart1, "LineGraph1"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.

        lineDataSet1.setColor(Color.RED); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.

        chartData.addDataSet(lineDataSet1); // 해당 LineDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.

        lineChart.setData(chartData); // 차트에 위의 DataSet을 넣는다.

        lineChart.invalidate(); // 차트 업데이트
        lineChart.setTouchEnabled(true); // 차트 터치 disable

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tx_pace:
                break;
            case R.id.tx_altitude:
                break;
            case R.id.tx_cadence:
                break;

        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

    }
}