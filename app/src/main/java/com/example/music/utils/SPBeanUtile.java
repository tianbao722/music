package com.example.music.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
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
import com.example.music.bean.LianXiGuJiBean;
import com.example.music.bean.MusicBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SPBeanUtile {

    //在图片曲谱文件夹下创建文件夹
    public static boolean createTuPiQuPuFile(String string) {
        File tuPianYuePuFile = MyApplication.getTuPianYuePuFile();
        boolean orExistsDir1 = FileUtils.createOrExistsDir(tuPianYuePuFile);
        if (orExistsDir1) {
            File file = new File(tuPianYuePuFile, string);
            boolean orExistsDir = FileUtils.createOrExistsDir(file);
            if (orExistsDir) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //在Def曲谱文件夹下创建文件夹
    public static boolean createDefQuPuFile(String string) {
        File tuPianYuePuFile = MyApplication.getDefYuePuFile();
        boolean orExistsDir1 = FileUtils.createOrExistsDir(tuPianYuePuFile);
        if (orExistsDir1) {
            File file = new File(tuPianYuePuFile, string);
            boolean orExistsDir = FileUtils.createOrExistsDir(file);
            if (orExistsDir) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //在我的音乐文件夹下创建文件夹
    public static boolean createWoDeYinYueFile(String string) {
        File tuPianYuePuFile = MyApplication.getWoDeYinYueFile();
        boolean orExistsDir1 = FileUtils.createOrExistsDir(tuPianYuePuFile);
        if (orExistsDir1) {
            File file = new File(tuPianYuePuFile, string);
            boolean orExistsDir = FileUtils.createOrExistsDir(file);
            if (orExistsDir) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //在动态乐谱文件夹下创建文件夹
    public static boolean createDongTaiYuePuFile(String string) {
        File tuPianYuePuFile = MyApplication.getDongTaiYuePuFile();
        boolean orExistsDir1 = FileUtils.createOrExistsDir(tuPianYuePuFile);
        if (orExistsDir1) {
            File file = new File(tuPianYuePuFile, string);
            boolean orExistsDir = FileUtils.createOrExistsDir(file);
            if (orExistsDir) {
                return true;
            } else {
                return false;
            }
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

    //获取动态乐谱文件夹名字集合
    public static ArrayList<BenDiYuePuBean> getDongTaiYuePuFileList() {
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = new ArrayList<>();
        File tuPianYuePuFile = MyApplication.getDongTaiYuePuFile();
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

    //获取所有文件夹下的音乐
    public static ArrayList<MusicBean> getAllMusic() {
        ArrayList<MusicBean> musicBeans = new ArrayList<>();
        ArrayList<BenDiYuePuBean> mList = SPBeanUtile.getWoDeYinYueFileList();
        String name = null;
        if (mList != null && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                String title = mList.get(i).getTitle();
                String path = MyApplication.getWoDeYinYueFile().getPath();
                String currentPath = path + "/" + title;
                List<File> files = FileUtils.listFilesInDir(currentPath);
                for (int j = 0; j < files.size(); j++) {
                    String path1 = files.get(j).getPath();
                    String fileName = FileUtils.getFileName(files.get(j));
                    if (fileName.length() >= 4) {
                        name = fileName.substring(0, fileName.length() - 4);
                    } else {
                        name = fileName;
                    }
                    String size = FileUtils.getSize(files.get(j));
                    MusicBean musicBean = new MusicBean(name, 0, size, path1);
                    musicBeans.add(musicBean);
                }
            }
            return musicBeans;
        } else {
            return null;
        }

    }

    //获取所有文件夹下的动态乐谱
    public static ArrayList<MusicBean> getAllVideo() {
        ArrayList<MusicBean> musicBeans = new ArrayList<>();
        ArrayList<BenDiYuePuBean> mList = SPBeanUtile.getDongTaiYuePuFileList();
        if (mList != null && mList.size() > 0) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            for (int i = 0; i < mList.size(); i++) {
                String title = mList.get(i).getTitle();
                String path = MyApplication.getDongTaiYuePuFile().getPath();
                String currentPath = path + "/" + title;
                List<File> files = FileUtils.listFilesInDir(currentPath);
                for (int j = 0; j < files.size(); j++) {
                    String path1 = files.get(j).getPath();
                    mmr.setDataSource(path1);
                    String fileName = FileUtils.getFileName(files.get(j));
                    String name = fileName.substring(0, fileName.length() - 4);
                    String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    String size = FileUtils.getSize(files.get(j));
                    long time1 = Long.parseLong(time);
                    MusicBean musicBean = new MusicBean(name, time1, size, path1);
                    musicBeans.add(musicBean);
                }
            }
            return musicBeans;
        } else {
            return null;
        }
    }

    //获取练习鼓机文件夹下的所有鼓机音乐
    public static ArrayList<MusicBean> getAllGuJi() {
        ArrayList<MusicBean> musicBeans = new ArrayList<>();
        File tuPianYuePuFile = MyApplication.getLianXiGuJiPuFile();
        boolean orExistsDir = FileUtils.createOrExistsDir(tuPianYuePuFile);
        if (orExistsDir) {
            List<File> files = FileUtils.listFilesInDir(tuPianYuePuFile);
            for (int j = 0; j < files.size(); j++) {
                String path1 = files.get(j).getPath();
                String fileName = FileUtils.getFileName(files.get(j));
                String name = fileName.substring(0, fileName.length() - 4);
                MusicBean musicBean = new MusicBean(name, 0, null, path1);
                musicBeans.add(musicBean);
            }
            return musicBeans;
        } else {
            return null;
        }
    }
}
