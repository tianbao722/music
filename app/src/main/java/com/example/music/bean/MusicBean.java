package com.example.music.bean;

public class MusicBean {
    private String name;
    private long time;
    private String size;
    private String path;

    public MusicBean(String name, long time, String size, String path) {
        this.name = name;
        this.time = time;
        this.size = size;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
