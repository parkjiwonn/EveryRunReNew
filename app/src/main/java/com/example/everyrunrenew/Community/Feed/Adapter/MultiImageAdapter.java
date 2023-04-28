package com.example.everyrunrenew.Community.Feed.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.everyrunrenew.R;

import java.util.ArrayList;

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder> {

    final String TAG = getClass().getSimpleName();

    private ArrayList<String> mData = null ;
    private Context mContext = null ;
    int pos;

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public MultiImageAdapter(ArrayList<String> list, Context context) {
        mData = list ;
        mContext = context;
    }

    // OnClickListener Custom --------------------------
    public interface OnItemClickListener{
        void onItemClick(int pos);
    }

    public OnItemClickListener onItemClickListener = null;

    public void setOnItemClicklistener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }
    //---------------------------------------------------

    @NonNull
    @Override
    public MultiImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        // context에서 LayoutInflater 객체를 얻는다.

        View view = inflater.inflate(R.layout.item_multiimage, parent, false) ;
        // 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.

        MultiImageAdapter.ViewHolder vh = new MultiImageAdapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull MultiImageAdapter.ViewHolder holder, int position) {
        String image = mData.get(position);
        Log.d(TAG, "onBindViewHolder: image : "+ image );
        Log.d(TAG, "onItemClick: uriList : " +mData );

        if(!image.contains("/"))
        {
            Log.d(TAG, "onBindViewHolder: photo"  );
            // 서버
            String url = "http://3.36.174.137/FeedImg/" + image;
            Glide.with(mContext).load(url).into(holder.image);
        }
        else{
            Log.d(TAG, "onBindViewHolder: 절대경로" );

            Glide.with(mContext)
                    .load(image)
                    .into(holder.image);

        }
    }

    @Override
    public int getItemCount() {
        return  mData.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageButton btn_cancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조.
            image = itemView.findViewById(R.id.image);
            btn_cancel = itemView.findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        Log.e(TAG, "어댑터 onClick : pos :  " + pos );
                        mData.remove(pos);
                        notifyItemRemoved(pos);
                        notifyDataSetChanged();

                    }

                }
            });
        }
    }
}
