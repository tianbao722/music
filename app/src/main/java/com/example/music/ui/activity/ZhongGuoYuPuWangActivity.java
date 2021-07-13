package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.music.R;
import com.example.music.utils.StatusBarUtil;

public class ZhongGuoYuPuWangActivity extends AppCompatActivity {

    private WebView mWebZhongGuoYuePu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhong_guo_yu_pu_wang);
        StatusBarUtil.transparencyBar(this);

        initView();
    }

    private void initView() {
        mWebZhongGuoYuePu = findViewById(R.id.web_zhongguoyuepuwang);
        mWebZhongGuoYuePu.loadUrl("https://www.cnscore.com/jianpu/");
        mWebZhongGuoYuePu.setWebViewClient(new WebViewClient());
    }
}