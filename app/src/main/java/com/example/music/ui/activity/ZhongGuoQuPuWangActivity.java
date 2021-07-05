package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.music.R;

public class ZhongGuoQuPuWangActivity extends AppCompatActivity {

    private WebView mWeb_zhongGuoQuPu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhong_guo_qu_pu_wang);
        initView();
    }

    private void initView() {
        mWeb_zhongGuoQuPu = findViewById(R.id.web_zhognguoqupuwang);
        mWeb_zhongGuoQuPu.loadUrl("https://www.qupu123.com/Mobile");
        mWeb_zhongGuoQuPu.setWebViewClient(new WebViewClient());
    }
}