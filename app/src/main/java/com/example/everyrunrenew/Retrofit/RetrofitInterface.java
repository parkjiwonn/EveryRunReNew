package com.example.everyrunrenew.Retrofit;



import com.example.everyrunrenew.RetrofitData.CommentData;
import com.example.everyrunrenew.RetrofitData.CrewData;
import com.example.everyrunrenew.RetrofitData.FeedData;
import com.example.everyrunrenew.RetrofitData.FinalRunningData;
import com.example.everyrunrenew.RetrofitData.JoinData;
import com.example.everyrunrenew.RetrofitData.UserInfoData;
import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitInterface {

    String RETROFIT_URL = "http://3.36.174.137/";

    //===========================USER INFO=========================================

    // 이메일 중복 검사
    @FormUrlEncoded
    @POST("UserInfo/CheckEmail.php")
    Call<UserInfoData> CheckEmail(
            @Field("user_email") String user_email
    );

    // ID 중복 검사
    @FormUrlEncoded
    @POST("UserInfo/CheckId.php")
    Call<UserInfoData> CheckId(
            @Field("user_id") String user_id
    );

    // 회원가입시 - 유저 정보 저장
    @FormUrlEncoded
    @POST("UserInfo/SaveUserInfo.php")
    Call<UserInfoData> SaveUserInfo(
            @Field("user_email") String user_email,
            @Field("user_pass") String user_pass,
            @Field("user_nick") String user_nick,
            @Field("user_height") int user_height,
            @Field("user_weight") int user_weight
    );

    // 로그인 - 유저정보 맞는지 확인
    @FormUrlEncoded
    @POST("UserInfo/Login.php")
    Call<UserInfoData> Login(
            @Field("user_email") String user_email,
            @Field("user_pass") String user_pass
    );

    // 비밀번호 재설정
    @FormUrlEncoded
    @POST("UserInfo/ResetPass.php")
    Call<UserInfoData> ResetPass(
            @Field("user_email") String user_email,
            @Field("user_pass") String user_pass
    );

    // 유저 정보 세팅
    @FormUrlEncoded
    @POST("UserInfo/SetUserInfo.php")
    Call<UserInfoData> SetUserInfo(
            @Field("user_email") String user_email
    );

    // 유저 키 몸무게 세팅
    @FormUrlEncoded
    @POST("UserInfo/SetUserBMI.php")
    Call<UserInfoData> SetUserBMI(
            @Field("user_email") String user_email
    );

    // 유저 bmi 수정하기
    @FormUrlEncoded
    @POST("UserInfo/UpdateUserBmi.php")
    Call<UserInfoData> UpdateUserBmi(
            @Field("user_email") String user_email,
            @Field("user_height") String user_height,
            @Field("user_weight") String user_weight
    );


    //===========================USER INFO=========================================

    //===========================Update User Info==================================
    // 프로필 사진 업로드 할 때 - 카메라, 갤러리로 사진 수정
    @Multipart
    @POST("UserProfile/WithPic.php")
    Call<UserInfoData> WithPic(
            @Part MultipartBody.Part uploaded_file,
            @Query("user_nick") String nick,
            @Query("user_email") String user_email
    );



    // 프로필 사진 업로드 할 때 - 기본이미지로 사진 수정
    @FormUrlEncoded
    @POST("UserProfile/NoPicOrJustBasic.php")
    Call<UserInfoData> NoPicOrJustBasic(
            @Field("user_email") String user_email,
            @Field("user_nick") String user_nick,
            @Field("option") Integer option


    );


    //===========================Update User Info==================================

    //===========================Create Create Crew=======================================

    // 기본이미지로 선택해 크로 개설할 때
    @FormUrlEncoded
    @POST("Crew/UploadWithBasic.php")
    Call<CrewData> Uploadwithbasic(
            @Field("title") String title,
            @Field("content") String content,
            @Field("area") Integer area,
            @Field("member") Integer member,
            @Field("total") Integer total,
            @Field("banner") String banner,
            @Field("email") String email

    );

    // 갤러리, 카메라로 이미지 선택해 크루 개설할 때
    @Multipart
    @POST("Crew/UploadWithPic.php")
    Call<CrewData> Uploadwithpic(
            @Part MultipartBody.Part uploaded_file,
            @Query("title") String title,
            @Query("content") String content,
            @Query("area") Integer area,
            @Query("member") Integer member,
            @Query("total") Integer total,
            @Query("email") String email
    );


    //===============================================================================

    //===========================Read Create Crew=======================================

    // 크루 기본 정보 세팅
    @FormUrlEncoded
    @POST("Crew/SetCrewInfo.php")
    Call<CrewData> SetCrewInfo(
            @Field("crew_id") int crew_id

    );

    // 크루 가입여부 확인
    @FormUrlEncoded
    @POST("Join/CheckJoin.php")
    Call<CrewData> CheckJoin(
            @Field("crew_id") int crew_id,
            @Field("user_email") String user_email

    );

    // 크루 가입 방법 확인
    @FormUrlEncoded
    @POST("Join/CheckMember.php")
    Call<CrewData> CheckMember(
            @Field("crew_id") int crew_id
    );

    // 크루 바로 가입
    @FormUrlEncoded
    @POST("Join/AddMember.php")
    Call<CrewData> AddMember(
            @Field("crew_id") int crew_id,
            @Field("user_email") String user_email,
            @Field("option") int option

    );

    // 크루 가입 신청하기
    @FormUrlEncoded
    @POST("Join/AddJoin.php")
    Call<CrewData> AddJoin(
            @Field("crew_id") int crew_id,
            @Field("user_email") String user_email
    );

    // 크루 가입 신청 취소하기
    @FormUrlEncoded
    @POST("Join/CancelJoin.php")
    Call<CrewData> CancelJoin(
            @Field("crew_id") int crew_id,
            @Field("user_email") String user_email
    );

    // 크루 멤버 리스트 불러오기
    @GET("Crew/MemberList.php")
    Call<List<UserInfoData>> MemberList(
            @Query("crew_id") int crew_id // 크루 고유 id
            // 크루 id를 보내서 해당 크루에 해당하는 모든 멤버리스트 불러오기

    );

    // 크루 탈퇴하기
    @FormUrlEncoded
    @POST("Crew/OutCrew.php")
    Call<CrewData> OutCrew(
            @Field("user_email") String user_email,
            @Field("crew_id") int crew_id
    );



    //====================================================================================

    //=================================Join Crew 가입 신청 관리================================

    // 가입 신청 리스트 불러오기
   @GET("Join/JoinList.php")
    Call<List<JoinData>> JoinList(
            @Query("crew_id") int crew_id // 크루 고유 id
            // 크루 id를 보내서 해당 크루에 해당하는 모든 멤버리스트 불러오기

    );

    // 크루 가입 신청 갯수 세기
    @FormUrlEncoded
    @POST("Join/SetJoinNum.php")
    Call<JoinData> SetJoinNum(
            @Field("crew_id") int crew_id
    );

    // 크루 가입 신청 취소했는지 확인하기
    @FormUrlEncoded
    @POST("Join/CheckCancel.php")
    Call<JoinData> CheckCancel(
            @Field("join_id") int join_id
    );

    // 크루 가입 신청 거절하기
    @FormUrlEncoded
    @POST("Join/DenyJoin.php")
    Call<JoinData> DenyJoin(
            @Field("join_id") int join_id
    );




    //=======================================================================================

    //===================================Crew- Change Reader=================================

    // 크루 멤버 리스트 불러오기
    @GET("Crew/CandidateList.php")
    Call<List<UserInfoData>> CandidateList(
            @Query("crew_id") int crew_id // 크루 고유 id
            // 크루 id를 보내서 해당 크루에 해당하는 모든 멤버리스트 불러오기

    );

    // 크루 가입 신청 거절하기
    @FormUrlEncoded
    @POST("Crew/ChangeLeader.php")
    Call<UserInfoData> ChangeLeader(
            @Field("crew_id") int crew_id,
            @Field("user_email") String user_email,
            @Field("now_user_email") String now_user_email

    );

    //=======================================================================================


    //===========================Update & Delete Crew=========================================

    // 이미지 선택 X & 기본 이미지로 변경
    @FormUrlEncoded
    @POST("Crew/NoPicOrJustBasic.php")
    Call<CrewData> NoPicOrJustBasic(
            @Field("crew_id") int crew_id,
            @Field("title") String title,
            @Field("content") String content,
            @Field("total") int total,
            @Field("area") int area,
            @Field("member") int member,
            @Field("option") int option

            //int crew_id, String title, String content, int total, int area, int member, int i

    );

    // 카메라 / 갤러리 에서 이미지 선택
    @Multipart
    @POST("Crew/CameraOrGallery.php")
    Call<CrewData> CameraOrGallery(
            @Part MultipartBody.Part uploaded_file,
            @Query("crew_id") int crew_id,
            @Query("title") String title,
            @Query("content") String content,
            @Query("total") int total,
            @Query("area") int area,
            @Query("member") int member

            //int crew_id, String title, String content, int total, int area, int member, String imagePath

    );

    //====================================================================================

    //==================================Community 정보====================================

    // 내 크루 리스트 불러오는 것
    @GET("Crew/MyCrewList.php")
    Call<List<CrewData>> MyCrewList(
            @Query("now_user_email") String now_user_email

    );

    // 모든 크루 리스트 불러오는 것
    @GET("Crew/TotalCrewList.php")
    Call<List<CrewData>> TotalCrewList(
            @Query("now_user_email") String now_user_email

    );

    // 랜덤한 4개의 크루 리스트 불러오는 것.
    @GET("Crew/RandomTotalCrewList.php")
    Call<List<CrewData>> RandomTotalCrewList(
            @Query("now_user_email") String now_user_email

    );

    //====================================================================================
    //================================RunningData==================================
    @FormUrlEncoded
    @POST("RunningData/SaveRunningData.php")
    Call<List<FinalRunningData>> SaveRunningData(

            @Field("User_email") String User_email,
            @Field("Date") String Date,
            @Field("StartTime") String StartTime,
            @Field("FinishTime") String FinishTime,
            @Field("distance") String distance,
            @Field("face") String face,
            @Field("time") String time,
            @Field("kcal") String kcal,
            @Field("altitude") String altitude,
            @Field("locationarray[]") ArrayList<LatLng> locationarray,
            @Field("cadence") String cadence,
            @Field("FacePerKmArraylist[]") ArrayList<LatLng> FacePerKmArraylist,
            @Field("FaceArraylist[]") ArrayList<String> FaceArraylist,
            @Field("FaceDoublelist[]") ArrayList<Double> FaceDoublelist,
            @Field("MaxHeight") String MaxHeight,
            @Field("MinHeight") String MinHeight,
            @Field("MinPace") String MinPace

    );


    //====================================================================================

    //==================================Feed 관련==========================================

    // 사진 없이 게시글 업로드
    @FormUrlEncoded
    @POST("Feed/UploadFeedWithoutPhoto.php")
    Call<FeedData> UploadFeedWithoutPhoto(
            @Field("user_email") String user_email,
            @Field("date") long date,
            @Field("content") String content,
            @Field("crew_id") int crew_id


    );

    // 사진과 게시글 업로드
    @Multipart
    @POST("Feed/UploadFeedWithPhoto.php")
    Call<String> UploadFeedWithPhoto(
            @Part ArrayList<MultipartBody.Part> files,
            @Query("content") String content,
            @Query("date") long date,
            @Query("user_email") String user_email,
            @Query("crew_id") int crew_id

    );

    // 피드 리스트 불러오기
    @GET("Feed/SetFeedList.php")
    Call<List<FeedData>> SetFeedList(
            @Query("user_email") String user_email,
            @Query("crew_id") int crew_id

    );

    @FormUrlEncoded
    @POST("Feed/SetFeedData.php")
    Call<FeedData> SetFeedData(
            @Field("feed_id") int feed_id
    );

    //다이어리 수정 - 사진 아예 갈아엎은 경우
    @Multipart
    @POST("Feed/UpdateFeed.php")
    Call<String> UpdateFeed(
            @Part ArrayList<MultipartBody.Part> files,
            @Query("content") String content,
            @Query("user_email") String user_email,
            @Query("date") long date,
            @Query("feed_id") int feed_id,
            @Query("crew_id") int crew_id
    );

    // 다이어리 수정 (사진이 섞여있는 경우 )
    @Multipart
    @POST("Feed/UpdateMixed.php")
    Call<String> UpdateMixed(
            @Part ArrayList<MultipartBody.Part> files, // 경로 포함하고 있는 사진들
            @Query("photo_list[]") ArrayList<String> photo_list,
            @Query("content") String content, //수정 내용
            @Query("date") long date, // 수정 시간
            @Query("feed_id") int feed_id, // 피드 id
            @Query("crew_id") int crew_id, // 크루 id
            @Query("user_email") String user_email // 사진 db에 저장할 때 쓸 유저 이메일

    );

    // 다이어리 사진 없이 수정하기.
    @FormUrlEncoded
    @POST("Feed/UpdateWithoutPhoto.php")
    Call<FeedData> UpdateWithoutPhoto(
            @Field("content") String content,
            @Field("date") long date,
            @Field("feed_id") int feed_id,
            @Field("crew_id") int crew_id,
            @Field("check") int check


    );

    // feed 좋아요
    @FormUrlEncoded
    @POST("Feed/favorite.php")
    Call<FeedData> clickfavorite(
            @Field("feed_id") int feed_id, // 게시글 고유 id
            @Field("user_email") String user_email, // 좋아요 누른 유저 이메일
            @Field("status") boolean status

    );

    // feed 삭제
    @FormUrlEncoded
    @POST("Feed/DeleteFeed.php")
    Call<FeedData> DeleteFeed(
            @Field("feed_id") int feed_id

    );

    //====================================================================================

    //======================================댓글 작성=======================================
    //댓글 작성
    @FormUrlEncoded
    @POST("Feed/SendComment.php")
    Call<CommentData> SendComment(
            @Field("content") String content,
            @Field("date") long date,
            @Field("feed_id") int feed_id,
            @Field("user_email") String user_email

    );

    // 댓글 리스트 불러오기
    @GET("Feed/commentlist.php")
    Call<List<CommentData>> commentlist(
            @Query("feed_id") int feed_id, //게시글 id
            @Query("now_user_email") String now_user_email

    );

    // 댓글 좋아요 선택
    // 유저가 댓글의 좋아요를 선택했을때 댓글의 좋아요 갯수가 1만큼 상승해야 되며 db에도 저장이 되어야 함.
    // 현재 댓글의 id, 좋아요 누른 유저의 이메일 보내야함.
    @FormUrlEncoded
    @POST("Feed/commentlike.php")
    Call<CommentData> clickcommentlike(
            @Field("now_user_email") String now_user_email,
            @Field("comment_id") int comment_id,
            @Field("status") boolean status

    );

    // 댓글 수정
    @FormUrlEncoded
    @POST("Feed/UpdateComment.php")
    Call<CommentData> UpdateComment(
            @Field("content") String content, // 댓글 수정 내용
            @Field("comment_id") int id // 댓글 id

    );

    // 댓글 삭제
    @FormUrlEncoded
    @POST("Feed/DeleteComment.php")
    Call<CommentData> DeleteComment(
            @Field("comment_id") int id, // 댓글 id
            @Field("feed_id") int feed_id // 댓글 id

    );


}
