package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.music.R;
import com.example.music.utils.StatusBarUtil;

public class XiaZaiYinYueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xia_zai_yin_yue);
        StatusBarUtil.transparencyBar(this);

    }
}