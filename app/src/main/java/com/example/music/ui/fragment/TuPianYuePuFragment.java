package com.example.music.ui.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.RecImageYuePuAdapter;
import com.example.music.adapter.TuPianYuePuAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.ui.activity.ImageActivity;
import com.example.music.utils.SPBeanUtile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TuPianYuePuFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private ArrayList<BenDiYuePuBean> strings;
    private RecyclerView mRecTuPianYuePu;
    private RecyclerView mRecImageYuePu;
    private TextView mTvXinZheng;
    private boolean classify = false;
    private TuPianYuePuAdapter tuPianYuePuAdapter;
    private int mPosition;//当前选择的Title的下标
    private ArrayList<ImageYuePuImageBean> imageFileList;
    private RecImageYuePuAdapter recImageYuePuAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.tu_pian_yue_pu_fragment, container, false);
        initView(inflate);
        return inflate;
    }


    private void initView(View inflate) {
        mContext = getActivity();
        mRecTuPianYuePu = inflate.findViewById(R.id.rec_tupianyuepy);
        mRecImageYuePu = inflate.findViewById(R.id.rec_image_yuepu);
        mTvXinZheng = inflate.findViewById(R.id.tv_xinzeng);
        mTvXinZheng.setOnClickListener(this);
        //初始化图片乐谱左边title
        initRecTuPianYuePu();
        //初始化图片乐谱右边图片
        initRecImageYuePu();
    }

    private void initRecImageYuePu() {
        mRecImageYuePu.setLayoutManager(new GridLayoutManager(mContext, 2));
        imageFileList = getImageFileList();
        recImageYuePuAdapter = new RecImageYuePuAdapter(imageFileList, mContext);
        mRecImageYuePu.setAdapter(recImageYuePuAdapter);
        //条目点击事件
        recImageYuePuAdapter.setOnItemClickListener(new RecImageYuePuAdapter.onItemClickListener() {
            @Override
            public void onItmeClick(int position) {
                ArrayList<String> image = new ArrayList<>();
                ImageYuePuImageBean imageYuePuImageBean = imageFileList.get(position);
                List<File> list = imageYuePuImageBean.getList();
                for (int i = 0; i < list.size(); i++) {
                    String path = list.get(i).getPath();
                    image.add(path);
                }
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putStringArrayListExtra("list", image);
                intent.putExtra("title", imageYuePuImageBean.getName());
                startActivity(intent);
            }
        });
    }

    private ArrayList<ImageYuePuImageBean> getImageFileList() {
        ArrayList<ImageYuePuImageBean> imageYuePuImageBeans = new ArrayList<>();
        String path = MyApplication.getTuPianYuePuFile().getPath();
        if (strings == null || strings.size() == 0) {
            return null;
        }
        String currentPath = path + "/" + strings.get(mPosition).getTitle();
        List<File> files = FileUtils.listFilesInDir(currentPath);
        if (files != null && files.size() > 0) {
            for (int i = 0; i < files.size(); i++) {
                String name = files.get(i).getName();
                List<File> files1 = FileUtils.listFilesInDir(files.get(i).getPath());
                ImageYuePuImageBean imageYuePuImageBean = new ImageYuePuImageBean(name, files1, files1.size());
                imageYuePuImageBeans.add(imageYuePuImageBean);
            }
        }
        return imageYuePuImageBeans;
    }

    private void initRecTuPianYuePu() {
        ArrayList<BenDiYuePuBean> spList = SPBeanUtile.getTuPianQuPuFileList();
        if (spList != null) {
            strings = spList;
        } else {
            strings = new ArrayList<>();
        }
        tuPianYuePuAdapter = new TuPianYuePuAdapter(mContext, strings);
        mRecTuPianYuePu.setLayoutManager(new LinearLayoutManager(mContext));
        mRecTuPianYuePu.setAdapter(tuPianYuePuAdapter);
        tuPianYuePuAdapter.setOnItemClickListener(new TuPianYuePuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mPosition = position;
                for (int i = 0; i < strings.size(); i++) {
                    BenDiYuePuBean benDiYuePuBean = strings.get(i);
                    benDiYuePuBean.setSelected(false);
                    strings.set(i, benDiYuePuBean);
                }
                BenDiYuePuBean benDiYuePuBean = strings.get(position);
                benDiYuePuBean.setSelected(true);
                strings.set(position, benDiYuePuBean);
                tuPianYuePuAdapter.notifyDataSetChanged();
                imageFileList.clear();
                imageFileList = getImageFileList();
                recImageYuePuAdapter.setData(getImageFileList());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_xinzeng:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_xinzeng, null);
                alertDialog.setContentView(inflate);
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                alertDialog.setCanceledOnTouchOutside(false);
                TextView mTvCancel = inflate.findViewById(R.id.tv_cancel);
                TextView mTvEnter = inflate.findViewById(R.id.tv_enter);
                EditText mEdBenDiQuPu = inflate.findViewById(R.id.ed_bendiqupu);
                //确定
                mTvEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = mEdBenDiQuPu.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            if (strings.size() != 0) {
                                for (int i = 0; i < strings.size(); i++) {
                                    String title = strings.get(i).getTitle();
                                    if (title.equals(text)) {
                                        classify = false;
                                        break;
                                    } else {
                                        classify = true;
                                    }
                                }
                                if (classify) {
                                    boolean tuPiQuPuFile = SPBeanUtile.createTuPiQuPuFile(text);
                                    if (tuPiQuPuFile) {
                                        for (int i = 0; i < strings.size(); i++) {
                                            BenDiYuePuBean benDiYuePuBean = strings.get(i);
                                            benDiYuePuBean.setSelected(false);
                                            strings.set(i, benDiYuePuBean);
                                        }
                                        BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                                        strings.add(0, benDiYuePuBean);
                                        tuPianYuePuAdapter.notifyDataSetChanged();
                                        if (alertDialog != null) {
                                            alertDialog.dismiss();
                                        }
                                        Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(mContext, "分类已经存在", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                boolean tuPiQuPuFile = SPBeanUtile.createTuPiQuPuFile(text);
                                if (tuPiQuPuFile) {
                                    BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                                    strings.add(0, benDiYuePuBean);
                                    tuPianYuePuAdapter.notifyDataSetChanged();
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                    Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "请输入分类名称", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //取消
                mTvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                        }
                    }
                });
                break;
        }
    }
}