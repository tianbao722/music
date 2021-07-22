package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.LianXiGuJiAdapter;
import com.example.music.bean.LianXiGuJiBean;
import com.example.music.utils.StatusBarUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.music.utils.DateUtil.parseTime;

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
        mIvBack.setOnClickListener(this);
        mTvYinSuJia.setOnClickListener(this);
        mTvYinSuJian.setOnClickListener(this);
        mList = new ArrayList<>();
        mList.add(new LianXiGuJiBean("42拍节奏", R.raw.a, false));
        mList.add(new LianXiGuJiBean("43拍节奏", R.raw.b, false));
        mList.add(new LianXiGuJiBean("44拍节奏二", R.raw.c, false));
        mList.add(new LianXiGuJiBean("44拍节奏一", R.raw.d, false));
        mList.add(new LianXiGuJiBean("86拍节奏一", R.raw.e, false));
        mList.add(new LianXiGuJiBean("布鲁斯节奏", R.raw.f, false));
        mList.add(new LianXiGuJiBean("流行节奏八", R.raw.g, false));
        mList.add(new LianXiGuJiBean("流行节奏二", R.raw.aa, false));
        mList.add(new LianXiGuJiBean("流行节奏六", R.raw.bb, false));
        mList.add(new LianXiGuJiBean("流行节奏七", R.raw.cc, false));
        mList.add(new LianXiGuJiBean("流行节奏三", R.raw.dd, false));
        mList.add(new LianXiGuJiBean("流行节奏四", R.raw.ee, false));
        mList.add(new LianXiGuJiBean("流行节奏五", R.raw.ff, false));
        mList.add(new LianXiGuJiBean("流行节奏一", R.raw.gg, false));
        mList.add(new LianXiGuJiBean("乡村节奏二", R.raw.aaa, false));
        mList.add(new LianXiGuJiBean("乡村节奏一", R.raw.bbb, false));
        mList.add(new LianXiGuJiBean("摇滚节奏二", R.raw.ccc, false));
        mList.add(new LianXiGuJiBean("摇滚节奏三", R.raw.ddd, false));
        mList.add(new LianXiGuJiBean("摇滚节奏四", R.raw.eee, false));
        mList.add(new LianXiGuJiBean("摇滚节奏一", R.raw.fff, false));
        mRecGuJi.setLayoutManager(new GridLayoutManager(mContext, 4));
        lianXiGuJiAdapter = new LianXiGuJiAdapter(mList, mContext);
        mRecGuJi.setAdapter(lianXiGuJiAdapter);
        lianXiGuJiAdapter.setOnItemClickListener(new LianXiGuJiAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //播放音乐
                for (int i = 0; i < mList.size(); i++) {
                    LianXiGuJiBean lianXiGuJiBean = mList.get(i);
                    lianXiGuJiBean.setSelected(false);
                    mList.set(i, lianXiGuJiBean);
                }
                LianXiGuJiBean lianXiGuJiBean = mList.get(position);
                lianXiGuJiBean.setSelected(true);
                mList.set(position, lianXiGuJiBean);
                lianXiGuJiAdapter.setData(mList);
                changeMusic(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                LianXiGuJiActivity.this.finish();
                break;
            case R.id.tv_guji_yinsu_jian://减音速
                if (mediaPlayer != null) {
                    setJianYinSu();
                } else {
                    Toast.makeText(mContext, "请先选择节奏", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_guji_yinsu_jia://加音速
                if (mediaPlayer != null) {
                    setJiaYinSu();
                } else {
                    Toast.makeText(mContext, "请先选择节奏", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setJianYinSu() {
        String s = mTvYinSuZhi.getText().toString();
        float YinSu = Float.parseFloat(s);
        if (YinSu == 1.5f) {
            mTvYinSuZhi.setText("1.4");
            setYinSu(1.4f);
        } else if (YinSu == 1.4f) {
            mTvYinSuZhi.setText("1.3");
            setYinSu(1.3f);
        } else if (YinSu == 1.3f) {
            mTvYinSuZhi.setText("1.2");
            setYinSu(1.2f);
        } else if (YinSu == 1.2f) {
            mTvYinSuZhi.setText("1.1");
            setYinSu(1.1f);
        } else if (YinSu == 1.1f) {
            mTvYinSuZhi.setText("1.0");
            setYinSu(1.0f);
        } else if (YinSu == 1.0f) {
            mTvYinSuZhi.setText("0.9");
            setYinSu(0.9f);
        } else if (YinSu == 0.9f) {
            mTvYinSuZhi.setText("0.8");
            setYinSu(0.8f);
        } else if (YinSu == 0.8f) {
            mTvYinSuZhi.setText("0.7");
            setYinSu(0.7f);
        } else if (YinSu == 0.7f) {
            mTvYinSuZhi.setText("0.6");
            setYinSu(0.6f);
        } else if (YinSu == 0.6f) {
            mTvYinSuZhi.setText("0.5");
            setYinSu(0.5f);
        }
    }

    private void setJiaYinSu() {
        String s = mTvYinSuZhi.getText().toString();
        float YinSu = Float.parseFloat(s);
        if (YinSu == 0.5f) {
            mTvYinSuZhi.setText("0.6");
            setYinSu(0.6f);
        } else if (YinSu == 0.6f) {
            mTvYinSuZhi.setText("0.7");
            setYinSu(0.7f);
        } else if (YinSu == 0.7f) {
            mTvYinSuZhi.setText("0.8");
            setYinSu(0.8f);
        } else if (YinSu == 0.8f) {
            mTvYinSuZhi.setText("0.9");
            setYinSu(0.9f);
        } else if (YinSu == 0.9f) {
            mTvYinSuZhi.setText("1.0");
            setYinSu(1.0f);
        } else if (YinSu == 1.0f) {
            mTvYinSuZhi.setText("1.1");
            setYinSu(1.1f);
        } else if (YinSu == 1.1f) {
            mTvYinSuZhi.setText("1.2");
            setYinSu(1.2f);
        } else if (YinSu == 1.2f) {
            mTvYinSuZhi.setText("1.3");
            setYinSu(1.3f);
        } else if (YinSu == 1.3f) {
            mTvYinSuZhi.setText("1.4");
            setYinSu(1.4f);
        } else if (YinSu == 1.4f) {
            mTvYinSuZhi.setText("1.5");
            setYinSu(1.5f);
        }
    }

    //设置音速
    public void setYinSu(float speed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (speed >= 0.5) {
                PlaybackParams params = mediaPlayer.getPlaybackParams();
                params.setSpeed(0.5f);//音速
                mediaPlayer.setPlaybackParams(params);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //切歌
    private MediaPlayer mediaPlayer;//音频播放器

    private void changeMusic(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(mContext, mList.get(position).getPath());
        // 开始播放
        mediaPlayer.start();
    }
}