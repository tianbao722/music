package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.MusicAdapter;
import com.example.music.bean.BanZouBean;
import com.example.music.bean.BanZouListBean;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.MusicBean;
import com.example.music.utils.PreferenceUtil;
import com.example.music.utils.SPBeanUtile;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BanZouActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecBanZouMusicRecSearch;
    private SearchView mBanZouMusiSearchView;
    private ImageView mIVBack;
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
        mIVBack = findViewById(R.id.baozou_iv_back);
        mIVBack.setOnClickListener(this);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        list = SPBeanUtile.getAllMusic();
        if (list != null && list.size() > 0) {
            mRecBanZouMusicRecSearch.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            mRecBanZouMusicRecSearch.setBackgroundColor(getResources().getColor(R.color.touming));
        }
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
                    } else {
                        Toast.makeText(mContext, "已关联", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ArrayList<BanZouBean> banZouBeans = new ArrayList<>();
                    BanZouBean banZouBean = new BanZouBean(name, path);
                    banZouBeans.add(banZouBean);
                    BanZouListBean banZouListBean = new BanZouListBean(banZouBeans);
                    String newJson = new Gson().toJson(banZouListBean);
                    PreferenceUtil.getInstance().saveString(title, newJson);
                }
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
}