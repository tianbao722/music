package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
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
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.DongTaiVideoAdapter;
import com.example.music.adapter.RecImageYuePuAdapter;
import com.example.music.adapter.TuPianYuePuAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.MusicBean;
import com.example.music.bean.MusicBean;
import com.example.music.ui.activity.ImageActivity;
import com.example.music.ui.activity.VideoPlayActivity;
import com.example.music.ui.activity.search.SearchVideoActivity;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.StatusBarUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

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
                startActivity(intent);
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
                videoList.clear();
                videoList = getImageFileList();
                dongTaiVideoAdapter.setData(getImageFileList());
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
        List<File> files2 = FileUtils.listFilesInDirWithFilter(currentPath, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getPath().endsWith(".mp4"));
            }
        });
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (int i = 0; i < files2.size(); i++) {
            String path1 = files2.get(i).getPath();
//            mmr.setDataSource(path1);
            String fileName = FileUtils.getFileName(files2.get(i));
            String name = fileName.substring(0, fileName.length() - 4);
//            String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String size = FileUtils.getSize(files2.get(i));
//            long time1 = Long.parseLong(time);
            MusicBean musicBean = new MusicBean(name, 0, size, path1);
            musicBeans.add(musicBean);
        }
        return musicBeans;
    }

    private void initRecImageYuePu() {
        mRecDongVideo.setLayoutManager(new LinearLayoutManager(mContext));
        videoList = getImageFileList();
        if (videoList == null) {
            videoList = new ArrayList<>();
        }
        dongTaiVideoAdapter = new DongTaiVideoAdapter(videoList, mContext);
        mRecDongVideo.setAdapter(dongTaiVideoAdapter);
        //条目点击事件
        dongTaiVideoAdapter.setOnItemClickListener(new DongTaiVideoAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MusicBean musicBean = videoList.get(position);
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra("name", musicBean.getName());
                intent.putExtra("path", musicBean.getPath());
                startActivity(intent);
            }
        });
    }

}