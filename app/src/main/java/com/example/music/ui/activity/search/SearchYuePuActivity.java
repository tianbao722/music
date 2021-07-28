package com.example.music.ui.activity.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.SearchDPFAdapter;
import com.example.music.adapter.SearchYuePuAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.bean.PDFImageBean;
import com.example.music.ui.activity.ImageActivity;
import com.example.music.ui.activity.PDFImageActivity;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.StatusBarUtil;

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
    private SearchDPFAdapter searchPDFAdapter;
    private ArrayList<ImageYuePuImageBean> list;//图片
    private ArrayList<PDFImageBean> PDFlist;//图片
    private Context mContext;
    private String txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_yue_pu);
        StatusBarUtil.transparencyBar(this);
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
        initTuPianAdapter();
        initListener();
    }

    private void initTuPianAdapter() {
        ArrayList<BenDiYuePuBean> tuPianQuPuFileList = SPBeanUtile.getTuPianQuPuFileList();
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = new ArrayList<>();
        if (tuPianQuPuFileList != null && tuPianQuPuFileList.size() > 0) {
            benDiYuePuBeans.addAll(tuPianQuPuFileList);
        }
        list = new ArrayList<>();
        getImageFileList(benDiYuePuBeans);
        mREcSearchYuePu.setLayoutManager(new GridLayoutManager(mContext, 2));
        searchYuePuAdapter = new SearchYuePuAdapter(mContext, list);
        mREcSearchYuePu.setAdapter(searchYuePuAdapter);
        //适配器点击监听
        searchYuePuAdapter.setOnItemClickListener(new SearchYuePuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String name = list.get(position).getName();
                List<File> list2 = list.get(position).getList();
                ArrayList<String> image = new ArrayList<>();
                for (int i = 0; i < list2.size(); i++) {
                    String path = list2.get(i).getPath();
                    image.add(path);
                }
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putStringArrayListExtra("list", image);
                intent.putExtra("title", name);
                startActivity(intent);
            }
        });
    }

    private void initPDFAdapter() {
        ArrayList<BenDiYuePuBean> tuPianQuPuFileList = SPBeanUtile.getDefQuPuFileList();
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = new ArrayList<>();
        if (tuPianQuPuFileList != null && tuPianQuPuFileList.size() > 0) {
            benDiYuePuBeans.addAll(tuPianQuPuFileList);
        }
        PDFlist = new ArrayList<>();
        getPDFFileList(benDiYuePuBeans);
        mREcSearchYuePu.setLayoutManager(new GridLayoutManager(mContext, 2));
        searchPDFAdapter = new SearchDPFAdapter(mContext, PDFlist);
        mREcSearchYuePu.setAdapter(searchPDFAdapter);
        searchPDFAdapter.setOnItemClickListener(new SearchDPFAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PDFImageBean pdfImageBean = PDFlist.get(position);
                Intent intent = new Intent(mContext, PDFImageActivity.class);
                intent.putExtra("name", pdfImageBean.getName());
                intent.putExtra("file", pdfImageBean.getFile().getPath());
                startActivity(intent);
            }
        });
    }

    //获取所有图片
    private void getImageFileList(ArrayList<BenDiYuePuBean> benDiYuePuBeans) {
        list.clear();
        String path = MyApplication.getTuPianYuePuFile().getPath();
        for (int i = 0; i < benDiYuePuBeans.size(); i++) {
            String currentPath = path + "/" + benDiYuePuBeans.get(i).getTitle();
            List<File> files = FileUtils.listFilesInDir(currentPath);
            if (files != null && files.size() > 0) {
                for (int j = 0; j < files.size(); j++) {
                    String name = files.get(j).getName();
                    List<File> files1 = FileUtils.listFilesInDir(files.get(j).getPath());
                    ImageYuePuImageBean imageYuePuImageBean = new ImageYuePuImageBean(name, files1, files1.size());
                    list.add(imageYuePuImageBean);
                }
            }
        }
    }

    //获取所有PDF
    private void getPDFFileList(ArrayList<BenDiYuePuBean> benDiYuePuBeans) {
        String path = MyApplication.getDefYuePuFile().getPath();
        for (int i = 0; i < benDiYuePuBeans.size(); i++) {
            String currentPath = path + "/" + benDiYuePuBeans.get(i).getTitle();
            List<File> files = FileUtils.listFilesInDir(currentPath);
            if (files != null && files.size() > 0) {
                for (int j = 0; j < files.size(); j++) {
                    String name = files.get(j).getName();
                    String size = FileUtils.getSize(files.get(j));
                    PDFImageBean pdfImageBean = new PDFImageBean(name, files.get(j), size);
                    PDFlist.add(pdfImageBean);
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
                initTuPianAdapter();
                mTvSearchTuPianYuePu.setTextColor(getResources().getColor(R.color.black));
                mTvSearchDefYuePu.setTextColor(getResources().getColor(R.color.hui));
                searchYuePuAdapter.getFilter().filter(txt);
                break;
            case R.id.tv_search_defyuepu://Def乐谱
                initPDFAdapter();
                mTvSearchTuPianYuePu.setTextColor(getResources().getColor(R.color.hui));
                mTvSearchDefYuePu.setTextColor(getResources().getColor(R.color.black));
                searchPDFAdapter.getFilter().filter(txt);
                break;
        }
    }
}