package com.example.everyrunrenew.Community.Feed.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.everyrunrenew.R;

import java.util.ArrayList;

public class SubItemAdapter  extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {
    private final String TAG = this.getClass().getSimpleName();

    private ArrayList<String> subItemList;
    private Context context;

    public SubItemAdapter(ArrayList<String> subItemList, Context context) {
        this.subItemList = subItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public SubItemAdapter.SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sub_item, parent, false);

        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemAdapter.SubItemViewHolder holder, int position) {
        String subItem = subItemList.get(position);
        Log.d(TAG, "onBindViewHolder: subitem ="+ subItem);
        String url = "http://3.36.174.137/FeedImg/" + subItem;
        Glide.with(holder.itemView.getContext()).load(url).into(holder.image);

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount:subItemList.size()="+subItemList.size());
        return subItemList.size();
    }

    public class SubItemViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public SubItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.rv_img);
        }
    }
}
