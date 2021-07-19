package com.example.music.bean;

import java.util.ArrayList;

public class UrlImageListBean {
    private ArrayList<String> list;

    public UrlImageListBean(ArrayList<String> list) {
        this.list = list;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }
}
