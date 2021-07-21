package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.music.R;

import java.io.IOException;

public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private TextView mTvName;
    private SurfaceView mSur;
    private String path;
    private int mWidth;
    private int mHeight;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        initView();
    }

    private void initView() {
        mIvBack = findViewById(R.id.voide_iv_back);
        mTvName = findViewById(R.id.video_play_name);
        mSur = findViewById(R.id.surface);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        path = intent.getStringExtra("path");
        mTvName.setText(name);

        mIvBack.setOnClickListener(this);
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


    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            //设置播放的视频路径
            mediaPlayer.setDataSource(path);
            //异步加载流媒体
            mediaPlayer.prepareAsync();
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
                    int videoHeight = mediaPlayer.getVideoHeight();
                    int videoWidth = mediaPlayer.getVideoWidth();
                    if (videoHeight > mHeight || videoWidth > mWidth) {
                        //如果视频的宽或者高超出屏幕,要缩放
                        float widthRatio = (float) videoWidth / (float) mWidth;
                        float heightRatio = (float) videoHeight / (float) mHeight;
                        //选择大的进行缩放
                        float max = Math.max(widthRatio, heightRatio);
                        videoWidth = (int) Math.ceil(videoWidth / max);
                        videoHeight = (int) Math.ceil(videoHeight / max);
                        //设置surfaceview的布局参数
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(videoWidth, videoHeight);
                        //设置垂直居中
                        layoutParams.gravity = Gravity.CENTER_VERTICAL;
                        mSur.setLayoutParams(layoutParams);
                    }
                    mediaPlayer.start();
                    //mediaPlayer.setLooping(true);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放完成监听
                    finish();
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
                VideoPlayActivity.this.finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finis();
        return super.onKeyDown(keyCode, event);
    }

    public void finis() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}