package com.example.music.ui.activity.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.Glide;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.FlowLayoutManager;
import com.example.music.adapter.SearchDPFAdapter;
import com.example.music.adapter.SearchJiLuAdapter;
import com.example.music.adapter.SearchYuePuAdapter;
import com.example.music.adapter.SpaceItemDecoration;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.bean.PDFImageBean;
import com.example.music.bean.SearchLiShiBean;
import com.example.music.sqlitleutile.DatabaseHelper;
import com.example.music.ui.activity.ImageActivity;
import com.example.music.ui.activity.PDFImageActivity;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.SoftKeyBoardListener;
import com.example.music.utils.StatusBarUtil;
import com.example.music.zview.MaxHeightRecyclerView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class SearchYuePuActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mYuePuSearchView;
    private ImageView mIvBack;
    private RecyclerView mREcSearchYuePu;
    private TextView mTvSearchTuPianYuePu;
    private TextView mTvSearchDefYuePu;
    private TextView mTvZong;
    private SearchYuePuAdapter searchYuePuAdapter;
    private SearchDPFAdapter searchPDFAdapter;
    private ArrayList<ImageYuePuImageBean> list;//图片
    private ArrayList<PDFImageBean> PDFlist;//图片
    private Activity mContext;
    private String txt;
    private boolean isPDFYuePu = true;
    private boolean isTouch = false;
    private ArrayList<SearchLiShiBean> strings;
    private DatabaseHelper mydb;
    private MaxHeightRecyclerView mRecSearchLiShi;
    private TextView mTvQingKong;
    private LinearLayout mLLSearchLiShi;
    private SoftKeyBoardListener softKeyBoardListener;
    //    private PopupWindow pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_yue_pu);
        StatusBarUtil.transparencyBar(this);
        mContext = this;
        initView();
        initSeawrchLiShi();
    }

    private void initView() {
        mYuePuSearchView = findViewById(R.id.yuepu_search_view);
        mREcSearchYuePu = findViewById(R.id.rec_search_yuepu);
        mTvSearchTuPianYuePu = findViewById(R.id.tv_search_tupianyuepu);
        mTvSearchDefYuePu = findViewById(R.id.tv_search_defyuepu);
        mRecSearchLiShi = findViewById(R.id.rec_search_jilu);
        mTvQingKong = findViewById(R.id.tv_qingkong);
        mLLSearchLiShi = findViewById(R.id.ll_search_lishi);
        mTvZong = findViewById(R.id.tv_zong);
        mIvBack = findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mTvSearchDefYuePu.setOnClickListener(this);
        mTvSearchTuPianYuePu.setOnClickListener(this);
        list = new ArrayList<>();
        PDFlist = new ArrayList<>();
        mydb = new DatabaseHelper(mContext);
        Cursor allData = mydb.getAllData(DatabaseHelper.TABLE_NAME);
        strings = new ArrayList<>();
        while (allData.moveToNext()) {
            //光标移动成功
            String name = allData.getString(allData.getColumnIndex(DatabaseHelper.NAME));
            String id = allData.getString(allData.getColumnIndex(DatabaseHelper.ID));
            startManagingCursor(allData);  //查找后关闭游标
            if (TextUtils.isEmpty(name)) {
                mydb.deleteData(DatabaseHelper.TABLE_NAME, id);
            } else {
                strings.add(new SearchLiShiBean(id, name));
            }
        }
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
                boolean isinsert = false;
                if (strings != null && strings.size() > 0 && !TextUtils.isEmpty(txt)) {
                    for (int i = 0; i < strings.size(); i++) {
                        String name = strings.get(i).getName();
                        if (name.equals(txt)) {
                            isinsert = true;
                            break;
                        } else {
                            isinsert = false;
                        }
                    }
                }
                if (!isinsert) {
                    mydb.insertData(DatabaseHelper.TABLE_NAME, txt);
                }
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
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            } else {
                showAleartDialogLoading();
            }
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
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
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
        } else {
            mTvZong.setText("图片乐谱总数：" + list.size());
        }
    }

    //加载中loading动画
    private AlertDialog alertDialog;

    private void showAleartDialogLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_loading, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(false);
        ImageView mIvLoading = inflate.findViewById(R.id.iv_loading);
        Glide.with(mContext).load(R.mipmap.loading).into(mIvLoading);
    }

    private void initPDFAdapter() {
        mREcSearchYuePu.setLayoutManager(new GridLayoutManager(mContext, 2));
        searchPDFAdapter = new SearchDPFAdapter(mContext, PDFlist);
        mREcSearchYuePu.setAdapter(searchPDFAdapter);
        searchPDFAdapter.setOnItemClickListener(new SearchDPFAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, PDFImageBean pdfImageBean) {
                boolean isinsert = false;
                if (strings != null && strings.size() > 0 && !TextUtils.isEmpty(txt)) {
                    for (int i = 0; i < strings.size(); i++) {
                        String name = strings.get(i).getName();
                        if (name.equals(txt)) {
                            isinsert = true;
                            break;
                        } else {
                            isinsert = false;
                        }
                    }
                }
                if (!isinsert) {
                    mydb.insertData(DatabaseHelper.TABLE_NAME, txt);
                }
                Intent intent = new Intent(mContext, PDFImageActivity.class);
                intent.putExtra("name", pdfImageBean.getName());
                intent.putExtra("file", pdfImageBean.getFile().getPath());
                startActivity(intent);
                SearchYuePuActivity.this.finish();
            }
        });
        if (isPDFYuePu) {
            isPDFYuePu = false;
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            } else {
                showAleartDialogLoading();
            }
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
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                    mTvZong.setText("PDF乐谱总数：" + imageYuePuImageBeans.size());
                    PDFlist.clear();
                    PDFlist = imageYuePuImageBeans;
                    if (searchPDFAdapter != null)
                        searchPDFAdapter.setData(imageYuePuImageBeans);
                    if (!TextUtils.isEmpty(txt)) {
                        searchPDFAdapter.getFilter().filter(txt);
                    }
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onFail(Throwable t) {

                }
            };
            ThreadUtils.executeBySingle(task);
        } else {
            mTvZong.setText("PDF乐谱总数：" + PDFlist.size());
        }
    }

    //获取所有图片
    private ArrayList<ImageYuePuImageBean> getImageFileList() {
        ArrayList<ImageYuePuImageBean> list = new ArrayList<>();
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = SPBeanUtile.getTuPianQuPuFileList();
        String path = MyApplication.getTuPianYuePuFile().getPath();
        if (benDiYuePuBeans != null && benDiYuePuBeans.size() > 0) {
            for (int i = 0; i < benDiYuePuBeans.size(); i++) {
                String currentPath = path + "/" + benDiYuePuBeans.get(i).getTitle();
                List<File> files = FileUtils.listFilesInDir(currentPath);
                if (files != null && files.size() > 0) {
                    for (int j = 0; j < files.size(); j++) {
                        boolean dir = FileUtils.isDir(files.get(j));
                        if (dir) {
                            String name = files.get(j).getName();
                            List<File> files1 = FileUtils.listFilesInDir(files.get(j).getPath());
//                        int size = 0;
//                        ArrayList<File> files2 = new ArrayList<>();
//                        for (int n = 0; n < files1.size(); n++) {
//                            boolean image = ImageUtils.isImage(files1.get(n));
//                            if (image) {
//                        size += 1;
//                        files2.add(files1.get(n));
//                            }
//                        }
                            ImageYuePuImageBean imageYuePuImageBean = new ImageYuePuImageBean(name, files1, files1.size());
                            list.add(imageYuePuImageBean);
                        }
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
        if (benDiYuePuBeans != null && benDiYuePuBeans.size() > 0) {
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
        }
        return pdfImageBeans;
    }

    private void initListener() {
        mYuePuSearchView.addTextChangedListener(textWatcher);
        mYuePuSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//点击了软键盘上的搜索键
                    //关闭软键盘
                    hideInput();
                    txt = mYuePuSearchView.getText().toString();
                    searchYuePuAdapter.getFilter().filter(txt);
                    if (searchPDFAdapter != null) {
                        searchPDFAdapter.getFilter().filter(txt);
                    }
                    return true;
                }
                return false;
            }
        });
        mYuePuSearchView.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点
                    if (strings.size() > 0)
                        mLLSearchLiShi.setVisibility(View.VISIBLE);
                    //弹出软键盘
                    showInput(mYuePuSearchView);
                } else {
                    // 失去焦点
                    hideInput();
                    if (mLLSearchLiShi.getVisibility() == View.VISIBLE)
                        mLLSearchLiShi.setVisibility(View.GONE);
                }
            }
        });
        softKeyBoardListener = new SoftKeyBoardListener(mContext);
        //软键盘状态监听
        softKeyBoardListener.setListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //软键盘已经显示，做逻辑
                mYuePuSearchView.setFocusable(true);
                mYuePuSearchView.setFocusableInTouchMode(true);
                mYuePuSearchView.requestFocus();
            }

            @Override
            public void keyBoardHide(int height) {
                //软键盘已经隐藏,做逻辑
                //清除焦点
                mYuePuSearchView.clearFocus();
            }
        });
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            txt = mYuePuSearchView.getText().toString();
            searchYuePuAdapter.getFilter().filter(txt);
            if (searchPDFAdapter != null) {
                searchPDFAdapter.getFilter().filter(txt);
            }
        }
    };

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }


    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
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

    private void initSeawrchLiShi() {
        if (strings.size() == 0) {
            return;
        }
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mRecSearchLiShi.addItemDecoration(new SpaceItemDecoration(dp2px(2)));
        mRecSearchLiShi.setLayoutManager(flowLayoutManager);
        SearchJiLuAdapter searchJiLuAdapter = new SearchJiLuAdapter(mContext, strings);
        mRecSearchLiShi.setAdapter(searchJiLuAdapter);
        searchJiLuAdapter.setOnItemClickListener(new SearchJiLuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (strings != null && strings.size() > 0) {
                    String name = strings.get(position).getName();
                    txt = name;
                    searchYuePuAdapter.getFilter().filter(name);
                    if (searchPDFAdapter != null) {
                        searchPDFAdapter.getFilter().filter(name);
                    }
                    mYuePuSearchView.setText(name);
                    mYuePuSearchView.clearFocus();
                }
            }
        });
        searchJiLuAdapter.setOnItemLongClickListener(new SearchJiLuAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                String id = strings.get(position).getId();
                mydb.deleteData(DatabaseHelper.TABLE_NAME, id);
                strings.remove(position);
                searchJiLuAdapter.setData(strings);
                if (strings.size() == 0) {
                    mLLSearchLiShi.setVisibility(View.GONE);
                }
            }
        });
        mTvQingKong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < strings.size(); i++) {
                    String id = strings.get(i).getId();
                    mydb.deleteData(DatabaseHelper.TABLE_NAME, id);
                }
                mLLSearchLiShi.setVisibility(View.GONE);
            }
        });
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}