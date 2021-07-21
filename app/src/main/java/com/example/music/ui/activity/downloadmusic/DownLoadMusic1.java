package com.example.music.ui.activity.downloadmusic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.DaoRuQuPuAdaper;
import com.example.music.adapter.FlowLayoutManager;
import com.example.music.adapter.SpaceItemDecoration;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.MusicBean;
import com.example.music.ui.activity.zhujiemian.BenDiYinYueActivity;
import com.example.music.utils.HomeProgressDialog;
import com.example.music.utils.SPBeanUtile;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.FileCallback;
import com.lzy.okhttputils.request.BaseRequest;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoadMusic1 extends AppCompatActivity {

    private WebView mWebMusic1;
    private Context mContext;
    private int position;//当前选中的文件夹的下标
    private ArrayList<BenDiYuePuBean> mList;
    private DaoRuQuPuAdaper daoRuQuPuAdaper;
    private AlertDialog alertDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load_music1);
        mContext = this;
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        mWebMusic1 = findViewById(R.id.web_download_music1);
        mList = SPBeanUtile.getWoDeYinYueFileList();
        if (mList == null) {
            mList = new ArrayList<>();
        }
        WebSettings webSettings = mWebMusic1.getSettings();
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
        mWebMusic1.setWebChromeClient(new WebChromeClient());
        mWebMusic1.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();//接收证书
            }
        });
        String url = "https://www.vfinemusic.com/music-library?utm_source=360&utm_medium=search&utm_campaign=PC-%E7%BD%91%E7%AB%99&utm_content=%E7%BD%91%E7%AB%99A&utm_term=%E4%B8%8B%E8%BD%BD%E9%9F%B3%E4%B9%90%E5%BA%93";
        mWebMusic1.loadUrl(url);
        //设置下载监听，每当有下载的时候就会进行拦截
        mWebMusic1.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                float size = contentLength / 1024 / 1024;
                //保留两位小数并四舍五入
                BigDecimal bg = new BigDecimal(size);
                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                String name = url.substring(url.lastIndexOf("/") + 1);
                showAlear(url, f1 + "MB", name);
            }
        });
    }

    private void showAlear(String url, String size, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_download_music, null);
        alertDialog1 = builder.create();
        alertDialog1.show();
        alertDialog1.setContentView(inflate);
        alertDialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog1.setCanceledOnTouchOutside(false);
        RecyclerView mRec = inflate.findViewById(R.id.download_music_rec);
        EditText mEditText = inflate.findViewById(R.id.download_music_ed_daoruqupu);
        TextView mTvMusicName = inflate.findViewById(R.id.download_tv_music_name);
        TextView mTvMusicSize = inflate.findViewById(R.id.download_tv_music_size);
        TextView mCancel = inflate.findViewById(R.id.download_music_cancel);
        TextView mDownLoad = inflate.findViewById(R.id.download_music_download);
        mTvMusicName.setText("名称：" + name);
        mTvMusicSize.setText("大小：" + size);
        daoRuQuPuAdaper = new DaoRuQuPuAdaper(mContext, mList);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mRec.addItemDecoration(new SpaceItemDecoration(dp2px(5)));
        mRec.setLayoutManager(flowLayoutManager);
        mRec.setAdapter(daoRuQuPuAdaper);
        //选择音乐文件夹
        daoRuQuPuAdaper.setOnItemClickListener(new DaoRuQuPuAdaper.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DownLoadMusic1.this.position = position;
                for (int i = 0; i < mList.size(); i++) {
                    BenDiYuePuBean benDiYuePuBean = mList.get(i);
                    benDiYuePuBean.setSelected(false);
                    mList.set(i, benDiYuePuBean);
                }
                BenDiYuePuBean benDiYuePuBean = mList.get(position);
                benDiYuePuBean.setSelected(true);
                mList.set(position, benDiYuePuBean);
                daoRuQuPuAdaper.notifyDataSetChanged();
            }
        });
        //添加音乐文件夹
        daoRuQuPuAdaper.setOnEndItemClickListener(new DaoRuQuPuAdaper.onEndItemClickListener() {
            @Override
            public void onEndItemClick() {
                showAlertDerlog();
            }
        });
        //取消下载
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        //下载
        mDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = mEditText.getText().toString();
                if (!TextUtils.isEmpty(fileName)) {
                    boolean nameXiangTong = isNameXiangTong(fileName);
                    if (nameXiangTong) {
                        DownLoad(url, fileName);
                    } else {
                        Toast.makeText(mContext, "已存在相同名称的音乐文件", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "请输入音乐名称", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ProgressDialog dialog;

    private void DownLoad(String urlString, String fileName) {
        dialog = HomeProgressDialog.getInstance(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCanceledOnTouchOutside(false);//点击屏幕其他地方不响应
        dialog.show();
        String path = MyApplication.getWoDeYinYueFile().getPath() + "/" + mList.get(position).getTitle() + "/";
        OkHttpUtils.get(urlString)//
                .tag(this)//
                .execute(new DownloadFileCallBack(path, fileName + ".mp3"));//保存到sd卡
    }

    private class DownloadFileCallBack extends FileCallback {

        public DownloadFileCallBack(String destFileDir, String destFileName) {
            super(destFileDir, destFileName);
        }

        @Override
        public void onBefore(BaseRequest request) {

        }

        @Override
        public void onResponse(boolean isFromCache, File file, Request request, Response response) {
            Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_LONG).show();
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            if (alertDialog1 != null) {
                alertDialog1.dismiss();
                alertDialog1 = null;
            }
            showalert();
        }

        @Override
        public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            System.out.println("downloadProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);
            if (dialog != null) {
                dialog.incrementProgressBy((int) (Math.round(progress * 10000) * 1.0f / 100));
            }
            String netSpeed = Formatter.formatFileSize(getApplicationContext(), networkSpeed);
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            Toast.makeText(mContext, "下载失败，请检查网络", Toast.LENGTH_SHORT).show();
        }
    }

    //下载成功
    private void showalert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_download_music_success, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView mTvJiXu = inflate.findViewById(R.id.success_jixudownload);
        TextView mTvQuKanKan = inflate.findViewById(R.id.success_qukankan);
        mTvJiXu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        mTvQuKanKan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownLoadMusic1.this, BenDiYinYueActivity.class);
                startActivity(intent);
                DownLoadMusic1.this.finish();
                alertDialog.dismiss();
            }
        });
    }

    private boolean isNameXiangTong(String fileName) {
        boolean isName = true;
        ArrayList<MusicBean> allMusic = SPBeanUtile.getAllMusic();
        if (allMusic != null && allMusic.size() > 0) {
            for (int i = 0; i < allMusic.size(); i++) {
                String name = allMusic.get(i).getName();
                if (fileName.equals(name)) {
                    isName = false;
                    break;
                } else {
                    isName = true;
                }
            }
        }
        return isName;
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private boolean classify = false;

    private void showAlertDerlog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_xinzeng, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView mTvCancel = inflate.findViewById(R.id.tv_cancel);
        TextView mTvEnter = inflate.findViewById(R.id.tv_enter);
        EditText mEdBenDiQuPu = inflate.findViewById(R.id.ed_bendiqupu);
        //确定
        mTvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEdBenDiQuPu.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    if (mList.size() != 0) {
                        for (int i = 0; i < mList.size(); i++) {
                            String title = mList.get(i).getTitle();
                            if (title.equals(text)) {
                                classify = false;
                                break;
                            } else {
                                classify = true;
                            }
                        }
                        if (classify) {
                            boolean tuPiQuPuFile = SPBeanUtile.createWoDeYinYueFile(text);
                            if (tuPiQuPuFile) {
                                for (int i = 0; i < mList.size(); i++) {
                                    BenDiYuePuBean benDiYuePuBean = mList.get(i);
                                    benDiYuePuBean.setSelected(false);
                                    mList.set(i, benDiYuePuBean);
                                }
                                BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                                mList.add(0, benDiYuePuBean);
                                daoRuQuPuAdaper.notifyDataSetChanged();
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "分类已经存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        boolean tuPiQuPuFile = SPBeanUtile.createWoDeYinYueFile(text);
                        if (tuPiQuPuFile) {
                            BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                            mList.add(0, benDiYuePuBean);
                            daoRuQuPuAdaper.notifyDataSetChanged();
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                            Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(mContext, "请输入分类名称", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //取消
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        });
    }
}