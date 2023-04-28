package com.example.everyrunrenew.Community.Feed.Adapter;

public class Photo {
    private int path;

    public Photo(int path) {
        this.path = path;
    }

    public int getPath() {
        return path;
    }

    public void setPath(int path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "path=" + path +
                '}';
    }
}
