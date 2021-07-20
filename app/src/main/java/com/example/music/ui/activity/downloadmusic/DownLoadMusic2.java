package com.example.music.ui.activity.downloadmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.music.R;
import com.example.music.utils.StatusBarUtil;

public class DownLoadMusic2 extends AppCompatActivity {

    private WebView mWebSouPuWang;
    private Context mContext;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sou_pu_wang);
        StatusBarUtil.transparencyBar(this);
        initView();
    }

    private void initView() {
        mWebSouPuWang = findViewById(R.id.web_soupuwang);
        mWebSouPuWang.loadUrl("https://5sing.kugou.com/index.html");
        mWebSouPuWang.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();//接收证书
            }
        });
        // 这行代码一定加上否则效果不会出现
        mWebSouPuWang.getSettings().setJavaScriptEnabled(true);

    }
}