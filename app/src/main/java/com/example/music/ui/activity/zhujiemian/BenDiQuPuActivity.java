package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.BenDiYuePuPagerAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.ui.activity.search.SearchYuePuActivity;
import com.example.music.ui.fragment.DPFYuePuFragment;
import com.example.music.ui.fragment.TuPianYuePuFragment;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.StatusBarUtil;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        StatusBarUtil.transparencyBar(this);
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
        startThread();
        tuPianYuePuFragment = new TuPianYuePuFragment();
        dpfYuePuFragment = new DPFYuePuFragment();
        titles = new ArrayList<>();
        titles.add("图片乐谱");
        titles.add("PDF乐谱");
        fragments = new ArrayList<>();
        fragments.add(tuPianYuePuFragment);
        fragments.add(dpfYuePuFragment);
        mTabQuPu.addTab(mTabQuPu.newTab().setText("图片乐谱"));
        mTabQuPu.addTab(mTabQuPu.newTab().setText("PDF乐谱"));
        BenDiYuePuPagerAdapter benDiYuePuPagerAdapter = new BenDiYuePuPagerAdapter(getSupportFragmentManager(), titles, fragments);
        mVp.setAdapter(benDiYuePuPagerAdapter);
        mTabQuPu.setupWithViewPager(mVp);
    }

    private void startThread() {
        ThreadUtils.Task task = new ThreadUtils.Task<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                //这是在后台子线程中执行的
                boolean imageFileList = getImageFileList();
                return imageFileList;
            }

            @Override
            public void onSuccess(Boolean b) {
                //当任务执行完成是调用,在UI线程
                if (b) {
                    Log.i("删除文件成功", "onSuccess: " + b);
                } else {
                    Log.i("删除文件失败", "onError: " + b);
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        };
        ThreadUtils.executeBySingle(task);
    }

    //删除所有不是图片的文件
    private boolean getImageFileList() {
        boolean isAllDelete = false;
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = SPBeanUtile.getTuPianQuPuFileList();
        String path = MyApplication.getTuPianYuePuFile().getPath();
        for (int i = 0; i < benDiYuePuBeans.size(); i++) {
            String currentPath = path + "/" + benDiYuePuBeans.get(i).getTitle();
            List<File> files = FileUtils.listFilesInDir(currentPath);
            if (files != null && files.size() > 0) {
                for (int j = 0; j < files.size(); j++) {
                    boolean dir = FileUtils.isDir(files.get(j));
                    if (dir) {
                        List<File> files1 = FileUtils.listFilesInDir(files.get(j).getPath());
                        for (int n = 0; n < files1.size(); n++) {
                            boolean image = ImageUtils.isImage(files1.get(n));
                            if (!image) {
                                isAllDelete = FileUtils.delete(files1.get(n));
                            }
                        }
                    }
                }
            }
        }
        return isAllDelete;
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