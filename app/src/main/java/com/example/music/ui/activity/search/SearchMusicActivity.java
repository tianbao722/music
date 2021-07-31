package com.example.music.ui.activity.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.MusicAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.MusicBean;
import com.example.music.sqlitleutile.DatabaseHelper;
import com.example.music.ui.activity.BanZouActivity;
import com.example.music.utils.SPBeanUtile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchMusicActivity extends AppCompatActivity {
    private SearchView mMusicSearchView;
    private RecyclerView mMusicRecSearch;
    private Context mContext;
    private MusicAdapter musicAdapter;
    private ImageView mIvBack;
    private ArrayList<MusicBean> list;
    private ImageView mIvLoading;
    private DatabaseHelper myDb;
    private String searchText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
        mContext = this;
        myDb = new DatabaseHelper(this);
        initView();
    }

    private void initView() {
        mMusicSearchView = findViewById(R.id.music_search_view);
        mMusicRecSearch = findViewById(R.id.music_rec_search);
        mIvLoading = findViewById(R.id.iv_loading);
        mIvBack = findViewById(R.id.iv_back);
        Glide.with(mContext).load(R.mipmap.loading).into(mIvLoading);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchMusicActivity.this.finish();
            }
        });
        setSearchView();
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute("s");
        list = new ArrayList<>();
        Intent intent = getIntent();
        mMusicRecSearch.setLayoutManager(new GridLayoutManager(mContext, 2));
        musicAdapter = new MusicAdapter(list, mContext);
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
                if (!TextUtils.isEmpty(searchText)) {
                    boolean b = myDb.insertData(searchText);
                }
                SearchMusicActivity.this.finish();
            }
        });
    }

    public void setSearchView() {
        // 设置搜索文本监听
        mMusicSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                musicAdapter.getFilter().filter(query);
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                musicAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    class MyAsyncTask extends AsyncTask<String, Integer, ArrayList<MusicBean>> {

        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程
            mIvLoading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<MusicBean> doInBackground(String... params) {
            //这是在后台子线程中执行的
            ArrayList<MusicBean> allMusic = SPBeanUtile.getAllMusic();
            return allMusic;
        }

        @Override
        protected void onCancelled() {
            //当任务被取消时回调
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //更新进度
        }

        @Override
        protected void onPostExecute(ArrayList<MusicBean> musicBeans) {
            super.onPostExecute(musicBeans);
            //当任务执行完成是调用,在UI线程
            mIvLoading.setVisibility(View.GONE);
            list = musicBeans;
            musicAdapter.setData(list);
        }
    }

}