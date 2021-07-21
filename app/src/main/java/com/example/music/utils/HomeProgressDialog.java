package com.example.music.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class HomeProgressDialog extends ProgressDialog {
    private static HomeProgressDialog instance;

    private HomeProgressDialog(Context context) {
        super(context);
    }

    public static HomeProgressDialog getInstance(Context context) {
        if (instance == null) {
            instance = new HomeProgressDialog(context);
        }
        return instance;
    }
}
