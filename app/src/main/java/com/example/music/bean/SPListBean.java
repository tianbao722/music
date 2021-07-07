package com.example.music.bean;

import java.util.ArrayList;

public class SPListBean {
    private ArrayList<BenDiYuePuBean> list;

    public SPListBean(ArrayList<BenDiYuePuBean> list) {
        this.list = list;
    }

    public ArrayList<BenDiYuePuBean> getList() {
        return list;
    }

    public void setList(ArrayList<BenDiYuePuBean> list) {
        this.list = list;
    }
}
