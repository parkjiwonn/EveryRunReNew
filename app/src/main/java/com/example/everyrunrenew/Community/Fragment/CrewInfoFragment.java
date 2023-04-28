package com.example.everyrunrenew.Community.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.everyrunrenew.Community.CrewSetting.MemberListActivity;
import com.example.everyrunrenew.Community.Crew_CRUD.ReadCrewActivity;
import com.example.everyrunrenew.R;
import com.example.everyrunrenew.Retrofit.RetrofitClient;
import com.example.everyrunrenew.Retrofit.RetrofitInterface;
import com.example.everyrunrenew.RetrofitData.CrewData;
import com.example.everyrunrenew.UserInfo.UserSharedPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrewInfoFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG

    String user_email;
    int crew_id; // 게시글 고유 id
    private UserSharedPreference preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    //========crew info=========
    String title;
    String content;
    String banner;
    int area;
    int member;
    int current;
    int total;
    public String reader;

    TextView tx_title, tx_area, tx_content, tx_people, tx_feed;
    ImageView img_crew;
    Button btn_join;
    ImageButton btn_setting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // 프래그먼트 뷰
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.crewinfo_fragment, container, false);
        view.requestTransparentRegion(view);
        //RequestActivity에서 전달한 번들 저장
        Bundle bundle = getArguments();
        crew_id = bundle.getInt("crew_id");
        Log.d(TAG, "onCreateView: crew_id="+crew_id);

        tx_title = view.findViewById(R.id.tx_title);
        tx_area = view.findViewById(R.id.tx_area);
        tx_content = view.findViewById(R.id.tx_content);
        tx_people = view.findViewById(R.id.tx_people);
        tx_feed = view.findViewById(R.id.tx_feed);
        img_crew = view.findViewById(R.id.crew_img);
        btn_join = view.findViewById(R.id.btn_join);
        btn_setting = ((ReadCrewActivity)getActivity()).findViewById(R.id.btn_setting);


        // db에서 크루 정보 가져오기
        preferenceHelper = new UserSharedPreference(getContext());
        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        user_email = preferenceHelper.getEmail();
        Log.d(TAG, "onCreateView: user_email ="+user_email);

        // 서버 통신하기 위한 메서드 호출
        getMysql(crew_id);

        // 가입하기 버튼 클릭 리스너
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 이 버튼이 보인다는 것은 해당 크루의 관리자도 아니고 참여자도 아니라는 뜻.
                // 가입하기 버튼을 눌렀을 때 해당 크루가 바로 가입되는 크루인지 아니면 리더 승인 후 가입되는 크루인지 구분해야 한다.
                // 구분하고 가입진행절차를 밟아야 한다.

                // (1) member = 1 바로 가입
                // (2) member = 2 리더 승인 후 가입

                // 하기 전에 현재 유저가 이 크루에 가입신청을 했는지 먼저 확인해야 한다.
                // 가입을 했는지 안했는지
                // 가입 안했다면 checkmember메서드 실행
                // 버튼 text가 가입 승인 대기중이라면 다이얼로그 나오고
                // 아니라면 checkmember로 가기
                String check = (String) btn_join.getText();
                Log.d(TAG, "onClick:btn check="+ check);
                if(check.equals("가입 승인 대기중"))
                {
                    Log.d(TAG, "btn onClick: 가입 승인 대기중");
                    // 가입 승인 대기중이니까 다이얼로그 띄워줘야 한다.
                    // (1) 다이얼로그 그냥 닫을 건지
                    // (2) 가입 신청 취소할 건지 선택해야 한다.
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("가입 신청이 완료되었습니다.\n리더가 승인하면 크루에 가입됩니다.");
                    builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setNegativeButton("신청 취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CancelJoin(user_email, crew_id);
                        }
                    });
                    builder.show();

                }else{
                    CheckMember(crew_id);
                }
            }
        });

        tx_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭시 멤버 리스트로 이동해야 한다.
                startActivity(new Intent(getActivity(), MemberListActivity.class).putExtra("crew_id", crew_id));
                // 인텐트로 크루 고유 id 같이 보내야 한다.
            }
        });

        return view;
    }

    private void CheckMember(int crew_id) {

        retrofitInterface.CheckMember(crew_id).enqueue(new Callback<CrewData>() {
            @Override
            public void onResponse(Call<CrewData> call, Response<CrewData> response) {
                CrewData result= response.body();
                Log.d(TAG, "onResponse: checkmember result = " + result);
                int member = result.getMember();
                String status = result.getStatus();

                if(status.equals("true"))
                {
                    if(member == 1)
                    {
                        /* 바로 가입할 수 있는 크루인 경우
                         바로 가입됐다는 다이얼로그와 함께 가입하기 버튼이 사라져야 한다.
                         그리고 크루원이 한명 증가해야 한다.
                         크루원 목록에서 새로 추가된 멤버 리스트를 볼 수 있어야 한다.*/
                        AddMember(crew_id, user_email);


                    }else{
                        /*리더 승인 후 가입할 수 있는 크루인 경우
                         * 가입 신청이 완료되었다는 다이얼로그가 나와야 한다.
                         * 한번더 버튼 선택했을 때 가입취소할 수 있는 다이얼로그가 나와야 한다.*/
                        AddJoin(crew_id, user_email);
                        AlertDialog.Builder acceptbuilder = new AlertDialog.Builder(getActivity());
                        acceptbuilder.setMessage("가입 신청이 완료되었습니다.\n리더가 승인하면 크루에 가입됩니다.");
                        acceptbuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // 가입 신청이 완료되었으니 버튼이 바껴야 한다.
                                btn_join.setText("가입 승인 대기중");

                            }
                        });

                        acceptbuilder.show();

                    }
                }else{
                    Log.d(TAG, "checkmember onResponse: status is false");
                }
            }

            @Override
            public void onFailure(Call<CrewData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });

    }

    // 현 크루에 가입 신청하는 메서드
    // 가입 신청하는 날짜를 보내야 한다.
    private void AddJoin(int crew_id, String user_email) {
        Log.d(TAG, "AddJoin: crew_id="+crew_id);
        Log.d(TAG, "AddJoin: user_email="+user_email);
        retrofitInterface.AddJoin(crew_id, user_email).enqueue(new Callback<CrewData>() {
            @Override
            public void onResponse(Call<CrewData> call, Response<CrewData> response) {

                CrewData result= response.body();
                Log.d(TAG, "AddJoin onResponse: result = "+ result);
                // 가입 대기중이니까 관리자가 가입 대기 중 인것을 확인할 수 있어야 한다.
            }

            @Override
            public void onFailure(Call<CrewData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    // 멤버 추가 메서드
    private void AddMember(int crew_id, String user_email) {

        Log.d(TAG, "AddMember: crew_id="+crew_id);
        Log.d(TAG, "AddMember: user_email="+user_email);

        retrofitInterface.AddMember(crew_id, user_email, 0).enqueue(new Callback<CrewData>() {
            @Override
            public void onResponse(Call<CrewData> call, Response<CrewData> response) {

                CrewData result = response.body();
                Log.d(TAG, "AddMember onResponse: result =" + result);

                String status = result.getStatus();
                if(status.equals("true"))
                {
                    AlertDialog.Builder joinbuider = new AlertDialog.Builder(getActivity());
                    joinbuider.setMessage("가입이 완료되었습니다.");
                    joinbuider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 가입이 완료되었으니 가입 버튼이 사라져야 한다.
                            // 크루원 한명 증가해야 한다.
                            Log.d(TAG, "onClick: current = " +current);
                            tx_people.setText(String.valueOf(++current));
                            btn_join.setVisibility(View.GONE);
                            // 가입이 됐으니 바로 crew_member db에 추가 되어야 한다.
                            btn_setting.setVisibility(View.VISIBLE);
                            // 설정 버튼 생기게 하기.

                        }
                    });
                    joinbuider.show();
                }



            }

            @Override
            public void onFailure(Call<CrewData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    // 가입 취소
    private void CancelJoin(String user_email, int crew_id) {
        Log.d(TAG, "CancelJoin: user_email ="+ user_email);
        Log.d(TAG, "CancelJoin: crew_id ="+ crew_id);
        retrofitInterface.CancelJoin(crew_id, user_email).enqueue(new Callback<CrewData>() {
            @Override
            public void onResponse(Call<CrewData> call, Response<CrewData> response) {
                CrewData result = response.body();
                Log.d(TAG, "CancelJoin onResponse: result="+result);
                String status = result.getStatus();
                if(status.equals("true"))
                {
                    // 크루 가입 신청이 취소되었다는 뜻
                    Toast.makeText(getActivity(), "가입 신청이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    // 취소가 되었으니까 버튼 바껴야 한다.
                    btn_join.setText("가입 하기");
                }
            }

            @Override
            public void onFailure(Call<CrewData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void getMysql(int crew_id) {


        Log.d(TAG, "getMysql: crew_id="+crew_id);
        // 크루 기본 정보들 세팅해주는 부분
        retrofitInterface.SetCrewInfo(crew_id).enqueue(new Callback<CrewData>() {
            @Override
            public void onResponse(Call<CrewData> call, Response<CrewData> response) {

                CrewData result = response.body();
                Log.d(TAG, "onResponse: result = " + result);

                // 스피너 - 지역 선택
                final String[] spinnerArea = getResources().getStringArray(R.array.area_list);

                String status = result.getStatus();
                title = result.getTitle();
                content = result.getContent();
                area = result.getArea();
                member = result.getMember();
                total = result.getTotal();
                current = result.getCurrent();
                banner = result.getBanner();
                reader = result.getReader();
                ((ReadCrewActivity)getActivity()).reader = reader;

                if (status.equals("true")) {
                    tx_title.setText(title);
                    tx_content.setText(content);
                    tx_area.setText(spinnerArea[area]);
                    tx_people.setText(String.valueOf(current));


                    // 기본이미지인 경우
                    if (banner.equals("basic")) {
                        img_crew.setImageResource(R.drawable.img1);
                    } else {
                        // 사용자가 설정한 이미지 UserProfileImg
                        String url = "http://3.36.174.137/CrewImg/" + banner;
                        // glide로 이미지 세팅해주기
                        Glide.with(getActivity()).load(url).into(img_crew);
                    }

                    // 가입하기 버튼 세팅
                    // 현 크루의 관리자 인지 아닌지 구분
                    Log.d(TAG, "setUI: reader = "+ reader);
                    Log.d(TAG, "setUI: user_email = "+user_email);
                    if(reader.equals(user_email))
                    {
                        // 관리자니까 버튼 사라져야 한다.
                        btn_join.setVisibility(View.GONE);

                    }else{
                        // 관리자가 아니다. 그럼 가입했는지 안했는지 구분해야 한다.
                        CheckJoin(user_email, crew_id);
                    }


                } else {
                    Log.d(TAG, "onResponse: set ui response fail");
                }
            }

            @Override
            public void onFailure(Call<CrewData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });

    }

    // 현 유저는 관리자가 아니라 참여자 입장일때 들어오는 메서드
    // 해당 유저가 현 크루에 가입했는지 안했는지 확인하는 메서드
    private void CheckJoin(String user_email, int crew_id) {

        Log.d(TAG, "CheckJoin: user_email =" + user_email);
        Log.d(TAG, "CheckJoin: crew_id = "+ crew_id);
        retrofitInterface.CheckJoin(crew_id, user_email).enqueue(new Callback<CrewData>() {
            @Override
            public void onResponse(Call<CrewData> call, Response<CrewData> response) {

                CrewData result = response.body();
                Log.d(TAG, "onResponse: checkjoin result =" + result);

                String status = result.getStatus();

                if(status.equals("true"))
                {
                    // 해당 크루의 참여자라는 뜻
                    // 참여자라면 가입하기 버튼이 없어야 한다.
                    btn_join.setVisibility(View.GONE);
                    // 참여자는 설정 버튼이 있어야 한다.
                }else{

                    // 참여하지 않았는데 가입 신청 중 상태인 지 구분
                    // status = Resister 가입 신청 중이고 승인 대기중이다
                    // status = false 가입 하지 않았다는 뜻.
                    if(status.equals("false"))
                    {
                        // 해당 크루에 참여하지 않는다는 뜻
                        btn_join.setVisibility(View.VISIBLE);


                    }else{
                        // 현재 가입 승인 대기중이라는 것.
                        btn_join.setText("가입 승인 대기중");

                    }




                }
            }

            @Override
            public void onFailure(Call<CrewData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }



}
