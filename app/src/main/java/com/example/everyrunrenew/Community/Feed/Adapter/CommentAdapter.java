package com.example.everyrunrenew.Community.Feed.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.RetrofitData.CommentData;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private final ArrayList<CommentData> commentDataArrayList;
    private Context context;
    int pos;

    public CommentAdapter(ArrayList<CommentData> commentDataArrayList, Context context) {
        this.commentDataArrayList = commentDataArrayList;
        this.context = context;
    }

    //-----------------------------------Custom Click Listener-----------------------
    public interface OnItemClickListener{
        void onLikeClick(View v, int pos);
    }
    // 커스텀 리스너 인터페이스 정의

    private CommentAdapter.OnItemClickListener cListener = null;
    // 전달된 객체를 저장할 변수 cListener 추가

    public void setOnItemClickListener(OnItemClickListener listener){
        this.cListener = listener;
    }
    // 커스텀 리스너 객체를 전달하는 메서드

    //-----------------------------------Custom Click Listener-----------------------

    public interface OnItemLongClickEventListener {
        void onLongClick(View v, int pos);
    }

    private CommentAdapter.OnItemLongClickEventListener longListener = null;
    // 전달된 객체를 저장할 변수 cListener 추가

    public void OnItemLongClickEventListener(OnItemLongClickEventListener listener){
        this.longListener = listener;
    }
    // 커스텀 리스너 객체를 전달하는 메서드

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context hcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) hcontext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate (R.layout.comment_item, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        CommentAdapter.ViewHolder hvh = new CommentAdapter.ViewHolder(view);
        // 뷰홀더 생성 , 리턴값이 뷰홀더이다.
        return hvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int pos) {

        CommentData commentData = commentDataArrayList.get(pos);
        String imageUrl = commentData.getProfile();
        String user_nick = commentData.getNick();
        String content = commentData.getContent();
        long time = commentData.getDate();
        int favorite = commentData.getFavorite();
        String message = commentData.getMessage();

        // 유저 프로필 세팅해주기
        if(imageUrl.equals("basic")){
            holder.user_img.setImageResource(R.drawable.user_img);
        }else{
            // 경로는 userimg로 설정해줘야 함.
            String url = "http://3.36.174.137/UserProfileImg/" + imageUrl;

            Glide.with(holder.itemView.getContext()).load(url).into(holder.user_img);
        }

        holder.tx_usernick.setText(user_nick);
        holder.tx_content.setText(content);
        holder.tx_time.setText(formatTimeString(time));
        holder.tx_favorite.setText(String.valueOf(favorite));

        // 좋아요 활성,비활성화
        if(message.equals("true")){
            holder.btn_like.setChecked(true);
        }else{
            holder.btn_like.setChecked(false);
        }

    }

    // 게시글 등록 시간 (long타입) 받아와서 변형해주는 함수.
    public static String formatTimeString(long regTime) {

        final int SEC = 60;
        final int MIN = 60;
        final int HOUR= 24;
        final int DAY= 30;
        final int MONTH= 12;

        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < SEC) {
            msg = "방금 전";
        } else if ((diffTime /= SEC) < MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= MIN) < HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= HOUR) < DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= DAY) < MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }

    @Override
    public int getItemCount() {
        return commentDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tx_usernick, tx_content, tx_time, tx_favorite;
        ImageView user_img;
        ToggleButton btn_like;

         public ViewHolder(@NonNull View itemView) {
            super(itemView);

             this.tx_usernick = itemView.findViewById(R.id.tx_Renick);
             this.tx_content = itemView.findViewById(R.id.tx_comment);
             this.tx_time = itemView.findViewById(R.id.tx_retime);
             this.tx_favorite = itemView.findViewById(R.id.tx_favorite);
             this.user_img = itemView.findViewById(R.id.commentimg);
             this.btn_like = itemView.findViewById(R.id.btn_like);


             itemView.setClickable(true);
             itemView.setOnLongClickListener(new View.OnLongClickListener() {
                 @Override
                 public boolean onLongClick(View view) {
                     pos = getAdapterPosition();

                     if(pos!= RecyclerView.NO_POSITION){

                         if(longListener != null){
                             longListener.onLongClick(view, pos);
                         }
                     }

                     return false;
                 }
             });

             // 댓글 좋아요 버튼을 클릭했을 때
             btn_like.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     int pos = getAdapterPosition();

                     if(pos != RecyclerView.NO_POSITION)
                     {
                         if(cListener != null)
                         {
                             cListener.onLikeClick(view, pos);
                         }
                     }
                 }
             });

         }
    }
}
