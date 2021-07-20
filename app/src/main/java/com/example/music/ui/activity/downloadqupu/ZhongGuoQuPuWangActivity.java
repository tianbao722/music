package com.example.music.ui.activity.downloadqupu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ImageUtils;
import com.example.music.Constants;
import com.example.music.R;
import com.example.music.bean.UrlImageListBean;
import com.example.music.ui.activity.zhujiemian.DaoRuQuPuActivity;
import com.example.music.utils.DownLoadUtile;
import com.example.music.utils.PreferenceUtil;
import com.example.music.utils.StatusBarUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

public class ZhongGuoQuPuWangActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView mWeb_zhongGuoQuPu;
    private Context mContext;
    private Bitmap bitmap;
    private TextView mTvCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhong_guo_qu_pu_wang);
        StatusBarUtil.transparencyBar(this);

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
        mTvCancel = findViewById(R.id.tv_cancel1);
        mTvCancel.setVisibility(View.GONE);
        mWeb_zhongGuoQuPu.loadUrl("https://www.qupu123.com/Mobile");
        mWeb_zhongGuoQuPu.setWebViewClient(new WebViewClient());
        mTvCancel.setOnClickListener(this);
        this.mContext = this;
        // 这行代码一定加上否则效果不会出现
        mWeb_zhongGuoQuPu.getSettings().setJavaScriptEnabled(true);
        mWeb_zhongGuoQuPu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = mWeb_zhongGuoQuPu.getHitTestResult();
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

            private ArrayList<String> list;
            private UrlImageListBean urlImageListBean;

            @Override
            public void onClick(View v) {
                String json = PreferenceUtil.getInstance().getString(Constants.webImage, null);
                if (!TextUtils.isEmpty(json)) {
                    urlImageListBean = new Gson().fromJson(json, UrlImageListBean.class);
                    list = urlImageListBean.getList();
                    if (list != null && list.size() > 0) {
                        list.add(url);
                    } else {
                        list = new ArrayList<>();
                        list.add(url);
                    }
                    urlImageListBean.setList(list);
                } else {
                    list = new ArrayList<>();
                    list.add(url);
                    urlImageListBean = new UrlImageListBean(list);
                }
                String json1 = new Gson().toJson(urlImageListBean);
                PreferenceUtil.getInstance().saveString(Constants.webImage, json1);
                Intent intent = new Intent(mContext, DaoRuQuPuActivity.class);
                startActivityForResult(intent, 1);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            mTvCancel.setVisibility(View.VISIBLE);
        } else {
            mTvCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel1:
                Intent intent = new Intent(mContext, DaoRuQuPuActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }
}