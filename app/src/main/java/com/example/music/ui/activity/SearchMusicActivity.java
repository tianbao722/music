package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.MusicAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.MusicBean;
import com.example.music.utils.SPBeanUtile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchMusicActivity extends AppCompatActivity {

    private SearchView mMusicSearchView;
    private RecyclerView mMusicRecSearch;
    private Context mContext;
    private MusicAdapter musicAdapter;
    private ArrayList<MusicBean> allMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
        mContext = this;
        initView();
    }

    private void initView() {
        mMusicSearchView = findViewById(R.id.music_search_view);
        mMusicRecSearch = findViewById(R.id.music_rec_search);
        setSearchView();
        allMusic = getAllMusic();
        Intent intent = getIntent();
        mMusicRecSearch.setLayoutManager(new GridLayoutManager(mContext, 2));
        musicAdapter = new MusicAdapter(allMusic, mContext);
        mMusicRecSearch.setAdapter(musicAdapter);
        musicAdapter.setOnItemClickListener(new MusicAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, MusicBean musicBean) {
                intent.putExtra("path", musicBean.getPath());
                String path = MyApplication.getWoDeYinYueFile().getPath();
                int length = path.length();
                String title = musicBean.getPath().substring(length + 1, musicBean.getPath().length() - 4 - (musicBean.getName().length()) - 1);
                intent.putExtra("name", musicBean.getName());
                intent.putExtra("title", title);
                setResult(2, intent);
                SearchMusicActivity.this.finish();
            }
        });
    }

    private ArrayList<MusicBean> getAllMusic() {
        ArrayList<MusicBean> musicBeans = new ArrayList<>();
        ArrayList<BenDiYuePuBean> mList = SPBeanUtile.getWoDeYinYueFileList();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (int i = 0; i < mList.size(); i++) {
            String title = mList.get(i).getTitle();
            String path = MyApplication.getWoDeYinYueFile().getPath();
            String currentPath = path + "/" + title;
            List<File> files = FileUtils.listFilesInDir(currentPath);
            for (int j = 0; j < files.size(); j++) {
                String path1 = files.get(j).getPath();
                mmr.setDataSource(path1);
                String fileName = FileUtils.getFileName(files.get(j));
                String name = fileName.substring(0, fileName.length() - 4);
                String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String size = FileUtils.getSize(files.get(j));
                long time1 = Long.parseLong(time);
                MusicBean musicBean = new MusicBean(name, time1, size, path1);
                musicBeans.add(musicBean);
            }
        }
        return musicBeans;
    }


    public void setSearchView() {
        // 设置搜索文本监听
        mMusicSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                musicAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}