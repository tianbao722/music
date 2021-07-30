package com.example.music.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.example.music.R;

import java.util.HashMap;

/**
 * @author zhouxw
 */
public class SoundPoolUtil {
    @SuppressLint("StaticFieldLeak")
    private static SoundPoolUtil mInstance;
    private SoundPool mSoundPool;
    private AudioManager mAudioManager;
    private Context mContext;
    private float volume;
    HashMap<Integer, Integer> musicId = new HashMap<>();
    /**
     * Max sound stream
     */
    private static final int MAX_STREAMS = 5;
    //stream type
    private static final int streamType = AudioManager.STREAM_MUSIC;


    private SoundPoolUtil(Context context) {
        mContext = context;
    }

    public static SoundPoolUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SoundPoolUtil.class) {
                if (mInstance == null) {
                    mInstance = new SoundPoolUtil(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float currentVolumeIndex = mAudioManager.getStreamVolume(streamType);
        float maxVolumeIndex = mAudioManager.getStreamMaxVolume(streamType);
        this.volume = currentVolumeIndex / maxVolumeIndex;
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        this.mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(MAX_STREAMS).build();
    }

    public void playSound(final int resId) {
        final float lefVolume = volume;
        final float rightVolume = volume;
        switch (resId) {
            case 0:
                musicId.put(0, mSoundPool.load(mContext, R.raw.metronome1, 1));
                break;
            case 1:
                musicId.put(1, mSoundPool.load(mContext, R.raw.metronome2, 1));
                break;
            case 2:
                musicId.put(2, mSoundPool.load(mContext, R.raw.metronome3, 1));
                break;
            default:
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                Log.d("zhouxw", "onLoadComplete");
                mSoundPool.play(musicId.get(resId), lefVolume, rightVolume, 1, 0, 1f);
            }
        });
    }


}



