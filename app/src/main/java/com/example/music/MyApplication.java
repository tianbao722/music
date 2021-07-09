package com.example.music;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import java.io.File;


/**
 * @author admin
 */
public class MyApplication extends Application {
    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public static MyApplication getInstance() {
        return myApplication;
    }

    public static Context context() {
        return myApplication.getApplicationContext();
    }

    //获取图片曲谱文件
    public static File getTuPianYuePuFile() {
        File tupianyuepu = new File(myApplication.getExternalFilesDir(null), Constants.TuPianYuePu);
        return tupianyuepu;
    }
}
