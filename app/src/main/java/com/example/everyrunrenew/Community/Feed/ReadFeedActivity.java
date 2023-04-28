package com.example.everyrunrenew.Community.Feed;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.example.everyrunrenew.Community.Crew_CRUD.ReadCrewActivity;
import com.example.everyrunrenew.Community.Feed.Adapter.CirclePagerIndicatorDecoration;
import com.example.everyrunrenew.Community.Feed.Adapter.CommentAdapter;
import com.example.everyrunrenew.Community.Feed.Adapter.SubItemAdapter;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.CommentData;
import com.example.everyrunrenew.RetrofitData.FeedData;
import com.example.everyrunrenew.Running.RunningData;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.databinding.ActivityReadFeedBinding;
import com.example.everyrunrenew.databinding.ActivityUpdateCrewBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadFeedActivity extends AppCompatActivity implements View.OnClickListener, FeedMenuBottomSheetDialog.BottomSheetListener , CommentBottomSheedDialog.BottomSheetListener{

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityReadFeedBinding binding;
    Context context;

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private UserSharedPreference preferenceHelper;

    int feed_id;
    String content;
    String user_nick;
    String user_profile;
    int favorite;
    int comment;
    ArrayList<String> photolist = new ArrayList<>();
    String user_email ;
    long date ;
    SubItemAdapter subItemAdapter;
    int crew_id;
    String message;
    boolean status;
    String now_user_email;
    String write_comment;

    //========================댓글 리사이클러뷰===========================
    CommentAdapter commentAdapter = null;
    ArrayList<CommentData> commentDataArrayList = null;
    //=================================================================

    boolean comment_status; // 댓글 좋아요 클릭 여부
    String comment_user;// 댓글 작성자
    String chosen_comment_content;
    int chosen_comment_id;
    int chosen_comment_pos;

    CommentBottomSheedDialog commentBottomSheedDialog;

    InputMethodManager manager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        feed_id = getIntent().getIntExtra("feed_id", 0);
        content = getIntent().getStringExtra("content");
        user_nick = getIntent().getStringExtra("user_nick");
        user_profile = getIntent().getStringExtra("user_profile");
        favorite = getIntent().getIntExtra("favorite", 0);
        comment = getIntent().getIntExtra("comment", 0);
        photolist = getIntent().getStringArrayListExtra("photo_list");
        user_email = getIntent().getStringExtra("user_email");
        date = getIntent().getLongExtra("date", 0);
        Log.d(TAG, "onCreate: date = " + date);
        crew_id = getIntent().getIntExtra("crew_id",0);
        message = getIntent().getStringExtra("message");

        preferenceHelper = new UserSharedPreference(this);
        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        //================================리사이클러뷰 세팅=====================================================================
        commentDataArrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentDataArrayList, getApplicationContext());
        binding.rvComment.setAdapter(commentAdapter);
        binding.rvComment.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        //=====================================================================================================================

        now_user_email = preferenceHelper.getEmail();


        manager = (InputMethodManager)context.getSystemService(Service.INPUT_METHOD_SERVICE) ;

        setUI();

        binding.btnSetting.setOnClickListener(this);
        binding.btnLike.setOnClickListener(this);
        binding.etComment.setOnClickListener(this);
        binding.btnSend.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setUI() {

        // 유저 닉네임, 프로필 세팅
        binding.txUsernick.setText(user_nick);

        if(user_profile.equals("basic"))
        {
            binding.imgUser.setImageResource(R.drawable.user_img);
        }else{
            // 경로는 userimg로 설정해줘야 함.
            String url = "http://3.36.174.137/UserProfileImg/" + user_profile;

            Glide.with(this).load(url).into(binding.imgUser);
        }

        // content, favorite, comment 세팅
        binding.txContent.setText(content);
        binding.txFavorite.setText(String.valueOf(favorite));
        binding.txComment.setText(String.valueOf(comment));
        binding.txDate.setText(formatTimeString(date));

        // 좋아요 활성, 비활성화 세팅
        if(message.equals("true")){
            binding.btnLike.setChecked(true);
        }else{
            binding.btnLike.setChecked(false);
        }

        // 사진 세팅
        if(photolist.size() != 0){
            // 사진이 한개라도 있다면
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            layoutManager.setInitialPrefetchItemCount(photolist.size());

            binding.rvPhoto.setHasFixedSize(true);
            binding.rvPhoto.setOnFlingListener(null);
            SnapHelper snapHelper = new PagerSnapHelper();

            snapHelper.attachToRecyclerView(binding.rvPhoto);
            binding.rvPhoto.addItemDecoration(new CirclePagerIndicatorDecoration());
            subItemAdapter  = new SubItemAdapter(photolist, context);
            binding.rvPhoto.setLayoutManager(layoutManager);
            binding.rvPhoto.setAdapter(subItemAdapter);

        }else{
            // 사진이 한장도 없다면
            Log.d(TAG, "setUI: 사진이 한장도 없음");
            binding.rvPhoto.setVisibility(View.GONE);
        }

        // 설정 버튼 설정
        // 글 작성자만 삭제할 수 있다.
        if(user_email.equals(now_user_email)){
            binding.btnSetting.setVisibility(View.VISIBLE);
        }else{
            binding.btnSetting.setVisibility(View.INVISIBLE);
        }


        
        commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onLikeClick(View v, int pos) {
                Log.d(TAG, "onLikeClick: 댓글 좋아요 클릭한 상태");

                if(commentDataArrayList.get(pos).getMessage().equals("true"))
                {
                    // 좋아요가 선택된 상태라면
                    comment_status = false;
                    Log.d(TAG, "onLikeClick: 좋아요 활성화된 상태");
                    // 좋아요 선택되어 있는데 좋아요 한번 더 누른 상태
                    int like_cnt = commentDataArrayList.get(pos).getFavorite();
                    like_cnt--;
                    commentDataArrayList.get(pos).setFavorite(like_cnt);
                    commentDataArrayList.get(pos).setMessage("false");
                    commentAdapter.notifyDataSetChanged();

                    int comment_id = commentDataArrayList.get(pos).getComment_id();
                    retrofitInterface.clickcommentlike(now_user_email, comment_id, comment_status).enqueue(new Callback<CommentData>() {
                        @Override
                        public void onResponse(Call<CommentData> call, Response<CommentData> response) {
                            if(response.isSuccessful() && response.body() != null)
                            {
                                CommentData result = response.body();
                                Log.e(TAG, "onResponse: 댓글 채워진 좋아요 선택 result : " + result );
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: 댓글 채워진 좋아요 선택 fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<CommentData> call, Throwable t) {
                            Log.e("onresponse", "에러 댓글 좋아요 해제 : " + t.getMessage());
                        }
                    });


                }else{
                    Log.e(TAG, "onLikeClick: 좋아요 선택되어 있지 않음" );
                    // 좋아요 선택되어 있지 않은데 좋아요 누른 상태
                    // 좋아요 갯수가 늘어나야 하고 버튼의 상태가 true로 되어야 한다.
                    comment_status = true;
                    int comment_id = commentDataArrayList.get(pos).getComment_id(); // 유저가 좋아요 누른 댓글의 id 가져오기
                    Log.e(TAG, "onLikeClick: comment_id :" + comment_id );

                    // 서버 연결
                    retrofitInterface.clickcommentlike(now_user_email, comment_id, comment_status).enqueue(new Callback<CommentData>() {
                        @Override
                        public void onResponse(Call<CommentData> call, Response<CommentData> response) {
                            if(response.isSuccessful() && response.body() != null){
                                CommentData result = response.body();
                                Log.e(TAG, "onResponse: 댓글 빈 좋아요 선택 result :"+ result );
                                int like_cnt = commentDataArrayList.get(pos).getFavorite();
                                like_cnt++;

                                commentDataArrayList.get(pos).setFavorite(like_cnt);
                                commentDataArrayList.get(pos).setMessage("true");
                                commentAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: 댓글 빈 좋아요 선택 fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<CommentData> call, Throwable t) {
                            Log.e("onresponse", "에러 댓글 좋아요 선택 : " + t.getMessage());
                        }
                    });
                }
            }
        });
        
        commentAdapter.OnItemLongClickEventListener(new CommentAdapter.OnItemLongClickEventListener() {
            @Override
            public void onLongClick(View v, int pos) {
                Log.d(TAG, "onLongClick: 아이템 길게 선택");
                // 게시글 수정 삭제 가능한 버튼
                // 댓글 수정 삭제는 댓글 작성자만 가능하게 하기
                comment_user = commentDataArrayList.get(pos).getUser_email();
                Log.d(TAG, "onLongClick: comment_user="+comment_user);
                if(now_user_email.equals(comment_user)){
                    commentBottomSheedDialog = new CommentBottomSheedDialog();
                    commentBottomSheedDialog.show(getSupportFragmentManager(), "bottomsheet");
                    chosen_comment_content = commentDataArrayList.get(pos).getContent();
                    chosen_comment_id = commentDataArrayList.get(pos).getComment_id();
                    chosen_comment_pos = pos;

                    Log.d(TAG, "onLongClick: chosen_comment_content="+chosen_comment_content);
                }

            }
        });


        // 댓글 세팅
        retrofitInterface.commentlist(feed_id, now_user_email).enqueue(new Callback<List<CommentData>>() {
            @Override
            public void onResponse(Call<List<CommentData>> call, Response<List<CommentData>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    Log.e(TAG, "onResponse: 댓글 리스트 불러오기 :"+ response.body() );
                    List<CommentData> result = response.body();

                    for(int i=0; i<result.size(); i++)
                    {
                        commentDataArrayList.add(result.get(i));

                    }

                    commentAdapter.notifyDataSetChanged();
                }
                else
                {
                    Log.e(TAG, "onResponse: 댓글 리스트 불러오기 response fail" );
                }
            }

            @Override
            public void onFailure(Call<List<CommentData>> call, Throwable t) {
                Log.e("onresponse", "댓글 리스트 불러오기 에러 : " + t.getMessage());
            }
        });




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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_setting:
                // 게시글 수정 삭제 가능한 버튼
                FeedMenuBottomSheetDialog feedMenuBottomSheetDialog = new FeedMenuBottomSheetDialog();
                feedMenuBottomSheetDialog.show(getSupportFragmentManager(), "bottomsheet");

                break;

            case R.id.btn_like:
                // 게시글 좋아요 기능


                if(message.equals("true"))
                {
                    // 좋아요가 이미 활성화 상태
                    Log.d(TAG, "onClick: 좋아요 이미 활성화");
                    favorite--;
                    binding.txFavorite.setText(String.valueOf(favorite));
                    status = false; // 좋아요 해제한 상태

                    retrofitInterface.clickfavorite(feed_id, user_email, status).enqueue(new Callback<FeedData>() {
                        @Override
                        public void onResponse(Call<FeedData> call, Response<FeedData> response) {
                            if(response.isSuccessful() && response.body() != null)
                            {
                                FeedData result = response.body();
                                Log.e(TAG, "onResponse: 좋아요 해제시 result : "+result );
                                String status = result.getStatus();

                                if (status.equals("true")) {
                                    // 좋아요 삭제 됐다는 얘기니까 좋아요 빈하트로 변경하기
                                    message = "false";

                                } else {
                                    Log.e(TAG, "onResponse: 좋아요 해제 status is false ");
                                }
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: 좋아요 해제 fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<FeedData> call, Throwable t) {
                            Log.e("onresponse", "에러 좋아요 해제시 서버통신 : " + t.getMessage());
                        }
                    });

                }else{
                    Log.d(TAG, "onClick: 좋아요 비활성화인 상태");
                    favorite++;
                    binding.txFavorite.setText(String.valueOf(favorite));
                    status = true;

                    retrofitInterface.clickfavorite(feed_id, user_email, status).enqueue(new Callback<FeedData>() {
                        @Override
                        public void onResponse(Call<FeedData> call, Response<FeedData> response) {
                            if(response.isSuccessful() && response.body() != null)
                            {
                                FeedData result = response.body();
                                Log.e(TAG, "onResponse: 좋아요 해제시 result : "+result );
                                String status = result.getStatus();

                                if (status.equals("true")) {
                                    // 좋아요 삭제 됐다는 얘기니까 좋아요 빈하트로 변경하기
                                    message = "true";

                                } else {
                                    Log.e(TAG, "onResponse: 좋아요 선택 status is false ");
                                }
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: 좋아요 해제 fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<FeedData> call, Throwable t) {
                            Log.e("onresponse", "에러 좋아요 해제시 서버통신 : " + t.getMessage());
                        }
                    });
                }

                break;

            case R.id.et_comment:
                Log.d(TAG, "onClick: 댓글 작성 시작");
                binding.scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                      binding.scrollView.fullScroll(binding.scrollView.FOCUS_DOWN)  ;

                    }
                });
                break;

            case R.id.btn_send:
                // 댓글 쓰기
                Log.d(TAG, "onClick: 댓글 작성 시작");
                // 유저가 작성한 댓글
                write_comment = binding.etComment.getText().toString();
                long date = System.currentTimeMillis(); // 작성한 시간
                
                sendComment(write_comment, date);
                break;
        }
    }

    // 댓글 남기기 메서드
    private void sendComment(String comment, long date) {

        retrofitInterface.SendComment(comment,date,feed_id, now_user_email).enqueue(new Callback<CommentData>() {
            @Override
            public void onResponse(Call<CommentData> call, Response<CommentData> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    CommentData result = response.body();
                    Log.e(TAG, "onResponse: 댓글 :" + result );
                    commentDataArrayList.add(0, result);

                    commentAdapter.notifyDataSetChanged();
                    int comment_cnt = result.getCnt();
                    Log.d(TAG, "onResponse: 댓글 수 =" + comment_cnt);
                    binding.txComment.setText(String.valueOf(comment_cnt));
                    binding.etComment.setText("");
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
                else
                {
                    Log.e(TAG, "onResponse: 댓글 status is faile" );
                }
            }

            @Override
            public void onFailure(Call<CommentData> call, Throwable t) {
                Log.e("onresponse", "에러 댓글 입력 : " + t.getMessage());
            }
        });
    }

    @Override
    public void onButtonClicked(int option) {
        Log.d(TAG, "onButtonClicked: 바텀 시트 보여짐");
        switch (option){
            case 1:
                // 수정 선택
                Log.d(TAG, "onButtonClicked: 수정 선택");
                startActivity(new Intent(ReadFeedActivity.this, UpdateFeedActivity.class).putExtra("feed_id", feed_id).putExtra("crew_id", crew_id));
                break;

            case 2:
                // 삭제 선택
                Log.d(TAG, "onButtonClicked: 삭제 선택");
                AlertDialog.Builder dig_feed_delete = new AlertDialog.Builder(this);
                dig_feed_delete.setMessage("이 게시물을 삭제하시겠어요?");
                dig_feed_delete.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dig_feed_delete.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteFeed(feed_id);
                    }
                });
                dig_feed_delete.show();

                break;
            }

        }


    // 게시글 삭제
    private void DeleteFeed(int feed_id) {
        retrofitInterface.DeleteFeed(feed_id).enqueue(new Callback<FeedData>() {
            @Override
            public void onResponse(Call<FeedData> call, Response<FeedData> response) {

                if(response.isSuccessful() && response !=null){
                    Log.d(TAG, "onResponse: feed 삭제 통신 성공");
                    startActivity(new Intent(ReadFeedActivity.this, ReadCrewActivity.class).putExtra("where", "write").putExtra("crew_id",crew_id));
                    finish();

                }else{
                    Log.d(TAG, "onResponse: feed 삭제 통신 실패");
                }
            }

            @Override
            public void onFailure(Call<FeedData> call, Throwable t) {
                Log.e("onresponse", "feed 삭제 통신 에러 : " + t.getMessage());
            }
        });
    }


    @Override
    public void onCommentButtonClicked(int option) {


        switch (option){
            case 1:
                // 수정 선택
                Log.d(TAG, "onCommentButtonClicked: 수정 선택");
                commentBottomSheedDialog.dismiss();
                // 키패드가 올라와야 하고
                UpdateComment();

                break;

            case 2:
                // 삭제 선택
                Log.d(TAG, "onCommentButtonClicked: 삭제 선택");
                commentBottomSheedDialog.dismiss();
                DeleteComment();
                break;
        }
    }

    // 댓글 삭제 메서드
    private void DeleteComment() {

        AlertDialog.Builder dig_delete = new AlertDialog.Builder(this);
        dig_delete.setTitle("댓글 삭제");
        dig_delete.setMessage("댓글을 완전히 삭제할까요?");
        dig_delete.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dig_delete.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: feed_id="+feed_id);
                retrofitInterface.DeleteComment(chosen_comment_id, feed_id).enqueue(new Callback<CommentData>() {
                    @Override
                    public void onResponse(Call<CommentData> call, Response<CommentData> response) {
                        if(response.isSuccessful() && response.body() != null)
                        {
                            CommentData result = response.body();
                            Log.e(TAG, "onResponse: 댓글 삭제 :" + result );

                            String status = result.getStatus();
                            int cnt = result.getCnt();

                            if(status.equals("true")){
                                    // 댓글 삭제후 남은 댓글 수 세팅해줘야 함.
                                binding.txComment.setText(String.valueOf(cnt));

                                commentDataArrayList.remove(chosen_comment_pos);
                                commentAdapter.notifyDataSetChanged();
                            }

                        }
                        else
                        {
                            Log.e(TAG, "onResponse: 댓글 삭제 status is faile" );
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentData> call, Throwable t) {
                        Log.e("onresponse", "댓글 삭제 통신 에러 : " + t.getMessage());
                    }
                });
            }
        });
        dig_delete.show();

    }

    // 댓글 수정 메서드
    private void UpdateComment() {

        Log.d(TAG, "UpdateComment: 들어옴");



        EditText editText = findViewById(R.id.et_comment);
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                manager.showSoftInput(editText, 0);
            }
        },100);

        binding.etComment.setText(chosen_comment_content);
        
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 댓글 수정 버튼 클릭");

                Log.d(TAG, "onClick: 선택된 댓글 id="+ chosen_comment_id);


                String update_content = binding.etComment.getText().toString();
                Log.d(TAG, "onClick: 수정된 댓글 내용="+ update_content);

                retrofitInterface.UpdateComment(update_content, chosen_comment_id).enqueue(new Callback<CommentData>() {
                    @Override
                    public void onResponse(Call<CommentData> call, Response<CommentData> response) {

                        if(response.isSuccessful() && response != null){
                            Log.d(TAG, "onResponse: 댓글 수정 서버 통신 성공");

                            CommentData result= response.body();
                            Log.d(TAG, "onResponse: 댓글 수정 후 result =" + result);
                            String status = result.getStatus();
                            if(status.equals("true")){
                                // 수정 완료
                                editText.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                        binding.etComment.setText("");
                                        Log.d(TAG, "run: chosen_comment_pos="+chosen_comment_pos);
                                        commentDataArrayList.get(chosen_comment_pos).setContent(update_content);
                                        commentAdapter.notifyDataSetChanged();
                                    }
                                },100);

                            }else{
                                // 수정 완료 X
                            }

                        }else{
                            Log.d(TAG, "onResponse: 댓글 수정 서버 통신 실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentData> call, Throwable t) {
                        Log.e("onresponse", "댓글 수정 통신 에러 : " + t.getMessage());
                    }
                });



            }
        });

    }
}