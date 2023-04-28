package com.example.everyrunrenew;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.everyrunrenew.UserInfo.LoginActivity;
import com.example.everyrunrenew.UserProfile.SettingActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

public class Stick_menu_Activity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName(); // log
    Button btn_logout;
    Button btn_map;
    int type;

    private Stick_shared preferenceHelper;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private InputStream inputStream;

    private Handler handler = new Handler();

    private static final UUID UUID_SERIAL_PORT_PROFILE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_menu);

        preferenceHelper = new Stick_shared(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice("블루투스 모듈 MAC 주소");

        btn_logout = findViewById(R.id.btn_logout);
        btn_map = findViewById(R.id.btn_map);
        type = preferenceHelper.getType();
        Log.d(TAG, "onCreate: type = "+ type);
        // 시각장애인만 현재 위치 보여줄까?
        if(type == 0) {
            // 시각장애인
        }else{
            // 보호자
            btn_map.setVisibility(View.INVISIBLE);
        }

        // 로그아웃 기능.
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dig = new AlertDialog.Builder(Stick_menu_Activity.this);
                dig.setMessage("로그아웃 하시겠습니까?");
                dig.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferenceHelper.clear(); // shared 정보 삭제
                        startActivity(new Intent(Stick_menu_Activity.this, Stick_main_Activity.class));
                        // shared 정보 삭제해야함.
                        finish();
                    }
                });
                dig.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dig.show();

            }
        });

        // 현재 위치 map 이동하기
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Stick_menu_Activity.this, Stick_map_Activity.class));

                finish();
            }
        });

        new Thread(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                try{
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID_SERIAL_PORT_PROFILE);
                    bluetoothSocket.connect();
                    inputStream = bluetoothSocket.getInputStream();

                    while (true) {
                        int bytesAvailable = inputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] buffer = new byte[bytesAvailable];
                            inputStream.read(buffer);
                            String message = new String(buffer, 0, bytesAvailable);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "run: 블루투스 값.");
                                }
                            });
                        }
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        }).start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            inputStream.close();
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}