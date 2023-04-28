package com.example.everyrunrenew.Record;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.everyrunrenew.R;
import com.skydoves.progressview.ProgressView;

import java.util.ArrayList;
import java.util.Random;

public class ListviewAdapter extends BaseAdapter {

    private final String TAG = this.getClass().getSimpleName(); // log

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ListData> listData;
    double face;
    double maxface;
    double minface;
    String str_gap;

    public ListviewAdapter(Context context, ArrayList<ListData> data) {
        mContext = context;
        listData = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public ListData getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {

        Log.d(TAG, "getView: 들어옴 ");
        View view = mLayoutInflater.inflate(R.layout.record_listview, null);

        TextView tx_km = view.findViewById(R.id.tx_km);
        TextView tx_pace = view.findViewById(R.id.tx_pace);
        TextView tx_gap = view.findViewById(R.id.tx_gap);
        ProgressView progressView = view.findViewById(R.id.pv_1);

        tx_km.setText(listData.get(position).getKm());


        Log.d(TAG, "getView: d=" + String.valueOf(listData.get(position).getDoubleface()));
        // progressview 수치 세팅해줘야 함.
        // 제일 낮은 숫자가 max가 되어야 한다.
        // 제일 높은 숫자가 min이 되어야 한다.
        if (face == 0) {
            minface = listData.get(position).getDoubleface(); // face가 0이라면 처음 들어온 face가 최대, 최소 페이스가 된다.
            maxface = listData.get(position).getDoubleface();
        }
        face = listData.get(position).getDoubleface();


        if (minface > listData.get(position).getDoubleface()) { // 최소값보다 적은값이 들어오면 그 값이 최소값이 된다.
            minface = listData.get(position).getDoubleface();
        }

        if (maxface < listData.get(position).getDoubleface()) { // 최대값보다 큰 값이 들어오면 그 값이 최대값이 된다.
            maxface = listData.get(position).getDoubleface();
        }

        if (listData.get(position).getDoubleface() == maxface) {
            Log.d(TAG, "getView: 가장 높은 페이스 maxface=" + maxface);
            Log.d(TAG, "getView: 가장 높은 페이스 minface=" + minface);
            Log.d(TAG, "getView: 가장 높은 페이스 face=" + face);
            progressView.setProgress(100);
            // progressview에 페이스 라벨세팅해주기
            tx_pace.setText(listData.get(position).getFace());
        } else {
            Log.d(TAG, "getView: maxface = " + maxface);
            Log.d(TAG, "getView: minface =" + minface);
            Log.d(TAG, "getView: face=" + face);

            Double d = new Double(100 / (maxface / face));
            Log.d(TAG, "getView: 빼고 나머지=" + d);
            float f = d.floatValue();
            progressView.setProgress(f);
            tx_pace.setText(listData.get(position).getFace());
        }

        // 페이스 gap 세팅해주기
        if (position == 0) {
            Log.d(TAG, "getView: 첫번째 포지션 값=" + position);
        } else {
            Log.d(TAG, "getView: 포지셔값 =" + position);
            // 이전 포지션의 face값을 비교해야함
            double gap = listData.get(position).getDoubleface() - listData.get(position - 1).getDoubleface();
            Log.d(TAG, "getView: pace gap=" + gap);
            if (gap > 0) {
                // 페이스 차이가 양수이면 -> 페이스가 늘어났다는 의미
                Log.d(TAG, "getView: gap 양수");
                // 반올림하기
                str_gap = String.format("%.2f", gap);
                // 정수만 뽑기
                int minute = (int) gap;
                // 정수를 string으로 변환하기
                String str_min = String.valueOf(minute);
                // 소수 부분 분리하기
                String str_second = str_gap.substring(str_gap.lastIndexOf(".") + 1);
                str_gap = "+" + str_min + "'" + str_second + "''";
                tx_gap.setText(str_gap);
                tx_gap.setTextColor(Color.RED);
            } else if (gap == 0) {
                // 페이스 차이가 없을 때
                tx_gap.setText("0'00''");
                tx_gap.setTextColor(Color.BLACK);

            } else {
                // 페이스 차이가 음수이면 -> 페이스가 줄어들었다는 의미
                Log.d(TAG, "getView: gap 음수");
                // 반올림하기
                str_gap = String.format("%.2f", gap);
                // 정수만 뽑기
                int minute = (int) gap;
                // 정수를 string으로 변환하기
                String str_min = String.valueOf(minute);
                // 소수 부분 분리하기
                String str_second = str_gap.substring(str_gap.lastIndexOf(".") + 1);
                str_gap = "-" + str_min + "'" + str_second + "''";
                tx_gap.setText(str_gap);
                tx_gap.setTextColor(Color.GREEN);
            }
        }


        return view;
    }
}
