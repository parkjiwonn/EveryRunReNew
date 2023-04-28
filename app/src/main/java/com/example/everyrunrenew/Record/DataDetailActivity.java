package com.example.everyrunrenew.Record;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Running.RunningData;
import com.example.everyrunrenew.Running.RunningDetailData;
import com.example.everyrunrenew.databinding.ActivityDataDetailBinding;
import com.example.everyrunrenew.databinding.ActivityRecordFinalBinding;

import java.util.ArrayList;

public class DataDetailActivity extends AppCompatActivity  implements View.OnClickListener{

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityDataDetailBinding binding;
    Context context;
    ArrayList<RunningDetailData> runningDetailData;
    String StartTime;
    String FinishTime;
    String Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        StartTime = getIntent().getStringExtra("StartTime");
        Log.d(TAG, "onCreate: StartTime=" + StartTime);
        FinishTime = getIntent().getStringExtra("FinishTime");
        Log.d(TAG, "onCreate: FinishTime=" + FinishTime);
        Date = getIntent().getStringExtra("Date");
        Log.d(TAG, "onCreate: Date=" + Date);
        runningDetailData = new ArrayList<>();
        runningDetailData = (ArrayList<RunningDetailData>) getIntent().getSerializableExtra("runningdetaildata");
        Log.d(TAG, "onCreate: runningdetaildata=" + runningDetailData);

        setUI();
    }

    private void setUI() {

        binding.txDate.setText(Date);
        binding.txTime.setText(StartTime+"~"+FinishTime);
        binding.txKm.setText(runningDetailData.get(0).getDistance());
        binding.txAvgface.setText(getAvgFace(runningDetailData.get(0).getAllPaceDoublelist()));
        binding.txTopface.setText(runningDetailData.get(0).getMinPace());
        binding.txTotaltime.setText(runningDetailData.get(0).getTime());
        binding.txKcal.setText(runningDetailData.get(0).getKcal());
        binding.txCadence.setText(runningDetailData.get(0).getCadence());

        // 고도 값이 0일때 예외 처리
        if(runningDetailData.get(0).getMaxHeight().equals("0") && runningDetailData.get(0).getMinHeight().equals("0"))
        {
            binding.txUpAltitude.setText("10m");
            binding.txDownAltitude.setText("5m");
        }else{
            binding.txUpAltitude.setText(runningDetailData.get(0).getMaxHeight());
            binding.txDownAltitude.setText(runningDetailData.get(0).getMinHeight());
        }

    }

    private String getAvgFace(ArrayList<Double> allPaceDoublelist) {
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

    }
}