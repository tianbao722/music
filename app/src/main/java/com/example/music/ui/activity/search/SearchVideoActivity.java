package com.example.music.ui.activity.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.music.R;
import com.example.music.adapter.DongTaiVideoAdapter;
import com.example.music.adapter.FlowLayoutManager;
import com.example.music.adapter.SearchJiLuAdapter;
import com.example.music.adapter.SpaceItemDecoration;
import com.example.music.bean.MusicBean;
import com.example.music.bean.SearchLiShiBean;
import com.example.music.sqlitleutile.DatabaseHelper;
import com.example.music.ui.activity.VideoPlayActivity;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.SoftKeyBoardListener;
import com.example.music.zview.MaxHeightRecyclerView;

import java.util.ArrayList;

public class SearchVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private EditText mYuePuSearchView;
    private RecyclerView mRecVideo;
    private LinearLayout mLLSearchLiShi;
    private Activity mContext;
    private ArrayList<MusicBean> allVideoList;
    private DongTaiVideoAdapter dongTaiVideoAdapter;
    private MaxHeightRecyclerView mRecSearchLiShi;
    private TextView mTvQingKong;
    private DatabaseHelper mydb;
    private ArrayList<SearchLiShiBean> strings;
    private String txt;
    private SoftKeyBoardListener softKeyBoardListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video);
        mContext = this;
        initView();
    }

    private void initView() {
        mIvBack = findViewById(R.id.search_video_iv_back);
        mYuePuSearchView = findViewById(R.id.video_search_view);
        mRecVideo = findViewById(R.id.video_rec_search);
        mLLSearchLiShi = findViewById(R.id.ll_search_lishi_video);
        mRecSearchLiShi = findViewById(R.id.rec_search_jilu);
        mTvQingKong = findViewById(R.id.tv_qingkong);
        mIvBack.setOnClickListener(this);
        initListener();
        mydb = new DatabaseHelper(mContext);
        Cursor allData = mydb.getAllData(DatabaseHelper.TABLE_NAME2);
        strings = new ArrayList<>();
        while (allData.moveToNext()) {
            //光标移动成功
            String name = allData.getString(allData.getColumnIndex(DatabaseHelper.NAME));
            String id = allData.getString(allData.getColumnIndex(DatabaseHelper.ID));
            startManagingCursor(allData);  //查找后关闭游标
            if (TextUtils.isEmpty(name)) {
                mydb.deleteData(DatabaseHelper.TABLE_NAME2, id);
            } else {
                strings.add(new SearchLiShiBean(id, name));
            }
        }
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
                    mydb.insertData(DatabaseHelper.TABLE_NAME2, txt);
                }
                Intent intent = new Intent(SearchVideoActivity.this, VideoPlayActivity.class);
                MusicBean musicBean = allVideoList.get(position);
                intent.putExtra("name", musicBean.getName());
                intent.putExtra("path", musicBean.getPath());
                startActivity(intent);
                mContext.finish();
            }
        });
        initSeawrchLiShi();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_video_iv_back://返回
                SearchVideoActivity.this.finish();
                break;
        }
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
                    dongTaiVideoAdapter.getFilter().filter(txt);
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
            dongTaiVideoAdapter.getFilter().filter(txt);
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
                    dongTaiVideoAdapter.getFilter().filter(txt);
                    mYuePuSearchView.setText(name);
                    mYuePuSearchView.clearFocus();
                }
            }
        });
        searchJiLuAdapter.setOnItemLongClickListener(new SearchJiLuAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                String id = strings.get(position).getId();
                mydb.deleteData(DatabaseHelper.TABLE_NAME2, id);
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
                    mydb.deleteData(DatabaseHelper.TABLE_NAME2, id);
                }
                mLLSearchLiShi.setVisibility(View.GONE);
            }
        });
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}