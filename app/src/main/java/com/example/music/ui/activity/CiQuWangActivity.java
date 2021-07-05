package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.music.R;

public class CiQuWangActivity extends AppCompatActivity {

    private WebView mWebCiQuWang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ci_qu_wang);
        initView();
    }

    private void initView() {
        mWebCiQuWang = findViewById(R.id.web_ciquwang);
        mWebCiQuWang.loadUrl("http://www.ktvc8.com/");
        mWebCiQuWang.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();//接收证书

            }
        });
        // 这行代码一定加上否则效果不会出现
        mWebCiQuWang.getSettings().setJavaScriptEnabled(true);

    }
}