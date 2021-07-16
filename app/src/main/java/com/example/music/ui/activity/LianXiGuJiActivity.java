package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.LianXiGuJiAdapter;
import com.example.music.bean.LianXiGuJiBean;
import com.example.music.bean.MusicBean;
import com.example.music.utils.StatusBarUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class LianXiGuJiActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private RecyclerView mRecGuJi;
    private TextView mTvYinSuZhi;
    private TextView mTvYinSuJian;
    private TextView mTvYinSuJia;
    private Context mContext;
    private LianXiGuJiAdapter lianXiGuJiAdapter;
    private ArrayList<LianXiGuJiBean> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lian_xi_gu_ji);
        StatusBarUtil.transparencyBar(this);
        mContext = this;
        initView();
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mRecGuJi = findViewById(R.id.guji_rec);
        mTvYinSuZhi = findViewById(R.id.tv_guji_yinsuzhi);
        mTvYinSuJian = findViewById(R.id.tv_guji_yinsu_jian);
        mTvYinSuJia = findViewById(R.id.tv_guji_yinsu_jia);
        mList = getImageFileList();
        mIvBack.setOnClickListener(this);
        mTvYinSuJia.setOnClickListener(this);
        mTvYinSuJian.setOnClickListener(this);
        mRecGuJi.setLayoutManager(new GridLayoutManager(mContext, 4));
        lianXiGuJiAdapter = new LianXiGuJiAdapter(mList, mContext);
        mRecGuJi.setAdapter(lianXiGuJiAdapter);
        lianXiGuJiAdapter.setOnItemClickListener(new LianXiGuJiAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == mList.size()) {
                    //跳转到下载鼓机

                } else {
                    //播放音乐
                }
            }
        });
    }

    private ArrayList<LianXiGuJiBean> getImageFileList() {
        ArrayList<LianXiGuJiBean> musicBeans = new ArrayList<>();
        String path = MyApplication.getJieZouXunLianFile().getPath();
        List<File> files2 = FileUtils.listFilesInDirWithFilter(path, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getPath().endsWith(".mp3"));
            }
        });
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (int i = 0; i < files2.size(); i++) {
            String path1 = files2.get(i).getPath();
            mmr.setDataSource(path1);
            String fileName = FileUtils.getFileName(files2.get(i));
            String name = fileName.substring(0, fileName.length() - 4);
            LianXiGuJiBean musicBean = new LianXiGuJiBean(name, path1, false);
            musicBeans.add(musicBean);
        }
        return musicBeans;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                LianXiGuJiActivity.this.finish();
                break;
            case R.id.tv_guji_yinsu_jian://减音速

                break;
            case R.id.tv_guji_yinsu_jia://加音速

                break;
        }
    }
}