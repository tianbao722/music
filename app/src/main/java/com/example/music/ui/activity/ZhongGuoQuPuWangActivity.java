package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.music.Constants;
import com.example.music.R;
import com.example.music.inter.JsCallJavaObj;

public class ZhongGuoQuPuWangActivity extends AppCompatActivity {

    private WebView mWeb_zhongGuoQuPu;
    private Context mContext;

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
        this.mContext = this;

        // 这行代码一定加上否则效果不会出现
        mWeb_zhongGuoQuPu.getSettings().setJavaScriptEnabled(true);
        mWeb_zhongGuoQuPu.addJavascriptInterface(new JsCallJavaObj() {
            @JavascriptInterface
            @Override
            public void showBigImg(String url) {
                Toast.makeText(mContext, "图片路径" + url, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, DaoRuQuPuActivity.class);
                intent.putExtra(Constants.webImage, url);
                startActivity(intent);
            }
        }, "jsCallJavaObj");
        mWeb_zhongGuoQuPu.setWebViewClient(new WebViewClient() {
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
        mWeb_zhongGuoQuPu.loadUrl(jsCode);
    }
}