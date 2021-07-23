package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music.R;
import com.example.music.utils.SpeedDialog;
import com.example.music.utils.StatusBarUtil;

import java.io.IOException;

import static com.example.music.utils.DateUtil.parseTime;

public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private ImageView mQuanPing;
    private ImageView mSanJiao;
    private LinearLayout mLL;
    private LinearLayout mLLYinSu;
    private LinearLayout mLLYinDiao;
    private ConstraintLayout mConsl;
    private ImageView mTvPlay;
    private TextView mTvName;
    private TextView mTotalTime;
    private TextView mPlayTime;
    private SeekBar mBar;
    private SurfaceView mSur;
    private String path;
    private int mWidth;
    private int mHeight;
    private MediaPlayer mediaPlayer;
    private boolean isQuanPing = true;      //全屏 ：true  半屏：false
    private Context mContext;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            // 展示给进度条和当前时间
            if (mediaPlayer != null) {
                int progress = mediaPlayer.getCurrentPosition();
                mBar.setProgress(progress);
                mPlayTime.setText(parseTime(progress));
                // 继续定时发送数据
                updateProgress();
            }
            return true;
        }
    });

    private static final int INTERNAL_TIME = 100;// 音乐进度间隔时间
    private int videoWidth;
    private int videoHeight;

    private void updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        Message msg = Message.obtain();// 获取一个现成的消息
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
        int progress = mediaPlayer.getCurrentPosition();
        msg.arg1 = progress;
        mHandler.sendMessageDelayed(msg, INTERNAL_TIME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        mContext = this;
        StatusBarUtil.transparencyBar(this);
        initView();
    }

    private void initView() {
        mIvBack = findViewById(R.id.voide_iv_back);
        mTvName = findViewById(R.id.video_play_name);
        mConsl = findViewById(R.id.voide_play_consl);
        mLLYinSu = findViewById(R.id.voide_yinsu);
        mLL = findViewById(R.id.voide_ll);
        mLLYinDiao = findViewById(R.id.voide_yindiao);
        mTvPlay = findViewById(R.id.video_btn_play_or_pause);
        mPlayTime = findViewById(R.id.video_play_tv_play_time);
        mBar = findViewById(R.id.video_play_time_seekBar);
        mTotalTime = findViewById(R.id.video_play_tv_total_time);
        mQuanPing = findViewById(R.id.video_play_quanping);
        mSanJiao = findViewById(R.id.video_play_sanjiao);
        mSur = findViewById(R.id.surface);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        path = intent.getStringExtra("path");
        mTvName.setText(name);
        mBar.setOnSeekBarChangeListener(seekBarChangeListener);//滑动条监听
        mIvBack.setOnClickListener(this);
        mTvPlay.setOnClickListener(this);
        mSanJiao.setOnClickListener(this);
        mLLYinSu.setOnClickListener(this);
        mLLYinDiao.setOnClickListener(this);
        mQuanPing.setOnClickListener(this);
        getScreen();
        initMediaPlayer();
    }


    //获取屏幕宽高
    private void getScreen() {
        WindowManager systemService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = systemService.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;
    }

    /**
     * 触摸屏幕 按键出现
     */
    public boolean onTouchEvent(android.view.MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isQuanPing) {
                    if (mConsl.getVisibility() == View.VISIBLE) {
                        mConsl.setVisibility(View.GONE);
                        mLL.setVisibility(View.GONE);
                    } else {
                        mConsl.setVisibility(View.VISIBLE);
                        mLL.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mLL.getVisibility() == View.VISIBLE) {
                        mLL.setVisibility(View.GONE);
                    } else {
                        mLL.setVisibility(View.VISIBLE);
                    }
                }
                break;

            default:
                break;
        }
        return true;
    }

    ;

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            //设置播放的视频路径
            mediaPlayer.setDataSource(path);
            mediaPlayer.setLooping(true);//循环播放
////            //异步加载流媒体
            mediaPlayer.prepareAsync();
            // 开始播放前的准备工作，加载多媒体资源，获取相关信息
