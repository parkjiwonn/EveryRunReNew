package com.example.everyrunrenew.Running;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.everyrunrenew.Community.CommunityActivity;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Record.NaverLatLng;
import com.example.everyrunrenew.Record.RecordPreviewActivity;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.UserProfile.SettingActivity;
import com.example.everyrunrenew.databinding.ActivityMainBinding;
import com.example.everyrunrenew.databinding.ActivityRunningRecordBinding;
import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;

public class RunningRecordActivity extends AppCompatActivity implements View.OnClickListener {


    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityRunningRecordBinding binding;
    Context context;
    //액티비티에서 바인드할 서비스의 레퍼런스를 저장할 변수
    private RunningRecordService mService;
    //bound 상태를 저장할 변수
    private boolean mBound;
    Handler handler;
    public static Intent startServiceIntent = null;

    ArrayList<RunningData> runningDataArrayList;
    ArrayList<LatLng> FacePerKmArraylist;
    ArrayList<String> FaceArraylist;
    ArrayList<Double> FaceDoublelist;

    NaverLatLng naverLatLng;
    private UserSharedPreference preferenceHelper;

    String varCadence;
    int km;
    String StartTime;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRunningRecordBinding.inflate(getLayoutInflater());


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(binding.getRoot());
        context = getApplicationContext();

        StartTime = getIntent().getStringExtra("StartTime");
        Log.d(TAG, "onCreate: StartTime=" + StartTime);

        handler =new Handler();
        naverLatLng = new NaverLatLng();
        runningDataArrayList = new ArrayList<>();
        FacePerKmArraylist = new ArrayList<>();
        FaceArraylist = new ArrayList<>();
        FaceDoublelist = new ArrayList<>();

        preferenceHelper = new UserSharedPreference(this);

        // 활동 퍼미션 체크 - 센서
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        binding.btnPause.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        
        Log.d(TAG, "onStart: 들어옴");
        
