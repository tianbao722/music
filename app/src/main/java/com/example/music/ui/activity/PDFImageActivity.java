package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
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

import com.example.music.R;
import com.example.music.adapter.BanZouAdapter;
import com.example.music.bean.BanZouBean;
import com.example.music.bean.BanZouListBean;
import com.example.music.bean.MusicBean;
import com.example.music.utils.PreferenceUtil;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.SpeedDialog;
import com.example.music.utils.SpringDraggable;
import com.example.music.utils.XToast;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.music.utils.DateUtil.parseTime;

public class PDFImageActivity extends AppCompatActivity implements View.OnClickListener, OnPageChangeListener, MediaPlayer.OnCompletionListener {

    private ImageView mIvBack;
    private TextView mPdfTvTitle;
    private TextView mPdfTvDi;
    private TextView mPdfTvPop;
    private TextView mPdfTvBanZou;
    private ConstraintLayout mConTop;
    private ConstraintLayout mConBottom;
    private int defaultNightMode;
    private Context mContext;
    private PDFView mPdf;
    private ArrayList<BanZouBean> list1;//伴奏
    private String title;
    private BanZouAdapter banZouAdapter;
    // 记录当前播放歌曲的位置
    public int mCurrentPosition;
    private XToast xToast;
    private MediaPlayer mediaPlayer;//音频播放器
    private XXPermissions with;
    private AlertDialog alertDialog;
    private static final int INTERNAL_TIME = 100;// 音乐进度间隔时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_image);
        mContext = this;
        initView();
    }

    private void initView() {
        mIvBack = findViewById(R.id.pdf_iv_back);
        mPdfTvTitle = findViewById(R.id.pdf__tv_title);
        mPdfTvDi = findViewById(R.id.pdf_tv_di);
        mPdfTvBanZou = findViewById(R.id.pdf_tv_banzou);
        mConTop = findViewById(R.id.con_top);
        mConBottom = findViewById(R.id.con_bottom);
        mPdfTvPop = findViewById(R.id.pdf_tv_pop);
        mPdf = findViewById(R.id.pdf);

        Intent intent = getIntent();
        title = intent.getStringExtra("name");
        String file = intent.getStringExtra("file");
        mPdfTvTitle.setText(title);
        defaultNightMode = AppCompatDelegate.getDefaultNightMode();
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            mIvBack.setImageDrawable(getResources().getDrawable(R.mipmap.fanhui1));
        } else {
            mIvBack.setImageDrawable(getResources().getDrawable(R.mipmap.fanhui));
        }

        mIvBack.setOnClickListener(this);
        mPdfTvDi.setOnClickListener(this);
        mPdfTvBanZou.setOnClickListener(this);
        if (file != null) {
            display(file);
        }

        initMusic();
    }

    private void display(String assetFileName) {
        File file = new File(assetFileName);
        if (file.exists()) {
            mPdf.fromFile(file)
                    //                .pages(0, 0, 0, 0, 0, 0) // 默认全部显示，pages属性可以过滤性显示
                    .defaultPage(1)//默认展示第一页
                    .onPageChange(this)//监听页面切换
                    .load();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pdf_iv_back:
                setFanhui();
                PDFImageActivity.this.finish();
                break;
            case R.id.pdf_tv_di:
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
            case R.id.pdf_tv_banzou:
                setAlerD();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setFanhui();
        PDFImageActivity.this.finish();
        return super.onKeyDown(keyCode, event);
    }

    private void setFanhui() {
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            //夜间 切换 日间
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//日间
            recreate();
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        mPdfTvPop.setText(page + "/" + pageCount);
    }

    public void initMusic() {
        ArrayList<MusicBean> allMusic = SPBeanUtile.getAllMusic();
        list1 = new ArrayList<>();
        if (allMusic != null && allMusic.size() > 0) {
            for (MusicBean bean : allMusic) {
                if (bean.getName().contains(title) || bean.getName().contains(title)) {
                    list1.add(new BanZouBean(bean.getName(), bean.getPath()));
                }
            }
        }
        String json = PreferenceUtil.getInstance().getString(title, null);
        if (!TextUtils.isEmpty(json)) {
            BanZouListBean banZouListBean = new Gson().fromJson(json, BanZouListBean.class);
            if (banZouListBean != null) {
                ArrayList<BanZouBean> list = banZouListBean.getList();
                if (list != null && list.size() > 0) {
                    setAlerD();
                } else {
                    BanZouListBean banZouListBean1 = new BanZouListBean(list1);
                    String json1 = new Gson().toJson(banZouListBean1);
                    PreferenceUtil.getInstance().saveString(title, json1);
                    setAlerD();
                }
            } else {
                BanZouListBean banZouListBean1 = new BanZouListBean(list1);
                String json1 = new Gson().toJson(banZouListBean1);
                PreferenceUtil.getInstance().saveString(title, json1);
                setAlerD();
            }
        } else {
            BanZouListBean banZouListBean1 = new BanZouListBean(list1);
            String json1 = new Gson().toJson(banZouListBean1);
            PreferenceUtil.getInstance().saveString(title, json1);
            setAlerD();
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
        RecyclerView mRecGuanLian = inflate.findViewById(R.id.rec_guanlian);
        String json = PreferenceUtil.getInstance().getString(title, null);
        BanZouListBean banZouListBean = new Gson().fromJson(json, BanZouListBean.class);
        if (banZouListBean != null) {
            list1 = banZouListBean.getList();
        } else {
            list1 = new ArrayList<>();
        }
        if (list1 != null && list1.size() > 0) {
            mRecGuanLian.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else {
            mRecGuanLian.setBackgroundColor(mContext.getResources().getColor(R.color.touming));
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
            }
        });
    }

    private SeekBar mTvSeekBar;
    private ImageView mIvPlayOrPause;
    private TextView mTvTitle;
    private TextView mTvTotalTime;
    private TextView mTvPlayTime;

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
                                showAler();
                                mTvTitle.setText(list1.get(mCurrentPosition).getName());
                                mTvSeekBar.setMax(mediaPlayer.getDuration());
                                mTvTotalTime.setText(parseTime(mediaPlayer.getDuration()));
                                mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                            }
                        });
                xToast.show();
            }

            @Override
            public void onDenied(List<String> denied, boolean never) {
                new XToast<>(PDFImageActivity.this)
                        .setDuration(1000)
                        .setView(R.layout.toast_hint)
                        .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_error)
                        .setText(android.R.id.message, "请先授予悬浮窗权限")
                        .show();
            }
        });
    }

    //滑动条监听
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        // 当手停止拖拽进度条时执行该方法
        // 获取拖拽进度
        // 将进度对应设置给MediaPlayer
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(progress);
            }
        }
    };

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
        mTvSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);//滑动条监听
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
                if (mediaPlayer == null) {
                    changeMusic(0);
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
                    } else {
                        mediaPlayer.start();
                        mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                    }
                }
            }
        });
        //音调
        mTvYinDiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYinDiao();
            }
        });
        //
    }

    private int yindiao;

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
                    if (mediaPlayer != null) {
                        PlaybackParams params = mediaPlayer.getPlaybackParams();
                        switch (yindiao) {
                            case 0:
                                setYinDaio(params, 1.00f);
                                break;
                            case -1:
                                setYinDaio(params, 1.25f);
                                break;
                            case -2:
                                setYinDaio(params, 1.50f);
                                break;
                            case -3:
                                setYinDaio(params, 1.75f);
                                break;
                            case -4:
                                setYinDaio(params, 2.00f);
                                break;
                            case -5:
                                setYinDaio(params, 2.25f);
                                break;
                            case -6:
                                setYinDaio(params, 2.50f);
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
                    if (mediaPlayer != null) {
                        PlaybackParams params = mediaPlayer.getPlaybackParams();
                        switch (yindiao) {
                            case 0:
                                setYinDaio(params, 1.0f);
                                break;
                            case 1:
                                setYinDaio(params, 0.884f);
                                break;
                            case 2:
                                setYinDaio(params, 0.768f);
                                break;
                            case 3:
                                setYinDaio(params, 0.652f);
                                break;
                            case 4:
                                setYinDaio(params, 0.536f);
                                break;
                            case 5:
                                setYinDaio(params, 0.420f);
                                break;
                            case 6:
                                setYinDaio(params, 0.304f);
                                break;
                        }
                    }
                } else if (yindiao >= 6) {
                    return;
                }
            }
        });
    }

    private void setYinDaio(PlaybackParams params, float v2) {
        params.setPitch(v2);//音调
        mediaPlayer.setPlaybackParams(params);
    }

    //切歌
    private void changeMusic(int position) {
        Log.e("BenDiYinYueActivity", "position:" + position);
        if (position < 0) {
            mCurrentPosition = position = list1.size() - 1;
            Log.e("BenDiYinYueActivity", "mList.size:" + list1.size());
        } else if (position > list1.size() - 1) {//如果当前播放的是最后一首歌，则把下标改成0
            mCurrentPosition = position = 0;
        }
        Log.e("BenDiYinYueActivity", "position:" + position);
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);//监听音乐播放完毕事件，自动下一曲
        }

        try {
            // 切歌之前先重置，释放掉之前的资源
            mediaPlayer.reset();
            // 设置播放源
            Log.d("Music", list1.get(position).getPath());
            mediaPlayer.setDataSource(list1.get(position).getPath());
            mTvTitle.setText(list1.get(position).getName());
//            Glide.with(this).load(mList.get(position).album_art).into(songImage);
            // 开始播放前的准备工作，加载多媒体资源，获取相关信息
            mediaPlayer.prepare();
            // 开始播放
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 切歌时重置进度条并展示歌曲时长
        mTvSeekBar.setProgress(0);
        mTvSeekBar.setMax(mediaPlayer.getDuration());
        mTvTotalTime.setText(parseTime(mediaPlayer.getDuration()));
        updateProgress();
        if (mediaPlayer.isPlaying()) {
            mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
        } else {
            mIvPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message message) {
            // 展示给进度条和当前时间
            if (mediaPlayer != null) {
//                PlaybackParams params = mediaPlayer.getPlaybackParams();
//                float pitch = params.getPitch();//音调
//                float speed = params.getSpeed();//音速
//                Log.i("音调", "音调: -----"+pitch);
//                Log.i("音速", "音速: -----"+speed);
                int progress = mediaPlayer.getCurrentPosition();
                mTvSeekBar.setProgress(progress);
                mTvPlayTime.setText(parseTime(progress));
                // 继续定时发送数据
                updateProgress();
            }
            return true;
        }
    });

    private void updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        Message msg = Message.obtain();// 获取一个现成的消息
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
        int progress = mediaPlayer.getCurrentPosition();
        msg.arg1 = progress;
        mHandler.sendMessageDelayed(msg, INTERNAL_TIME);
    }

    /**
     * 显示速度弹窗
     */
    private SpeedDialog speedDialog;

    private void showSpeedPop() {
        if (speedDialog == null) {
            speedDialog = new SpeedDialog(this, R.style.my_dialog);
            speedDialog.setOnChangeListener(new SpeedDialog.OnTimerListener() {
                @Override
                public void OnChange(float speed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            PlaybackParams params = mediaPlayer.getPlaybackParams();
                            params.setSpeed(speed);//音速
                            mediaPlayer.setPlaybackParams(params);
                        } catch (Exception e) {
                            Log.e("Exception", "setPlaySpeed: ", e);
                        }
                    }
                }
            });
        }
        speedDialog.show();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        changeMusic(mCurrentPosition);
    }
}