package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.MusicAdapter;
import com.example.music.bean.BanZouBean;
import com.example.music.bean.BanZouListBean;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.MusicBean;
import com.example.music.utils.HomeProgressDialog;
import com.example.music.utils.PreferenceUtil;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.XToast;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BanZouActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecBanZouMusicRecSearch;
    private SearchView mBanZouMusiSearchView;
    private ImageView mIVBack;
    private TextView mTvNullMusic;
    private ImageView mIvLoading;
    private ArrayList<MusicBean> list;
    private Context mContext;
    private MusicAdapter musicAdapter;
    private String title;
    private boolean isRepetition = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban_zou);
        mContext = this;
        initView();
        setSearchView();
    }

    public void setSearchView() {
        // 设置搜索文本监听
        mBanZouMusiSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void initView() {
        mRecBanZouMusicRecSearch = findViewById(R.id.banzou_music_rec_search);
        mBanZouMusiSearchView = findViewById(R.id.banzou_music_search_view);
        mTvNullMusic = findViewById(R.id.tv_null_music);
        mIvLoading = findViewById(R.id.iv_loading);
        mIVBack = findViewById(R.id.baozou_iv_back);
        Glide.with(mContext).load(R.mipmap.loading).into(mIvLoading);
        mIVBack.setOnClickListener(this);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute("s");
        list = new ArrayList<>();
        musicAdapter = new MusicAdapter(list, mContext);
        mRecBanZouMusicRecSearch.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecBanZouMusicRecSearch.setAdapter(musicAdapter);
        musicAdapter.setOnItemClickListener(new MusicAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, MusicBean musicBean) {
                String json = PreferenceUtil.getInstance().getString(title, null);
                String name = musicBean.getName();
                String path = musicBean.getPath();
                if (!TextUtils.isEmpty(json)) {
                    BanZouListBean banZouListBean = new Gson().fromJson(json, BanZouListBean.class);
                    ArrayList<BanZouBean> banZouBeans = banZouListBean.getList();
                    for (int i = 0; i < banZouBeans.size(); i++) {
                        BanZouBean banZouBean = banZouBeans.get(i);
                        String name1 = banZouBean.getName();
                        String path1 = banZouBean.getPath();
                        if (name.equals(name1) && path.equals(path1)) {
                            isRepetition = false;
                            break;
                        } else {
                            isRepetition = true;
                        }
                    }
                    if (isRepetition) {
                        BanZouBean banZouBean = new BanZouBean(name, path);
                        banZouBeans.add(banZouBean);
                        banZouListBean.setList(banZouBeans);
                        String newJson = new Gson().toJson(banZouListBean);
                        PreferenceUtil.getInstance().saveString(title, newJson);
                    }
                } else {
                    ArrayList<BanZouBean> banZouBeans = new ArrayList<>();
                    BanZouBean banZouBean = new BanZouBean(name, path);
                    banZouBeans.add(banZouBean);
                    BanZouListBean banZouListBean = new BanZouListBean(banZouBeans);
                    String newJson = new Gson().toJson(banZouListBean);
                    PreferenceUtil.getInstance().saveString(title, newJson);
                }
                new XToast<>(BanZouActivity.this)
                        .setDuration(2000)
                        .setView(R.layout.toast_hint)
                        .setAnimStyle(android.R.style.Animation_Translucent)
                        .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                        .setText(android.R.id.message, "已关联")
                        .show();
                setResult(2, intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baozou_iv_back:
                BanZouActivity.this.finish();
                break;
        }
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
            if (list != null && list.size() > 0) {
                mTvNullMusic.setVisibility(View.GONE);
                mRecBanZouMusicRecSearch.setVisibility(View.VISIBLE);
            } else {
                mTvNullMusic.setVisibility(View.VISIBLE);
                mRecBanZouMusicRecSearch.setVisibility(View.GONE);
            }
        }
    }
}