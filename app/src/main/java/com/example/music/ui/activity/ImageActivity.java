package com.example.music.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.music.R;
import com.example.music.adapter.ImageMagnifyAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager vpPop;
    TextView tvPop;
    TextView mTvDi;
    TextView mTvBanZou;
    ImageView mIvBack;
    ConstraintLayout mConTop;
    ConstraintLayout mConLayout;
    ConstraintLayout mConBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        vpPop = findViewById(R.id.vp_pop);
        tvPop = findViewById(R.id.tv_pop);
        mConTop = findViewById(R.id.con_top);
        mConBottom = findViewById(R.id.con_bottom);
        mConLayout = findViewById(R.id.con_layout);
        mTvDi = findViewById(R.id.tv_di);
        mIvBack = findViewById(R.id.iv_back);
        mTvBanZou = findViewById(R.id.tv_banzou);
        mIvBack.setOnClickListener(this);
        mTvDi.setOnClickListener(this);
        mTvBanZou.setOnClickListener(this);
        mConLayout.setOnClickListener(this);
        Intent intent = getIntent();
        int postion1 = intent.getIntExtra("position", 0);
        final ArrayList<String> list = intent.getStringArrayListExtra("list");
        ImageMagnifyAdapter imgVpAda = new ImageMagnifyAdapter(this, list);
        vpPop.setAdapter(imgVpAda);
        tvPop.setText(postion1 + 1 + "/" + list.size());
        vpPop.setCurrentItem(postion1);
        imgVpAda.setCallBack(new ImageMagnifyAdapter.onCallBack() {
            @Override
            public void onItemClick() {
                if (mConTop.getVisibility() == View.GONE) {
                    mConTop.setVisibility(View.VISIBLE);
                    mConBottom.setVisibility(View.VISIBLE);
                } else {
                    mConTop.setVisibility(View.GONE);
                    mConBottom.setVisibility(View.GONE);
                }
            }
        });
        vpPop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tvPop.setText(position + 1 + "/" + list.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                ImageActivity.this.finish();
                break;
            case R.id.tv_banzou:

                break;
            case R.id.tv_di:

                break;
            case R.id.con_layout:

                break;
        }
    }
}