package com.example.everyrunrenew.Running;

import
android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Record.NaverLatLng;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
public class RunningRecordService extends Service  implements SensorEventListener, LocationListener{

    private static final String TAG = "RunningRecordService";
    private Thread timeThread;
    private Thread notiThread;
    private int mCount = 0;
    private IBinder iBinder = new RunningRecordService.MyBinder();
    public static Intent serviceIntent = null;

    boolean ServiceRunning = true;

    // sensor 사용시 필요한 객체들
    private SensorManager sensorManager;
    private Sensor stepCountSensor;

    RemoteViews remoteViews; // notification에 시간과 거리,페이스 표시해주는 notification.
    NotificationManager manager;

    NaverLatLng naverLatLng;

    int second = 0;
    int minute, hour, totalSecond; // 초, 분, 시간, 총 시간
    int step; // 현재 걸음수
    double latitude; //위도
    double longitude; //경도
    double distance; //이동 거리
    int height, minHeight, maxHeight; // 현재 고도, 최저 고도, 최고 고도
    double avgSpeed; // 평균 속도
    double avgFace; // 평균 페이스

    double pace ,minPace;


    // Record 액티비티에 세팅해줄 변수들
    String varTime;
    String varStep;
    String varDistance = "0.00";
    String varAltitude; // 현재 고도
    String varHeight; // 고도차
    String varMinHeight;
    String varMaxHeight;
    String varAvgSpeed; // 평균 속도
    String varCurrentSpeed; // 현재 속도
    // 러닝 페이스 : 달린 거리를 얼마 만에 주파했는지, 달린 거리로 다시 시간을 나눠 페이스를 따진다.
    String varCurrentFace; // 현재 페이스(평균페이스)
    String varMinFace; // 최저 페이스
    String varAvgFace ="0'00''"; // 평균 페이스

    //유저 위치 요청하는 것.
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private LocationListener locationListener;

    ArrayList<LatLng> locationArray = new ArrayList<>(); // 유저 위치 arraylist
    ArrayList<Double> PaceArray = new ArrayList<>(); // 페이스 담을 arraylist

    boolean run = true; //

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // 걸음 센서 이벤트 발생시
        Log.d(TAG, "onSensorChanged: 들어옴");

