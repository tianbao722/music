package com.example.music.ui.activity.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.DongTaiVideoAdapter;
import com.example.music.adapter.FileMoveAdapter;
import com.example.music.adapter.FlowLayoutManager;
import com.example.music.adapter.SearchJiLuAdapter;
import com.example.music.adapter.SpaceItemDecoration;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.MusicBean;
import com.example.music.bean.SearchLiShiBean;
import com.example.music.sqlitleutile.DatabaseHelper;
import com.example.music.ui.activity.VideoPlayActivity;
import com.example.music.ui.activity.zhujiemian.DongTaiPuActivity;
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
    private ArrayList<BenDiYuePuBean> titles;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video);
        mContext = this;
        initView();
        titles = SPBeanUtile.getDongTaiYuePuFileList();
        if (titles == null) {
            titles = new ArrayList<>();
        }
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
        intent = getIntent();
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
            public void onItemClick(MusicBean musicBean) {
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
                intent.putExtra("name", musicBean.getName());
                intent.putExtra("path", musicBean.getPath());
                startActivity(intent);
                mContext.finish();
            }
        });
        dongTaiVideoAdapter.setOnItemLongClickListener(new DongTaiVideoAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(ArrayList<MusicBean> list, int position) {
                allVideoList = list;
                showAlertLongImage(position);
            }
        });
        initSeawrchLiShi();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_video_iv_back://返回
                setResult(2, intent);
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


    //右边音乐的删除和重命名
    private void showAlertLongImage(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_updata, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(true);
        TextView mTvDelete = inflate.findViewById(R.id.tv_delete);
        TextView mTvChongMingMing = inflate.findViewById(R.id.tv_chongmingming);
        TextView mTvMoveFile = inflate.findViewById(R.id.tv_move_file);
        //移动文件
        mTvMoveFile.setVisibility(View.VISIBLE);
        mTvMoveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMoveFile(position);
                alertDialog.dismiss();
            }
        });
        //删除
        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = allVideoList.get(position).getPath();
                boolean delete = FileUtils.delete(path);
                if (delete) {
                    allVideoList.remove(position);
                    dongTaiVideoAdapter.setData(allVideoList);
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });
        //重命名
        mTvChongMingMing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertChongMingMingImage(position);
                alertDialog.dismiss();
            }
        });
    }

    //移动文件的弹窗
    private int selectMovePosition;

    private void showAlertMoveFile(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_move_file, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(true);
        RecyclerView mRecMove = inflate.findViewById(R.id.rec_move_file);
        TextView mTvCanCel = inflate.findViewById(R.id.tv_cencel1);
        TextView mTvEnter = inflate.findViewById(R.id.tv_enter1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecMove.setLayoutManager(linearLayoutManager);
        if (titles == null) {
            titles = new ArrayList<>();
        }
        FileMoveAdapter fileMoveAdapter = new FileMoveAdapter(mContext, titles);
        mRecMove.setAdapter(fileMoveAdapter);
        fileMoveAdapter.notifyDataSetChanged();
        fileMoveAdapter.setOnItemClickListener(new FileMoveAdapter.onItemClickListener() {
            public void onItemClick(int position) {
                selectMovePosition = position;
                for (int i = 0; i < titles.size(); i++) {
                    BenDiYuePuBean benDiYuePuBean = titles.get(i);
                    benDiYuePuBean.setSelected(false);
                    titles.set(i, benDiYuePuBean);
                }
                BenDiYuePuBean benDiYuePuBean = titles.get(position);
                benDiYuePuBean.setSelected(true);
                titles.set(position, benDiYuePuBean);
                fileMoveAdapter.setData(titles);
            }
        });
        mTvCanCel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        mTvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = allVideoList.get(position).getName();
                //原路径
                String path1 = allVideoList.get(position).getPath();
                String title1 = titles.get(selectMovePosition).getTitle();
                //新路径
                String path = MyApplication.getDongTaiYuePuFile().getPath() + "/" + title1 + "/" + title + ".mp4";
                boolean move = FileUtils.move(path1, path);
                if (move) {
                    alertDialog.dismiss();
                    Toast.makeText(mContext, "移动成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "移动失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //右边音乐名字的重命名
    private boolean classify = false;

    private void showAlertChongMingMingImage(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_xinzeng, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView mTvCancel = inflate.findViewById(R.id.tv_cancel);
        TextView mTvEnter = inflate.findViewById(R.id.tv_enter);
        EditText mEdBenDiQuPu = inflate.findViewById(R.id.ed_bendiqupu);
        String title = allVideoList.get(position).getName();
        mEdBenDiQuPu.setText(title);
        //确定
        mTvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEdBenDiQuPu.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    if (allVideoList.size() != 0) {
                        for (int i = 0; i < allVideoList.size(); i++) {
                            String title = allVideoList.get(i).getName();
                            if (title.equals(text)) {
                                classify = false;
                                break;
                            } else {
                                classify = true;
                            }
                        }
                        if (classify) {
                            String path = allVideoList.get(position).getPath();
                            boolean rename = FileUtils.rename(path, text + ".mp4");
                            if (rename) {
                                MusicBean imageYuePuImageBean = allVideoList.get(position);
                                int qian = MyApplication.getDongTaiYuePuFile().getPath().length() + 1;
                                int zong = path.length();
                                int hou = allVideoList.get(position).getName().length() + 5;
                                int qianzhong = zong - hou;
                                String substring = path.substring(qian, qianzhong);
                                String file = MyApplication.getDongTaiYuePuFile().getPath() + "/" + substring + "/" + text + ".mp4";
                                imageYuePuImageBean.setName(text);
                                imageYuePuImageBean.setPath(file);
                                allVideoList.set(position, imageYuePuImageBean);
                                dongTaiVideoAdapter.setData(allVideoList);
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                Toast.makeText(mContext, "重命名成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "重命名失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "该名称已经存在", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(mContext, "请输入名称", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //取消
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        });
    }

}