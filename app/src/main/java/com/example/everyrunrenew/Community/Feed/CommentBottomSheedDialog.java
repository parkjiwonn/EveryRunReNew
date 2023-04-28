package com.example.everyrunrenew.Community.Feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.everyrunrenew.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CommentBottomSheedDialog extends BottomSheetDialogFragment {

    private final String TAG = this.getClass().getSimpleName(); // log
    //초기 변수 설정
    private View view;
    //인터페이스 변수
    private CommentBottomSheedDialog.BottomSheetListener mListener;
    private TextView tx_update, tx_delete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.comment_bottom_sheet_layout, container, false);

        mListener = (CommentBottomSheedDialog.BottomSheetListener) getContext();

        tx_update = view.findViewById(R.id.tx_update);
        tx_delete = view.findViewById(R.id.tx_delete);

        tx_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 댓글 수정 클릭");
                mListener.onCommentButtonClicked(1);
            }
        });

        tx_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 댓글 삭제 클릭");
                mListener.onCommentButtonClicked(2);
            }
        });



        return view;
    }

    // 부모 액티비티와 연결하기위한 인터페이스
    public interface BottomSheetListener {
        void onCommentButtonClicked(int option);
    }
}
