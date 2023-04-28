package com.example.everyrunrenew.Community.AboutChatGallery;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.everyrunrenew.R;

public class LookUpViewHolder extends RecyclerView.ViewHolder{

    public static final String TAG = LookUpViewHolder.class.getSimpleName();
    // PhotoAdapter photoAdapter = new PhotoAdapter(itemView.getContext());
    ImageView image;
    TextView checkBox;
    Boolean clicked;

    public LookUpViewHolder(@NonNull View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.image);
        checkBox = itemView.findViewById(R.id.checkbox);

    }

    // getItemDetails()메소드 통해 itemDetails 를 리턴하고 있다.
    // 추상 클래스인 itemDetails에는 getPosition과 getSelectionKey라는 메소드를 구현하게끔 되어 있다.
    // 위와 같이 adpater의 position과 item id를 리턴시켜준다.
    public ItemDetailsLookup.ItemDetails<Long> getItemDetails(){
        Log.e(TAG, "getItemDetails: 들어옴" );
        return new ItemDetailsLookup.ItemDetails<Long>() {
            @Override
            public int getPosition() {
                return getAdapterPosition();
            } // 어댑터 position 리턴

            @Nullable
            @Override
            public Long getSelectionKey() {
                return getItemId();
            } // 어댑터 item id 리턴

            @Override
            public boolean inSelectionHotspot(@NonNull MotionEvent e) {
                return true;
            }
        };
    }

    public void setPhoto(Photo photo) {
        Log.e(TAG, "setPhoto: 들어옴 8" );
        //String path = "/storage/emulated/0/DCIM/Camera/IMG_20220730_081724.jpg"
        Glide.with(image).load(photo.getPath()).transition(DrawableTransitionOptions.withCrossFade()).into(image);
    }



    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        //getFileNameListner.change("test" + );
        Log.e(TAG, "setSelectionTracker: 들어옴 9");

        if (selectionTracker != null && selectionTracker.isSelected((long) getAdapterPosition())){
            clicked = true;
            Log.e(TAG, "setSelectionTracker: clicked = " + clicked );
            checkBox.setBackground(ContextCompat.getDrawable(checkBox.getContext(), R.drawable.textview_circle));
            image.setBackground(ContextCompat.getDrawable(image.getContext(), R.drawable.imageview_selected));
            Log.e(TAG, "setSelectionTracker: " + getItemId() );
        }
        else{
            clicked = false;
            Log.e(TAG, "setSelectionTracker: clicked = " + clicked );
            checkBox.setBackground(ContextCompat.getDrawable(checkBox.getContext(), R.drawable.textview_circle_unselected));
            image.setBackground(ContextCompat.getDrawable(image.getContext(), R.drawable.imageview_unselected));
            Log.e(TAG, "setSelectionTracker: " + getItemId() );
        }



    }
}
