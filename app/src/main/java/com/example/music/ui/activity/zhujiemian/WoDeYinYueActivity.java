package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.MusicAdapter;
import com.example.music.adapter.TuPianYuePuAdapter;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.MusicBean;
import com.example.music.ui.activity.search.SearchMusicActivity;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.SpeedDialog;
import com.example.music.utils.StatusBarUtil;
import com.ywl5320.bean.TimeBean;
import com.ywl5320.libenum.SampleRateEnum;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnErrorListener;
import com.ywl5320.listener.OnInfoListener;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.util.WlTimeUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class WoDeYinYueActivity extends AppCompatActivity {
    private TextView mTvBeiSu;
    private ImageView mTvSearchWoDeYinYue;
    private TextView mTvYinDiao;
    private TextView mTvBoFangMoShi;
    private TextView mYinYueTvXinZeng;
    private TextView tvTotalTime;
    private TextView mTvMusicName;
    private TextView tvPlayTime;
    private ImageView mIvBack;
    private ImageView mBtnPreVious;
    private ImageView btnPlayOrPause;
    private ImageView mBtnNext;
    private SeekBar timeSeekBar;
    private RecyclerView mYinYue_Rec_image;
    private RecyclerView mYinYueRecTitle;
    private Context mContext;
    private ArrayList<BenDiYuePuBean> strings;
    private TuPianYuePuAdapter tuPianYuePuAdapter;
    private int mPosition;
    private ArrayList<MusicBean> mList;//歌曲列表
    private MusicAdapter musicAdapter;
    // 记录当前播放歌曲的位置
    public int mCurrentPosition;
    private boolean classify = false;
    private int mPattern = 0;//0：列表循环 1：单曲循环 2：单曲播放
    private WlMusic wlMusic;
    private int position = 0;
    private int isPause = 0;//0:表示没有播放音乐，从0开始播放， 1：表示暂停 继续播放

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    TimeBean timeBean = (TimeBean) msg.obj;
                    timeSeekBar.setProgress(timeBean.getCurrSecs() * 100 / timeBean.getTotalSecs());
                    String playTime = WlTimeUtil.secdsToDateFormat(timeBean.getCurrSecs(), timeBean.getTotalSecs());
                    String totalTime = WlTimeUtil.secdsToDateFormat(timeBean.getTotalSecs(), timeBean.getTotalSecs());
                    tvPlayTime.setText(playTime);
                    tvTotalTime.setText(totalTime);
                    if (mList != null && mList.size() > 0)
                        mTvMusicName.setText(mList.get(mCurrentPosition).getName());
                    if (playTime.equals(totalTime)) {
                        changeMusic(++mCurrentPosition);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_de_yin_yue);
        mContext = this;
        StatusBarUtil.transparencyBar(this);
        initView();
        initWlMusic();
        initListener();
        //初始化音乐左边title
        initRecTuPianYuePu();
        //初始化音乐右边歌曲
        initRecImageYuePu();
    }

    private void initListener() {
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
//                MyLog.d("curr:" + timeBean.getCurrSecs() + ", total:" + timeBean.getTotalSecs());
                Message message = Message.obtain();
                message.obj = timeBean;
                message.what = 1;
                handler.sendMessage(message);
            }
        });
    }

    private void initWlMusic() {
        wlMusic = WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);//是否返回音频PCM数据
        wlMusic.setShowPCMDB(true);//是否返回音频分贝大小
        wlMusic.setPlayCircle(false);//循环播放
        wlMusic.setVolume(100);//声音大小100%
        wlMusic.setPlaySpeed(1.0f);//播放速度正常
        wlMusic.setPlayPitch(1.0f);//播放音调正常
        wlMusic.setConvertSampleRate(SampleRateEnum.RATE_44100);//设定恒定采样率（null为取消）
    }

    private void initView() {
        mBtnPreVious = findViewById(R.id.btn_previous);
        timeSeekBar = findViewById(R.id.time_seekBar);
        btnPlayOrPause = findViewById(R.id.btn_play_or_pause);
        mTvMusicName = findViewById(R.id.tv_gequname);
        mBtnNext = findViewById(R.id.btn_next);
        mTvBeiSu = findViewById(R.id.tv_beisu);
        tvTotalTime = findViewById(R.id.tv_total_time);
        mYinYue_Rec_image = findViewById(R.id.yinyue_rec_image_yuepu);
        tvPlayTime = findViewById(R.id.tv_play_time);
        mTvYinDiao = findViewById(R.id.tv_yindiao);
        mYinYueRecTitle = findViewById(R.id.yinyue_rec_tupianyuepy);
        mTvBoFangMoShi = findViewById(R.id.tv_bofangmoshi);
        mYinYueTvXinZeng = findViewById(R.id.yinyue_tv_xinzeng);
        mTvSearchWoDeYinYue = findViewById(R.id.tv_search_wodeyinyue);
        mIvBack = findViewById(R.id.iv_back);

        mBtnPreVious.setOnClickListener(this::onViewClicked);
        btnPlayOrPause.setOnClickListener(this::onViewClicked);
        mBtnNext.setOnClickListener(this::onViewClicked);
        mTvBeiSu.setOnClickListener(this::onViewClicked);
        mTvYinDiao.setOnClickListener(this::onViewClicked);
        mTvBoFangMoShi.setOnClickListener(this::onViewClicked);
        mYinYueTvXinZeng.setOnClickListener(this::onViewClicked);
        mTvSearchWoDeYinYue.setOnClickListener(this::onViewClicked);
        mIvBack.setOnClickListener(this::onViewClicked);
    }

    @OnClick({R.id.btn_previous, R.id.btn_play_or_pause, R.id.btn_next, R.id.tv_beisu, R.id.tv_yindiao, R.id.tv_bofangmoshi, R.id.tv_search_wodeyinyue, R.id.yinyue_tv_xinzeng, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_previous://上一曲
                if (wlMusic != null && mList != null && mList.size() > 0) {
                    changeMusic(--mCurrentPosition);
                } else {
                    Toast.makeText(mContext, "请先添加音乐", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_play_or_pause://播放或者暂停
                if (wlMusic != null && wlMusic.isPlaying() && isPause == 2) {
                    isPause = 1;
                    wlMusic.pause();//暂停
                    btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
                } else if (isPause == 0 && wlMusic != null && !wlMusic.isPlaying() && mList != null && mList.size() > 0) {
                    mCurrentPosition = 0;
                    wlMusic.setSource(mList.get(0).getPath());
                    wlMusic.prePared();
                    isPause = 2;
                    btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                } else if (wlMusic != null && wlMusic.isPlaying() && isPause == 1) {
                    isPause = 2;
                    wlMusic.resume();
                    btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                }
                break;
            case R.id.btn_next://下一曲
                if (wlMusic != null && mList != null && mList.size() > 0) {
                    changeMusic(++mCurrentPosition);
                } else {
                    Toast.makeText(mContext, "请先添加音乐", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_beisu://播放倍速
                if (mList != null && wlMusic != null) {
                    showSpeedPop();
                } else {
                    Toast.makeText(mContext, "请先添加音乐", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_yindiao://音调
                if (mList != null && wlMusic != null) {
                    showYinDiao();
                } else {
                    Toast.makeText(mContext, "请先添加音乐", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_bofangmoshi://播放模式
                if (mList != null) {
                    showBoFangMoShi();
                } else {
                    Toast.makeText(mContext, "请先添加音乐", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_search_wodeyinyue://搜索
                Intent intent = new Intent(mContext, SearchMusicActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.yinyue_tv_xinzeng://新增
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
                //确定
                mTvEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = mEdBenDiQuPu.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            if (strings.size() != 0) {
                                for (int i = 0; i < strings.size(); i++) {
                                    String title = strings.get(i).getTitle();
                                    if (title.equals(text)) {
                                        classify = false;
                                        break;
                                    } else {
                                        classify = true;
                                    }
                                }
                                if (classify) {
                                    boolean tuPiQuPuFile = SPBeanUtile.createWoDeYinYueFile(text);
                                    if (tuPiQuPuFile) {
                                        for (int i = 0; i < strings.size(); i++) {
                                            BenDiYuePuBean benDiYuePuBean = strings.get(i);
                                            benDiYuePuBean.setSelected(false);
                                            strings.set(i, benDiYuePuBean);
                                        }
                                        BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                                        strings.add(0, benDiYuePuBean);
                                        tuPianYuePuAdapter.notifyDataSetChanged();
                                        if (alertDialog != null) {
                                            alertDialog.dismiss();
                                        }
                                        Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(mContext, "分类已经存在", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                boolean tuPiQuPuFile = SPBeanUtile.createWoDeYinYueFile(text);
                                if (tuPiQuPuFile) {
                                    BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                                    strings.add(0, benDiYuePuBean);
                                    tuPianYuePuAdapter.notifyDataSetChanged();
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                    Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "请输入分类名称", Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.iv_back://返回
                WoDeYinYueActivity.this.finish();
                break;
        }
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
                    if (wlMusic != null) {
                        switch (yindiao) {
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
                    if (wlMusic != null) {
                        switch (yindiao) {
                            case 0:
                                setYinDaio(1.0f);
                                break;
                            case 1:
                                setYinDaio(0.884f);
                                break;
                            case 2:
                                setYinDaio(0.768f);
                                break;
                            case 3:
                                setYinDaio(0.652f);
                                break;
                            case 4:
                                setYinDaio(0.536f);
                                break;
                            case 5:
                                setYinDaio(0.420f);
                                break;
                            case 6:
                                setYinDaio(0.304f);
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
        wlMusic.setPlayPitch(v2);
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
                    wlMusic.setPlaySpeed(speed);
                }
            });
        }
        speedDialog.show();
    }

    //播放模式
    private void showBoFangMoShi() {
        if (mTvBoFangMoShi.getText().equals("列表循环")) {
            mPattern = 1;
            mTvBoFangMoShi.setText("单曲循环");
            wlMusic.setPlayCircle(true);//设置单曲循环
        } else if (mTvBoFangMoShi.getText().equals("单曲循环")) {
            mPattern = 2;
            mTvBoFangMoShi.setText("单曲播放");
        } else if (mTvBoFangMoShi.getText().equals("单曲播放")) {
            mPattern = 0;
            mTvBoFangMoShi.setText("列表循环");
        }
    }

    private void initRecTuPianYuePu() {
        ArrayList<BenDiYuePuBean> spList = SPBeanUtile.getWoDeYinYueFileList();
        if (spList != null) {
            strings = spList;
        } else {
            strings = new ArrayList<>();
        }
        tuPianYuePuAdapter = new TuPianYuePuAdapter(mContext, strings);
        mYinYueRecTitle.setLayoutManager(new LinearLayoutManager(mContext));
        mYinYueRecTitle.setAdapter(tuPianYuePuAdapter);
        tuPianYuePuAdapter.setOnItemClickListener(new TuPianYuePuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mPosition = position;
                for (int i = 0; i < strings.size(); i++) {
                    BenDiYuePuBean benDiYuePuBean = strings.get(i);
                    benDiYuePuBean.setSelected(false);
                    strings.set(i, benDiYuePuBean);
                }
                BenDiYuePuBean benDiYuePuBean = strings.get(position);
                benDiYuePuBean.setSelected(true);
                strings.set(position, benDiYuePuBean);
                tuPianYuePuAdapter.notifyDataSetChanged();
                mList.clear();
                mList = getImageFileList();
                musicAdapter.setData(getImageFileList());
            }
        });
    }

    private void initRecImageYuePu() {
        mYinYue_Rec_image.setLayoutManager(new GridLayoutManager(mContext, 2));
        mList = getImageFileList();
        musicAdapter = new MusicAdapter(mList, mContext);
        mYinYue_Rec_image.setAdapter(musicAdapter);
        //条目点击事件
        musicAdapter.setOnItemClickListener(new MusicAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, MusicBean musicBean) {
                mCurrentPosition = position;
                changeMusic(mCurrentPosition);
            }
        });
    }

    //切歌
    private void changeMusic(int mCurrentPosition) {
        if (wlMusic == null) return;
        if (mCurrentPosition > mList.size() - 1) {
            mCurrentPosition = 0;
        } else if (mCurrentPosition < 0) {
            mCurrentPosition = mList.size() - 1;
        }
        this.mCurrentPosition = mCurrentPosition;
        wlMusic.playNext(mList.get(mCurrentPosition).getPath());
        if (wlMusic.isPlaying()) {
            btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
        } else {
            btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
        }
    }

    private ArrayList<MusicBean> getImageFileList() {
        ArrayList<MusicBean> musicBeans = new ArrayList<>();
        String path = MyApplication.getWoDeYinYueFile().getPath();
        if (strings == null || strings.size() == 0) {
            return null;
        }
        String currentPath = path + "/" + strings.get(mPosition).getTitle();
        List<File> files2 = FileUtils.listFilesInDirWithFilter(currentPath, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getPath().endsWith(".mp3"));
            }
        });
        for (int i = 0; i < files2.size(); i++) {
            String path1 = files2.get(i).getPath();
            String fileName = FileUtils.getFileName(files2.get(i));
            String name = fileName.substring(0, fileName.length() - 4);
            String size = FileUtils.getSize(files2.get(i));
            MusicBean musicBean = new MusicBean(name, 0, size, path1);
            musicBeans.add(musicBean);
        }
        return musicBeans;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wlMusic != null) {
            wlMusic.stop();
        }
    }
}