package com.example.music.bean;

import android.graphics.drawable.Drawable;

public class ZengZhiFuWuBean {
    private String name;
    private String pickName;
    private Drawable icon;

    public ZengZhiFuWuBean(String name, String pickName, Drawable icon) {
        this.name = name;
        this.pickName = pickName;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPickName() {
        return pickName;
    }

    public void setPickName(String pickName) {
        this.pickName = pickName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
