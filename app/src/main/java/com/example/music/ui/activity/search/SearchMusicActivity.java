package com.example.music.ui.activity.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.FlowLayoutManager;
import com.example.music.adapter.MusicAdapter;
import com.example.music.adapter.SearchJiLuAdapter;
import com.example.music.adapter.SpaceItemDecoration;
import com.example.music.bean.MusicBean;
import com.example.music.bean.SearchLiShiBean;
import com.example.music.sqlitleutile.DatabaseHelper;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.SoftKeyBoardListener;
import com.example.music.zview.MaxHeightRecyclerView;

import java.util.ArrayList;

public class SearchMusicActivity extends AppCompatActivity {
    private EditText mMusicSearchView;
    private RecyclerView mMusicRecSearch;
    private LinearLayout mLLSearchList;
    private Activity mContext;
    private MusicAdapter musicAdapter;
    private ImageView mIvBack;
    private ArrayList<MusicBean> list;
    private DatabaseHelper myDb;
    private String searchText = null;
    private TextView mTvQingKong;
    private MaxHeightRecyclerView mRecSearchLiShi;
    private DatabaseHelper mydb;
    private ArrayList<SearchLiShiBean> strings;
    private SoftKeyBoardListener softKeyBoardListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
        mContext = this;
        myDb = new DatabaseHelper(this);
        initView();
        initSeawrchLiShi();
    }

    private void initView() {
        mMusicSearchView = findViewById(R.id.music_search_view);
        mMusicRecSearch = findViewById(R.id.music_rec_search);
        mLLSearchList = findViewById(R.id.ll_search_lishi_music);
        mIvBack = findViewById(R.id.iv_back);
        mTvQingKong = findViewById(R.id.tv_qingkong);
        mRecSearchLiShi = findViewById(R.id.rec_search_jilu);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchMusicActivity.this.finish();
            }
        });
        mydb = new DatabaseHelper(mContext);
        Cursor allData = mydb.getAllData(DatabaseHelper.TABLE_NAME1);
        strings = new ArrayList<>();
        while (allData.moveToNext()) {
            //光标移动成功
            String name = allData.getString(allData.getColumnIndex(DatabaseHelper.NAME));
            String id = allData.getString(allData.getColumnIndex(DatabaseHelper.ID));
            startManagingCursor(allData);  //查找后关闭游标
            if (TextUtils.isEmpty(name)) {
                mydb.deleteData(DatabaseHelper.TABLE_NAME1, id);
            } else {
                strings.add(new SearchLiShiBean(id, name));
            }
        }
        initListener();
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
                boolean isinsert = false;
                if (strings != null && strings.size() > 0 && !TextUtils.isEmpty(searchText)) {
                    for (int i = 0; i < strings.size(); i++) {
                        String name = strings.get(i).getName();
                        if (name.equals(searchText)) {
                            isinsert = true;
                            break;
                        } else {
                            isinsert = false;
                        }
                    }
                }
                if (!isinsert) {
                    mydb.insertData(DatabaseHelper.TABLE_NAME1, searchText);
                }
                intent.putExtra("path", musicBean.getPath());
                String path = MyApplication.getWoDeYinYueFile().getPath();
                int length = path.length();
                String title = musicBean.getPath().substring(length + 1, musicBean.getPath().length() - 4 - (musicBean.getName().length()) - 1);
                intent.putExtra("name", musicBean.getName());
                intent.putExtra("title", title);
                setResult(2, intent);
                if (!TextUtils.isEmpty(searchText)) {
//                    boolean b = myDb.insertData(searchText);
                }
                SearchMusicActivity.this.finish();
            }
        });
        musicAdapter.setOnItemLongClickListener(new MusicAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {

            }
        });
    }

    private void initListener() {
        mMusicSearchView.addTextChangedListener(textWatcher);
        mMusicSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//点击了软键盘上的搜索键
                    //关闭软键盘
                    hideInput();
                    searchText = mMusicSearchView.getText().toString();
                    musicAdapter.getFilter().filter(searchText);
                    return true;
                }
                return false;
            }
        });
        mMusicSearchView.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点
                    if (strings.size() > 0)
                        mLLSearchList.setVisibility(View.VISIBLE);
                    //弹出软键盘
                    showInput(mMusicSearchView);
                } else {
                    // 失去焦点
                    hideInput();
                    if (mLLSearchList.getVisibility() == View.VISIBLE)
                        mLLSearchList.setVisibility(View.GONE);
                }
            }
        });
        softKeyBoardListener = new SoftKeyBoardListener(mContext);
        //软键盘状态监听
        softKeyBoardListener.setListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //软键盘已经显示，做逻辑
                mMusicSearchView.setFocusable(true);
                mMusicSearchView.setFocusableInTouchMode(true);
                mMusicSearchView.requestFocus();
            }

            @Override
            public void keyBoardHide(int height) {
                //软键盘已经隐藏,做逻辑
                //清除焦点
                mMusicSearchView.clearFocus();
            }
        });
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

    class MyAsyncTask extends AsyncTask<String, Integer, ArrayList<MusicBean>> {

        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            } else {
                showAleartDialogLoading();
            }
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
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            list = musicBeans;
            musicAdapter.setData(list);
        }
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
            searchText = mMusicSearchView.getText().toString();
            musicAdapter.getFilter().filter(searchText);

        }
    };

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
                    searchText = name;
                    musicAdapter.getFilter().filter(name);
                    mMusicSearchView.setText(name);
                    mMusicSearchView.clearFocus();
                }
            }
        });
        searchJiLuAdapter.setOnItemLongClickListener(new SearchJiLuAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                String id = strings.get(position).getId();
                mydb.deleteData(DatabaseHelper.TABLE_NAME1, id);
                strings.remove(position);
                searchJiLuAdapter.setData(strings);
                if (strings.size() == 0) {
                    mLLSearchList.setVisibility(View.GONE);
                }
            }
        });
        mTvQingKong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < strings.size(); i++) {
                    String id = strings.get(i).getId();
                    mydb.deleteData(DatabaseHelper.TABLE_NAME1, id);
                }
                mLLSearchList.setVisibility(View.GONE);
            }
        });
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

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
}