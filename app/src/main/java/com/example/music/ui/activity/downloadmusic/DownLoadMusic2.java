package com.example.music.ui.activity.downloadmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
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
        WebSettings webSettings = mWebSouPuWang.getSettings();
        //支持JS
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //设置加载进来的网页自适应手机屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //将网页加载到当前应用界面中，不再跳转到浏览器
        mWebSouPuWang.setWebChromeClient(new WebChromeClient());
        mWebSouPuWang.setWebViewClient(new WebViewClient());
        mWebSouPuWang.loadUrl("https://5sing.kugou.com/index.html");

    }
}