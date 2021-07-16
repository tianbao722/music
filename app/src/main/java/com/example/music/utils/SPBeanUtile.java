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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SPBeanUtile {

    //在图片曲谱文件夹下创建文件夹
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

    //在Def曲谱文件夹下创建文件夹
    public static boolean createDefQuPuFile(String string) {
        File tuPianYuePuFile = MyApplication.getDefYuePuFile();
        File file = new File(tuPianYuePuFile, string);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (orExistsDir) {
            return true;
        } else {
            return false;
        }
    }

    //在我的音乐文件夹下创建文件夹
    public static boolean createWoDeYinYueFile(String string) {
        File tuPianYuePuFile = MyApplication.getWoDeYinYueFile();
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

    //获取Def乐谱文件夹名字集合
    public static ArrayList<BenDiYuePuBean> getDefQuPuFileList() {
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = new ArrayList<>();
        File tuPianYuePuFile = MyApplication.getDefYuePuFile();
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

    //获取我的音乐文件夹名字集合
    public static ArrayList<BenDiYuePuBean> getWoDeYinYueFileList() {
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = new ArrayList<>();
        File tuPianYuePuFile = MyApplication.getWoDeYinYueFile();
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

    //获取节奏训练文件夹名字集合
    public static ArrayList<BenDiYuePuBean> getJieZouXunLianFileList() {
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = new ArrayList<>();
        File tuPianYuePuFile = MyApplication.getJieZouXunLianFile();
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
