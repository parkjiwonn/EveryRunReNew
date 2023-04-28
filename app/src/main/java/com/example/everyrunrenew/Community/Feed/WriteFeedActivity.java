package com.example.everyrunrenew.Community.Feed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.everyrunrenew.Community.Crew_CRUD.ReadCrewActivity;
import com.example.everyrunrenew.Community.Feed.Adapter.MultiImageAdapter;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.FeedData;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;
import com.example.everyrunrenew.databinding.ActivityMainBinding;
import com.example.everyrunrenew.databinding.ActivityWriteFeedBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteFeedActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityWriteFeedBinding binding;

    Context context;
    ArrayList<String> list = new ArrayList<>(); // 사진 경로담은 list
    RecyclerView recyclerView;
    // 이미지를 보여줄 리사이클러뷰
    MultiImageAdapter adapter;
    // 리사이클러뷰에 적용시킬 어댑터

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private UserSharedPreference preferenceHelper;

    String user_email;
    int crew_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        preferenceHelper = new UserSharedPreference(this);
        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        crew_id = getIntent().getIntExtra("crew_id", 0);
        Log.d(TAG, "onCreate: crew_id=" + crew_id);

        setUI();

        binding.btnAddphoto.setOnClickListener(this); // 사진추가하기버튼 클릭리스너
        binding.btnAdd.setOnClickListener(this); // 게시글 저장
    }

    private void setUI() {

        // 현재 게시글 작성한 유저 이메일 갖고오기
        user_email = preferenceHelper.getEmail();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_addphoto:
                // 갤러리 들어가야 하고 사진 여러개 선택할 수 있어야 함.
                Intent intent = new Intent(WriteFeedActivity.this, MultiImgaeGalleryActivity.class);

                startActivityForResult(intent, 2000);
                break;

            case R.id.btn_add:
                // 필요한 것.
                // 게시글 내용, 게시글 올린 사람(유저 이메일), 게시글 올린 시간
                
                // 사진 업로드를 했는지 안했는지에 따라서 저장이 달라지게 해야 한다.
                // 사진 list에 사진이 한장이라도 있는지 확인
                // 그에 따라서 레트로핏 통신을 달리해야 함.

                String content = binding.etContent.getText().toString();
                long now = System.currentTimeMillis();

                // 게시글은 무조건 써야함.
                if(TextUtils.isEmpty(content)){
                    Log.d(TAG, "onClick: 게시글 비어있음");
                    AlertDialog.Builder dig_empty = new AlertDialog.Builder(this);
                    dig_empty.setMessage("게시글을 작성해주세요");
                    dig_empty.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dig_empty.show();

                }else{
                    Log.d(TAG, "onClick: 게시글 비어있지 X");
                    if(list.size() > 0)
                    {
                        Log.d(TAG, "onClick: 사진 한장이라도 있음");
                        // 사진이랑 게시글 같이 올리겠다는 것.

                        Log.d(TAG, "onClick: content ="+ content +", now ="+ now);
                        UploadFeedWithPhoto(content, now, list,crew_id);

                    }else{
                        Log.d(TAG, "onClick: 사진 한장도 없음.");
                        // 게시글만 올리겠다는 것.
                        UploadFeedWithoutPhoto(content, now,crew_id);
                    }
                }

                
                break;
        }
    }

    // 사진 없이 게시글 올리는 것.
    private void UploadFeedWithoutPhoto(String content, long now, int crew_id) {
        Log.d(TAG, "UploadFeedWithoutPhoto: crew_id="+ crew_id);
        retrofitInterface.UploadFeedWithoutPhoto(user_email, now, content, this.crew_id).enqueue(new Callback<FeedData>() {
            @Override
            public void onResponse(Call<FeedData> call, Response<FeedData> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "UploadFeedWithoutPhoto onResponse: 서버 응답 성공");
                    FeedData result = response.body();
                    Log.d(TAG, "UploadFeedWithoutPhoto onResponse: result =" + result);
                    String status = result.getStatus();
                    if(status.equals("true")) {
                        Log.d(TAG, "UploadFeedWithoutPhoto onResponse: 저장 성공");
                        // 게시하고 feed리스트에서 자신이 게시한 게시물이 추가되었다는 것을 보여줘야 하는데
                        // 그렇게 되면 프래그먼트 이동을 해야 함...
                        // 저장 되어서 온건지 아닌지 확인해야 할거 같다.
                        startActivity(new Intent(WriteFeedActivity.this, ReadCrewActivity.class).putExtra("where", "write"));
                        finish();

                    }else{
                        Log.d(TAG, "UploadFeedWithoutPhoto onResponse: 저장 실패");
                    }
                }else{
                    Log.d(TAG, "UploadFeedWithoutPhoto onResponse: 서버 응답 없음.");
                }
            }

            @Override
            public void onFailure(Call<FeedData> call, Throwable t) {
                Log.e(TAG, "UploadFeedWithoutPhoto 서버 통신 에러 = " + t.getMessage());
            }
        });

    }

    // 사진과 함께 게시글 올리는 것.
    private void UploadFeedWithPhoto(String content, long now, ArrayList<String> list, int crew_id) {
        Log.d(TAG, "UploadFeedWithPhoto: crew_id=" + crew_id);
        // 여러 file 들을 담아줄 arraylist
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        // 파일 경로들을 가지고 있는 uriList
        for(int i=0; i< list.size(); i++){
            File diaryfile = new File(list.get(i));
            Log.d(TAG, "UploadFeedWithPhoto: urllist =" + list );
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

        retrofitInterface.UploadFeedWithPhoto(files, content, now, user_email, crew_id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "UploadFeedWithPhoto onResponse: 서버 응답 성공");

                    String jsonResponse = response.body();
                    try
                    {
                        Log.d("in try = ", jsonResponse);

                        // 응답받은 부분을 json object로 변환하기 위함.
                        PhotoResponse(jsonResponse);
                        Log.d("next parseRegData = ", jsonResponse);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "UploadFeedWithPhoto onResponse: 서버 응답 실패");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "UploadFeedWithPhoto 서버 통신 에러 = " + t.getMessage());
            }
        });
    }

    private void PhotoResponse(String response)throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        if(jsonObject.optString("status").equals("true")){

            Log.d(TAG, "PhotoResponse: status is true");

            Log.d(TAG, "PhotoResponse: crew_id=" + crew_id);
            startActivity(new Intent(WriteFeedActivity.this, ReadCrewActivity.class).putExtra("where", "write").putExtra("crew_id",crew_id));
            finish();

        }else{
            Log.d(TAG, "PhotoResponse: status is false");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2000){
            if(resultCode == Activity.RESULT_OK){
                list = data.getStringArrayListExtra("photolist");
                Log.d(TAG, "onActivityResult: photolist =" + list);
                adapter = new MultiImageAdapter(list, getApplicationContext());
                binding.rvPhoto.setAdapter(adapter);
                binding.rvPhoto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                // 액티비티에 다중이미지가 세팅되니까 이미지의 갯수를 세팅해줘야함.
                binding.txImagecount.setText(list.size()+" / 5");
            }
        }
    }
}