        if(RunningRecordService.serviceIntent == null){
            Log.d(TAG, "onStart: serviceIntent == null");
            //mbound = false
            Log.d(TAG, "onStart: mbound =" + mBound);
            Intent intent = new Intent(this, RunningRecordService.class);
            Log.d(TAG, "onStart: bindService");
            startServiceIntent = intent;
            bindService(intent, mConnection, BIND_AUTO_CREATE);
            // bindService 다음으로 service의 onBind 실행

            intent.setAction("startForeground");
            //오레오 이상부터 동작하는 코드
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 서비스에서 getService이후 서비스 객체가 생성되고 setUI 실행됨.
                Log.d(TAG, "onStart: startforegroundservice");
                startForegroundService(intent);
            } else {
                Log.d(TAG, "onStart: startservice");
                startService(intent);
            }
        }else{
            Log.d(TAG, "onStart: serviceIntent != null");
            Log.d(TAG, "onStart: 서비스가 계속 돌고 있음.");
           // Log.d(TAG, "onStart: 시간 값 = " + mService.getVarTime());
            //-> null 값 나옴.
            Intent intent = new Intent(this, RunningRecordService.class);
            bindService(intent, mConnection, BIND_AUTO_CREATE);
            handler.postDelayed(RunningRecord, 0);
//            handler.postDelayed(RunningRecord, 100);
//            binding.txTime.setText(mService.getVarTime());
        }
       

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            Log.d(TAG, "onStop: mbound = " + mBound);
            // 앱 죽기 전에는 true였는데 stop 되고 false 가 된다.
            // 앱 죽였을 때 onStop이 된다.
            Log.d(TAG, "onStop: unbindService");
            unbindService(mConnection);
            mBound = false;
            Log.d(TAG, "onStop: mbound 2=" + mBound);
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스 onBind에서 IBinder 객체 생성 후 들어옴
            Log.d(TAG, "onServiceConnected: 들어옴");
            RunningRecordService.MyBinder binder = (RunningRecordService.MyBinder) service;
            Log.d(TAG, "onServiceConnected: binder.getService");
            mService = binder.getService();
            // 서비스의 getService 실행
            Log.d(TAG, "onServiceConnected: binder =" + binder);
            mBound = true;
            // mBound가 원래 false였는데 onBind가 되면서 true가 된다.
            handler.postDelayed(RunningRecord, 0);

            if (binder != null) {
                Log.d(TAG, "onServiceConnected: binder != nulll");

            } else {
                Log.d(TAG, "onServiceConnected: binder == null");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 예기치 않은 종료
            Log.d(TAG, "onServiceDisconnected: 들어옴");
        }
    };

    public Runnable RunningRecord = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: RunningRecord 들어옴");
            if(mBound){
                Log.d(TAG, "run: mBound is true");
                Log.d(TAG, "run: Service.getVarTime" + mService.getVarTime());
                binding.txTime.setText(mService.getVarTime());
                binding.txDistance.setText(mService.getVarDistance());
                int distance_km = (int)(mService.getDistance()/1000);
                // 여기서 이동거리의 정수부를 구하기.
                Log.d(TAG, "run: km ="+ km); // 초기값 0
                Log.d(TAG, "run: distance_km="+distance_km); // 이동거리의 정수부

                if(km < distance_km)
                {
                    Log.d(TAG, "run: 키로미터 증가함 ");
                    Log.d(TAG, "run: km ="+km);
                    Log.d(TAG, "run: distance_km="+distance_km);
                    Log.d(TAG, "run: 이때의 위도 경도 ="+ mService.getLatitude() +","+ mService.getLongitude());
                    // 이때의 위도 경도를 arraylist에 담아주기
                    FacePerKmArraylist.add(new LatLng(mService.getLatitude(), mService.getLongitude()));
                    Log.d(TAG, "run: FacePerKmArraylist =" + FacePerKmArraylist);
                    km = distance_km;
                    // 구간별 페이스 arraylist에 담아야함.
                    FaceArraylist.add(mService.varAvgFace);
                    Log.d(TAG, "run: FaceArraylist" + FaceArraylist);
                    FaceDoublelist.add(mService.avgFace);

                }else{
                    Log.d(TAG, "run: 키로미터 그대로 ");
                    Log.d(TAG, "run: km ="+km);
                    Log.d(TAG, "run: distance_km="+distance_km);
                }

                binding.txNowpace.setText(mService.getVarAvgFace());

                handler.postDelayed(this, 100);
            }

        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_pause:
                // 일시 정지 버튼
                // 선택했을 때 쓰레드가 멈춰야 하고 핸들러 실행이 취소되야 한다.
                Log.d(TAG, "onClick: 일시 정지 함.");
                mService.setRun(false);
                // run 변수 false 바꿔서 thread 멈춰주기
                mService.getTimeThread().interrupt();
                mService.getNotiThread().interrupt();
                // 시간, noti 쓰레드 멈추기
                handler.removeCallbacks(RunningRecord); // 핸들러 실행 취소하기.
                Log.d(TAG, "onClick: distance =" + mService.getVarDistance());
                Log.d(TAG, "onClick: getVarAvgFace =" + mService.getVarAvgFace());
                Log.d(TAG, "onClick: getVarTime =" + mService.getVarTime());
                Log.d(TAG, "onClick: getVarAltitude =" + mService.getVarAltitude());
                Log.d(TAG, "onClick: getTotalSecone =" + mService.getTotalSecond());

                // 케이던스 구해야함.
                // test
                Log.d(TAG, "setUI: step=" + mService.getStep());
                int step = mService.getStep();
                double minute = (double) mService.getTotalSecond()/60;
                double cadence = (double)step/minute;
                Log.d(TAG, "setUI: cadence = "+ cadence);

                if(cadence != 0.0){
                    Log.d(TAG, "onClick: 케이던스 측정됨");
                    varCadence = String.format("%.0f", cadence);
                }else{
                    Log.d(TAG, "onClick: 케이던스 측정 안됨.");
                    varCadence = "70";
                }

                

                // 변화한 값들이 잘 찍히는것 확인함.
                runningDataArrayList.add(new RunningData(mService.getVarDistance(), mService.getVarAvgFace(), mService.getVarTime(),
                        setKcal(mService.getTotalSecond(), mService.getAvgSpeed())  , mService.getVarAltitude(), NaverLatLng.locationarray , varCadence, FacePerKmArraylist
                        ,FaceArraylist, FaceDoublelist, mService.getPaceArray(), mService.getVarMaxHeight(), mService.getVarMinHeight(), mService.getVarMinFace()
                ) );
                // preview 액티비티로 이동하고 다시시작하면 다시 해당 액티비티로 돌아와야 하기 때문에
                // startActivityforresult 로 이동시키기
                Intent intent = new Intent(RunningRecordActivity.this, RecordPreviewActivity.class);
                // intent에 담아서 보내야 할 것
                // 이동거리(km), 평균페이스, 시간, 칼로리, 고도, 지도(이동경로)
                intent.putExtra("runningdata", runningDataArrayList);
                intent.putExtra("StartTime", StartTime);
                Log.d(TAG, "onClick: runningDataArraylist="+runningDataArrayList);
                startActivityForResult(intent, 2000);

                break;
        }
    }

    private String setKcal(int totalSecond, double avgSpeed) {
        String kcal;
        Log.d(TAG, "setKcal: totalSecond ="+totalSecond +"avgspeed =" + avgSpeed );

        double met;
        int weight = Integer.parseInt(preferenceHelper.getWEIGHT());

        if(avgSpeed <= 8.04){
            Log.d(TAG, "setKcal: 8.0METs");
            met = 8.0;

        }else if(8.04 < avgSpeed && avgSpeed <=8.36){
            Log.d(TAG, "setKcal: 9.0METs");
            met = 9.0;
        }else if(8.36 < avgSpeed && avgSpeed <= 9.65 ){
            Log.d(TAG, "setKcal: 10.0METs");
            met = 10.0;
        }else if(9.65 < avgSpeed && avgSpeed <= 10.77){
            Log.d(TAG, "setKcal: 11.0METs");
            met = 11.0;
        }else if(10.7< avgSpeed && avgSpeed <= 11.25){
            Log.d(TAG, "setKcal: 11.5METs");
            met = 11.5;
        }else if(11.25 < avgSpeed && avgSpeed <= 12.06){
            Log.d(TAG, "setKcal: 12.5METs");
            met = 12.5;
        }else if(12.06 < avgSpeed && avgSpeed <= 12.86){
            Log.d(TAG, "setKcal: 13.5METs");
            met = 13.5;
        }else if(12.86 < avgSpeed &&  avgSpeed<= 13.83){
            Log.d(TAG, "setKcal: 14.0METs");
            met = 14.0;
        }else if(13.83 < avgSpeed &&  avgSpeed<= 14.47){
            Log.d(TAG, "setKcal: 15.0METs");
            met = 15.0;
        }else if(14.47 < avgSpeed && avgSpeed<=16.08){
            Log.d(TAG, "setKcal: 16.0METs");
            met = 16.0;
        }else {
            Log.d(TAG, "setKcal: 18.0METs");
            met = 18.0;
        }

        double calorie = met*(3.5*weight*totalSecond*5)/60/1000;

        Log.d(TAG, "setKcal: kal = "+calorie);

        kcal = String.format("%.0f", calorie);

        return kcal;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // MainActivity 에서 요청할 때 보낸 요청 코드 (3000)
                case 2000:
                    // preview 액티비티에서 다시 돌아옴 -> 재시작 했음.
                    Log.d(TAG, "onActivityResult: preview 액티비티에서 다시 돌아옴 -> 재시작 햇음.");
                    if(mService!=null)
                    {
                        Log.d(TAG, "onActivityResult: mService != null");
                        // service가 null이 아니라면 run 변수 true로 변경해주기
                        mService.setRun(true);
                        // binding 다시 해주기
                        Intent intent = new Intent(this, RunningRecordService.class);
                        bindService(intent, mConnection, BIND_AUTO_CREATE);
                        handler.postDelayed(RunningRecord, 0);
                    }
                    break;
            }
        }
    }
}