//            mediaPlayer.prepare();
            //获取SurfaceHolder
            SurfaceHolder holder = mSur.getHolder();
            //确保surfaceHolder已经准备好了。因此需要给surfaceHolder设置一个callback，
            //调用addCallback()方法。Callback 有三个回调函数
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    //SurfaceHolder被创建的时候回调
                    mediaPlayer.setDisplay(holder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    //SurfaceHolder被销毁的时候回调，在这里可以做一些释放资源的操作，防止内存泄漏
                }
            });
            //MediaPlayer加载流媒体完毕的监听
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //获取视频的宽高
                    videoHeight = mediaPlayer.getVideoHeight();
                    videoWidth = mediaPlayer.getVideoWidth();
                    if (videoHeight > mHeight || videoWidth > mWidth) {
//                    如果视频的宽或者高超出屏幕,要缩放
                        float widthRatio = (float) videoWidth / (float) mWidth;
                        float heightRatio = (float) videoHeight / (float) mHeight;
                        //选择大的进行缩放
                        float max = Math.max(widthRatio, heightRatio);
                        videoWidth = (int) Math.ceil(videoWidth / max);
                        videoHeight = (int) Math.ceil(videoHeight / max);
                        mQuanPing.setImageDrawable(getResources().getDrawable(R.mipmap.quanping));
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
                        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(videoWidth, videoHeight);
                        mSur.setLayoutParams(lp);
                    } else {
                        setBanPing();
                    }
                    mediaPlayer.start();
                    // 切歌时重置进度条并展示歌曲时长
                    mBar.setProgress(0);
                    mBar.setMax(mediaPlayer.getDuration());
                    mTotalTime.setText(parseTime(mediaPlayer.getDuration()));
                    updateProgress();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放完成监听
                    if (isQuanPing) {
                        setBanPing();
                    }
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    //发生错误监听
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voide_iv_back:
                finis();
                break;
            case R.id.video_play_quanping://全屏
                if (isQuanPing) {//全屏切换半屏
                    setBanPing();
                } else {//半屏切换全屏
                    setQuanPing();
                }
                break;
            case R.id.video_play_sanjiao://显示||隐藏音速，音调
                if (mLLYinSu.getVisibility() == View.VISIBLE) {
                    mLLYinSu.setVisibility(View.GONE);
                    mLLYinDiao.setVisibility(View.GONE);
                } else {
                    mLLYinSu.setVisibility(View.VISIBLE);
                    mLLYinDiao.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.voide_yinsu://音速
                if (mediaPlayer != null) {
                    showSpeedPop();
                } else {
                    Toast.makeText(mContext, "请先播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.voide_yindiao://音调
                if (mediaPlayer != null) {
                    showYinDiao();
                } else {
                    Toast.makeText(mContext, "请先播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.video_btn_play_or_pause://暂停||播放
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mTvPlay.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
                } else {
                    mediaPlayer.start();
                    mTvPlay.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                }
                break;
        }
    }

    /**
     * 显示音调弹窗
     */
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
        try {
            params.setPitch(v2);//音调
            mediaPlayer.setPlaybackParams(params);
        } catch (Exception e) {
            Toast.makeText(mContext, "错误", Toast.LENGTH_SHORT).show();
        }
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
                            Log.e("速度异常", "异常: " + e.getMessage(), e);
                        }
                    }
                }
            });
        }
        speedDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK://监听返回键
                if (isQuanPing) {   //全屏切换半屏
                    setBanPing();
                    return true;
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    //半屏
    private void setBanPing() {
        isQuanPing = false;
        if (videoWidth < mWidth) {
            int cha = mWidth - videoWidth;
            videoHeight = videoHeight + cha;
        }
        mQuanPing.setImageDrawable(getResources().getDrawable(R.mipmap.quanping));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, videoHeight);
        mSur.setLayoutParams(lp);
    }

    //全屏
    private void setQuanPing() {
        isQuanPing = true;
        mQuanPing.setImageDrawable(getResources().getDrawable(R.mipmap.quxiaoquanping));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 手动横屏
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        mSur.setLayoutParams(lp);
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

    public void finis() {
        if (isQuanPing) { // 全屏转半屏
            setBanPing();
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            VideoPlayActivity.this.finish();
        }
    }
}