package com.example.music.ui.activity.zhujiemian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.LianXiGuJiAdapter;
import com.example.music.bean.LianXiGuJiBean;
import com.example.music.bean.MusicBean;
import com.example.music.utils.HomeProgressDialog;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.StatusBarUtil;
import com.ywl5320.libenum.SampleRateEnum;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnPreparedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.music.utils.DateUtil.parseTime;

public class LianXiGuJiActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private RecyclerView mRecGuJi;
    private TextView mTvYinSuZhi;
    private TextView mTvYinSuJian;
    private TextView mTvYinSuJia;
    private Context mContext;
    private LianXiGuJiAdapter lianXiGuJiAdapter;
    private ArrayList<LianXiGuJiBean> mList;
    private ArrayList<MusicBean> allGuJi;
    private ProgressDialog dialog;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            dialog.incrementProgressBy(msg.arg1);
            if (msg.arg1 == 19) {
                dialog.dismiss();
                dialog = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lian_xi_gu_ji);
        StatusBarUtil.transparencyBar(this);
        mContext = this;
        initView();
        initWlMusic();
    }

    private WlMusic wlMusic;

    private void initWlMusic() {
        wlMusic = WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);//是否返回音频PCM数据
        wlMusic.setShowPCMDB(true);//是否返回音频分贝大小
        wlMusic.setPlayCircle(true);//循环播放
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
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mRecGuJi = findViewById(R.id.guji_rec);
        mTvYinSuZhi = findViewById(R.id.tv_guji_yinsuzhi);
        mTvYinSuJian = findViewById(R.id.tv_guji_yinsu_jian);
        mTvYinSuJia = findViewById(R.id.tv_guji_yinsu_jia);
        mIvBack.setOnClickListener(this);
        mTvYinSuJia.setOnClickListener(this);
        mTvYinSuJian.setOnClickListener(this);
        mList = new ArrayList<>();
        mList.add(new LianXiGuJiBean("42拍节奏", R.raw.a, false));
        mList.add(new LianXiGuJiBean("43拍节奏", R.raw.b, false));
        mList.add(new LianXiGuJiBean("44拍节奏二", R.raw.c, false));
        mList.add(new LianXiGuJiBean("44拍节奏一", R.raw.d, false));
        mList.add(new LianXiGuJiBean("86拍节奏一", R.raw.e, false));
        mList.add(new LianXiGuJiBean("布鲁斯节奏", R.raw.f, false));
        mList.add(new LianXiGuJiBean("流行节奏八", R.raw.g, false));
        mList.add(new LianXiGuJiBean("流行节奏二", R.raw.aa, false));
        mList.add(new LianXiGuJiBean("流行节奏六", R.raw.bb, false));
        mList.add(new LianXiGuJiBean("流行节奏七", R.raw.cc, false));
        mList.add(new LianXiGuJiBean("流行节奏三", R.raw.dd, false));
        mList.add(new LianXiGuJiBean("流行节奏四", R.raw.ee, false));
        mList.add(new LianXiGuJiBean("流行节奏五", R.raw.ff, false));
        mList.add(new LianXiGuJiBean("流行节奏一", R.raw.gg, false));
        mList.add(new LianXiGuJiBean("乡村节奏二", R.raw.aaa, false));
        mList.add(new LianXiGuJiBean("乡村节奏一", R.raw.bbb, false));
        mList.add(new LianXiGuJiBean("摇滚节奏二", R.raw.ccc, false));
        mList.add(new LianXiGuJiBean("摇滚节奏三", R.raw.ddd, false));
        mList.add(new LianXiGuJiBean("摇滚节奏四", R.raw.eee, false));
        mList.add(new LianXiGuJiBean("摇滚节奏一", R.raw.fff, false));
        allGuJi = SPBeanUtile.getAllGuJi();
        if (allGuJi.size() == 20) {

        } else {
            dialog = HomeProgressDialog.getInstance(mContext);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCanceledOnTouchOutside(false);//点击屏幕其他地方不响应
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        saveToSDCard();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }).start();
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.KEYCODE_BACK) {
                        Toast.makeText(mContext, "请稍等，正在加载鼓机文件，预计需要两分钟", Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        mRecGuJi.setLayoutManager(new GridLayoutManager(mContext, 4));
        lianXiGuJiAdapter = new LianXiGuJiAdapter(mList, mContext);
        mRecGuJi.setAdapter(lianXiGuJiAdapter);
        lianXiGuJiAdapter.setOnItemClickListener(new LianXiGuJiAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //播放音乐
                for (int i = 0; i < mList.size(); i++) {
                    LianXiGuJiBean lianXiGuJiBean = mList.get(i);
                    lianXiGuJiBean.setSelected(false);
                    mList.set(i, lianXiGuJiBean);
                }
                LianXiGuJiBean lianXiGuJiBean = mList.get(position);
                lianXiGuJiBean.setSelected(true);
                mList.set(position, lianXiGuJiBean);
                lianXiGuJiAdapter.setData(mList);
                changeMusic(mList.get(position).getName());
            }
        });
    }

    //添鼓机到本地
    public void saveToSDCard() throws Throwable {
        for (int i = 0; i < mList.size(); i++) {
            InputStream inStream = getResources().openRawResource(mList.get(i).getPath());
            File file = new File(MyApplication.getLianXiGuJiPuFile(), mList.get(i).getName() + ".mp3");
            FileOutputStream fileOutputStream = new FileOutputStream(file);//存入SDCard
            byte[] buffer = new byte[10];
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] bs = outStream.toByteArray();
            fileOutputStream.write(bs);
            outStream.close();
            inStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            Message message = new Message();
            message.arg1 = i;
            handler.sendMessage(message);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (wlMusic != null) {
                    wlMusic.stop();
                    wlMusic = null;
                }
                LianXiGuJiActivity.this.finish();
                break;
            case R.id.tv_guji_yinsu_jian://减音速
                if (wlMusic != null) {
                    setJianYinSu();
                } else {
                    Toast.makeText(mContext, "请先选择节奏", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_guji_yinsu_jia://加音速
                if (wlMusic != null) {
                    setJiaYinSu();
                } else {
                    Toast.makeText(mContext, "请先选择节奏", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void setJianYinSu() {
        String s = mTvYinSuZhi.getText().toString();
        float YinSu = Float.parseFloat(s);
        if (YinSu == 2.0f) {
            mTvYinSuZhi.setText("1.9");
            setYinSu(1.9f);
        } else if (YinSu == 1.9f) {
            mTvYinSuZhi.setText("1.8");
            setYinSu(1.8f);
        } else if (YinSu == 1.8f) {
            mTvYinSuZhi.setText("1.7");
            setYinSu(1.7f);
        } else if (YinSu == 1.7f) {
            mTvYinSuZhi.setText("1.6");
            setYinSu(1.6f);
        } else if (YinSu == 1.6f) {
            mTvYinSuZhi.setText("1.5");
            setYinSu(1.5f);
        } else if (YinSu == 1.5f) {
            mTvYinSuZhi.setText("1.4");
            setYinSu(1.4f);
        } else if (YinSu == 1.4f) {
            mTvYinSuZhi.setText("1.3");
            setYinSu(1.3f);
        } else if (YinSu == 1.3f) {
            mTvYinSuZhi.setText("1.2");
            setYinSu(1.2f);
        } else if (YinSu == 1.2f) {
            mTvYinSuZhi.setText("1.1");
            setYinSu(1.1f);
        } else if (YinSu == 1.1f) {
            mTvYinSuZhi.setText("1.0");
            setYinSu(1.0f);
        } else if (YinSu == 1.0f) {
            mTvYinSuZhi.setText("0.9");
            setYinSu(0.9f);
        } else if (YinSu == 0.9f) {
            mTvYinSuZhi.setText("0.8");
            setYinSu(0.8f);
        } else if (YinSu == 0.8f) {
            mTvYinSuZhi.setText("0.7");
            setYinSu(0.7f);
        } else if (YinSu == 0.7f) {
            mTvYinSuZhi.setText("0.6");
            setYinSu(0.6f);
        } else if (YinSu == 0.6f) {
            mTvYinSuZhi.setText("0.5");
            setYinSu(0.5f);
        }
    }

    private void setJiaYinSu() {
        String s = mTvYinSuZhi.getText().toString();
        float YinSu = Float.parseFloat(s);
        if (YinSu == 0.5f) {
            mTvYinSuZhi.setText("0.6");
            setYinSu(0.6f);
        } else if (YinSu == 0.6f) {
            mTvYinSuZhi.setText("0.7");
            setYinSu(0.7f);
        } else if (YinSu == 0.7f) {
            mTvYinSuZhi.setText("0.8");
            setYinSu(0.8f);
        } else if (YinSu == 0.8f) {
            mTvYinSuZhi.setText("0.9");
            setYinSu(0.9f);
        } else if (YinSu == 0.9f) {
            mTvYinSuZhi.setText("1.0");
            setYinSu(1.0f);
        } else if (YinSu == 1.0f) {
            mTvYinSuZhi.setText("1.1");
            setYinSu(1.1f);
        } else if (YinSu == 1.1f) {
            mTvYinSuZhi.setText("1.2");
            setYinSu(1.2f);
        } else if (YinSu == 1.2f) {
            mTvYinSuZhi.setText("1.3");
            setYinSu(1.3f);
        } else if (YinSu == 1.3f) {
            mTvYinSuZhi.setText("1.4");
            setYinSu(1.4f);
        } else if (YinSu == 1.4f) {
            mTvYinSuZhi.setText("1.5");
            setYinSu(1.5f);
        } else if (YinSu == 1.5f) {
            mTvYinSuZhi.setText("1.6");
            setYinSu(1.6f);
        } else if (YinSu == 1.6f) {
            mTvYinSuZhi.setText("1.7");
            setYinSu(1.7f);
        } else if (YinSu == 1.7f) {
            mTvYinSuZhi.setText("1.8");
            setYinSu(1.8f);
        } else if (YinSu == 1.8f) {
            mTvYinSuZhi.setText("1.9");
            setYinSu(1.9f);
        } else if (YinSu == 1.9f) {
            mTvYinSuZhi.setText("2.0");
            setYinSu(2.0f);
        }
    }

    //设置音速
    public void setYinSu(float speed) {
        wlMusic.setPlaySpeed(speed);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wlMusic != null) {
                wlMusic.stop();
                wlMusic = null;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeMusic(String name) {
        allGuJi = SPBeanUtile.getAllGuJi();
        String path = null;
        for (int i = 0; i < allGuJi.size(); i++) {
            String name1 = allGuJi.get(i).getName();
            if (name1.equals(name)) {
                path = allGuJi.get(i).getPath();
                break;
            }
        }
        if (wlMusic != null && path != null) {
            wlMusic.playNext(path);
        }
    }
}