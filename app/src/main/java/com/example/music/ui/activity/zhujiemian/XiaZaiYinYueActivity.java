package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.music.R;
import com.example.music.ui.activity.downloadmusic.DownLoadMusic1;
import com.example.music.ui.activity.downloadmusic.DownLoadMusic2;
import com.example.music.utils.StatusBarUtil;

public class XiaZaiYinYueActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvMusic1;
    private TextView mTvMusic2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xia_zai_yin_yue);
        StatusBarUtil.transparencyBar(this);
        initView();
    }

    private void initView() {
        mTvMusic1 = findViewById(R.id.tv_download_music1);
        mTvMusic2 = findViewById(R.id.tv_download_music2);


        mTvMusic1.setOnClickListener(this);
        mTvMusic2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_download_music1:
                Intent intent = new Intent(XiaZaiYinYueActivity.this, DownLoadMusic1.class);
                startActivity(intent);
                break;
            case R.id.tv_download_music2:
                Intent intent1 = new Intent(XiaZaiYinYueActivity.this, DownLoadMusic2.class);
                startActivity(intent1);
                break;
        }
    }
}