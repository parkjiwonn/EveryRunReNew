package com.example.everyrunrenew.RetrofitData;

import com.google.gson.annotations.SerializedName;

public class CommentData {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("nick")
    private String nick;

    @SerializedName("profile")
    private String profile;

    @SerializedName("comment_id")
    private int comment_id;

    @SerializedName("content")
    private String content;

    @SerializedName("date")
    private long date;

    @SerializedName("favorite")
    private int favorite;

    @SerializedName("feed_id") // 게시글 id
    private int feed_id;

    @SerializedName("user_email")
    private String user_email;

    @SerializedName("top")
    private int top;

    @SerializedName("level")
    private int level;

    @SerializedName("child")
    private int child;

    @SerializedName("cnt")
    private int cnt;

    @SerializedName("viewType")
    private int viewType;

    @SerializedName("NowLoginUser")
    private String NowLoginUser;

    @SerializedName("del")
    private int del;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(int feed_id) {
        this.feed_id = feed_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getNowLoginUser() {
        return NowLoginUser;
    }

    public void setNowLoginUser(String nowLoginUser) {
        NowLoginUser = nowLoginUser;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +
                "comment_id=" + comment_id +
                ", status =" + status +
                ", message =" + message + // 좋아요 선택 여부 확인
                ", nick =" + nick +
                ", profile =" + profile +
                ", content=" + content +
                ", date=" + date +
                ", favorite=" + favorite + // 좋아요 갯수
                ", feed_id=" + feed_id +
                ", NowLoginUser =" + NowLoginUser + // 현재 로그인한 유저
                ", user_email =" + user_email +
                ", top =" + top +
                ", level=" + level +
                ", child =" + child +
                ", viewType =" + viewType +
                ", del =" + del + // 자식댓글이 다 삭제가 되었는지 안되었는지 확인 하기 위함.
                ", cnt =" + cnt + // 해당 다이어리 게시글 댓글 갯수

                '}';
    }

}
