package com.example.music.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ImageUtils;
import com.example.music.Constants;
import com.example.music.R;
import com.example.music.inter.JsCallJavaObj;
import com.example.music.utils.DownLoadUtile;
import com.example.music.utils.StatusBarUtil;

import java.io.File;

public class CiQuWangActivity extends AppCompatActivity {

    private WebView mWebCiQuWang;
    private Context mContext;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ci_qu_wang);
        StatusBarUtil.transparencyBar(this);
        this.mContext = this;
        initView();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 0:
                    Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(mContext, "保存失败，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
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
        mWebCiQuWang.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = mWebCiQuWang.getHitTestResult();
                int type = result.getType();
                if (type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || type == WebView.HitTestResult.IMAGE_TYPE) {//判断长按的地方是否是链接形式的图片
                    String extra = result.getExtra();
                    showAlerDialog(extra);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void showAlerDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_webvew, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setContentView(inflate);
        TextView mTvBaoCunXiangCe = inflate.findViewById(R.id.tv_baocunxiangce);
        TextView mTvDaoRuQuPu = inflate.findViewById(R.id.tv_daoruqupu);
        mTvBaoCunXiangCe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bitmap = DownLoadUtile.GetImageInputStream(url);
//                        b = DownLoadUtile.saveImageToGallery(mContext, bitmap);
                        File file = ImageUtils.save2Album(bitmap, Bitmap.CompressFormat.JPEG);
                        Message message = new Message();
                        if (file != null) {
                            message.arg1 = 0;
                            handler.sendMessage(message);
                        } else {
                            message.arg1 = 1;
                            handler.sendMessage(message);
                        }
                    }
                }).start();
                alertDialog.dismiss();
            }
        });
        mTvDaoRuQuPu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "图片路径" + url, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, DaoRuQuPuActivity.class);
                intent.putExtra(Constants.webImage, url);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
    }
}