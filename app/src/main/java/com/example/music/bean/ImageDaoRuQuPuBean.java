package com.example.music.bean;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageDaoRuQuPuBean {
    private String url;
    private Bitmap bitmap;
    private Uri uri;

    public ImageDaoRuQuPuBean(String url, Bitmap bitmap, Uri uri) {
        this.url = url;
        this.bitmap = bitmap;
        this.uri = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
