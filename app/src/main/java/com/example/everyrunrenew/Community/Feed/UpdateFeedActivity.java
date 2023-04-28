package com.example.everyrunrenew.Community.Feed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.everyrunrenew.Community.Crew_CRUD.ReadCrewActivity;
import com.example.everyrunrenew.Community.Feed.Adapter.MultiImageAdapter;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.FeedData;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.databinding.ActivityUpdateCrewBinding;
import com.example.everyrunrenew.databinding.ActivityUpdateFeedBinding;
import com.example.everyrunrenew.databinding.ActivityWriteFeedBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateFeedActivity extends AppCompatActivity implements View.OnClickListener{


    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityUpdateFeedBinding binding;

    Context context;

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private UserSharedPreference preferenceHelper;

    ArrayList<String> uriList = new ArrayList<>();
    // 이미지 uri 담을 리스트 -> 초반세팅을 위한 list

    // 수정되지 않은 사진들 담은 arraylist
    ArrayList<String> photo = new ArrayList<>();
    // 수정된 사진들을 담은 arraylist
    ArrayList<String> uri = new ArrayList<>();


    MultiImageAdapter adapter;
    // 리사이클러뷰에 적용시킬 어댑터

    int feed_id;
    int crew_id;
    int check=1;
    String now_user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        preferenceHelper = new UserSharedPreference(this);
        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        feed_id = getIntent().getIntExtra("feed_id", 0);
        Log.d(TAG, "onCreate: feed_id=" + feed_id);
        crew_id = getIntent().getIntExtra("crew_id", 0);
        Log.d(TAG, "onCreate: crew_id="+crew_id);
        setUI();

        now_user_email = preferenceHelper.getEmail();
        binding.btnAddphoto.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);

    }

    private void setUI() {



        // 업데이트할 떄 원래의 피드 데이터 세팅해주기
        retrofitInterface.SetFeedData(feed_id).enqueue(new Callback<FeedData>() {
            @Override
            public void onResponse(Call<FeedData> call, Response<FeedData> response) {

                if(response.isSuccessful()){
                    Log.d(TAG, "SetFeedData onResponse: 서버 응답 성공");
                    FeedData result = response.body();
                    Log.d(TAG, "SetFeedData onResponse: result = "+ result);

                    String content = result.getContent();
                    binding.etContent.setText(content);


                    // 사진 리스트 세팅해야 함.
                    for(int i=0; i<result.getPhotolist().size(); i++){
                        uriList.add(result.getPhotolist().get(i));
                    }
                    binding.txImagecount.setText(uriList.size()+" / 5");
                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    binding.rvPhoto.setAdapter(adapter);
                    binding.rvPhoto.setLayoutManager(new LinearLayoutManager(UpdateFeedActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    // 수정 전 데이터 세팅하는 것 완료
                }else{
                    Log.d(TAG, "SetFeedData onResponse: 서버 응답 실패");
                }

            }

            @Override
            public void onFailure(Call<FeedData> call, Throwable t) {
                Log.e(TAG, "SetFeedData 에러 = " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                // 수정 완료 버튼
                String update_content = binding.etContent.getText().toString();
                long update_date = System.currentTimeMillis();

                // 사진 모두 삭제하고 그냥 글만 수정할떄
                if(uriList.size() == 0){
                    check =0;
                    Upwithoutpic(update_content, update_date, check);
                }else{
                    // 사진이랑 글 같이 수정할 때
                    for(int i=0; i<uriList.size(); i++){
                        // 수정한 사진인지 수정하지 않은 사진인지 구분하기 위함
                        String str = uriList.get(i);
                        Log.d(TAG, "onClick: str = "+str);

                        if(str.contains("/")){
                            // 수정한 사진
                            uri.add(str);

                        }else{
                            // 수정되지 않은 사진
                            photo.add(str);
                        }
                    }

                    Log.d(TAG, "onClick: photo에 들어간 사진들 =" +photo);
                    Log.d(TAG, "onClick: uri에 들어간 사진들 =" +uri);

                    ModifyData(photo, uri, update_content, update_date);
                }

                break;

            case R.id.btn_addphoto:
                // 갤러리 들어가야 하고 사진 여러개 선택할 수 있어야 함.
                Intent intent = new Intent(UpdateFeedActivity.this, MultiImgaeGalleryActivity.class);

                startActivityForResult(intent, 2000);
                break;
        }
    }


    // 사진이랑 게시글 같이 수정하는 메서드
    private void ModifyData(ArrayList<String> photo, ArrayList<String> uri, String update_content, long update_date) {

        int photo_size= photo.size();
        int uri_size = uri.size();
        Log.e(TAG, "modify: photo_size : "+ photo_size);
        Log.e(TAG, "modify: uri_size : "+ uri_size);

        if(photo_size == 0)
        {
            // 업로드된 사진 모두 경로값을 가질때
            Log.e(TAG, "modify: 모두 경로값을 가지는 사진인 경우" );
            diarymodify(update_content, update_date);
        }
        else if(uri_size == 0)
        {
            // 사진을 하나도 추가하지 않은 경우 텍스트만 건들인거고 사진은 그대로라는 뜻.
            Log.e(TAG, "modify: 사진 그대로, 사진 하나도 추가하지 않음" );
            Upwithoutpic(update_content, update_date, check);

        }
        else
        {
            // 경로값을 가지고 있는 이미지, 건드리지 않은 이미지가 같이 있는 경우.
            Log.e(TAG, "modify: 수정되지 않은 사진을 포함하고 있는 경우" );
            updateMixed(photo, uri, update_content, update_date, now_user_email);
        }
    }

    private void Upwithoutpic(String update_content, long update_date, int check) {
        if (check == 1){
            Log.d(TAG, "Upwithoutpic: 사진 건들이지 X 텍스트만 수정");
        }else{
            Log.d(TAG, "Upwithoutpic: 사진 아예 없이 수정");
        }

        retrofitInterface.UpdateWithoutPhoto(update_content, update_date, feed_id,crew_id, check).enqueue(new Callback<FeedData>() {
            @Override
            public void onResponse(Call<FeedData> call, Response<FeedData> response) {
                if(response.isSuccessful() &&  response.body() != null)
                {
                    FeedData result = response.body();
                    Log.e(TAG, "onResponse 사진 없이 텍스트만 수정했을 경우 : result"+ result );
                    startActivity(new Intent(UpdateFeedActivity.this, ReadCrewActivity.class).putExtra("where", "write").putExtra("crew_id",crew_id));
                    finish();


                }
                else
                {
                    Log.e(TAG, "onResponse: 사진 없이 텍스트만 수정했을 경우 fail" );
                }
            }

            @Override
            public void onFailure(Call<FeedData> call, Throwable t) {

            }
        });

    }


    // 새로 추가된 사진, 원래 있던 사진 섞인 경우
    private void updateMixed(ArrayList<String> photo, ArrayList<String> uriList, String update_content, long update_date, String now_user_email) {

        // 여러 file 들을 담아줄 arraylist
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        for(int i=0; i<uriList.size(); i++){
            File diaryfile = new File(uriList.get(i));
            Log.d(TAG, "UploadFeedWithPhoto: urllist =" + uriList );
            if (!diaryfile.exists()) {       // 원하는 경로에 폴더가 있는지 확인

                diaryfile.mkdirs();    // 하위폴더를 포함한 폴더를 전부 생성

            }
            // uri 타입의 파일 경로를 가지는 requestBody 객체 생성한다.
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), diaryfile);

            // 사진 파일 이름
            String fileName = "photo" + i + "_";
            // RequestBody로 Multipartbody.Part 객체 생성.
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo[]" , fileName, fileBody);
            // fileName

            // 파일 추가해주기
            files.add(filePart);
        }

        // 사진이 섞인 경우
        // 새로 추가한 사진 + 원래 그래로인 사진
        retrofitInterface.UpdateMixed(files, photo, update_content, update_date, feed_id, crew_id, now_user_email).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    String jsonResponse = response.body();
                    Log.e(TAG, "onResponse: updateMixed jsonResponse : " + jsonResponse );

                    // 업로드 성공해서 서버에 이미지 올라가는것 까지 확인.
                    startActivity(new Intent(UpdateFeedActivity.this, ReadCrewActivity.class).putExtra("where", "write").putExtra("crew_id",crew_id));
                    finish();


                }
                else{
                    Log.e(TAG, "onResponse: updateMixed fail" );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("onresponse", "다이어리 수정 updateMixed 서버 에러 : " + t.getMessage());
            }
        });

    }

    // 사진을 아예 다시 갈아엎은 경우
    // 사진 제 업로드 해야 함.
    private void diarymodify(String update_content, long update_date) {
        // 여러 file 들을 담아줄 arraylist
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        for(int i=0; i<uriList.size(); i++){
            File diaryfile = new File(uriList.get(i));
            Log.d(TAG, "UploadFeedWithPhoto: urllist =" + uriList );
            if (!diaryfile.exists()) {       // 원하는 경로에 폴더가 있는지 확인

                diaryfile.mkdirs();    // 하위폴더를 포함한 폴더를 전부 생성

            }
            // uri 타입의 파일 경로를 가지는 requestBody 객체 생성한다.
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), diaryfile);

            // 사진 파일 이름
            String fileName = "photo" + i + "_";
            // RequestBody로 Multipartbody.Part 객체 생성.
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo[]" , fileName, fileBody);
            // fileName

            // 파일 추가해주기
            files.add(filePart);
        }

        Log.d(TAG, "diarymodify: feed_id="+feed_id);
        Log.d(TAG, "diarymodify: crew_id="+crew_id);
        retrofitInterface.UpdateFeed(files, update_content, now_user_email, update_date,feed_id,crew_id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    Log.d(TAG, "onResponse: 피드 수정 success");
                    String jsonResponse = response.body();

                    try
                    {
                        Log.e("in try", jsonResponse);

                        // 응답받은 부분을 json object로 변환하기 위함.
                        PhotoResponse(jsonResponse);
                        Log.e("next parseRegData", jsonResponse);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Log.d(TAG, "피드 수정 onResponse: fail" );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "UpdateFeed 에러 = " + t.getMessage());
            }
        });


    }

    private void PhotoResponse(String response) throws JSONException{
        JSONObject jsonObject = new JSONObject(response);
        // 해당 키값에 해당하는 값이 있는 경우에 value 값을 리턴하지만
        // 값이 없는 경우에 ""와 같은 빈 문자열을 반환한다.

        if (jsonObject.optString("status").equals("true"))
        {
            Log.e("jsonobject2", jsonObject.optString("status"));
            // true 찍힘.
            Log.e("jsonobject2", response);

            Log.e("jsonobject3", jsonObject.optString("status"));

            // 업로드 성공해서 서버에 이미지 올라가는것 까지 확인.
            startActivity(new Intent(UpdateFeedActivity.this, ReadCrewActivity.class).putExtra("where", "write").putExtra("crew_id",crew_id));
            finish();
//
        }
        else
        {

            Toast.makeText(UpdateFeedActivity.this, "수정 실패", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2000){
            if(resultCode == Activity.RESULT_OK){
               if(data == null){
                   // 이미지 선택XX
                   Log.d(TAG, "onActivityResult: 이미지 선택X");
               }else{
                   // 이미지 하나라도 선택한 경우
                   Log.d(TAG, "onActivityResult: 이미지 하나라도 선택함");

                   ArrayList<String> add_list = new ArrayList<>();
                   add_list = data.getStringArrayListExtra("photolist");
                   for(int i=0; i<data.getStringArrayListExtra("photolist").size(); i++){

                       uriList.add(add_list.get(i));
                   }

                   adapter = new MultiImageAdapter(uriList, getApplicationContext());
                   binding.rvPhoto.setAdapter(adapter);
                   binding.rvPhoto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                   // 액티비티에 다중이미지가 세팅되니까 이미지의 갯수를 세팅해줘야함.
                   binding.txImagecount.setText(uriList.size()+" / 5");

               }
            }
        }
    }
}