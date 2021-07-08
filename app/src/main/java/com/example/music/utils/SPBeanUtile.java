package com.example.music.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.Constants;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.SPListBean;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SPBeanUtile {
    //创建图片乐谱文件夹
    public static boolean createTuPiQuPuFile(String string) {
        File tuPianYuePuFile = MyApplication.getTuPianYuePuFile();
        File file = new File(tuPianYuePuFile, string);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (orExistsDir) {
            return true;
        } else {
            return false;
        }
    }

    //获取图片乐谱文件夹名字集合
    public static ArrayList<BenDiYuePuBean> getTuPianQuPuFileList() {
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = new ArrayList<>();
        File tuPianYuePuFile = MyApplication.getTuPianYuePuFile();
        boolean orExistsDir = FileUtils.createOrExistsDir(tuPianYuePuFile);
        if (orExistsDir) {//判断目录是否存在,不存在判断是否创建成功
            List<File> files = FileUtils.listFilesInDir(tuPianYuePuFile);
            if (files != null && files.size() > 0) {
                for (int i = 0; i < files.size(); i++) {
                    if (i == 0) {
                        String fileNameNoExtension = FileUtils.getFileNameNoExtension(files.get(i));
                        benDiYuePuBeans.add(new BenDiYuePuBean(fileNameNoExtension, true));
                    } else {
                        String fileNameNoExtension = FileUtils.getFileNameNoExtension(files.get(i));
                        benDiYuePuBeans.add(new BenDiYuePuBean(fileNameNoExtension, false));
                    }
                }
                return benDiYuePuBeans;
            }
        }
        return null;
    }
}
