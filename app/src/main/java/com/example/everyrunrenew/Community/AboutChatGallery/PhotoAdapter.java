package com.example.everyrunrenew.Community.AboutChatGallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everyrunrenew.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter  extends RecyclerView.Adapter<LookUpViewHolder>{
    public static final String TAG = PhotoAdapter.class.getSimpleName();
    private ArrayList<String> photoList;
    private List<Photo> photos;
    private Context context;
    private ArrayList<String> list = new ArrayList<>();


    private getFileNameListner getFileNameListner;

    public interface getFileNameListner{
        void change(ArrayList<String> result, String path);
    };

    public void changeFt(getFileNameListner getFileNameListner){
        this.getFileNameListner = getFileNameListner;
    }

    // selection 라이브러리는 3가지 타입의 key 타입을 지원한다.
    // 하나의 item을 식별하기 위한 고유값(ID)로 설정하면 된다.

    @Nullable
    private SelectionTracker<Long> selectionTracker;

    public PhotoAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.photoList = list;
        setHasStableIds(true);
        // adapter에게 id를 이용해 item을 식별하겠다는 설정을 하도록 한다.
        photos = getPhotoList(context);
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public LookUpViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        Log.e(TAG, "LookUpViewHolder: 들어옴 6" );
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_photo,viewGroup,false);

        return new LookUpViewHolder(view);
    }

    // selectionTracker 참조할수있다.
    // 뷰홀더가 바인딩 되는 시점에 selectiontracker의 정보를 읽어 선택내용에 대한 UI를 결정할수있다.
    @Override
    public void onBindViewHolder(@NonNull LookUpViewHolder lookUpViewHolder, int position) {
        Log.e(TAG, "onBindViewHolder: 들어옴 7" );
        Log.e(TAG, "onBindViewHolder: 클릭클릭" );

        Photo photo = photos.get(position);
        String photoPathpath = photo.getPath();
        lookUpViewHolder.setPhoto(photo);

        if(photoList.size() <= 4)
        {
            lookUpViewHolder.setSelectionTracker(selectionTracker); // adapter에서 selectiontracker를 참조할수있게 되었음.
            //Log.e(TAG, "onBindViewHolder: clicked =" + clicked );


            if(lookUpViewHolder.clicked)
            {
                // 클릭이 된 상태
                Log.e(TAG, "onBindViewHolder: clicked is true" );
                Log.e(TAG, "onBindViewHolder: photolist = " +photoList );
                Log.e(TAG, "onBindViewHolder: list = " +list );
                Log.e(TAG, "onBindViewHolder: photopath =" + photoPathpath );

                photoList.add(photoPathpath);
                getFileNameListner.change(photoList, photoPathpath);
                lookUpViewHolder.checkBox.setText(String.valueOf(photoList.size()));
                Log.e(TAG, "onBindViewHolder: photolist.size()=" + photoList.size() );


            }
            else
            {
                // 클릭이 해제된 상태
                Log.e(TAG, "onBindViewHolder: clicked is false" );

                getFileNameListner.change(photoList, photoPathpath);
                lookUpViewHolder.checkBox.setText("");


            }


        }









    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    // 아이템 id를 지정한다.
    @Override
    public long getItemId(int position) {
        return position;
    }

    private List<Photo> getPhotoList(Context context){
        Log.e(TAG, "getPhotoList: 들어옴 3" );
        String[] projection = new String[]{MediaStore.Images.Media.DISPLAY_NAME,MediaStore.Images.Media.DATA};
        //절대 경로로 바꾸는 메서드
        //query()에는 5개의 인자가 들어간다.
        // 1. uri : 찾고자 하는 데이터의 uri = 데이터의 위치를 표현
        // 2. projection : DB의 column과 같다. 결과로 받고 싶은 데이터의 종류를 알려준다.
        // 3. selection : DB의 where과 같다. 어떤 조건으로 필터링된 결과를 받을 때 사용한다.
        // 4. selection args : selection 과 함께 사용된다.
        // 5. sort order : 쿼리 결과 데이터를 sorting할 때 사용한다.
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);

        ArrayList<Photo> photoList = new ArrayList<>();
        while(cursor.moveToNext()){
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(projection[0]));
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(projection[idx]));;
            Photo photo = new Photo(name, path);
            Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(idx));

            Log.e(TAG, "getPhotoList: uri = " + uri );
            Log.e(TAG, "getPhotoList: photo0 =" + photo );
            Log.e(TAG, "getPhotoList: name =" + name );
            Log.e(TAG, "getPhotoList: path =" + path );

            photoList.add(photo);
        }
        cursor.close();
        return photoList;
    }

    // adapter에 setter를 만들어 selectiontracker를 넘겨받음.
    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        Log.e(TAG, "setSelectionTracker: 들어옴 5" );
        this.selectionTracker = selectionTracker;
    }
}
