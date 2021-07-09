package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music.R;
import com.example.music.adapter.BenDiYuePuPagerAdapter;
import com.example.music.ui.fragment.DPFYuePuFragment;
import com.example.music.ui.fragment.TuPianYuePuFragment;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BenDiQuPuActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private ImageView mIvShear;
    private TabLayout mTabQuPu;
    private ViewPager mVp;
    private TuPianYuePuFragment tuPianYuePuFragment;
    private DPFYuePuFragment dpfYuePuFragment;
    private ArrayList<String> titles;
    private ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ben_di_qu_pu);
        initView();
        initListener();
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mIvShear = findViewById(R.id.iv_shear);
        mTabQuPu = findViewById(R.id.tab_qupu);
        mVp = findViewById(R.id.vp);
        mIvBack.setOnClickListener(this);
        mIvShear.setOnClickListener(this);
        tuPianYuePuFragment = new TuPianYuePuFragment();
        dpfYuePuFragment = new DPFYuePuFragment();
        titles = new ArrayList<>();
        titles.add("图片乐谱");
        titles.add("DPF乐谱");
        fragments = new ArrayList<>();
        fragments.add(tuPianYuePuFragment);
        fragments.add(dpfYuePuFragment);
        mTabQuPu.addTab(mTabQuPu.newTab().setText("图片乐谱"));
        mTabQuPu.addTab(mTabQuPu.newTab().setText("DPF乐谱"));
        BenDiYuePuPagerAdapter benDiYuePuPagerAdapter = new BenDiYuePuPagerAdapter(getSupportFragmentManager(), titles, fragments);
        mVp.setAdapter(benDiYuePuPagerAdapter);
        mTabQuPu.setupWithViewPager(mVp);
    }

    private void initListener() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.bendiquputab1, null);
        View inflate1 = LayoutInflater.from(this).inflate(R.layout.bendiquputab2, null);
        //TabLayout监听
        mTabQuPu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView mTvTabTitle = inflate.findViewById(R.id.tv_tab_title);
                mTvTabTitle.setText(tab.getText());
                tab.setCustomView(inflate);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView mTvTabTitle = inflate1.findViewById(R.id.tv_tab_title);
                mTvTabTitle.setText(tab.getText());
                tab.setCustomView(inflate1);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                TextView mTvTabTitle = inflate.findViewById(R.id.tv_tab_title);
                mTvTabTitle.setText(tab.getText());
                tab.setCustomView(inflate);
            }
        });
        mTabQuPu.getTabAt(0).select();
    }

    private void setTabTV() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                BenDiQuPuActivity.this.finish();
                break;
            case R.id.iv_shear:
                Intent intent = new Intent(BenDiQuPuActivity.this, SearchYuePuActivity.class);
                startActivity(intent);
                break;
        }
    }
}