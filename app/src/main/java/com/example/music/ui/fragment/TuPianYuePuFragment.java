package com.example.music.ui.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.RecImageYuePuAdapter;
import com.example.music.adapter.TuPianYuePuAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.bean.MusicBean;
import com.example.music.ui.activity.BanZouActivity;
import com.example.music.ui.activity.ImageActivity;
import com.example.music.utils.SPBeanUtile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kotlin.text.Regex;

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
    private ImageView mIvLoading;

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
        mIvLoading = inflate.findViewById(R.id.iv_loading);
        mTvXinZheng.setOnClickListener(this);
        Glide.with(mContext).load(R.mipmap.loading).into(mIvLoading);
        //初始化图片乐谱左边title
        initRecTuPianYuePu();
        //初始化图片乐谱右边图片
        initRecImageYuePu();
    }

    private void initRecImageYuePu() {
        mRecImageYuePu.setLayoutManager(new GridLayoutManager(mContext, 2));
        imageFileList = new ArrayList<>();
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
                String name = imageYuePuImageBean.getName();
                intent.putExtra("title", name);
                startActivity(intent);
            }
        });
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute("s");
        recImageYuePuAdapter.setOnItemLongClickListener(new RecImageYuePuAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                showAlertLongImage(position);
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
                boolean dir = FileUtils.isDir(files.get(i));
                if (dir) {
                    String name = files.get(i).getName();
                    List<File> files1 = FileUtils.listFilesInDir(files.get(i).getPath());
                    int size = 0;
                    ArrayList<File> files2 = new ArrayList<>();
                    for (int j = 0; j < files1.size(); j++) {
                        boolean image = ImageUtils.isImage(files1.get(j));
                        if (image) {
                            size += 1;
                            files2.add(files1.get(j));
                        }
                    }
                    ImageYuePuImageBean imageYuePuImageBean = new ImageYuePuImageBean(name, files2, size);
                    imageYuePuImageBeans.add(imageYuePuImageBean);
                }
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
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute("s");
            }
        });
        tuPianYuePuAdapter.setOnItmeLongClickListner(new TuPianYuePuAdapter.onItmeLongClickListner() {
            @Override
            public void onItemLongClick(int position) {
                showAlertLong(position);
            }
        });
    }

    //右边图片的删除和重命名
    private void showAlertLongImage(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_updata, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(true);
        TextView mTvDelete = inflate.findViewById(R.id.tv_delete);
        TextView mTvChongMingMing = inflate.findViewById(R.id.tv_chongmingming);
        //删除
        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = imageFileList.get(position).getName();
                String file = MyApplication.getTuPianYuePuFile() + "/" + strings.get(mPosition).getTitle() + "/" + title;
                boolean delete = FileUtils.delete(file);
                if (delete) {
                    imageFileList.remove(position);
                    tuPianYuePuAdapter.notifyDataSetChanged();
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });
        //重命名
        mTvChongMingMing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertChongMingMingImage(position);
                alertDialog.dismiss();
            }
        });
    }

    //左边title的删除的重命名
    private void showAlertLong(int mPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_updata, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(true);
        TextView mTvDelete = inflate.findViewById(R.id.tv_delete);
        TextView mTvChongMingMing = inflate.findViewById(R.id.tv_chongmingming);
        //删除
        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = strings.get(mPosition).getTitle();
                String file = MyApplication.getTuPianYuePuFile() + "/" + title;
                boolean delete = FileUtils.delete(file);
                if (delete) {
                    strings.remove(mPosition);
                    tuPianYuePuAdapter.notifyDataSetChanged();
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });
        //重命名
        mTvChongMingMing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertChongMingMing(mPosition);
                alertDialog.dismiss();
            }
        });
    }

    //左边文件夹名字的重命名
    private void showAlertChongMingMing(int mPosition) {
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
        String title = strings.get(mPosition).getTitle();
        mEdBenDiQuPu.setText(title);
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
                            String path = MyApplication.getTuPianYuePuFile().getPath() + "/" + strings.get(mPosition).getTitle();
                            boolean rename = FileUtils.rename(path, text);
                            if (rename) {
                                strings.set(mPosition, new BenDiYuePuBean(text, true));
                                tuPianYuePuAdapter.notifyDataSetChanged();
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                Toast.makeText(mContext, "重命名成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "重命名失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "分类已经存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        boolean rename = FileUtils.rename(MyApplication.getTuPianYuePuFile().getPath() + strings.get(mPosition).getTitle(), text);
                        if (rename) {
                            strings.set(mPosition, new BenDiYuePuBean(text, true));
                            tuPianYuePuAdapter.notifyDataSetChanged();
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                            Toast.makeText(mContext, "重命名成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "重命名失败", Toast.LENGTH_SHORT).show();
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
    }

    //右边文件夹名字的重命名
    private void showAlertChongMingMingImage(int position) {
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
        String title = imageFileList.get(position).getName();
        mEdBenDiQuPu.setText(title);
        //确定
        mTvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEdBenDiQuPu.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    if (imageFileList.size() != 0) {
                        for (int i = 0; i < imageFileList.size(); i++) {
                            String title = imageFileList.get(i).getName();
                            if (title.equals(text)) {
                                classify = false;
                                break;
                            } else {
                                classify = true;
                            }
                        }
                        if (classify) {
                            String path = MyApplication.getTuPianYuePuFile().getPath() + "/" + strings.get(mPosition).getTitle() + "/" + imageFileList.get(position).getName();
                            boolean rename = FileUtils.rename(path, text);
                            if (rename) {
                                ImageYuePuImageBean imageYuePuImageBean = imageFileList.get(position);
                                imageYuePuImageBean.setName(text);
                                imageFileList.set(position, imageYuePuImageBean);
                                recImageYuePuAdapter.notifyDataSetChanged();
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                Toast.makeText(mContext, "重命名成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "重命名失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "该名称已经存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String path = MyApplication.getTuPianYuePuFile().getPath() + "/" + strings.get(mPosition).getTitle() + "/" + imageFileList.get(position).getName();
                        boolean rename = FileUtils.rename(path, text);
                        if (rename) {
                            ImageYuePuImageBean imageYuePuImageBean = imageFileList.get(position);
                            imageYuePuImageBean.setName(text);
                            imageFileList.set(position, imageYuePuImageBean);
                            recImageYuePuAdapter.notifyDataSetChanged();
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                            Toast.makeText(mContext, "重命名成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "重命名失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(mContext, "请输入名称", Toast.LENGTH_SHORT).show();
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

    class MyAsyncTask extends AsyncTask<String, Integer, ArrayList<ImageYuePuImageBean>> {

        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程
            mIvLoading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ImageYuePuImageBean> doInBackground(String... params) {
            //这是在后台子线程中执行的
            ArrayList<ImageYuePuImageBean> imageFileList = getImageFileList();
            return imageFileList;
        }

        @Override
        protected void onCancelled() {
            //当任务被取消时回调
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //更新进度
        }

        @Override
        protected void onPostExecute(ArrayList<ImageYuePuImageBean> imageYuePuImageBeans) {
            super.onPostExecute(imageYuePuImageBeans);
            //当任务执行完成是调用,在UI线程
            mIvLoading.setVisibility(View.GONE);
            imageFileList.clear();
            imageFileList = imageYuePuImageBeans;
            recImageYuePuAdapter.setData(imageYuePuImageBeans);
        }
    }
}