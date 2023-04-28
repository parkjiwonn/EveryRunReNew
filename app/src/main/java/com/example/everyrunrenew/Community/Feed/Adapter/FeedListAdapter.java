package com.example.everyrunrenew.Community.Feed.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.example.everyrunrenew.Community.Feed.ReadFeedActivity;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.RetrofitData.FeedData;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.ViewHolder>{

    private final String TAG = this.getClass().getSimpleName();
    // 데이터 리스트 객체 생성
    private final ArrayList<FeedData> feedDataArrayList;
    private Context context;
    int getDiaryNum;
    int pos;
    SubItemAdapter subItemAdapter;
    String image;

    public FeedListAdapter(ArrayList<FeedData> feedDataArrayList, Context context) {
        this.feedDataArrayList = feedDataArrayList;
        this.context = context;
    }

    //--------------------Custom Click Listener-----------------------
    public interface OnItemClickListener{
        void onLikeClick(View v, int pos);
        void onCommentClick(View v, int pos);

    }
    // 커스텀 리스너 인터페이스 정의

    private FeedListAdapter.OnItemClickListener dListener = null;
    // 전달된 객체를 저장할 변수 dListener 추가

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.dListener = listener;
    }
    // 커스텀 리스너 객체를 전달하는 메서드

    //--------------------Custom Click Listener-----------------------


    @NonNull
    @Override
    public FeedListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context dcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) dcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate(R.layout.item_feed, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        FeedListAdapter.ViewHolder dvh = new FeedListAdapter.ViewHolder(view);
        // 뷰홀더 생성되는 부분

        return dvh;
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
    public void onBindViewHolder(@NonNull FeedListAdapter.ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: 들어옴" );

        FeedData feedData = feedDataArrayList.get(position);

        holder.tx_nick.setText(feedData.getUser_nick());
        holder.tx_date.setText(formatTimeString(feedData.getDate()));
        holder.tx_content.setText(feedData.getContent());


        holder.tx_favorite.setText(String.valueOf(feedData.getFavorite()));
        holder.tx_comment.setText(String.valueOf(feedData.getComment()));

        // 좋아요 활성/비활성화 옵션
        if(feedData.getMessage().equals("true")){
            holder.btn_favorite.setChecked(true);
        }else{
            holder.btn_favorite.setChecked(false);
        }

        String imageUrl = feedData.getUser_profile(); // 유저 프로필 이미지 url
        if(imageUrl.equals("basic"))
        {
            holder.img_profile.setImageResource(R.drawable.user_img);

        }
        else
        {
            // 경로는 userimg로 설정해줘야 함.
            String url = "http://3.36.174.137/UserProfileImg/" + imageUrl;

            Glide.with(holder.itemView.getContext()).load(url).into(holder.img_profile);
        }


        if(feedData.getPhotolist().size() != 0){
            // 사진이 하나라도 있다면
            // 자식 레이아웃 매니저 설정 || 가로로 생성
            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.rv_photo.getContext(), LinearLayoutManager.HORIZONTAL, false);
            layoutManager.setInitialPrefetchItemCount(feedData.getPhotolist().size());

            // todo
            // 리사이클러뷰 안 아이템들의 크기를 가변적으로 바꿀지 아니면 일정한 크기를 사용할지를 지정한다.
            holder.rv_photo.setHasFixedSize(true);
            //snaphelper에서 발생하는 onflinglistener already set 오류 방지용 코드
            holder.rv_photo.setOnFlingListener(null);
            // 리사이클러뷰를 좌우로 스크롤할 때 item들이 아무데서나 멈추는게 아니라 자석이 끌어 당기는것 처럼 position 1번 자리의 좌표에서 멈추는 것을 도와준다.
            SnapHelper snapHelper = new PagerSnapHelper();
            // viewpager 처럼 작용 || snaphelper를 리사이클러뷰 에 실행한다는 뜻
            snapHelper.attachToRecyclerView(holder.rv_photo);
            // paint 적용
            holder.rv_photo.addItemDecoration(new CirclePagerIndicatorDecoration());
            // 하위 어답터 설정
            Log.d(TAG, "onBindViewHolder: feedData.getPhotolist() ="+ feedData.getPhotolist());

            subItemAdapter = new SubItemAdapter(feedData.getPhotolist(), context);
            // 어댑터랑 리사이클러뷰 배치
            holder.rv_photo.setLayoutManager(layoutManager);
            // 어댑터 생팅
            holder.rv_photo.setAdapter(subItemAdapter);
        }else{
            // 사진이 한장도 없다면
            Log.d(TAG, "onBindViewHolder: 사진이 한장도 없음");
            holder.rv_photo.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return feedDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tx_nick, tx_date, tx_content, tx_favorite, tx_comment;
        ImageView img_profile;
        ImageButton btn_comment;
        ToggleButton btn_favorite;
        RecyclerView rv_photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tx_nick = itemView.findViewById(R.id.tx_usernick);
            this.tx_date = itemView.findViewById(R.id.tx_date);
            this.tx_content = itemView.findViewById(R.id.tx_content);
            this.tx_favorite = itemView.findViewById(R.id.tx_favorite);
            this.tx_comment = itemView.findViewById(R.id.tx_comment);
            this.img_profile = itemView.findViewById(R.id.img_user);
            this.btn_comment = itemView.findViewById(R.id.btn_comment);
            this.btn_favorite = itemView.findViewById(R.id.btn_like);
            this.rv_photo = itemView.findViewById(R.id.rv_photo);




            // 아이템 클릭 상태 true
            itemView.setClickable(true);
            // 아이템 뷰 클릭리스너
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();
                    // 리사이클러뷰 아이템 클릭시 다이어리 상세보기 페이지로 넘어가야함.

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        FeedData item = feedDataArrayList.get(pos);
                        String content = item.getContent();
                        String user_nick = item.getUser_nick();
                        String user_profile = item.getUser_profile();
                        int favorite = item.getFavorite();
                        int comment = item.getComment();
                        ArrayList<String> photo_list = item.getPhotolist();
                        String user_email = item.getUser_email();
                        int feed_id = item.getFeed_id();
                        long date = item.getDate();
                        int crew_id = item.getCrew_id();
                        String message = item.getMessage();

                        Intent intent = new Intent(context, ReadFeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("content", content);
                        intent.putExtra("user_nick", user_nick);
                        intent.putExtra("user_profile", user_profile);
                        intent.putExtra("favorite", favorite);
                        intent.putExtra("comment", comment);
                        intent.putExtra("photo_list", photo_list);
                        intent.putExtra("user_email", user_email);
                        intent.putExtra("feed_id", feed_id);
                        intent.putExtra("date", date);
                        intent.putExtra("crew_id", crew_id);
                        intent.putExtra("message", message);
                        context.startActivity(intent);

                    }
                }
            });

            // 좋아요 버튼 눌렀을 때
            btn_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(dListener != null)
                        {
                            dListener.onLikeClick(view, pos);
                        }
                    }
                }
            });


            // 댓글 버튼 눌렀을 때
            btn_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(dListener != null)
                        {
                            dListener.onCommentClick(view, pos);
                        }
                    }
                }
            });


        }
    }
}
