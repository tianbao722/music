package com.example.music.bean;

import java.util.ArrayList;

public class BanZouListBean {
    private ArrayList<BanZouBean> list;

    public BanZouListBean(ArrayList<BanZouBean> list) {
        this.list = list;
    }

    public ArrayList<BanZouBean> getList() {
        return list;
    }

    public void setList(ArrayList<BanZouBean> list) {
        this.list = list;
    }
}
