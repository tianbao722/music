package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music.R;
import com.example.music.ui.activity.downloadqupu.NiuRenYuePuWangActivity;
import com.example.music.ui.activity.downloadqupu.ZhongGuoQuPuWangActivity;
import com.example.music.ui.activity.downloadqupu.ZhongGuoYuPuWangActivity;
import com.example.music.utils.StatusBarUtil;

public class DownloadTheSongActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvZhongGuoQuPuWang;
    private TextView mTvNuRenyuepuwang;
    private TextView mTvSouPuWang;
    private TextView mTvSouQuWang;
    private TextView mTv17JiTaWang;
    private ImageView mIvBack;
    private TextView mTvZhongGuoYuePuWang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_the_song);
        StatusBarUtil.transparencyBar(this);

        initView();
    }

    private void initView() {
        //中国曲谱网
        mTvZhongGuoQuPuWang = findViewById(R.id.tv_zhongguoqupuwang);
        //牛人乐谱网
        mTvNuRenyuepuwang = findViewById(R.id.tv_niurenyuepuwang);
        //搜谱网
        mTvSouPuWang = findViewById(R.id.tv_soupuwang);
        //词曲网
        mTvSouQuWang = findViewById(R.id.tv_souquwang);
        //17吉他网
        mTv17JiTaWang = findViewById(R.id.tv_17jitawang);
        mIvBack = findViewById(R.id.iv_back);
        //中国乐谱网
        mTvZhongGuoYuePuWang = findViewById(R.id.tv_zhongguoyuepuwang);
        mTvNuRenyuepuwang.setOnClickListener(this);
        mTvSouPuWang.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mTvSouQuWang.setOnClickListener(this);
        mTv17JiTaWang.setOnClickListener(this);
        mTvZhongGuoYuePuWang.setOnClickListener(this);
        mTvZhongGuoQuPuWang.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_zhongguoqupuwang:
                Intent intent = new Intent(DownloadTheSongActivity.this, ZhongGuoQuPuWangActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_niurenyuepuwang:
                Intent intent1 = new Intent(DownloadTheSongActivity.this, NiuRenYuePuWangActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_soupuwang:
//                Intent intent2 = new Intent(DownloadTheSongActivity.this, SouPuWangActivity.class);
//                startActivity(intent2);
                break;
            case R.id.tv_souquwang:
//                Intent intent3 = new Intent(DownloadTheSongActivity.this, CiQuWangActivity.class);
//                startActivity(intent3);
                break;
            case R.id.tv_17jitawang:
//                Intent intent4 = new Intent(DownloadTheSongActivity.this, I7JiTaWangActivity.class);
//                startActivity(intent4);
                break;
            case R.id.tv_zhongguoyuepuwang:
                Intent intent5 = new Intent(DownloadTheSongActivity.this, ZhongGuoYuPuWangActivity.class);
                startActivity(intent5);
                break;
            case R.id.iv_back:
                DownloadTheSongActivity.this.finish();
                break;
        }
    }
}