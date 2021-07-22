package com.example.music.bean;

public class LianXiGuJiBean {
    private String name;
    private int raw;
    private boolean isSelected;

    public LianXiGuJiBean(String name, int raw, boolean isSelected) {
        this.name = name;
        this.raw = raw;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPath() {
        return raw;
    }

    public void setPath(int raw) {
        this.raw = raw;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
