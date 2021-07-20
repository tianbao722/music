package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music.R;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;

public class PDFImageActivity extends AppCompatActivity implements View.OnClickListener, OnPageChangeListener {

    private ImageView mIvBack;
    private TextView mPdfTvTitle;
    private TextView mPdfTvDi;
    private TextView mPdfTvPop;
    private TextView mPdfTvBanZou;
    private ConstraintLayout mConTop;
    private ConstraintLayout mConBottom;
    private int defaultNightMode;
    private Context mContext;
    private PDFView mPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_image);
        mContext = this;
        initView();
    }

    private void initView() {
        mIvBack = findViewById(R.id.pdf_iv_back);
        mPdfTvTitle = findViewById(R.id.pdf__tv_title);
        mPdfTvDi = findViewById(R.id.pdf_tv_di);
        mPdfTvBanZou = findViewById(R.id.pdf_tv_banzou);
        mConTop = findViewById(R.id.con_top);
        mConBottom = findViewById(R.id.con_bottom);
        mPdfTvPop = findViewById(R.id.pdf_tv_pop);
        mPdf = findViewById(R.id.pdf);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String file = intent.getStringExtra("file");
        mPdfTvTitle.setText(name);
        defaultNightMode = AppCompatDelegate.getDefaultNightMode();
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            mIvBack.setImageDrawable(getResources().getDrawable(R.mipmap.fanhui1));
        } else {
            mIvBack.setImageDrawable(getResources().getDrawable(R.mipmap.fanhui));
        }

        mIvBack.setOnClickListener(this);
        mPdfTvDi.setOnClickListener(this);
        mPdfTvBanZou.setOnClickListener(this);
        if (file != null) {
            display(file);
        }
    }

    private void display(String assetFileName) {
        File file = new File(assetFileName);
        if (file.exists()) {
            mPdf.fromFile(file)
                    //                .pages(0, 0, 0, 0, 0, 0) // 默认全部显示，pages属性可以过滤性显示
                    .defaultPage(1)//默认展示第一页
                    .onPageChange(this)//监听页面切换
                    .load();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pdf_iv_back:
                setFanhui();
                PDFImageActivity.this.finish();
                break;
            case R.id.pdf_tv_di:
                if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    //夜间 切换 日间
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//日间
                    recreate();
                } else {
                    //日间 切换 夜间
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//夜间
                    recreate();
                }
                break;
            case R.id.pdf_tv_banzou:

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setFanhui();
        PDFImageActivity.this.finish();
        return super.onKeyDown(keyCode, event);
    }

    private void setFanhui() {
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            //夜间 切换 日间
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//日间
            recreate();
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        mPdfTvPop.setText(page + "/" + pageCount);
    }
}