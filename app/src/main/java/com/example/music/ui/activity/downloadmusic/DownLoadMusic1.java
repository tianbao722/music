package com.example.music.ui.activity.downloadmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.music.R;

public class DownLoadMusic1 extends AppCompatActivity {

    private WebView mWebMusic1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load_music1);
        initView();
    }

    private void initView() {
        mWebMusic1 = findViewById(R.id.web_download_music1);
        mWebMusic1.loadUrl("https://www.vfinemusic.com/music-library?utm_source=360&utm_medium=search&utm_campaign=PC-%E7%BD%91%E7%AB%99&utm_content=%E7%BD%91%E7%AB%99A&utm_term=%E4%B8%8B%E8%BD%BD%E9%9F%B3%E4%B9%90%E5%BA%93");
        mWebMusic1.setWebViewClient(new WebViewClient());
        mWebMusic1.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();//接收证书
            }
        });
        // 这行代码一定加上否则效果不会出现
        mWebMusic1.getSettings().setJavaScriptEnabled(true);
    }
}