package com.example.music.bean;

import java.io.File;
import java.util.List;

public class ImageYuePuImageBean {
    private String name;//名字
    private List<File> list;//图片集合
    private int content;//数量
    private String path;//文件夹路径

    public ImageYuePuImageBean(String name, List<File> list, int content) {
        this.name = name;
        this.list = list;
        this.content = content;
    }

    public ImageYuePuImageBean(String name, List<File> list, int content, String path) {
        this.name = name;
        this.list = list;
        this.content = content;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
