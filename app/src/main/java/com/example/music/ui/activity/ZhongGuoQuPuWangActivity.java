package com.example.music.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ImageUtils;
import com.example.music.Constants;
import com.example.music.R;
import com.example.music.inter.JsCallJavaObj;
import com.example.music.utils.DownLoadUtile;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ZhongGuoQuPuWangActivity extends AppCompatActivity {

    private WebView mWeb_zhongGuoQuPu;
    private Context mContext;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhong_guo_qu_pu_wang);
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


    private void initView() {
        mWeb_zhongGuoQuPu = findViewById(R.id.web_zhognguoqupuwang);
        mWeb_zhongGuoQuPu.loadUrl("https://www.qupu123.com/Mobile");
        mWeb_zhongGuoQuPu.setWebViewClient(new WebViewClient());
        this.mContext = this;
        // 这行代码一定加上否则效果不会出现
        mWeb_zhongGuoQuPu.getSettings().setJavaScriptEnabled(true);
        mWeb_zhongGuoQuPu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = mWeb_zhongGuoQuPu.getHitTestResult();
                int type = result.getType();
                if (type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {//判断长按的地方是否是链接形式的图片
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
                        File file = ImageUtils.save2Album(bitmap, CompressFormat.JPEG);
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