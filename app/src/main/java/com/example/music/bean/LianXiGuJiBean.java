package com.example.music.bean;

public class LianXiGuJiBean {
    private String name;
    private String path;
    private boolean isSelected;

    public LianXiGuJiBean(String name, String path, boolean isSelected) {
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
