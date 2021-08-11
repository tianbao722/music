package com.example.music.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.adapter.BanZouAdapter;
import com.example.music.adapter.ImageMagnifyAdapter;
import com.example.music.bean.BanZouBean;
import com.example.music.bean.BanZouListBean;
import com.example.music.bean.MusicBean;
import com.example.music.utils.PreferenceUtil;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.SpeedDialog;
import com.example.music.utils.SpringDraggable;
import com.example.music.utils.StatusBarUtil;
import com.example.music.utils.XToast;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.ywl5320.bean.TimeBean;
import com.ywl5320.libenum.SampleRateEnum;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnInfoListener;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.util.WlTimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.music.utils.DateUtil.parseTime;


public class ImageActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {
    private ViewPager vpPop;
    private TextView tvPop;
    private TextView mTvDi;
    private TextView mTvBanZou;
    private TextView mMusicTvTitle;
    private ImageView mIvBack;
    private ConstraintLayout mConTop;
    private ConstraintLayout mConLayout;
    private ConstraintLayout mConBottom;
    private ArrayList<String> list;//图片
    private int defaultNightMode;
    private Context mContext;
    private ArrayList<BanZouBean> list1 = new ArrayList<>();//伴奏
    private String title;
    private BanZouAdapter banZouAdapter;
    private XXPermissions with;
    private XToast xToast;
    private static final int INTERNAL_TIME = 100;// 音乐进度间隔时间
    // 记录当前播放歌曲的位置
    public int mCurrentPosition;
    private int yindiao;
    private int isPause = 0;//0:表示没有播放音乐，从0开始播放， 1：表示暂停 继续播放

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    TimeBean timeBean = (TimeBean) msg.obj;
                    mTvSeekBar.setProgress(timeBean.getCurrSecs() * 100 / timeBean.getTotalSecs());
                    String playTime = WlTimeUtil.secdsToDateFormat(timeBean.getCurrSecs(), timeBean.getTotalSecs());
                    String totalTime = WlTimeUtil.secdsToDateFormat(timeBean.getTotalSecs(), timeBean.getTotalSecs());
                    mTvPlayTime.setText(playTime);
                    mTvTotalTime.setText(totalTime);
                    mTvTitle.setText(list1.get(mCurrentPosition).getName());
                    break;
                default:
                    break;
            }
        }
    };
    private SeekBar mTvSeekBar;
    private ImageView mIvPlayOrPause;
    private TextView mTvTitle;
    private TextView mTvTotalTime;//总时间
    private TextView mTvPlayTime;//播放时间
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mContext = this;
        StatusBarUtil.transparencyBar(this);
        initView();
        initWlMusic();
    }

    private WlMusic wlMusic;

    private void initWlMusic() {
        wlMusic = WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);//是否返回音频PCM数据
        wlMusic.setShowPCMDB(true);//是否返回音频分贝大小
        wlMusic.setPlayCircle(false);//循环播放
        wlMusic.setVolume(100);//声音大小100%
        wlMusic.setPlaySpeed(1.0f);//播放速度正常
        wlMusic.setPlayPitch(1.0f);//播放音调正常
        wlMusic.setConvertSampleRate(SampleRateEnum.RATE_44100);//设定恒定采样率（null为取消）

        //可以播放的回调
        wlMusic.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                wlMusic.start();
            }
        });
        //更新进度
        wlMusic.setOnInfoListener(new OnInfoListener() {
            @Override
            public void onInfo(TimeBean timeBean) {
                Message message = Message.obtain();
                message.obj = timeBean;
                message.what = 1;
                handler.sendMessage(message);
            }
        });
    }

    private void initView() {
        vpPop = findViewById(R.id.vp_pop);
        tvPop = findViewById(R.id.tv_pop);
        mConTop = findViewById(R.id.con_top);
        mConBottom = findViewById(R.id.con_bottom);
        mConLayout = findViewById(R.id.con_layout);
        mTvDi = findViewById(R.id.tv_di);
        mIvBack = findViewById(R.id.iv_back1);
        mMusicTvTitle = findViewById(R.id.music_tv_title);
        mTvBanZou = findViewById(R.id.tv_banzou);
        mIvBack.setOnClickListener(this);
        mTvDi.setOnClickListener(this);
        mTvBanZou.setOnClickListener(this);
        mConLayout.setOnClickListener(this);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        mMusicTvTitle.setText(title);
        title = title.replaceAll("[0-9]", "");
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute("s");
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        defaultNightMode = AppCompatDelegate.getDefaultNightMode();
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            mIvBack.setImageDrawable(getResources().getDrawable(R.mipmap.fanhui1));
        } else {
            mIvBack.setImageDrawable(getResources().getDrawable(R.mipmap.fanhui));
        }
        int postion1 = intent.getIntExtra("position", 0);
        this.list = intent.getStringArrayListExtra("list");
        ImageMagnifyAdapter imgVpAda = new ImageMagnifyAdapter(this, this.list);
        vpPop.setAdapter(imgVpAda);
        tvPop.setText(postion1 + 1 + "/" + this.list.size());
        vpPop.setCurrentItem(postion1);
        imgVpAda.setCallBack(new ImageMagnifyAdapter.onCallBack() {
            @Override
            public void onItemClick() {
                if (mConTop.getVisibility() == View.GONE) {
                    mConTop.setVisibility(View.VISIBLE);
                    mConBottom.setVisibility(View.VISIBLE);
                } else {
                    mConTop.setVisibility(View.GONE);
                    mConBottom.setVisibility(View.GONE);
                }
            }
        });
        vpPop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tvPop.setText(position + 1 + "/" + ImageActivity.this.list.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back1:
                setFanhui();
                ImageActivity.this.finish();
                break;
            case R.id.tv_banzou:
                setAlerD();
                break;
            case R.id.tv_di:
                if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    //夜间 切换 日间
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//日间
                    recreate();
                } else {
                    //日间 切换 夜间
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//夜间
                    recreate();
                }
                break;
        }
    }

    private void setAlerD() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_banzou, null);
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(true);
        TextView mTvGuanLian = inflate.findViewById(R.id.tv_guanlian);
        TextView mTvNull = inflate.findViewById(R.id.tv_null);
        RecyclerView mRecGuanLian = inflate.findViewById(R.id.rec_guanlian);
        if (list1 != null && list1.size() > 0) {
            mRecGuanLian.setVisibility(View.VISIBLE);
            mTvNull.setVisibility(View.GONE);
        } else {
            mRecGuanLian.setVisibility(View.GONE);
            mTvNull.setVisibility(View.VISIBLE);
        }
        mRecGuanLian.setLayoutManager(new LinearLayoutManager(mContext));
        banZouAdapter = new BanZouAdapter(mContext, list1);
        mRecGuanLian.setAdapter(banZouAdapter);
        banZouAdapter.notifyDataSetChanged();
        //点击播放，单曲循环
        banZouAdapter.setOnItemClickListener(new BanZouAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mCurrentPosition = position;
                if (xToast != null && xToast.isShow()) {
                    xToast.cancel();
                    xToast = null;
                }
                show7();
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                showAler();
                changeMusic(position);
                alertDialog.dismiss();
            }
        });
        //点击删除
        banZouAdapter.setOnItemDeleteClickListener(new BanZouAdapter.onItemDeleteClickListener() {
            @Override
            public void onItemDelete(int position) {
                list1.remove(position);
                BanZouListBean banZouListBean1 = new BanZouListBean(list1);
                String json1 = new Gson().toJson(banZouListBean1);
                PreferenceUtil.getInstance().saveString(title, json1);
                banZouAdapter.notifyDataSetChanged();
            }
        });
        //关联伴奏
        mTvGuanLian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BanZouActivity.class);
                intent.putExtra("title", title);
                startActivityForResult(intent, 1);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setFanhui();
    }

    private void setFanhui() {
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            //夜间 切换 日间
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//日间
            recreate();
        }
        if (xToast != null && xToast.isShow()) {
            xToast.cancel();
            xToast = null;
        }
        if (wlMusic != null && wlMusic.isPlaying()) {
            wlMusic.stop();
            wlMusic = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            if (banZouAdapter != null) {
                setAlerD();
            }
        }
    }

    public void show7() {
        with = XXPermissions.with(this);
        with.permission(Permission.SYSTEM_ALERT_WINDOW);
        with.request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> granted, boolean all) {
                // 传入 Application 表示这个是一个全局的 Toast
                xToast = new XToast(getApplication())
                        .setView(R.layout.toast_phone)
                        .setGravity(Gravity.END | Gravity.BOTTOM)
                        .setYOffset(200)
                        // 设置指定的拖拽规则
                        .setDraggable(new SpringDraggable())
                        .setOnClickListener(android.R.id.icon, new XToast.OnClickListener<ImageView>() {
                            @Override
                            public void onClick(XToast<?> toast, ImageView view) {
                                if (alertDialog != null && alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                }
                                if (alertDialog != null && alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                } else {
                                    showAler();
                                }
                                if (isPause == 1) {
                                    mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
                                } else {
                                    mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                                }
                            }
                        });
                xToast.show();
            }

            @Override
            public void onDenied(List<String> denied, boolean never) {
                new XToast<>(ImageActivity.this)
                        .setDuration(1000)
                        .setView(R.layout.toast_hint)
                        .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_error)
                        .setText(android.R.id.message, "请先授予悬浮窗权限")
                        .show();
            }
        });
    }

    private int position = 0;

    private void showAler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        alertDialog = builder.create();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_banzou_music, null);
        alertDialog.show();
        alertDialog.setContentView(inflate);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCanceledOnTouchOutside(true);
        mTvTitle = inflate.findViewById(R.id.alertdialog_tv_banzou_music_name);
        TextView mTvBeiSu = inflate.findViewById(R.id.banzou_tv_beisu);
        ImageView mIvPreVious = inflate.findViewById(R.id.banzou_btn_previous);
        mIvPlayOrPause = inflate.findViewById(R.id.banzou_btn_play_or_pause);
        ImageView mIvNext = inflate.findViewById(R.id.banzou_btn_next);
        TextView mTvYinDiao = inflate.findViewById(R.id.banzou_tv_yindiao);
        mTvPlayTime = inflate.findViewById(R.id.banzou_tv_play_time);
        mTvTotalTime = inflate.findViewById(R.id.banzou_tv_total_time);
        mTvSeekBar = inflate.findViewById(R.id.banzou_time_seekBar);
        //滑动监听
        mTvSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                position = wlMusic.getDuration() * progress / 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                wlMusic.seek(position, false, false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wlMusic.seek(position, true, true);
            }
        });
        //倍速
        mTvBeiSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpeedPop();
            }
        });
        //上一曲
        mIvPreVious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMusic(--mCurrentPosition);//当前歌曲位置减1
            }
        });
        //下一曲
        mIvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMusic(++mCurrentPosition);//当前歌曲位置加1
            }
        });
        //播放和暂停
        mIvPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 首次点击播放按钮，默认播放第0首，下标从0开始
                if (wlMusic != null && wlMusic.isPlaying() && isPause == 2) {
                    isPause = 1;
                    wlMusic.pause();//暂停
                    mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
                } else if (isPause == 0 && wlMusic != null && !wlMusic.isPlaying() && list1 != null && list1.size() > 0) {
                    mCurrentPosition = 0;
                    wlMusic.setSource(list1.get(0).getPath());
                    wlMusic.prePared();
                    isPause = 2;
                    mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                } else if (wlMusic != null && wlMusic.isPlaying() && isPause == 1) {
                    isPause = 2;
                    wlMusic.resume();
                    mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                }
            }
        });
        //音调
        mTvYinDiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list1 != null && wlMusic != null) {
                    showYinDiao();
                } else {
                    Toast.makeText(mContext, "请先添加音乐", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //
    }

    /**
     * 音速
     */
    private void showSpeedPop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(this).inflate(R.layout.popupwindow_yinsu, null);
        alertDialog.setContentView(inflate);
        alertDialog.setCanceledOnTouchOutside(true);//点击弹窗外可关闭弹窗
        //找控件ID
        TextView mTvJianYinDiao = inflate.findViewById(R.id.tv_jianyinsu);
        TextView mTvYinDiaoZhi = inflate.findViewById(R.id.tv_yinsuzhi);
        TextView mTvJiaYinDiao = inflate.findViewById(R.id.tv_jiayinsu);
        float playSpeed = wlMusic.getPlaySpeed();
        mTvYinDiaoZhi.setText(playSpeed + "");
        //减音速
        mTvJianYinDiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mTvYinDiaoZhi.getText().toString();
                if (wlMusic != null) {
                    setJianYinSu(mTvYinDiaoZhi, s);
                } else {
                    Toast.makeText(mContext, "请先选择播放", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //加音速
        mTvJiaYinDiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mTvYinDiaoZhi.getText().toString();
                if (wlMusic != null) {
                    setJiaYinSu(mTvYinDiaoZhi, s);
                } else {
                    Toast.makeText(mContext, "请先选择节奏", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //减音速
    private void setJianYinSu(TextView yinsuzhi, String s) {
        float YinSu = Float.parseFloat(s);
        if (YinSu == 2.0f) {
            yinsuzhi.setText("1.9");
            setYinSu(1.9f);
        } else if (YinSu == 1.9f) {
            yinsuzhi.setText("1.8");
            setYinSu(1.8f);
        } else if (YinSu == 1.8f) {
            yinsuzhi.setText("1.7");
            setYinSu(1.7f);
        } else if (YinSu == 1.7f) {
            yinsuzhi.setText("1.6");
            setYinSu(1.6f);
        } else if (YinSu == 1.6f) {
            yinsuzhi.setText("1.5");
            setYinSu(1.5f);
        } else if (YinSu == 1.5f) {
            yinsuzhi.setText("1.4");
            setYinSu(1.4f);
        } else if (YinSu == 1.4f) {
            yinsuzhi.setText("1.3");
            setYinSu(1.3f);
        } else if (YinSu == 1.3f) {
            yinsuzhi.setText("1.2");
            setYinSu(1.2f);
        } else if (YinSu == 1.2f) {
            yinsuzhi.setText("1.1");
            setYinSu(1.1f);
        } else if (YinSu == 1.1f) {
            yinsuzhi.setText("1.0");
            setYinSu(1.0f);
        } else if (YinSu == 1.0f) {
            yinsuzhi.setText("0.9");
            setYinSu(0.9f);
        } else if (YinSu == 0.9f) {
            yinsuzhi.setText("0.8");
            setYinSu(0.8f);
        } else if (YinSu == 0.8f) {
            yinsuzhi.setText("0.7");
            setYinSu(0.7f);
        } else if (YinSu == 0.7f) {
            yinsuzhi.setText("0.6");
            setYinSu(0.6f);
        } else if (YinSu == 0.6f) {
            yinsuzhi.setText("0.5");
            setYinSu(0.5f);
        }
    }

    //加音速
    private void setJiaYinSu(TextView yinsuzhi, String s) {
        float YinSu = Float.parseFloat(s);
        if (YinSu == 0.5f) {
            yinsuzhi.setText("0.6");
            setYinSu(0.6f);
        } else if (YinSu == 0.6f) {
            yinsuzhi.setText("0.7");
            setYinSu(0.7f);
        } else if (YinSu == 0.7f) {
            yinsuzhi.setText("0.8");
            setYinSu(0.8f);
        } else if (YinSu == 0.8f) {
            yinsuzhi.setText("0.9");
            setYinSu(0.9f);
        } else if (YinSu == 0.9f) {
            yinsuzhi.setText("1.0");
            setYinSu(1.0f);
        } else if (YinSu == 1.0f) {
            yinsuzhi.setText("1.1");
            setYinSu(1.1f);
        } else if (YinSu == 1.1f) {
            yinsuzhi.setText("1.2");
            setYinSu(1.2f);
        } else if (YinSu == 1.2f) {
            yinsuzhi.setText("1.3");
            setYinSu(1.3f);
        } else if (YinSu == 1.3f) {
            yinsuzhi.setText("1.4");
            setYinSu(1.4f);
        } else if (YinSu == 1.4f) {
            yinsuzhi.setText("1.5");
            setYinSu(1.5f);
        } else if (YinSu == 1.5f) {
            yinsuzhi.setText("1.6");
            setYinSu(1.6f);
        } else if (YinSu == 1.6f) {
            yinsuzhi.setText("1.7");
            setYinSu(1.7f);
        } else if (YinSu == 1.7f) {
            yinsuzhi.setText("1.8");
            setYinSu(1.8f);
        } else if (YinSu == 1.8f) {
            yinsuzhi.setText("1.9");
            setYinSu(1.9f);
        } else if (YinSu == 1.9f) {
            yinsuzhi.setText("2.0");
            setYinSu(2.0f);
        }
    }

    //设置音速
    public void setYinSu(float speed) {
        wlMusic.setPlaySpeed(speed);
    }

    //切歌
    private void changeMusic(int position) {
        if (wlMusic == null) return;
        if (mCurrentPosition > list1.size() - 1) {
            mCurrentPosition = 0;
        } else if (mCurrentPosition < 0) {
            mCurrentPosition = list1.size() - 1;
        }
        this.mCurrentPosition = mCurrentPosition;
        wlMusic.playNext(list1.get(mCurrentPosition).getPath());
        isPause = 2;
        if (wlMusic.isPlaying()) {
            mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
        } else {
            mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
        }
    }

    private void setYinDiaoZhi(TextView mTvYinDiaoZhi, float playSpeed) {
        if (playSpeed == 1.0f) {
            mTvYinDiaoZhi.setText("0");
        } else if (playSpeed == 0.884f) {
            mTvYinDiaoZhi.setText("1");
        } else if (playSpeed == 0.768f) {
            mTvYinDiaoZhi.setText("2");
        } else if (playSpeed == 0.652f) {
            mTvYinDiaoZhi.setText("3");
        } else if (playSpeed == 0.536f) {
            mTvYinDiaoZhi.setText("4");
        } else if (playSpeed == 0.420f) {
            mTvYinDiaoZhi.setText("5");
        } else if (playSpeed == 0.304f) {
            mTvYinDiaoZhi.setText("6");
        } else if (playSpeed == 1.25f) {
            mTvYinDiaoZhi.setText("-1");
        } else if (playSpeed == 1.50f) {
            mTvYinDiaoZhi.setText("-2");
        } else if (playSpeed == 1.75f) {
            mTvYinDiaoZhi.setText("-3");
        } else if (playSpeed == 2.00f) {
            mTvYinDiaoZhi.setText("-4");
        } else if (playSpeed == 2.25f) {
            mTvYinDiaoZhi.setText("-5");
        } else if (playSpeed == 2.50f) {
            mTvYinDiaoZhi.setText("-6");
        }
    }

    //音调加减
    private void showYinDiao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View inflate = LayoutInflater.from(this).inflate(R.layout.popupwindow_yindiao, null);
        alertDialog.setContentView(inflate);
        alertDialog.setCanceledOnTouchOutside(true);//点击弹窗外可关闭弹窗
        //找控件ID
        TextView mTvJianYinDiao = inflate.findViewById(R.id.tv_jianyindiao);
        TextView mTvYinDiaoZhi = inflate.findViewById(R.id.tv_yindiaozhi);
        TextView mTvJiaYinDiao = inflate.findViewById(R.id.tv_jiayindiao);
        float playSpeed = wlMusic.getPlayPitch();
        setYinDiaoZhi(mTvYinDiaoZhi, playSpeed);
        //减音调
        mTvJianYinDiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mTvYinDiaoZhi.getText().toString();
                yindiao = Integer.parseInt(s);
                if (yindiao <= -6) {
                    return;
                } else {
                    yindiao -= 1;
                    mTvYinDiaoZhi.setText(yindiao + "");
                    if (wlMusic != null) {
                        switch (yindiao) {
                            case 1:
                                setYinDaio(1.25f);
                                break;
                            case 2:
                                setYinDaio(1.50f);
                                break;
                            case 3:
                                setYinDaio(1.75f);
                                break;
                            case 4:
                                setYinDaio(2.25f);
                                break;
                            case 5:
                                setYinDaio(2.00f);
                                break;
                            case 6:
                                setYinDaio(2.50f);
                                break;
                            case 0:
                                setYinDaio(1.00f);
                                break;
                            case -1:
                                setYinDaio(0.917f);
                                break;
                            case -2:
                                setYinDaio(0.834f);
                                break;
                            case -3:
                                setYinDaio(0.751f);
                                break;
                            case -4:
                                setYinDaio(0.668f);
                                break;
                            case -5:
                                setYinDaio(0.585f);
                                break;
                            case -6:
                                setYinDaio(0.5f);
                                break;
                        }
                    }
                }
            }
        });
        //加音调
        mTvJiaYinDiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mTvYinDiaoZhi.getText().toString();
                yindiao = Integer.parseInt(s);
                if (yindiao < 6) {
                    yindiao += 1;
                    mTvYinDiaoZhi.setText(yindiao + "");
                    if (wlMusic != null) {
                        switch (yindiao) {
                            case 1:
                                setYinDaio(1.25f);
                                break;
                            case 2:
                                setYinDaio(1.50f);
                                break;
                            case 3:
                                setYinDaio(1.75f);
                                break;
                            case 4:
                                setYinDaio(2.25f);
                                break;
                            case 5:
                                setYinDaio(2.00f);
                                break;
                            case 6:
                                setYinDaio(2.50f);
                                break;
                            case 0:
                                setYinDaio(1.00f);
                                break;
                            case -1:
                                setYinDaio(0.917f);
                                break;
                            case -2:
                                setYinDaio(0.834f);
                                break;
                            case -3:
                                setYinDaio(0.751f);
                                break;
                            case -4:
                                setYinDaio(0.668f);
                                break;
                            case -5:
                                setYinDaio(0.585f);
                                break;
                            case -6:
                                setYinDaio(0.5f);
                                break;
                        }
                    }
                } else if (yindiao >= 6) {
                    return;
                }
            }
        });
    }

    //设置音调
    private void setYinDaio(float v2) {
        wlMusic.setPlayPitch(v2);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        changeMusic(mCurrentPosition);
    }

    //加载中loading动画
    private AlertDialog alertDialogLoading;

    private void showAleartDialogLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        alertDialogLoading = builder.create();
        alertDialogLoading.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_loading, null);
        alertDialogLoading.setContentView(inflate);
        alertDialogLoading.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialogLoading.setCanceledOnTouchOutside(false);
        ImageView mIvLoading = inflate.findViewById(R.id.iv_loading);
        Glide.with(mContext).load(R.mipmap.loading).into(mIvLoading);
    }

    class MyAsyncTask extends AsyncTask<String, Integer, ArrayList<MusicBean>> {

        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程
            if (alertDialogLoading != null && !alertDialogLoading.isShowing()) {
                alertDialogLoading.show();
            } else {
                showAleartDialogLoading();
            }
            super.onPreExecute();
        }

        @Override
        protected ArrayList<MusicBean> doInBackground(String... params) {
            //这是在后台子线程中执行的
            ArrayList<MusicBean> allMusic = SPBeanUtile.getAllMusic();
            if (allMusic != null && allMusic.size() > 0) {
                for (MusicBean bean : allMusic) {
                    if (list1 != null) {
                        if (bean.getName().contains(title) || bean.getName().contains(title)) {
                            list1.add(new BanZouBean(bean.getName(), bean.getPath()));
                        }
                    }
                }
            }
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
            if (alertDialogLoading != null && alertDialogLoading.isShowing()) {
                alertDialogLoading.dismiss();
            }
            if (list1 != null && list1.size() > 0) {
                for (int i = 0; i < list1.size(); i++) {
                    if (list1.get(i) == null) {
                        list1.remove(i);
                    }
                }
                setAlerD();
            }
        }
    }
}
