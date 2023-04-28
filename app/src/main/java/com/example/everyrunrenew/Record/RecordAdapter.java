package com.example.everyrunrenew.Record;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everyrunrenew.R;
import com.example.everyrunrenew.RetrofitData.FinalRunningData;
import com.example.everyrunrenew.Running.HardCord;
import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;

public class RecordAdapter  extends RecyclerView.Adapter<RecordAdapter.ViewHolder>{

    private final ArrayList<HardCord> runningDataArrayList;
    private Context context;
    int pos;

    private final String TAG = this.getClass().getSimpleName();

    public RecordAdapter(ArrayList<HardCord> runningDataArrayList, Context context) {
        this.runningDataArrayList = runningDataArrayList;
        this.context = context;
    }



    @NonNull
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context hcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) hcontext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate (R.layout.item_runningdata, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        RecordAdapter.ViewHolder hvh = new RecordAdapter.ViewHolder(view);
        // 뷰홀더 생성 , 리턴값이 뷰홀더이다.
        return hvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, int position) {

        HardCord finalRunningData = runningDataArrayList.get(position);

        String title = finalRunningData.getTitle();
        String date = finalRunningData.getDate();
        String distance = finalRunningData.getDistance();
        String time = finalRunningData.getTime();
        String pace= finalRunningData.getPace();

        holder.tx_date.setText(date);
        holder.tx_title.setText(title);
        holder.tx_distance.setText(distance);
        holder.tx_time.setText(time);
        holder.tx_pace.setText(pace);

    }

    @Override
    public int getItemCount() {
        return runningDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tx_date, tx_title, tx_distance, tx_time, tx_pace;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tx_date = itemView.findViewById(R.id.tx_date);
            this.tx_title = itemView.findViewById(R.id.tx_title);
            this.tx_distance = itemView.findViewById(R.id.tx_distance);
            this.tx_time = itemView.findViewById(R.id.tx_time);
            this.tx_pace = itemView.findViewById(R.id.tx_pace);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){

                        //context.startActivity(new Intent(context,));

                    }
                }
            });
        }
    }
}
