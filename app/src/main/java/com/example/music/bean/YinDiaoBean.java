package com.example.music.bean;

public class YinDiaoBean {
    private String yindiao;
    private float yindiaozhi;
    private boolean selecte;

    public YinDiaoBean(String yindiao, float yindiaozhi, boolean selecte) {
        this.yindiao = yindiao;
        this.yindiaozhi = yindiaozhi;
        this.selecte = selecte;
    }

    public String getYindiao() {
        return yindiao;
    }

    public void setYindiao(String yindiao) {
        this.yindiao = yindiao;
    }

    public float getYindiaozhi() {
        return yindiaozhi;
    }

    public void setYindiaozhi(float yindiaozhi) {
        this.yindiaozhi = yindiaozhi;
    }

    public boolean isSelecte() {
        return selecte;
    }

    public void setSelecte(boolean selecte) {
        this.selecte = selecte;
    }
}
