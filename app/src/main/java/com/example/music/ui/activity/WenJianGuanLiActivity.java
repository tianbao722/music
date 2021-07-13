package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.music.R;
import com.example.music.utils.StatusBarUtil;

public class WenJianGuanLiActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wen_jian_guan_li);
        StatusBarUtil.transparencyBar(this);
        initView();
    }

    private void initView() {

    }
}