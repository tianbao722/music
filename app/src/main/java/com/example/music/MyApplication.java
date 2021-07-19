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
import android.os.Environment;
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

    public boolean getGenMuLu() {
        //首先判断外部存储是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            Log.e("qq", "sd = " + sd);//sd = /storage/emulated/0
            return sd.canWrite();
        } else {
            return false;
        }
    }

    //获取图片曲谱文件
    public static File getTuPianYuePuFile() {
        File tupianyuepu = new File(myApplication.getExternalFilesDir(null), Constants.TuPianYuePu);
        return tupianyuepu;
    }

    //获取临时文件
    public static File getLinShiPuFile() {
        File tupianyuepu = new File(myApplication.getExternalFilesDir(null), Constants.LinShi);
        return tupianyuepu;
    }

    //获取我的音乐文件
    public static File getWoDeYinYueFile() {
        File tupianyuepu = new File(myApplication.getExternalFilesDir(null), Constants.WoDeYinYue);
        return tupianyuepu;
    }

    //获取Def曲谱文件
    public static File getDefYuePuFile() {
        File tupianyuepu = new File(myApplication.getExternalFilesDir(null), Constants.Def);
        return tupianyuepu;
    }

    //获取节奏训练歌曲文件
    public static File getJieZouXunLianFile() {
        File tupianyuepu = new File(myApplication.getExternalFilesDir(null), Constants.JieZouXunLian);
        return tupianyuepu;
    }


}
