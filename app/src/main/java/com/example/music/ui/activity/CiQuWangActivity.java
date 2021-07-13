package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.music.Constants;
import com.example.music.R;
import com.example.music.inter.JsCallJavaObj;
import com.example.music.utils.StatusBarUtil;

public class CiQuWangActivity extends AppCompatActivity {

    private WebView mWebCiQuWang;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ci_qu_wang);
        StatusBarUtil.transparencyBar(this);
        this.mContext = this;
        initView();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
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
        mWebCiQuWang.addJavascriptInterface(new JsCallJavaObj() {
            @JavascriptInterface
            @Override
            public void showBigImg(String url) {
                Toast.makeText(mContext, "图片路径" + url, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, DaoRuQuPuActivity.class);
                intent.putExtra(Constants.webImage, url);
                startActivity(intent);
            }
        }, "jsCallJavaObj");
        mWebCiQuWang.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setWebImageClick(view);
            }
        });
    }

    /**
     * 设置网页中图片的点击事件
     *
     * @param view
     */
    private void setWebImageClick(WebView view) {
        String jsCode = "javascript:(function(){" +
                "var imgs=document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<imgs.length;i++){" +
                "imgs[i].onclick=function(){" +
                "window.jsCallJavaObj.showBigImg(this.src);" +
                "}}})()";
        mWebCiQuWang.loadUrl(jsCode);
    }
}