            // 걸음 수 측정 시작
            Log.d(TAG, "onSensorChanged: isServiceLive = true");
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (sensorEvent.values[0] == 1.0f) {
                    // 센서 이벤트가 발생할때 마다 걸음수 증가
                    step++;

                    varStep= String.valueOf(step);
                    Log.d(TAG, "onSensorChanged: sendstep =" +varStep);
                }
            }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLocationChanged(@NonNull Location location) {
        /**-> 위치 정보를 가져올수 있는 메서드이다. 위치 이동이나 시간 경과등으로 인해 호출된다.
         * 최신 위치는 location 파라미터가 가지고 있다.
         **/

        Log.d(TAG, "onLocationChanged: 들어옴.");
        locationListener = this.locationListener; // location 리스너

        if(isServiceRunning()){
            if(run){
                if(location == null) {
                    Log.d(TAG, "onLocationChanged: location == null");
                }else{
                    Log.d(TAG, "onLocationChanged: location != null");
                    latitude = location.getLatitude(); // 위도 구하기
                    longitude = location.getLongitude();  // 경도 구하기
                    Log.d(TAG, "onLocationChanged: latitude = "+latitude);
                    Log.d(TAG, "onLocationChanged: longitude = "+longitude);

                    double currentSpeed = Math.ceil(location.getSpeed()*100*3.6)/100; // 현재 속도
                    Log.d(TAG, "onLocationChanged: currentSpeed ="+currentSpeed);
                    // 위치 array에 현재 위도 경도 추가하기
                    locationArray.add(new LatLng(location.getLatitude(), location.getLongitude()));

                    naverLatLng.setLocationarray(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onLocationChanged: getlocationarray =" + naverLatLng.getLocationarray());

                    Log.d(TAG, "onLocationChanged: locationArray = "+locationArray);
                    // 이동거리 구하기
                    distance = (int) SphericalUtil.computeLength(locationArray);
                    Log.d(TAG, "onLocationChanged: distance = "+distance);
                    //======================고도 관련=====================================
                    // 고도구하기
                    double altitude = location.getAltitude();
                    Log.d(TAG, "onLocationChanged: altitude = "+altitude);

                    if(height == 0){
                        // 현재 고도가 0이라면 현재 고도값이 최저, 최고가 된다.
                        minHeight = (int)Math.floor(altitude);
                        maxHeight = (int)Math.floor(altitude);
                    }
                    height = (int)Math.floor(altitude);
                    // 현재 고도값 세팅해주기

                    // 저장되어 있던 최저 고도보다 낮은 값이 들어오면
                    if(minHeight > altitude){
                        minHeight = (int)Math.floor(altitude);
                    }

                    // 저장되어 있던 최고 고도보다 높은 값이 들어오면
                    if(maxHeight < altitude){
                        maxHeight = (int)Math.floor(altitude);
                    }
                    //======================고도 관련=====================================



                    varDistance = String.format("%.2f", distance/1000); // 킬로미터 변환
                    varAltitude = String.valueOf((int)location.getAltitude());
                    varMinHeight = String.valueOf(minHeight);
                    varMaxHeight = String.valueOf(maxHeight);
                    //고도차
                    varHeight = String.valueOf(maxHeight - minHeight);
                    // 평균속도 -> kph : km per hour
                    avgSpeed  = (Math.ceil((distance/totalSecond)*100))/100*3.6;
                    varAvgSpeed = String.valueOf(avgSpeed);
                    varCurrentSpeed = String.valueOf(currentSpeed);
                    double kilometer = distance/1000; // 거리 km로 변환
                    // 현재 페이스 = (초 ->분으로 변환) / 거리
                    avgFace = (Math.ceil(totalSecond/kilometer))/60;
                    // 케이던스 : 분당 밟는 스텝의 수

                    //====================페이스 관련======================================
                    if(pace == 0){
                        minPace = avgFace;
                    } // pace 가 0이라면 현재 평균페이스가 최저 페이스가 된다.

                    pace = avgFace;

                    if(minPace > avgFace){
                        // 최소 페이스보다 적은 값이 들어오면 해당 값이 최저 페이스가 된다.
                        minPace = avgFace;
                    }
                    //=======================최저 페이스 형식 맞춰주기===============================================
                    varMinFace = String.format("%.2f", minPace);
                    int Min_minute = (int)minPace;
                    String str_Minminute = String.valueOf(Min_minute);
                    String str_Minsecond = varMinFace.substring(varMinFace.lastIndexOf(".") + 1);
                    varMinFace = str_Minminute+"'"+str_Minsecond+"''";
                    Log.d(TAG, "onLocationChanged: varMinFace = "+ varMinFace);



                    Log.d(TAG, "onLocationChanged: varDistatnce = "+varDistance);
                    Log.d(TAG, "onLocationChanged: varAltitude = "+varAltitude);
                    Log.d(TAG, "onLocationChanged: varMinHeight = "+varMinHeight);
                    Log.d(TAG, "onLocationChanged: varMaxHeight = "+varMaxHeight);
                    Log.d(TAG, "onLocationChanged: varHeight = "+varHeight);
                    Log.d(TAG, "onLocationChanged: varAvgSpeed = "+varAvgSpeed);
                    Log.d(TAG, "onLocationChanged: varCurrentSpeed = "+varCurrentSpeed);
                    Log.d(TAG, "onLocationChanged: varCurrentFace =" + avgFace);
                    //=================일반 페이스 형식 맞춰주기=================================================
                    if(Double.isInfinite(avgFace)){
                        Log.d(TAG, "onLocationChanged: avgFace is infinity");
                        varAvgFace = "0'00''";
                    }else{
                        Log.d(TAG, "onLocationChanged: avgFace isn't infinity");
                        varAvgFace = String.format("%.2f", avgFace);
                        Log.d(TAG, "onLocationChanged: varAvgFace = "+ varAvgFace);
                        // 페이스 배열안에 담기
                        double d = Double.parseDouble(varAvgFace);
                        PaceArray.add(d);
                        naverLatLng.setPaceLocationlist(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, "onLocationChanged: 페이스 당 위치="+ naverLatLng.getPaceLocationlist());
                        Log.d(TAG, "onLocationChanged: PaceArray ="+PaceArray);
                        // 페이스 형식을 맞춰줘야 함.
                        int minute = (int)avgFace;
                        Log.d(TAG, "onLocationChanged: minute ="+ minute);
                        String StrMinute = String.valueOf(minute);
                        String second = varAvgFace.substring(varAvgFace.lastIndexOf(".") + 1);
                        Log.d(TAG, "onLocationChanged: second = "+second);

                        varAvgFace = StrMinute+"'" + second + "''";
                        Log.d(TAG, "onLocationChanged: varAvgFace set =" + varAvgFace);

                    }
                    // avgFace = infinity 나오는 것은 나눗셈의 오류. 0으로 나눴을때 생김.
                    // distance가 0.0km가 아닐때 avgFace 계산됨.




                }
            }

        }else{
            Log.d(TAG, "onLocationChanged: isServiceRunning() == false");
        }

    }

    public class MyBinder extends Binder {
        public RunningRecordService getService(){
            Log.d(TAG, "getService: 들어옴");
            // 서비스 객체 생성
            return RunningRecordService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceIntent = intent;

        if(serviceIntent != null){
            if ("startForeground".equals(intent.getAction())){
                // 액티비티에서 SetUI되고 onStartCommand 됨
                // 바인드 서비스에서는 바인드 되고 서비스가 실행됨.
                Log.d(TAG, "onStartCommand: startforeground 일치");
                startForegroundService();
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 켜지지 않은 상태에서 앱 죽였을 때
        Log.e(TAG,"onDestroy!");
        if (timeThread != null){
            Log.d(TAG, "onDestroy: mThread != null");
            timeThread.interrupt();
            timeThread = null;
            second = 0;
        }else{
            Log.d(TAG, "onDestroy: mThread == null");
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // 액티비티 onstart 되고서 시작됨.
        Log.d(TAG, "onBind: 들어옴");
       naverLatLng = new NaverLatLng(); // 네이버 위도경도 담는 객체

        // 소요시간 쓰레드 돌리기

            Log.d(TAG, "onStartCommand: mThread == null");
            timeThread = new Thread("My Thread"){
                @Override
                public void run() {


                    while(isServiceRunning()){
                        // 서비스 돌고있는지 확인
                        while(run){
                        // 일시정지했는지 확인
                            try {
                                totalSecond++;

                                second = totalSecond; // 모든 시간 (초)

                                minute = second / 60; // 분
                                hour = minute /60; // 시간
                                second = second % 60; // 초

                                varTime=String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":"
                                        + String.format("%02d", second);
                                Log.d(TAG, "run: totalSecond =" + totalSecond);
                                Log.d(TAG, "run: varTime= "+varTime);


                                Log.d(TAG, "run: varDistance= "+varDistance);
                                Log.d(TAG, "run: varAvgFace =" + varAvgFace);
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }


                    }

                }
            };
            timeThread.start();


        // 걸음수 측정하기 - 케이던스 측정하기 위함.
        // 스텝 센서 - 걸음수 측정
        // 걸음 센서 연결
        // * 옵션
        // - TYPE_STEP_DETECTOR:  리턴 값이 무조건 1, 앱이 종료되면 다시 0부터 시작
        // - TYPE_STEP_COUNTER : 앱 종료와 관계없이 계속 기존의 값을 가지고 있다가 1씩 증가한 값을 리턴
        //
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepCountSensor == null) {
            Log.d(TAG, "onStartCommand: no step sensor");
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
        }
        if (stepCountSensor != null) {
            // 센서 속도 설정
            // * 옵션
            // - SENSOR_DELAY_NORMAL: 20,000 초 딜레이
            // - SENSOR_DELAY_UI: 6,000 초 딜레이
            // - SENSOR_DELAY_GAME: 20,000 초 딜레이
            // - SENSOR_DELAY_FASTEST: 딜레이 없음
            Log.d(TAG, "onStartCommand: ready step sensor");
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
            Toast.makeText(this, "Ready Step Sensor", Toast.LENGTH_SHORT).show();
        }

        // Location
        locationRequest = com.google.android.gms.location.LocationRequest.create();
        // locationRequest

        // PRIORITY_HIGH_ACCURACY -> 가장 정확한 위치를 요청한다. 이 설정을 사용하면 위치 서비스가 GPS를 사용하여 위치를 확인할 가능성이 높다.
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        //-> 해당 메서드는 요청의 우선순위를 설정해 google play 서비스 위치 서비스에 사용할 위치 소스에 관한 강력한 힌트를 제공한다.
        //기존
        locationRequest.setInterval(5000); // -> 업데이트 간격 : 앱에서 선호하는 위치 업데이트 수신 간격을 밀리초 단위로 설정한다.
        // 위치 업데이트는 배터리 사용량을 최적화 하기 위해 설정된 간격보다 다소 빠르거나 느릴수 있고 아예 업데이트가 없을수도 있다.
        locationRequest.setFastestInterval(2000); // -> 가장 빠른 업데이트 간격 : 앱이 위치 업데이트를 처리할 수 있는 가장 빠른 간격을 밀리초 단위로 설정한다.

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 위치관리자를 구하는 것. 액티비티의 상위 클래스인 Context 클래스의 getSystemService 메소드를 호출하면 되는 것이다.

        // gps 값 받아오는, 현재 위치 받아올수 있도록 권한 설정 되어있는지 학인하는 것.
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

        }
        Toast.makeText(this,"Waiting for GPS connection!", Toast.LENGTH_SHORT).show();
        /*-------------------------------------------------------------------------------------------------------------*/



        return iBinder;

    }



    @SuppressLint("RemoteViewLayout")
    private void startForegroundService(){
        Log.d(TAG, "startForegroundService: 들어옴");

        remoteViews = new RemoteViews(getPackageName(), R.layout.running_notification); // remoteview 객체 생성

        // notification 생성하기
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");//오레오 부터 channelId가 반드시 필요하다.
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setCustomBigContentView(remoteViews);

        Intent notificationIntent = new Intent(this, RunningRecordActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE );
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상부터 이 코드가 동작한다.
            Log.d(TAG, "startForegroundService: 오레오 이상");
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        Log.d(TAG, "startForegroundService: startForeground 실행");

        notiThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(isServiceRunning()){
                    // 서비스가 돌고 있는지 확인
                    while(run){
                        // 일시정지했는지 아닌지 확인
                        Log.d(TAG, "run: notiThread 시작");
                        Log.d(TAG, "run: vartime");
                        // 세팅해야하는 변수
                        // varTime, varDistance, varFace
                        remoteViews.setTextViewText(R.id.tx_time, varTime); // 시간 세팅
                        remoteViews.setTextViewText(R.id.tx_distance, varDistance); // 이동 거리 세팅
                        remoteViews.setTextViewText(R.id.tx_face, varAvgFace);
                        Log.d(TAG, "run: varDistance");
                        manager.notify(1, builder.build());
                        try{
                            Thread.sleep(1000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }

                }



            }
        });
        notiThread.start();



        startForeground(1, builder.build());//id를 0으로 하면안된다.
    }

    public ArrayList<LatLng> getLocationArray() {
        return locationArray;
    }

    public void setLocationArray(ArrayList<LatLng> locationArray) {
        this.locationArray = locationArray;
    }

    public ArrayList<Double> getPaceArray() {
        return PaceArray;
    }

    public void setPaceArray(ArrayList<Double> paceArray) {
        PaceArray = paceArray;
    }

    public String getVarMinFace() {
        return varMinFace;
    }

    public void setVarMinFace(String varMinFace) {
        this.varMinFace = varMinFace;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public String getVarStep() {
        return varStep;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public void setVarStep(String varStep) {
        this.varStep = varStep;
    }

    public String getVarDistance() {
        return varDistance;
    }

    public void setVarDistance(String varDistance) {
        this.varDistance = varDistance;
    }

    public String getVarAltitude() {
        return varAltitude;
    }

    public void setVarAltitude(String varAltitude) {
        this.varAltitude = varAltitude;
    }

    public String getVarHeight() {
        return varHeight;
    }

    public void setVarHeight(String varHeight) {
        this.varHeight = varHeight;
    }

    public String getVarMinHeight() {
        return varMinHeight;
    }

    public void setVarMinHeight(String varMinHeight) {
        this.varMinHeight = varMinHeight;
    }

    public String getVarMaxHeight() {
        return varMaxHeight;
    }

    public void setVarMaxHeight(String varMaxHeight) {
        this.varMaxHeight = varMaxHeight;
    }

    public String getVarAvgSpeed() {
        return varAvgSpeed;
    }

    public void setVarAvgSpeed(String varAvgSpeed) {
        this.varAvgSpeed = varAvgSpeed;
    }

    public String getVarCurrentSpeed() {
        return varCurrentSpeed;
    }

    public void setVarCurrentSpeed(String varCurrentSpeed) {
        this.varCurrentSpeed = varCurrentSpeed;
    }

    public String getVarCurrentFace() {
        return varCurrentFace;
    }

    public void setVarCurrentFace(String varCurrentFace) {
        this.varCurrentFace = varCurrentFace;
    }

    public String getVarAvgFace() {
        return varAvgFace;
    }

    public void setVarAvgFace(String varAvgFace) {
        this.varAvgFace = varAvgFace;
    }

    public Thread getNotiThread() {
        return notiThread;
    }

    public void setNotiThread(Thread notiThread) {
        this.notiThread = notiThread;
    }

    public Thread getTimeThread() {
        return timeThread;
    }

    public void setTimeThread(Thread timeThread) {
        this.timeThread = timeThread;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getTotalSecond() {
        return totalSecond;
    }

    public void setTotalSecond(int totalSecond) {
        this.totalSecond = totalSecond;
    }

    public String getVarTime() {
        return varTime;
    }

    public void setVarTime(String varTime) {
        this.varTime = varTime;
    }

    public boolean isServiceRunning(){
        return  ServiceRunning;
    }

    public void setServiceRunning(boolean serviceRunning){
        this.ServiceRunning = serviceRunning;
        if(isServiceRunning()) {
        }else{
            timeThread.interrupt();
            Log.d(TAG, "setServiceRunning: TimeThread.getState = "+timeThread.getState());
        }
    }
}