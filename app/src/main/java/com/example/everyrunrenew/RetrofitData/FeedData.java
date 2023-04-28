package com.example.everyrunrenew.RetrofitData;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FeedData {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("feed_id")
    private int feed_id;

    @SerializedName("user_nick")
    private String user_nick;

    @SerializedName("user_email")
    private String user_email;

    @SerializedName("user_profile")
    private String user_profile;

    @SerializedName("content")
    private String content;

    @SerializedName("favorite")
    private int favorite;

    @SerializedName("comment")
    private int comment;

    @SerializedName("crew_id")
    private int crew_id;


    @SerializedName("date")
    private long date;

    @SerializedName("photolist")
    private ArrayList<String> photolist;

    public FeedData(int feed_id, String user_nick, String user_email, String user_profile, String content, int favorite, int comment, long date, ArrayList<String> photolist) {

        this.feed_id = feed_id;
        this.user_nick = user_nick;
        this.user_email = user_email;
        this.user_profile = user_profile;
        this.content = content;
        this.favorite = favorite;
        this.comment = comment;
        this.date = date;
        this.photolist = photolist;
    }

    public int getCrew_id() {
        return crew_id;
    }

    public void setCrew_id(int crew_id) {
        this.crew_id = crew_id;
    }

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

    public int getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(int feed_id) {
        this.feed_id = feed_id;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<String> getPhotolist() {
        return photolist;
    }

    public void setPhotolist(ArrayList<String> photolist) {
        this.photolist = photolist;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +

                "content=" + content +
                ", status=" + status +
                ", message=" + message +
                ", user_nick=" + user_nick +
                ", user_profile=" + user_profile +
                ", favorite=" + favorite +
                ", comment=" + comment +
                ", photolist =" +photolist+
                ", user_email=" + user_email +
                ", feed_id=" + feed_id +
                ", date=" + date +
                ", crew_id=" + crew_id +
                '}';
    }

}
