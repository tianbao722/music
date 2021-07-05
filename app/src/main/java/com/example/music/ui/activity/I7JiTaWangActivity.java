package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.music.R;

public class I7JiTaWangActivity extends AppCompatActivity {

    private WebView mWeb17JiTaWang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i7_ji_ta_wang);
        initView();
    }

    private void initView() {
        mWeb17JiTaWang = findViewById(R.id.web_17jitawang);
        mWeb17JiTaWang.loadUrl("https://www.17jita.com/tab/img/");
        mWeb17JiTaWang.setWebViewClient(new WebViewClient());

    }
}