package com.example.music.bean;

import java.io.File;

public class BanZouBean {
    private String name;
    private String path;

    public BanZouBean(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
