package com.example.everyrunrenew.Running;

public class HardCord {
    // 날짜
    // 제목
    // 이동거리
    // 소요 시간
    // 평균 페이스

    String date;
    String title;
    String distance;
    String time;
    String pace;

    public HardCord(String date, String title, String distance, String time, String pace) {
        this.date = date;
        this.title = title;
        this.distance = distance;
        this.time = time;
        this.pace = pace;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }
}
