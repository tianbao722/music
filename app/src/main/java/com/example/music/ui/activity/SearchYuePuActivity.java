package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.SearchYuePuAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.utils.SPBeanUtile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchYuePuActivity extends AppCompatActivity implements View.OnClickListener {
    private SearchView mYuePuSearchView;
    private ImageView mIvBack;
    private RecyclerView mREcSearchYuePu;
    private TextView mTvSearchTuPianYuePu;
    private TextView mTvSearchDefYuePu;
    private SearchYuePuAdapter searchYuePuAdapter;
    private ArrayList<ImageYuePuImageBean> list;
    private Context mContext;
    private boolean isSelect = true;
    private String txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_yue_pu);
        mContext = this;
        initView();
    }

    private void initView() {
        mYuePuSearchView = findViewById(R.id.yuepu_search_view);
        mREcSearchYuePu = findViewById(R.id.rec_search_yuepu);
        mTvSearchTuPianYuePu = findViewById(R.id.tv_search_tupianyuepu);
        mTvSearchDefYuePu = findViewById(R.id.tv_search_defyuepu);
        mIvBack = findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mTvSearchDefYuePu.setOnClickListener(this);
        mTvSearchTuPianYuePu.setOnClickListener(this);
        ArrayList<BenDiYuePuBean> tuPianQuPuFileList = SPBeanUtile.getTuPianQuPuFileList();
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = new ArrayList<>();
        benDiYuePuBeans.addAll(tuPianQuPuFileList);
        list = new ArrayList<>();
        getImageFileList(true, benDiYuePuBeans);
        mREcSearchYuePu.setLayoutManager(new GridLayoutManager(mContext, 2));
        searchYuePuAdapter = new SearchYuePuAdapter(mContext, list);
        mREcSearchYuePu.setAdapter(searchYuePuAdapter);
        initListener();
    }

    private void getImageFileList(boolean isTuPianOrDef, ArrayList<BenDiYuePuBean> benDiYuePuBeans) {
        list.clear();
        String path;
        if (isTuPianOrDef) {
            path = MyApplication.getTuPianYuePuFile().getPath();
        } else {
            path = MyApplication.getDefYuePuFile().getPath();
        }
        for (int i = 0; i < benDiYuePuBeans.size(); i++) {
            String currentPath = path + "/" + benDiYuePuBeans.get(i).getTitle();
            List<File> files = FileUtils.listFilesInDir(currentPath);
            if (files != null && files.size() > 0) {
                for (int j = 0; j < files.size(); j++) {
                    String name = files.get(j).getName();
                    List<File> files1 = FileUtils.listFilesInDir(files.get(i).getPath());
                    ImageYuePuImageBean imageYuePuImageBean = new ImageYuePuImageBean(name, files1, files1.size());
                    list.add(imageYuePuImageBean);
                }
            }
        }
    }

    private void initListener() {
        // 设置搜索文本监听
        mYuePuSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                txt = newText;
                searchYuePuAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回
                SearchYuePuActivity.this.finish();
                break;
            case R.id.tv_search_tupianyuepu://图片乐谱
                mTvSearchTuPianYuePu.setTextColor(getResources().getColor(R.color.black));
                mTvSearchDefYuePu.setTextColor(getResources().getColor(R.color.hui));
                ArrayList<BenDiYuePuBean> tuPianQuPuFileList = SPBeanUtile.getTuPianQuPuFileList();
                getImageFileList(true, tuPianQuPuFileList);
                searchYuePuAdapter.getFilter().filter(txt);
                break;
            case R.id.tv_search_defyuepu://Def乐谱
                mTvSearchTuPianYuePu.setTextColor(getResources().getColor(R.color.hui));
                mTvSearchDefYuePu.setTextColor(getResources().getColor(R.color.black));
                ArrayList<BenDiYuePuBean> defQuPuFileList = SPBeanUtile.getDefQuPuFileList();
                getImageFileList(false, defQuPuFileList);
                searchYuePuAdapter.getFilter().filter(txt);
                break;
        }
    }
}