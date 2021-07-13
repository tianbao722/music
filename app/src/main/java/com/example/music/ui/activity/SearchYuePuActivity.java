package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.music.R;

public class SearchYuePuActivity extends AppCompatActivity {
    private SearchView mYuePuSearchView;
    private RecyclerView mREcSearchYuePu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_yue_pu);
        initView();
    }

    private void initView() {
        mYuePuSearchView = findViewById(R.id.yuepu_search_view);
        mREcSearchYuePu = findViewById(R.id.rec_search_yuepu);
        initListener();
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
                return false;
            }
        });
    }
}