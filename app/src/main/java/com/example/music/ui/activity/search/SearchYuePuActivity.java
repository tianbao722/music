package com.example.music.ui.activity.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.Glide;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.SearchDPFAdapter;
import com.example.music.adapter.SearchYuePuAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.bean.PDFImageBean;
import com.example.music.ui.activity.ImageActivity;
import com.example.music.ui.activity.PDFImageActivity;
import com.example.music.ui.fragment.TuPianYuePuFragment;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.StatusBarUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class SearchYuePuActivity extends AppCompatActivity implements View.OnClickListener {
    private SearchView mYuePuSearchView;
    private ImageView mIvBack;
    private RecyclerView mREcSearchYuePu;
    private TextView mTvSearchTuPianYuePu;
    private TextView mTvSearchDefYuePu;
    private TextView mTvZong;
    private SearchYuePuAdapter searchYuePuAdapter;
    private SearchDPFAdapter searchPDFAdapter;
    private ArrayList<ImageYuePuImageBean> list;//图片
    private ArrayList<PDFImageBean> PDFlist;//图片
    private Context mContext;
    private String txt;
    private ImageView mIvLoading;
    private boolean isPDFYuePu = true;
    private boolean isTouch = false;

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
        mTvZong = findViewById(R.id.tv_zong);
        mIvBack = findViewById(R.id.iv_back);
        mIvLoading = findViewById(R.id.iv_loading);
        Glide.with(mContext).load(R.mipmap.loading).into(mIvLoading);
        mIvBack.setOnClickListener(this);
        mTvSearchDefYuePu.setOnClickListener(this);
        mTvSearchTuPianYuePu.setOnClickListener(this);
        list = new ArrayList<>();
        PDFlist = new ArrayList<>();
        initTuPianAdapter(true);
        initListener();
    }

    private void initTuPianAdapter(boolean isSearch) {
        mREcSearchYuePu.setLayoutManager(new GridLayoutManager(mContext, 2));
        searchYuePuAdapter = new SearchYuePuAdapter(mContext, list);
        mREcSearchYuePu.setAdapter(searchYuePuAdapter);
        //适配器点击监听
        searchYuePuAdapter.setOnItemClickListener(new SearchYuePuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, ImageYuePuImageBean imageYuePuImageBean) {
                String name = imageYuePuImageBean.getName();
                List<File> list2 = imageYuePuImageBean.getList();
                ArrayList<String> image = new ArrayList<>();
                for (int i = 0; i < list2.size(); i++) {
                    String path = list2.get(i).getPath();
                    image.add(path);
                }
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putStringArrayListExtra("list", image);
                intent.putExtra("title", name);
                startActivity(intent);
                SearchYuePuActivity.this.finish();
            }
        });
        if (isSearch) {
            mIvLoading.setVisibility(View.VISIBLE);
            ThreadUtils.Task task = new ThreadUtils.Task<ArrayList<ImageYuePuImageBean>>() {
                @Override
                public ArrayList<ImageYuePuImageBean> doInBackground() throws Throwable {
                    //这是在后台子线程中执行的
                    ArrayList<ImageYuePuImageBean> imageFileList = getImageFileList();
                    return imageFileList;
                }

                @Override
                public void onSuccess(ArrayList<ImageYuePuImageBean> imageYuePuImageBeans) {
                    //当任务执行完成是调用,在UI线程
                    mIvLoading.setVisibility(View.GONE);
                    mTvZong.setText("图片乐谱总数：" + imageYuePuImageBeans.size());
                    list.clear();
                    list = imageYuePuImageBeans;
                    if (searchYuePuAdapter != null)
                        searchYuePuAdapter.setData(imageYuePuImageBeans);
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onFail(Throwable t) {

                }
            };
            ThreadUtils.executeBySingle(task);
        }else {
            mTvZong.setText("图片乐谱总数：" + list.size());
        }
    }

    private void initPDFAdapter() {
        mREcSearchYuePu.setLayoutManager(new GridLayoutManager(mContext, 2));
        searchPDFAdapter = new SearchDPFAdapter(mContext, PDFlist);
        mREcSearchYuePu.setAdapter(searchPDFAdapter);
        searchPDFAdapter.setOnItemClickListener(new SearchDPFAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, PDFImageBean pdfImageBean) {
                Intent intent = new Intent(mContext, PDFImageActivity.class);
                intent.putExtra("name", pdfImageBean.getName());
                intent.putExtra("file", pdfImageBean.getFile().getPath());
                startActivity(intent);
                SearchYuePuActivity.this.finish();
            }
        });
        if (isPDFYuePu) {
            isPDFYuePu = false;
            mIvLoading.setVisibility(View.VISIBLE);
            ThreadUtils.Task task = new ThreadUtils.Task<ArrayList<PDFImageBean>>() {
                @Override
                public ArrayList<PDFImageBean> doInBackground() throws Throwable {
                    //这是在后台子线程中执行的
                    ArrayList<PDFImageBean> imageFileList = getPDFFileList();
                    return imageFileList;
                }

                @Override
                public void onSuccess(ArrayList<PDFImageBean> imageYuePuImageBeans) {
                    //当任务执行完成是调用,在UI线程
                    mIvLoading.setVisibility(View.GONE);
                    mTvZong.setText("PDF乐谱总数：" + imageYuePuImageBeans.size());
                    PDFlist.clear();
                    PDFlist = imageYuePuImageBeans;
                    if (searchPDFAdapter != null)
                        searchPDFAdapter.setData(imageYuePuImageBeans);
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onFail(Throwable t) {

                }
            };
            ThreadUtils.executeBySingle(task);
        }else {
            mTvZong.setText("PDF乐谱总数：" + PDFlist.size());
        }
    }

    //获取所有图片
    private ArrayList<ImageYuePuImageBean> getImageFileList() {
        ArrayList<ImageYuePuImageBean> list = new ArrayList<>();
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = SPBeanUtile.getTuPianQuPuFileList();
        String path = MyApplication.getTuPianYuePuFile().getPath();
        for (int i = 0; i < benDiYuePuBeans.size(); i++) {
            String currentPath = path + "/" + benDiYuePuBeans.get(i).getTitle();
            List<File> files = FileUtils.listFilesInDir(currentPath);
            if (files != null && files.size() > 0) {
                for (int j = 0; j < files.size(); j++) {
                    boolean dir = FileUtils.isDir(files.get(j));
                    if (dir) {
                        String name = files.get(j).getName();
                        List<File> files1 = FileUtils.listFilesInDir(files.get(j).getPath());
                        int size = 0;
                        ArrayList<File> files2 = new ArrayList<>();
                        for (int n = 0; n < files1.size(); n++) {
                            boolean image = ImageUtils.isImage(files1.get(n));
                            if (image) {
                                size += 1;
                                files2.add(files1.get(n));
                            }
                        }
                        ImageYuePuImageBean imageYuePuImageBean = new ImageYuePuImageBean(name, files2, size);
                        list.add(imageYuePuImageBean);
                    }
                }
            }
        }
        return list;
    }

    //获取所有PDF
    private ArrayList<PDFImageBean> getPDFFileList() {
        ArrayList<PDFImageBean> pdfImageBeans = new ArrayList<>();
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = SPBeanUtile.getDefQuPuFileList();
        String path = MyApplication.getDefYuePuFile().getPath();
        for (int i = 0; i < benDiYuePuBeans.size(); i++) {
            String currentPath = path + "/" + benDiYuePuBeans.get(i).getTitle();
            List<File> files = FileUtils.listFilesInDirWithFilter(currentPath, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return (pathname.getPath().endsWith(".pdf"));
                }
            });
            if (files != null && files.size() > 0) {
                for (int j = 0; j < files.size(); j++) {
                    String name = files.get(j).getName();
                    String size = FileUtils.getSize(files.get(j));
                    PDFImageBean pdfImageBean = new PDFImageBean(name, files.get(j), size);
                    pdfImageBeans.add(pdfImageBean);
                }
            }
        }
        return pdfImageBeans;
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
                if (searchPDFAdapter != null) {
                    searchPDFAdapter.getFilter().filter(newText);
                }
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
                initTuPianAdapter(false);
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