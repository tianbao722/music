package com.example.music.bean;

import java.io.File;
import java.util.List;

public class ImageYuePuImageBean {
    private String name;
    private List<File> list;
    private int content;


    public ImageYuePuImageBean(String name, List<File> list, int content) {
        this.name = name;
        this.list = list;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<File> getList() {
        return list;
    }

    public void setList(List<File> list) {
        this.list = list;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }
}
