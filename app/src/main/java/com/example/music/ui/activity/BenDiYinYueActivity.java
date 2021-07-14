package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
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
import android.widget.Button;
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
import com.example.music.bean.Song;
import com.example.music.utils.Constant;
import com.example.music.utils.MusicUtils;
import com.example.music.utils.ObjectUtils;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.SPUtils;
import com.example.music.utils.SpeedDialog;
import com.example.music.utils.StatusBarUtil;
import com.example.music.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.music.utils.DateUtil.parseTime;

public class BenDiYinYueActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    @BindView(R.id.tv_clear_list)
    TextView tvClearList;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_play_time)
    TextView tvPlayTime;
    @BindView(R.id.time_seekBar)
    SeekBar timeSeekBar;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.btn_previous)
    ImageView btnPrevious;
    @BindView(R.id.btn_play_or_pause)
    ImageView btnPlayOrPause;
    @BindView(R.id.btn_next)
    ImageView btnNext;
    @BindView(R.id.tv_play_song_info)
    TextView tvPlaySongInfo;
    @BindView(R.id.play_state_img)
    ImageView playStateImg;
    @BindView(R.id.play_state_lay)
    LinearLayout playStateLay;
    @BindView(R.id.tv_beisu)
    TextView tvbeisu;
    @BindView(R.id.music_search_view)
    SearchView mSearchView;
    @BindView(R.id.tv_yindiao)
    TextView tv_yindiao;
    @BindView(R.id.tv_bofangmoshi)
    TextView mTvBoFangMoShi;
    @BindView(R.id.yinyue_rec_tupianyuepy)
    RecyclerView mYinYueRecTitle;
    @BindView(R.id.yinyue_tv_xinzeng)
    TextView mYinYueTVXinZeng;
    @BindView(R.id.yinyue_rec_image_yuepu)
    RecyclerView mYinYue_Rec_image;
    @BindView(R.id.tv_search_wodeyinyue)
    ImageView mTvSearchWoDeYinYue;
    private ArrayList<MusicBean> mList;//歌曲列表
    private RxPermissions rxPermissions;//权限请求
    private MediaPlayer mediaPlayer;//音频播放器
    // 记录当前播放歌曲的位置
    public int mCurrentPosition;
    //记录播放模式
    private int mPattern = 0;//0：列表循环 1：单曲循环 2：单曲播放
    private static final int INTERNAL_TIME = 1000;// 音乐进度间隔时间
    private ArrayList<BenDiYuePuBean> strings;
    private Context mContext;
    private TuPianYuePuAdapter tuPianYuePuAdapter;
    private int mPosition;
    private boolean classify = false;
    private MusicAdapter musicAdapter;

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
                timeSeekBar.setProgress(progress);
                tvPlayTime.setText(parseTime(progress));
                // 继续定时发送数据
                updateProgress();
            }
            return true;
        }
    });
    private int yindiao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ben_di_yin_yue);
        mContext = this;
        StatusBarUtil.transparencyBar(this);
        ButterKnife.bind(this);
        StatusBarUtil.StatusBarLightMode(this);
        rxPermissions = new RxPermissions(this);//使用前先实例化
        timeSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);//滑动条监听
        setSearchView();//设置搜索本地音乐的监听
        //初始化音乐左边title
        initRecTuPianYuePu();
        //初始化音乐右边歌曲
        initRecImageYuePu();
    }

    private void initStyle() {
        //toolbar背景变透明
        toolbar.setBackgroundColor(getResources().getColor(R.color.half_transparent));
        //文字变白色
        tvTitle.setTextColor(getResources().getColor(R.color.white));
        tvClearList.setTextColor(getResources().getColor(R.color.white));
        StatusBarUtil.transparencyBar(this);
    }

    @OnClick({R.id.tv_clear_list, R.id.btn_previous, R.id.btn_play_or_pause, R.id.btn_next, R.id.tv_beisu, R.id.tv_yindiao, R.id.tv_bofangmoshi, R.id.tv_search_wodeyinyue, R.id.yinyue_tv_xinzeng, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_clear_list://清空数据
                mList.clear();
                musicAdapter.notifyDataSetChanged();
                SPUtils.putString(Constant.MUSIC_DATA_FIRST, "yes", this);
                toolbar.setBackgroundColor(getResources().getColor(R.color.white));
                StatusBarUtil.StatusBarLightMode(this);
                tvTitle.setTextColor(getResources().getColor(R.color.black));
                tvClearList.setTextColor(getResources().getColor(R.color.black));
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnCompletionListener(this);//监听音乐播放完毕事件，自动下一曲
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.reset();
                }
                break;
            case R.id.btn_previous://上一曲
                changeMusic(--mCurrentPosition);//当前歌曲位置减1
                break;
            case R.id.btn_play_or_pause://播放或者暂停
                // 首次点击播放按钮，默认播放第0首，下标从0开始
                if (mediaPlayer == null) {
                    changeMusic(0);
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
                        playStateImg.setBackground(getResources().getDrawable(R.mipmap.list_play_state));
                    } else {
                        mediaPlayer.start();
                        btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
                        playStateImg.setBackground(getResources().getDrawable(R.mipmap.list_pause_state));
                    }
                }
                break;
            case R.id.btn_next://下一曲
                changeMusic(++mCurrentPosition);//当前歌曲位置加1
                break;
            case R.id.tv_beisu://播放倍速
                showSpeedPop();
                break;
            case R.id.tv_yindiao://音调
                showYinDiao();
                break;
            case R.id.tv_bofangmoshi://播放模式
                showBoFangMoShi();
                break;
            case R.id.tv_search_wodeyinyue://搜索
                Intent intent = new Intent(mContext, SearchMusicActivity.class);
                startActivity(intent);
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
                BenDiYinYueActivity.this.finish();
                break;
        }
    }

    //播放模式
    private void showBoFangMoShi() {
        if (mTvBoFangMoShi.getText().equals("列表循环")) {
            mPattern = 1;
            mTvBoFangMoShi.setText("单曲循环");
        } else if (mTvBoFangMoShi.getText().equals("单曲循环")) {
            mPattern = 2;
            mTvBoFangMoShi.setText("单曲播放");
        } else if (mTvBoFangMoShi.getText().equals("单曲播放")) {
            mPattern = 0;
            mTvBoFangMoShi.setText("列表循环");
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
                            Log.e("Exception", "setPlaySpeed: ", e);
                        }
                    }
                }
            });
        }
        speedDialog.show();
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
            mCurrentPosition = position = mList.size() - 1;
            Log.e("BenDiYinYueActivity", "mList.size:" + mList.size());
        } else if (position > mList.size() - 1) {//如果当前播放的是最后一首歌，则把下标改成0
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
            Log.d("Music", mList.get(position).getPath());
            mediaPlayer.setDataSource(mList.get(position).getPath());
            tvPlaySongInfo.setText("歌名： " + mList.get(position).getName());
//            Glide.with(this).load(mList.get(position).album_art).into(songImage);
            tvPlaySongInfo.setSelected(true);//跑马灯效果
            playStateLay.setVisibility(View.VISIBLE);

            // 开始播放前的准备工作，加载多媒体资源，获取相关信息
            mediaPlayer.prepare();
            // 开始播放
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 切歌时重置进度条并展示歌曲时长
        timeSeekBar.setProgress(0);
        timeSeekBar.setMax(mediaPlayer.getDuration());
        tvTotalTime.setText(parseTime(mediaPlayer.getDuration()));

        updateProgress();
        if (mediaPlayer.isPlaying()) {
            btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_play));
            playStateImg.setBackground(getResources().getDrawable(R.mipmap.list_pause_state));
        } else {
            btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
            playStateImg.setBackground(getResources().getDrawable(R.mipmap.list_play_state));
        }
    }

    private void updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        Message msg = Message.obtain();// 获取一个现成的消息
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
        int progress = mediaPlayer.getCurrentPosition();
        msg.arg1 = progress;
        mHandler.sendMessageDelayed(msg, INTERNAL_TIME);
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

    //播放完成之后自动下一曲
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mPattern == 1) {//单曲循环
            changeMusic(mCurrentPosition);
        } else if (mPattern == 2) {//单曲播放
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.stop();
                mediaPlayer = null;
                timeSeekBar.setProgress(0);
                timeSeekBar.setMax(0);
                tvTotalTime.setText(parseTime(0));
                tvPlayTime.setText(parseTime(0));
                playStateLay.setVisibility(View.GONE);
            }
            btnPlayOrPause.setBackground(getResources().getDrawable(R.mipmap.icon_pause));
            playStateImg.setBackground(getResources().getDrawable(R.mipmap.list_play_state));
            return;
        } else {//列表循环
            changeMusic(++mCurrentPosition);
        }
    }

    private boolean setPlaySpeed(float speed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                PlaybackParams params = mediaPlayer.getPlaybackParams();
                params.setSpeed(speed);
                mediaPlayer.setPlaybackParams(params);
                return true;
            } catch (Exception e) {
                Log.e("Exception", "setPlaySpeed: ", e);
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            Message message = new Message();
            mHandler.handleMessage(message);
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void setSearchView() {
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
//                mAdapter.getFilter().filter(newText);
                return false;
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
            public void onItemClick(int position) {
                mCurrentPosition = position;
                changeMusic(mCurrentPosition);
            }
        });
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
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (int i = 0; i < files2.size(); i++) {
            String path1 = files2.get(i).getPath();
            mmr.setDataSource(path1);
            String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String size = FileUtils.getSize(files2.get(i));
            long time1 = Long.parseLong(time);
            MusicBean musicBean = new MusicBean(name, time1, size,path1);
            musicBeans.add(musicBean);
        }
        return musicBeans;
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
                tuPianYuePuAdapter.notifyDataSetChanged();
            }
        });
    }
}