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
import com.example.music.adapter.PDFImageAdapter;
import com.example.music.adapter.RecImageYuePuAdapter;
import com.example.music.adapter.TuPianYuePuAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.bean.PDFImageBean;
import com.example.music.ui.activity.PDFImageActivity;
import com.example.music.utils.SPBeanUtile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class DPFYuePuFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mDefRecDefYuePuTitle;
    private RecyclerView mDefRecDefYuePuImage;
    private TextView mDefTvXinJian;
    private Context mContext;
    private ArrayList<BenDiYuePuBean> strings;
    private ArrayList<PDFImageBean> imageFileList;
    private PDFImageAdapter recImageYuePuAdapter;
    private int mPosition;
    private TuPianYuePuAdapter tuPianYuePuAdapter;
    private boolean classify = false;

    public static DPFYuePuFragment newInstance() {
        return new DPFYuePuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.d_p_f_yue_pu_fragment, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mContext = getActivity();
        mDefRecDefYuePuTitle = inflate.findViewById(R.id.def_rec_tupianyuepy);
        mDefTvXinJian = inflate.findViewById(R.id.edf_tv_xinzeng);
        mDefRecDefYuePuImage = inflate.findViewById(R.id.edg_rec_image_yuepu);
        mDefTvXinJian.setOnClickListener(this);
        //初始化Def乐谱左边title
        initRecTuPianYuePu();
        //初始化Def乐谱右边图片
        initRecImageYuePu();
    }

    private void initRecImageYuePu() {
        mDefRecDefYuePuImage.setLayoutManager(new GridLayoutManager(mContext, 2));
        imageFileList = getImageFileList();
        recImageYuePuAdapter = new PDFImageAdapter(imageFileList, mContext);
        mDefRecDefYuePuImage.setAdapter(recImageYuePuAdapter);
        //条目点击事件
        recImageYuePuAdapter.setOnItemClickListener(new PDFImageAdapter.onItemClickListener() {
            @Override
            public void onItmeClick(int position) {
                PDFImageBean imageYuePuImageBean = imageFileList.get(position);
                Intent intent = new Intent(mContext, PDFImageActivity.class);
                intent.putExtra("name", imageYuePuImageBean.getName());
                intent.putExtra("file", imageYuePuImageBean.getFile().getPath());
                startActivity(intent);
            }
        });
    }

    private ArrayList<PDFImageBean> getImageFileList() {
        ArrayList<PDFImageBean> imageYuePuImageBeans = new ArrayList<>();
        String path = MyApplication.getDefYuePuFile().getPath();
        if (strings == null || strings.size() <= 0) {
            return null;
        }
        String currentPath = path + "/" + strings.get(mPosition).getTitle();
        List<File> files = FileUtils.listFilesInDirWithFilter(currentPath, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getPath().endsWith(".pdf"));
            }
        });
        if (files != null && files.size() > 0) {
            for (int i = 0; i < files.size(); i++) {
                String fileName = files.get(i).getName();
                String name = fileName.substring(0, fileName.length() - 4);
                String size = FileUtils.getSize(files.get(i));
                PDFImageBean imageYuePuImageBean = new PDFImageBean(name, files.get(i), size);
                imageYuePuImageBeans.add(imageYuePuImageBean);
            }
        }
        return imageYuePuImageBeans;
    }

    private void initRecTuPianYuePu() {
        ArrayList<BenDiYuePuBean> spList = SPBeanUtile.getDefQuPuFileList();
        if (spList != null) {
            strings = spList;
        } else {
            strings = new ArrayList<>();
        }
        tuPianYuePuAdapter = new TuPianYuePuAdapter(mContext, strings);
        mDefRecDefYuePuTitle.setLayoutManager(new LinearLayoutManager(mContext));
        mDefRecDefYuePuTitle.setAdapter(tuPianYuePuAdapter);
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
                recImageYuePuAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edf_tv_xinzeng:
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
                                    boolean tuPiQuPuFile = SPBeanUtile.createDefQuPuFile(text);
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
                                boolean tuPiQuPuFile = SPBeanUtile.createDefQuPuFile(text);
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