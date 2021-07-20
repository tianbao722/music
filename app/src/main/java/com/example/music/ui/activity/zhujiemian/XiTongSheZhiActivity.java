package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.music.R;
import com.example.music.utils.StatusBarUtil;

public class XiTongSheZhiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xi_tong_she_zhi);
        StatusBarUtil.transparencyBar(this);

    }
}