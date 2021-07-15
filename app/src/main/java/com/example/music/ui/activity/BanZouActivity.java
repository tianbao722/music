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
        ArrayList<MusicBean> allMusic = getAllMusic();
        list = new ArrayList<>();
        for (MusicBean bean : allMusic) {
            if (bean.getName().contains(title) || bean.getName().contains(title)) {
                list.add(bean);
            }
        }
        musicAdapter = new MusicAdapter(list, mContext);
        mRecBanZouMusicRecSearch.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecBanZouMusicRecSearch.setAdapter(musicAdapter);
        musicAdapter.setOnItemClickListener(new MusicAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, MusicBean musicBean) {
                String json = PreferenceUtil.getInstance().getString(title, null);
                if (!TextUtils.isEmpty(json)) {
                    BanZouListBean banZouListBean = new Gson().fromJson(json, BanZouListBean.class);
                    ArrayList<BanZouBean> banZouBeans = banZouListBean.getList();
                    String name = list.get(position).getName();
                    String path = list.get(position).getPath();
                    BanZouBean banZouBean = new BanZouBean(name, path);
                    banZouBeans.add(banZouBean);
                    banZouListBean.setList(banZouBeans);
                    String newJson = new Gson().toJson(banZouListBean);
                    PreferenceUtil.getInstance().saveString(title, newJson);
                } else {
                    ArrayList<BanZouBean> banZouBeans = new ArrayList<>();
                    String name = list.get(position).getName();
                    String path = list.get(position).getPath();
                    BanZouBean banZouBean = new BanZouBean(name, path);
                    banZouBeans.add(banZouBean);
                    BanZouListBean banZouListBean = new BanZouListBean(banZouBeans);
                    String newJson = new Gson().toJson(banZouListBean);
                    PreferenceUtil.getInstance().saveString(title, newJson);
                }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baozou_iv_back:
                BanZouActivity.this.finish();
                break;
        }
    }
}