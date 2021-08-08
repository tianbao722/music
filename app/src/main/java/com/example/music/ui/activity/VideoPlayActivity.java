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
import com.ywl5320.wlmedia.WlMedia;
import com.ywl5320.wlmedia.enums.WlComplete;
import com.ywl5320.wlmedia.listener.WlOnMediaInfoListener;
import com.ywl5320.wlmedia.listener.WlOnPcmDataListener;
import com.ywl5320.wlmedia.listener.WlOnVideoViewListener;
import com.ywl5320.wlmedia.log.WlLog;
import com.ywl5320.wlmedia.surface.WlSurfaceView;
import com.ywl5320.wlmedia.widget.WlSeekBar;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.example.music.utils.DateUtil.parseTime;

public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private ImageView mQuanPing;
    private ImageView mSanJiao;
    private LinearLayout mLL;
    private LinearLayout mLLYinSu;
    private LinearLayout mLLYinDiao;
    private ConstraintLayout mConsl;
    private ConstraintLayout mVideoConsl;
    private ImageView mTvPlay;
    private TextView mTvName;
    private TextView mTotalTime;
    private TextView mPlayTime;
    private SeekBar wlSeekBar;
    private WlSurfaceView wlSurfaceView;
    private String path;
    private int mWidth;
    private int mHeight;
    private boolean isQuanPing = true;      //全屏 ：true  半屏：false
    private Context mContext;
    private WlMedia wlMedia;
    private WlmediaListener wlmediaListener;
    final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};

    public int sizeOfInt(int x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            // 展示给进度条和当前时间
            if (wlMedia != null) {
                int progress = (int) wlMedia.getNowTime();
                wlSeekBar.setProgress(progress);
                double nowTime = wlMedia.getNowTime();
                if (nowTime <= 0) {
                    mPlayTime.setText("00:00");
                } else {
                    int mm = (int) (nowTime / 60);
                    int ss = (int) (nowTime % 60);
                    int mmm = sizeOfInt(mm);
                    int sss = sizeOfInt(ss);
                    if (mmm == 1 && sss == 1) {
                        mPlayTime.setText("0" + mm + ":0" + ss);
                    } else if (sss == 1) {
                        mPlayTime.setText(mm + ":0" + ss);
                    } else if (mmm == 1) {
                        mPlayTime.setText("0" + mm + ":" + ss);
                    }
                }
                double duration = wlMedia.getDuration();
                if (duration <= 0) {
                    mTotalTime.setText("00:00");
                } else {
                    int tmm = (int) (duration / 60);
                    int tss = (int) (duration % 60);
                    int tmmm = sizeOfInt(tmm);
                    int tsss = sizeOfInt(tss);
                    if (tmmm == 1 && tsss == 1) {
                        mTotalTime.setText("0" + tmm + ":0" + tss);
                    } else if (tsss == 1) {
                        mTotalTime.setText(tmm + ":0" + tss);
                    } else if (tmmm == 1) {
                        mTotalTime.setText("0" + tmm + ":" + tss);
                    }
                }
                wlSeekBar.setMax((int) duration);
                // 继续定时发送数据
                updateProgress();
            }
            return true;
        }
    });
    private static final int INTERNAL_TIME = 100;// 音乐进度间隔时间
    private int videoWidth;
    private int videoHeight;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;

    private void updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        Message msg = Message.obtain();// 获取一个现成的消息
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
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

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mIvBack = findViewById(R.id.voide_iv_back);
        mTvName = findViewById(R.id.video_play_name);
        mConsl = findViewById(R.id.voide_play_consl);
        mVideoConsl = findViewById(R.id.voide_consl);
        mLLYinSu = findViewById(R.id.voide_yinsu);
        mLL = findViewById(R.id.voide_ll);
        mLLYinDiao = findViewById(R.id.voide_yindiao);
        mTvPlay = findViewById(R.id.video_btn_play_or_pause);
        mPlayTime = findViewById(R.id.video_play_tv_play_time);
        wlSeekBar = findViewById(R.id.video_play_time_seekBar);
        mTotalTime = findViewById(R.id.video_play_tv_total_time);
        mQuanPing = findViewById(R.id.video_play_quanping);
        mSanJiao = findViewById(R.id.video_play_sanjiao);
        wlSurfaceView = findViewById(R.id.surface);
        wlSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
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
                }
                return false;
            }
        });
        //滑动监听
        wlSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (wlMedia.getDuration() > 0) {
                    wlMedia.seek(progress);
                } else {
                    wlMedia.seekEnd();
                }
            }
        });
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        path = intent.getStringExtra("path");
        mTvName.setText(name);
        mIvBack.setOnClickListener(this);
        mTvPlay.setOnClickListener(this);
        mSanJiao.setOnClickListener(this);
        mLLYinSu.setOnClickListener(this);
        mLLYinDiao.setOnClickListener(this);
        mQuanPing.setOnClickListener(this);
        getScreen();
        initWlMedia();
    }

    private void initWlMedia() {
        wlMedia = new WlMedia();
        wlMedia.setSource(path);
        wlMedia.setUseSoundTouch(true);
        wlMedia.setClearLastPicture(false);
        wlMedia.setLoopPlay(true);
        wlMedia.setTimeOut(30);
        wlSurfaceView.setWlMedia(wlMedia);
        wlmediaListener = new WlmediaListener();
        wlMedia.setOnMediaInfoListener(wlmediaListener);
        wlMedia.setOnPcmDataListener(new WlOnPcmDataListener() {
            @Override
            public void onPcmInfo(int bit, int channel, int samplerate) {
                WlLog.d("pcmcallback bit:" + bit + ",channel:" + channel + ",sample" + samplerate);
            }

            @Override
            public void onPcmData(int size, byte[] data, double db) {
                WlLog.d("pcmcallback size:" + size + ",db:" + db);
            }
        });
        wlSurfaceView.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
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
                    mVideoConsl.setLayoutParams(lp);
                } else {
                    setBanPing();
                }
                if (wlMedia != null)
                    wlMedia.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height) {

            }

            @Override
            public void moveX(double value, int move_type) {
                if (move_type == WlSurfaceView.MOVE_START) {//滑动前 显示UI
                    if (wlMedia != null)
                        wlMedia.seekStart();
                } else if (move_type == WlSurfaceView.MOVE_ING) {//滑动中
                    wlSeekBar.setProgress((int) (value));
                } else if (move_type == WlSurfaceView.MOVE_STOP) {//滑动结束 seek
                    if (value >= 0 && wlMedia.getDuration() > 0) {
                        if (wlMedia != null)
                            wlMedia.seek(value);
                    } else {
                        if (wlMedia != null)
                            wlMedia.seekEnd();
                    }
                }
            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });
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
                if (wlMedia != null) {
                    showSpeedPop();
                } else {
                    Toast.makeText(mContext, "请先播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.voide_yindiao://音调
                if (wlMedia != null) {
                    showYinDiao();
                } else {
                    Toast.makeText(mContext, "请先播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.video_btn_play_or_pause://暂停||播放
                if (wlMedia.isPause()) {
                    if (wlMedia != null)
                        wlMedia.resume();
                    mTvPlay.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                } else {
                    if (wlMedia != null)
                        wlMedia.pause();
                    mTvPlay.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
                }
                break;
        }
    }

    /**
     * 显示音调弹窗
     */
    private int yindiao;

    private void setYinDiaoZhi(TextView mTvYinDiaoZhi, float playSpeed) {
        if (playSpeed == 1.0f) {
            mTvYinDiaoZhi.setText("0");
        } else if (playSpeed == 0.917f) {
            mTvYinDiaoZhi.setText("1");
        } else if (playSpeed == 0.834f) {
            mTvYinDiaoZhi.setText("2");
        } else if (playSpeed == 0.751f) {
            mTvYinDiaoZhi.setText("3");
        } else if (playSpeed == 0.668f) {
            mTvYinDiaoZhi.setText("4");
        } else if (playSpeed == 0.585f) {
            mTvYinDiaoZhi.setText("5");
        } else if (playSpeed == 0.5f) {
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
        double pitch = wlMedia.getPitch();
        float pitch1 = (float) pitch;
        setYinDiaoZhi(mTvYinDiaoZhi, pitch1);
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
                    if (wlMedia != null) {
                        switch (yindiao) {
                            case 1:
                                setYinDaio(0.917f);
                                break;
                            case 2:
                                setYinDaio(0.834f);
                                break;
                            case 3:
                                setYinDaio(0.751f);
                                break;
                            case 4:
                                setYinDaio(0.668f);
                                break;
                            case 5:
                                setYinDaio(0.585f);
                                break;
                            case 6:
                                setYinDaio(0.5f);
                                break;
                            case 0:
                                setYinDaio(1.00f);
                                break;
                            case -1:
                                setYinDaio(1.25f);
                                break;
                            case -2:
                                setYinDaio(1.50f);
                                break;
                            case -3:
                                setYinDaio(1.75f);
                                break;
                            case -4:
                                setYinDaio(2.00f);
                                break;
                            case -5:
                                setYinDaio(2.25f);
                                break;
                            case -6:
                                setYinDaio(2.50f);
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
                    if (wlMedia != null) {
                        switch (yindiao) {
                            case 1:
                                setYinDaio(0.917f);
                                break;
                            case 2:
                                setYinDaio(0.834f);
                                break;
                            case 3:
                                setYinDaio(0.751f);
                                break;
                            case 4:
                                setYinDaio(0.668f);
                                break;
                            case 5:
                                setYinDaio(0.585f);
                                break;
                            case 6:
                                setYinDaio(0.5f);
                                break;
                            case 0:
                                setYinDaio(1.00f);
                                break;
                            case -1:
                                setYinDaio(1.25f);
                                break;
                            case -2:
                                setYinDaio(1.50f);
                                break;
                            case -3:
                                setYinDaio(1.75f);
                                break;
                            case -4:
                                setYinDaio(2.00f);
                                break;
                            case -5:
                                setYinDaio(2.25f);
                                break;
                            case -6:
                                setYinDaio(2.50f);
                                break;
                        }
                    }
                } else if (yindiao >= 6) {
                    return;
                }
            }
        });
    }

    private void setYinDaio(float v2) {
        if (wlMedia != null)
            wlMedia.setPitch(v2);
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
        double speed = wlMedia.getSpeed();
        float speed1 = (float) speed;
        mTvYinDiaoZhi.setText(speed1 + "");
        //减音速
        mTvJianYinDiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mTvYinDiaoZhi.getText().toString();
                if (wlMedia != null) {
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
                if (wlMedia != null) {
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
        wlMedia.setSpeed(speed);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK://监听返回键
                if (isQuanPing) {   //全屏切换半屏
                    setBanPing();
                    return true;
                } else {
                    if (wlMedia != null) {
                        wlMedia.stop();
                        wlMedia.release();
                        wlMedia = null;
                    }
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    //半屏
    private boolean isHeight = false;

    private void setBanPing() {
        isQuanPing = false;
        if (videoWidth < mWidth) {
            if (!isHeight) {
                int cha = mWidth - videoWidth;
                videoHeight = videoHeight + cha;
                isHeight = true;
            }
        }
        mQuanPing.setImageDrawable(getResources().getDrawable(R.mipmap.quanping));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, videoHeight);
        mVideoConsl.setLayoutParams(lp);
    }

    //全屏
    private void setQuanPing() {
        isQuanPing = true;
        mQuanPing.setImageDrawable(getResources().getDrawable(R.mipmap.quxiaoquanping));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 手动横屏
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        mVideoConsl.setLayoutParams(lp);
    }

    public void finis() {
        if (isQuanPing) { // 全屏转半屏
            setBanPing();
        } else {
            if (wlMedia != null) {
                wlMedia.stop();
                wlMedia.release();
                wlMedia = null;
            }
            VideoPlayActivity.this.finish();
        }
    }

    public class WlmediaListener implements WlOnMediaInfoListener {
        @Override
        public void onPrepared() {
            if (wlMedia != null) {
                wlMedia.start();
                updateProgress();
            }
        }

        @Override
        public void onError(int code, String msg) {
            WlLog.d("onError " + msg);
            showToast(msg);
        }

        @Override
        public void onComplete(WlComplete type, String msg) {
//            if(type == WlComplete.WL_COMPLETE_EOF)
//            {
//                showToast("播放完成");
//            }
//            else if(type == WlComplete.WL_COMPLETE_ERROR)
//            {
//                showToast("播放出错：" + msg);
//            }
//            else if(type == WlComplete.WL_COMPLETE_TIMEOUT)
//            {
//                showToast("链接超时，正在重连");
//                wlMedia.next();
//            }
        }

        @Override
        public void onTimeInfo(double currentTime, double bufferTime) {
//            if (wlMedia.getDuration() > 0) {
//                wlSeekBar.setProgress(currentTime / wlMedia.getDuration(), bufferTime / wlMedia.getDuration());
//            } else {
//                wlSeekBar.setProgress(0.5);
//            }
//            WlLog.d("onTimeInfo 2 " + currentTime + " " + bufferTime);
        }

        @Override
        public void onSeekFinish() {

        }

        @Override
        public void onLoopPlay(int loopCount) {

        }

        @Override
        public void onLoad(boolean load) {
//            if (load) {
//                wlCircleLoadView.setVisibility(View.VISIBLE);
//            } else {
//                wlCircleLoadView.setVisibility(View.GONE);
//            }
        }

        @Override
        public byte[] decryptBuffer(byte[] encryptBuffer) {
            return new byte[0];
        }

        @Override
        public byte[] readBuffer(int read_size) {
            return new byte[0];
        }

        @Override
        public void onPause(boolean pause) {

        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    }
}