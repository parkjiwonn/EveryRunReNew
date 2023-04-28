package com.example.everyrunrenew.Community.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everyrunrenew.Community.Crew_CRUD.ReadCrewActivity;
import com.example.everyrunrenew.Community.Feed.Adapter.FeedListAdapter;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.FeedData;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrewFeedFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG


    private UserSharedPreference preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private String user_email;

    // 리사이클러뷰, 어댑터, arraylist 선언
    RecyclerView recyclerView = null;
    FeedListAdapter feedListAdapter = null;
    ArrayList<FeedData> feedDataArrayList;

    int crew_id;

    boolean status; // 좋아요 선택 구분

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // 프래그먼트 뷰
        View view = inflater.inflate(R.layout.crewfeed_fragment, container, false);
        //RequestActivity에서 전달한 번들 저장
        Bundle bundle = getArguments();
        crew_id = bundle.getInt("crew_id");
        Log.d(TAG, "onCreateView: crew_id="+crew_id);
        // 다이어리 리스트 뿌려줄 다이어리 리사이클러뷰
        recyclerView = view.findViewById(R.id.rv_feed);
        feedDataArrayList = new ArrayList<>();
        // 어댑터에 다이어리 arraylist 넣어준다.
        feedListAdapter = new FeedListAdapter(feedDataArrayList, getContext());
        recyclerView.setAdapter(feedListAdapter);
        // 리사이클러뷰 레이아웃 매니저 만들기
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        getMysql(crew_id);

        feedListAdapter.setOnItemClickListener(new FeedListAdapter.OnItemClickListener() {
            @Override
            public void onLikeClick(View v, int pos) {

                Log.d(TAG, "onLikeClick: 좋아요 클릭");
                if(feedDataArrayList.get(pos).getMessage().equals("true")){
                    // 좋아요가 이미 선택되었다는 의미
                    // 한번 더 누르면 좋아요 취소가 되어야 함.
                    Log.d(TAG, "onLikeClick:  좋아요 이미 선택되어있는 상태");
                    status = false;
                    // 좋아요수 감소해야함.
                    int cnt = feedDataArrayList.get(pos).getFavorite();
                    cnt--;
                    Log.d(TAG, "onLikeClick: cnt =" + cnt);
                    feedDataArrayList.get(pos).setFavorite(cnt);
                    feedDataArrayList.get(pos).setMessage("false");
                    feedListAdapter.notifyDataSetChanged();

                    retrofitInterface.clickfavorite(feedDataArrayList.get(pos).getFeed_id(), user_email, status).enqueue(new Callback<FeedData>() {
                        @Override
                        public void onResponse(Call<FeedData> call, Response<FeedData> response) {
                            if(response.isSuccessful() && response.body() != null)
                            {
                                FeedData result = response.body();
                                Log.e(TAG, "onResponse: 좋아요 해제시 result : "+result );
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
                    // 좋아요를 한번도 누르지 않았다는 의미
                    Log.d(TAG, "onLikeClick: 좋아요 선택 X");
                    status = true;
                    int like_cnt = feedDataArrayList.get(pos).getFavorite();
                    Log.d(TAG, "onLikeClick: lick_cnt = "+like_cnt);
                    like_cnt++;
                    Log.d(TAG, "onLikeClick: lick_cnt 증가후 = "+like_cnt);

                    feedDataArrayList.get(pos).setFavorite(like_cnt);
                    feedDataArrayList.get(pos).setMessage("true");
                    feedListAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onLikeClick: feed_id="+feedDataArrayList.get(pos).getFeed_id()+", user_email ="+user_email+", status="+ status);
                    // 좋아요 선택시 증가된 좋아요 갯수 db에 저장하기
                    retrofitInterface.clickfavorite(feedDataArrayList.get(pos).getFeed_id(), user_email, status).enqueue(new Callback<FeedData>() {
                        @Override
                        public void onResponse(Call<FeedData> call, Response<FeedData> response) {
                            Log.d(TAG, "onResponse: response="+response);
                            Log.d(TAG, "onResponse: call =" +call);
                            if(response.isSuccessful() && response.body() != null)
                            {
                                FeedData result = response.body();
                                Log.e(TAG, "onResponse: like response :"+ result );
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: like response fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<FeedData> call, Throwable t) {
                            Log.e("onresponse", "에러 좋아요 선택시 서버통신 : " + t.getMessage());
                        }
                    });

                }

            }

            @Override
            public void onCommentClick(View v, int pos) {
                Log.d(TAG, "onLikeClick: 댓글 클릭");
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: 들어옴");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void getMysql(int crew_id) {

        preferenceHelper = new UserSharedPreference(getContext());

        user_email = preferenceHelper.getEmail();

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        Log.d(TAG, "getMysql: crew_id="+crew_id);
        // feed 전체 데이터 갖고 와야 함.
        retrofitInterface.SetFeedList(user_email,crew_id).enqueue(new Callback<List<FeedData>>() {
            @Override
            public void onResponse(Call<List<FeedData>> call, Response<List<FeedData>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    List<FeedData> result = response.body();

                    for(int i =0; i<result.size(); i++)
                    {

                        feedDataArrayList.add(result.get(i));
                    }

                    feedListAdapter.notifyDataSetChanged();

                    Log.d(TAG, " response body : " + result );
                }
                else
                {
                    Log.d(TAG, "onResponse: 유저가 쓴 feed response false" );
                }
            }

            @Override
            public void onFailure(Call<List<FeedData>> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });


    }


}
