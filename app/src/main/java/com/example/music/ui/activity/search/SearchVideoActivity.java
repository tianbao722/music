package com.example.music.ui.activity.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.music.R;
import com.example.music.adapter.DongTaiVideoAdapter;
import com.example.music.bean.MusicBean;
import com.example.music.ui.activity.VideoPlayActivity;
import com.example.music.utils.SPBeanUtile;

import java.util.ArrayList;

public class SearchVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private SearchView mSearchVideo;
    private RecyclerView mRecVideo;
    private Context mContext;
    private ArrayList<MusicBean> allVideoList;
    private DongTaiVideoAdapter dongTaiVideoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video);
        mContext = this;
        initView();
    }

    private void initView() {
        mIvBack = findViewById(R.id.search_video_iv_back);
        mSearchVideo = findViewById(R.id.video_search_view);
        mRecVideo = findViewById(R.id.video_rec_search);
        mIvBack.setOnClickListener(this);
        setSearchView();
        allVideoList = SPBeanUtile.getAllVideo();
        if (allVideoList == null) {
            allVideoList = new ArrayList<>();
        }
        mRecVideo.setLayoutManager(new LinearLayoutManager(mContext));
        dongTaiVideoAdapter = new DongTaiVideoAdapter(allVideoList, mContext);
        mRecVideo.setAdapter(dongTaiVideoAdapter);
        dongTaiVideoAdapter.setOnItemClickListener(new DongTaiVideoAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(SearchVideoActivity.this, VideoPlayActivity.class);
                MusicBean musicBean = allVideoList.get(position);
                intent.putExtra("name", musicBean.getName());
                intent.putExtra("path", musicBean.getPath());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_video_iv_back://返回
                SearchVideoActivity.this.finish();
                break;
        }
    }

    public void setSearchView() {
        // 设置搜索文本监听
        mSearchVideo.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                dongTaiVideoAdapter.getFilter().filter(query);
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                dongTaiVideoAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}