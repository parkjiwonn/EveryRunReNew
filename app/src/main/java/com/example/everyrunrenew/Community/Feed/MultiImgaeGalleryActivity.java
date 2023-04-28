package com.example.everyrunrenew.Community.Feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.everyrunrenew.Community.AboutChatGallery.PhotoAdapter;
import com.example.everyrunrenew.Community.AboutChatGallery.PhotoDetailsLookUp;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.databinding.ActivityMultiImgaeGalleryBinding;
import com.example.everyrunrenew.databinding.ActivityWriteFeedBinding;

import java.util.ArrayList;

public class MultiImgaeGalleryActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName(); // log
    private ActivityMultiImgaeGalleryBinding binding;
    Context context;

    private static final int REQ_CODE_PERMISSION = 0;

    //============리사이클러뷰 관련 변수=========================
    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    ArrayList<String> photoArrayList = new ArrayList<>();
    //=========================================================
    SelectionTracker<Long> selectionTracker; // selection tracker
    ArrayList<String> list = new ArrayList<>(); // 사진 경로담은 list
    String photopath; // 개별 사진 경로

    TextView btn_add; // 뒤로가기 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_imgae_gallery);

        Log.e(TAG, "onCreate: 들어옴" );
        // 갤러리 접근 권한
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE_PERMISSION );

    }

    // 갤러리 접근권한 받아오는 부분, 접근 권한 허용되면 setupUI 실행.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: 들어옴 1" );
        // 갤러리 권한
        if(requestCode == REQ_CODE_PERMISSION){
            for(int grantResult :grantResults){
                if(grantResult == PackageManager.PERMISSION_DENIED){
                    finish();
                    return;
                }
            }
            setupUI();
        }
    }

    // 갤러리 UI 세팅하는 메서드
    // 리사이클러뷰 어댑터 리스트 연결해주는 부분
    private void setupUI() {
        Log.e(TAG, "setupUI: 들어옴 2");
        setContentView(R.layout.activity_multi_imgae_gallery);
        // ==================리사이클러뷰 연결 부분=============================
        recyclerView = findViewById(R.id.recycler_view); // 액티비티에 있는 리사이클러뷰 위젯 참조하기
        photoAdapter = new PhotoAdapter(this, photoArrayList); // 어댑터 객체 생성
        recyclerView.setAdapter(photoAdapter); // 어댑터 연결
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 그리드 형식으로 리사이클러뷰 만들기
        // =====================================================================
        setupSelectionTracker(); // selectiontracker를 만들어서
        photoAdapter.setSelectionTracker(selectionTracker); // 어댑터에 selectiontracker를 넘겨준다.

        Log.e(TAG, "setupUI: 클릭리스너 전");


        // 어댑터 클릭리스너 인터페이스 받는 부분
        photoAdapter.changeFt(new PhotoAdapter.getFileNameListner() {
            @Override
            public void change(ArrayList<String> result, String path) {
                // 선택한 사진들 경로 담은 arraylist
                // 선택한 개별 사진 경로
                list = result;
                System.out.println(list); // 선택된 사진들 모두 담은 array
                photopath = path;
                System.out.println(path);
                Log.e(TAG, "change: result size ="  );

            }
        });
        
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 선택버튼 클릭");
                // 사진 list를 이전 액티비티로 보내야 한다.
                Intent intent = new Intent(MultiImgaeGalleryActivity.this, WriteFeedActivity.class);
                intent.putExtra("photolist", list);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        Log.e(TAG, "setupUI: 클릭리스너 후" );

    }


    // selectiontracker 빌드하는 부분
    private void setupSelectionTracker() {
        Log.e(TAG, "setupSelectionTracker: 들어옴 4" );
        selectionTracker = new SelectionTracker.Builder<>( // 빌더를 통해 selectionTracker를 만든다.
                "selection_id",
                recyclerView,
                new StableIdKeyProvider(recyclerView),
                new PhotoDetailsLookUp(recyclerView),
                StorageStrategy.createLongStorage())
                // builder를 만드는데 필요한 파라미터
                // selectionid : 선택내용에 대한 id를 지정
                // recyclerview : 선택내용을 추적할 recyclerview를 지정한다.
                // key provider : 캐시를 위한 선택되는 아이템의 key 제공자
                // itemDetailsLookup : Recyclerview 아이템에 대한 정보
                // storage : saved state에서 키를 저장하기 위한 전략
                .withSelectionPredicate(SelectionPredicates.<Long>createSelectAnything()) // 어떠한 선택을 할지 결정할 수 있게 한다.
                .build();

        // 갤러리의 사진을 선택했을 때 onitemstatechanged, selectionchanged가 선택됨.
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onItemStateChanged(@NonNull Object key, boolean selected) {
                super.onItemStateChanged(key, selected);
                Log.e(TAG, "onItemStateChanged: 아이템의 상태가 변화되었을때 불려짐 " );
                Log.e(TAG, "onItemStateChanged: key = " + key ); // key는 아이템의 position 값이 불러와짐
                // selected 찍어보기
                Log.e(TAG, "onItemStateChanged: selected = " + selected );

                if(selected == false){
                    // 선택이 해제되었다는 의미
                    Log.e(TAG, "onItemStateChanged: photoarraylist= "+photoArrayList );
                    photoArrayList.clear();
                    photoAdapter.notifyDataSetChanged();

                }
                else
                {
                    // 3장 이상인 경우
                    if(photoArrayList.size() > 4)
                    {
                        // 3장 이상은 전송 불가라는 다이얼로그 띄우기.
                        showLimitNumOfPic();
                    }
                    else{

                    }

                }



            }

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
                Log.e(TAG, "onSelectionRefresh: 기존 데이터 집합이 변경되면 호출" );
            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                Log.e(TAG, "onSelectionChanged: refresh,restore에 대한 호출 제외한 모든 변경이 완료된후 호출된다.");
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
                Log.e(TAG, "onSelectionRestored: 선택 항목이 복원된 후 즉시 호출" );
            }
        });
    }

    // 사진 갯수 제한 알려주는 다이얼로그
    private void showLimitNumOfPic() {
        AlertDialog.Builder dig_limit = new AlertDialog.Builder(this);
        dig_limit.setMessage("이미지 추가는 5장까지 가능합니다.");
        dig_limit.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // 리스트에서 추가된 사진 제거하기
                photoArrayList.remove(photopath);
                System.out.println(photoArrayList);


            }
        });
        dig_limit.show();
    }
}
