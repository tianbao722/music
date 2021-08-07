package com.example.music.ui.activity.zhujiemian;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.DongTaiVideoAdapter;
import com.example.music.adapter.FileMoveAdapter;
import com.example.music.adapter.RecImageYuePuAdapter;
import com.example.music.adapter.TuPianYuePuAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.bean.MusicBean;
import com.example.music.bean.MusicBean;
import com.example.music.ui.activity.ImageActivity;
import com.example.music.ui.activity.VideoPlayActivity;
import com.example.music.ui.activity.search.SearchVideoActivity;
import com.example.music.ui.fragment.TuPianYuePuFragment;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.StatusBarUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DongTaiPuActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private TextView mTvXinZeng;
    private TextView mTvDownLoad;
    private ImageView mIvSearch;
    private RecyclerView mRecDongTitle;
    private RecyclerView mRecDongVideo;
    private ArrayList<BenDiYuePuBean> strings;
    private TuPianYuePuAdapter tuPianYuePuAdapter;
    private Context mContext;
    private int mPosition;//当前选择的Title的下标
    private ArrayList<MusicBean> videoList;
    private DongTaiVideoAdapter dongTaiVideoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jie_pai_qi);
        mContext = this;
        StatusBarUtil.transparencyBar(this);
        initView();
    }

    private void initView() {
        mIvBack = findViewById(R.id.dong_iv_back);
        mIvSearch = findViewById(R.id.dong_iv_shear);
        mTvDownLoad = findViewById(R.id.dong_tv_download);
        mTvXinZeng = findViewById(R.id.dong_tv_xinzeng);
        mRecDongTitle = findViewById(R.id.dong_rec_tupianyuepy);
        mRecDongVideo = findViewById(R.id.dong_rec_image_yuepu);
        mIvBack.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);
        mTvXinZeng.setOnClickListener(this);
        mTvDownLoad.setOnClickListener(this);

        //初始化动态乐谱左边title
        initRecTuPianYuePu();
        //初始化动态乐谱右边Video
        initRecImageYuePu();
    }

    private boolean classify = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dong_iv_back://返回
                DongTaiPuActivity.this.finish();
                break;
            case R.id.dong_iv_shear://搜索
                Intent intent = new Intent(DongTaiPuActivity.this, SearchVideoActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.dong_tv_xinzeng://新增
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
                                    boolean tuPiQuPuFile = SPBeanUtile.createDongTaiYuePuFile(text);
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
                                        MyAsyncTask myAsyncTask = new MyAsyncTask();
                                        myAsyncTask.execute("s");
                                        Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(mContext, "分类已经存在", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                boolean tuPiQuPuFile = SPBeanUtile.createDongTaiYuePuFile(text);
                                if (tuPiQuPuFile) {
                                    BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                                    strings.add(0, benDiYuePuBean);
                                    tuPianYuePuAdapter.notifyDataSetChanged();
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                                    myAsyncTask.execute("s");
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
            case R.id.dong_tv_download:

                break;
        }
    }


    private void initRecTuPianYuePu() {
        ArrayList<BenDiYuePuBean> spList = SPBeanUtile.getDongTaiYuePuFileList();
        if (spList != null) {
            strings = spList;
        } else {
            strings = new ArrayList<>();
        }
        tuPianYuePuAdapter = new TuPianYuePuAdapter(mContext, strings);
        mRecDongTitle.setLayoutManager(new LinearLayoutManager(mContext));
        mRecDongTitle.setAdapter(tuPianYuePuAdapter);
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

    //获取选中title文件夹下的Video
    private ArrayList<MusicBean> getImageFileList() {
        ArrayList<MusicBean> musicBeans = new ArrayList<>();
        String path = MyApplication.getDongTaiYuePuFile().getPath();
        if (strings == null || strings.size() == 0) {
            return null;
        }
        String currentPath = path + "/" + strings.get(mPosition).getTitle();
        List<File> files2 = FileUtils.listFilesInDir(currentPath);
        if (files2 != null && files2.size() > 0) {
            for (int i = 0; i < files2.size(); i++) {
                String path1 = files2.get(i).getPath();
                boolean fileExists = FileUtils.isFileExists(path1);
                if (fileExists) {
                    String fileName = FileUtils.getFileName(files2.get(i));
                    String pattern = ".*._.*";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(fileName);
                    boolean matches = m.matches();
                    if (!matches) {
                        String name = fileName.substring(0, fileName.length() - 4);
                        String size = FileUtils.getSize(files2.get(i));
                        MusicBean musicBean = new MusicBean(name, 0, size, path1);
                        musicBeans.add(musicBean);
                    }
                }
            }
        }
        return musicBeans;
    }

    private void initRecImageYuePu() {
        mRecDongVideo.setLayoutManager(new LinearLayoutManager(mContext));
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute("s");
        if (videoList == null) {
            videoList = new ArrayList<>();
        }
        dongTaiVideoAdapter = new DongTaiVideoAdapter(videoList, mContext);
        mRecDongVideo.setAdapter(dongTaiVideoAdapter);
        //条目点击事件
        dongTaiVideoAdapter.setOnItemClickListener(new DongTaiVideoAdapter.onItemClickListener() {
            @Override
            public void onItemClick(MusicBean musicBean) {
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra("name", musicBean.getName());
                intent.putExtra("path", musicBean.getPath());
                startActivity(intent);
            }
        });
        dongTaiVideoAdapter.setOnItemLongClickListener(new DongTaiVideoAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(ArrayList<MusicBean> list, int position) {
                videoList = list;
                showAlertLongImage(position);
            }
        });
    }

    //加载中loading动画
    private AlertDialog alertDialog;

    private void showAleartDialogLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_loading, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(false);
        ImageView mIvLoading = inflate.findViewById(R.id.iv_loading);
        Glide.with(mContext).load(R.mipmap.loading).into(mIvLoading);
    }

    class MyAsyncTask extends AsyncTask<String, Integer, ArrayList<MusicBean>> {

        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            } else {
                showAleartDialogLoading();
            }
            super.onPreExecute();
        }

        @Override
        protected ArrayList<MusicBean> doInBackground(String... params) {
            //这是在后台子线程中执行的
            ArrayList<MusicBean> imageFileList = getImageFileList();
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
        protected void onPostExecute(ArrayList<MusicBean> imageYuePuImageBeans) {
            super.onPostExecute(imageYuePuImageBeans);
            //当任务执行完成是调用,在UI线程
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            videoList.clear();
            videoList = imageYuePuImageBeans;
            dongTaiVideoAdapter.setData(videoList);
        }
    }


    //右边音乐的删除和重命名
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
        TextView mTvMoveFile = inflate.findViewById(R.id.tv_move_file);
        //移动文件
        mTvMoveFile.setVisibility(View.VISIBLE);
        mTvMoveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMoveFile(position);
                alertDialog.dismiss();
            }
        });
        //删除
        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = videoList.get(position).getName();
                String file = MyApplication.getDongTaiYuePuFile() + "/" + strings.get(mPosition).getTitle() + "/" + title + ".mp4";
                boolean delete = FileUtils.delete(file);
                if (delete) {
                    videoList.clear();
                    videoList = getImageFileList();
                    dongTaiVideoAdapter.setData(getImageFileList());
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

    //移动文件的弹窗
    private int selectMovePosition;

    private void showAlertMoveFile(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_move_file, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(true);
        RecyclerView mRecMove = inflate.findViewById(R.id.rec_move_file);
        TextView mTvCanCel = inflate.findViewById(R.id.tv_cencel1);
        TextView mTvEnter = inflate.findViewById(R.id.tv_enter1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecMove.setLayoutManager(linearLayoutManager);
        if (strings == null) {
            strings = new ArrayList<>();
        }
        FileMoveAdapter fileMoveAdapter = new FileMoveAdapter(mContext, strings);
        mRecMove.setAdapter(fileMoveAdapter);
        fileMoveAdapter.notifyDataSetChanged();
        fileMoveAdapter.setOnItemClickListener(new FileMoveAdapter.onItemClickListener() {
            public void onItemClick(int position) {
                selectMovePosition = position;
                for (int i = 0; i < strings.size(); i++) {
                    BenDiYuePuBean benDiYuePuBean = strings.get(i);
                    benDiYuePuBean.setSelected(false);
                    strings.set(i, benDiYuePuBean);
                }
                BenDiYuePuBean benDiYuePuBean = strings.get(position);
                benDiYuePuBean.setSelected(true);
                strings.set(position, benDiYuePuBean);
                fileMoveAdapter.setData(strings);
            }
        });
        mTvCanCel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        mTvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = videoList.get(position).getName();
                //原路径
                String file = MyApplication.getDongTaiYuePuFile() + "/" + strings.get(mPosition).getTitle() + "/" + title + ".mp4";
                String title1 = strings.get(selectMovePosition).getTitle();
                //新路径
                String path = MyApplication.getDongTaiYuePuFile().getPath() + "/" + title1 + "/" + title + ".mp4";
                boolean move = FileUtils.move(file, path);
                if (move) {
                    mPosition = selectMovePosition;
                    for (int i = 0; i < strings.size(); i++) {
                        BenDiYuePuBean benDiYuePuBean = strings.get(i);
                        benDiYuePuBean.setSelected(false);
                        strings.set(i, benDiYuePuBean);
                    }
                    BenDiYuePuBean benDiYuePuBean = strings.get(mPosition);
                    benDiYuePuBean.setSelected(true);
                    strings.set(mPosition, benDiYuePuBean);
                    mRecDongTitle.scrollToPosition(mPosition);
                    tuPianYuePuAdapter.setData(strings);
                    videoList.clear();
                    videoList = getImageFileList();
                    dongTaiVideoAdapter.setData(getImageFileList());
                    alertDialog.dismiss();
                    Toast.makeText(mContext, "移动成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "移动失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //右边音乐名字的重命名
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
        String title = videoList.get(position).getName();
        mEdBenDiQuPu.setText(title);
        //确定
        mTvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEdBenDiQuPu.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    if (videoList.size() != 0) {
                        for (int i = 0; i < videoList.size(); i++) {
                            String title = videoList.get(i).getName();
                            if (title.equals(text)) {
                                classify = false;
                                break;
                            } else {
                                classify = true;
                            }
                        }
                        if (classify) {
                            String path = MyApplication.getDongTaiYuePuFile().getPath() + "/" + strings.get(mPosition).getTitle() + "/" + videoList.get(position).getName() + ".mp4";
                            boolean rename = FileUtils.rename(path, text + ".mp4");
                            if (rename) {
                                MusicBean imageYuePuImageBean = videoList.get(position);
                                String file = MyApplication.getDongTaiYuePuFile().getPath() + "/" + strings.get(mPosition).getTitle() + "/" + text + ".mp4";
                                imageYuePuImageBean.setName(text);
                                imageYuePuImageBean.setPath(file);
                                videoList.set(position, imageYuePuImageBean);
                                dongTaiVideoAdapter.setData(videoList);
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
                String file = MyApplication.getDongTaiYuePuFile() + "/" + title;
                boolean delete = FileUtils.delete(file);
                if (delete) {
                    strings.remove(mPosition);
                    tuPianYuePuAdapter.notifyDataSetChanged();
                    DongTaiPuActivity.this.mPosition = 0;
                    if (strings != null && strings.size() > 0) {
                        for (int i = 0; i < strings.size(); i++) {
                            BenDiYuePuBean benDiYuePuBean = strings.get(i);
                            benDiYuePuBean.setSelected(false);
                            strings.set(i, benDiYuePuBean);
                        }
                        BenDiYuePuBean benDiYuePuBean = strings.get(0);
                        benDiYuePuBean.setSelected(true);
                        strings.set(0, benDiYuePuBean);
                        tuPianYuePuAdapter.notifyDataSetChanged();
                        MyAsyncTask myAsyncTask = new MyAsyncTask();
                        myAsyncTask.execute("s");
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        videoList.clear();
                        dongTaiVideoAdapter.setData(videoList);
                    }
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
                            String path = MyApplication.getDongTaiYuePuFile().getPath() + "/" + strings.get(mPosition).getTitle();
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
                        boolean rename = FileUtils.rename(MyApplication.getDongTaiYuePuFile().getPath() + strings.get(mPosition).getTitle(), text);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2){
            MyAsyncTask myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute("s");
        }
    }
}