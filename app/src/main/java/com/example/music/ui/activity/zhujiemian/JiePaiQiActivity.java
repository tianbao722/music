package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music.R;
import com.example.music.utils.SoundPoolUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JiePaiQiActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SPEED_PRE = "speedPreferences";
    private static final String SPEED = "Speed";
    private static final String MOLECULE = "Molecule";
    private static final String DENO = "Deno";
    private static final int PLAY = 0x001;
    private static final int MAXSPEED = 280;
    private static final int MINSPEED = 40;
    private static final int MINDENO = 4;
    private static final int MAXDENO = 32;
    private TextView tvSpeed, tvRhythm;
    private int speed, molecule, deno;
    private boolean isPlaying = false;
    private ImageView mIvBack;
    private ImageButton btnAdd, btnIncrease, btnPlay, btnMoleculeAdd, btnMoleculeIncr, btnDenoAdd, btnDenoIncr;
    private SharedPreferences mSharedPreferences;
    private SoundPoolUtil mSoundPoolUtil;
    private ExecutorService mExecutors;

    Runnable playTask = new Runnable() {
        @Override
        public void run() {
            playSound();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mian_fei_jiao_xue);
        mSharedPreferences = getSharedPreferences(SPEED_PRE, Context.MODE_PRIVATE);
        initData();
        initView();
        mSoundPoolUtil.playSound(0);
    }

    private void initView() {
        tvSpeed = findViewById(R.id.tv_speed);
        btnAdd = findViewById(R.id.btn_add);
        mIvBack = findViewById(R.id.iv_back);
        btnIncrease = findViewById(R.id.btn_increase);
        btnPlay = findViewById(R.id.btn_play);
        btnDenoAdd = findViewById(R.id.btn_add_meno);
        btnDenoIncr = findViewById(R.id.btn_increase_deno);
        btnMoleculeAdd = findViewById(R.id.btn_add_molecule);
        btnMoleculeIncr = findViewById(R.id.btn_increase_molecule);
        tvRhythm = findViewById(R.id.tv_rhythm);
        btnAdd.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        btnIncrease.setOnClickListener(this);
        btnDenoAdd.setOnClickListener(this);
        btnDenoIncr.setOnClickListener(this);
        btnMoleculeAdd.setOnClickListener(this);
        btnMoleculeIncr.setOnClickListener(this);
        tvSpeed.setText(speed + "");
        String label = molecule + " / " + deno;
        tvRhythm.setText(label);
    }

    private void initData() {
        mSoundPoolUtil = SoundPoolUtil.getInstance(this);
        mSoundPoolUtil.init(this);
        mExecutors = Executors.newSingleThreadExecutor();
        speed = mSharedPreferences.getInt(SPEED, 60);
        molecule = mSharedPreferences.getInt(MOLECULE, 1);
        deno = mSharedPreferences.getInt(DENO, 4);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        switch (v.getId()) {
            case R.id.btn_add: {
                if (speed >= MAXSPEED) {
                    speed = MINSPEED;
                } else {
                    speed++;
                }
                editor.putInt(SPEED, speed);
                editor.apply();
                tvSpeed.setText(speed + "");
                break;
            }
            case R.id.btn_increase: {
                if (speed <= MINSPEED) {
                    speed = MAXSPEED;
                } else {
                    speed--;
                }
                editor.putInt(SPEED, speed);
                editor.apply();
                tvSpeed.setText(speed + "");
                break;
            }
            case R.id.btn_play: {
                isPlaying = !isPlaying;
                if (isPlaying) {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                } else {
                    btnPlay.setImageResource(R.drawable.ic_play);
                }
                mExecutors.execute(playTask);
                break;
            }
            case R.id.btn_add_meno: {
                if (deno >= MAXDENO) {
                    deno = MINDENO;
                } else {
                    deno *= 2;
                }
                String label = molecule + " / " + deno;
                tvRhythm.setText(label);
                editor.putInt(DENO, deno);
                editor.apply();
                break;

            }
            case R.id.btn_add_molecule: {
                if (molecule == 4) {
                    molecule += 2;
                } else if (molecule >= 6) {
                    molecule = 1;
                } else {
                    molecule++;
                }
                String label = molecule + " / " + deno;
                editor.putInt(MOLECULE, molecule);
                editor.apply();
                tvRhythm.setText(label);
                break;
            }
            case R.id.btn_increase_deno: {
                if (deno <= MINDENO) {
                    deno = MAXDENO;
                } else {
                    deno /= 2;
                }
                String label = molecule + " / " + deno;
                editor.putInt(DENO, deno);
                editor.apply();
                tvRhythm.setText(label);
                break;
            }
            case R.id.btn_increase_molecule: {
                if (molecule == 6) {
                    molecule -= 2;
                } else if (molecule <= 1) {
                    molecule = 6;
                } else {
                    molecule--;
                }
                String label = molecule + " / " + deno;
                editor.putInt(MOLECULE, molecule);
                editor.apply();
                tvRhythm.setText(label);

            }
            break;
            case R.id.iv_back:
                JiePaiQiActivity.this.finish();
                break;
            default:
        }
    }

    private void playSound() {
        int index = 0;
        do {
            if (index == 0) {
                mSoundPoolUtil.playSound(0);
            } else if ((molecule == 4 && index == 2) || (molecule == 6 && index == 3)) {
                mSoundPoolUtil.playSound(2);
            } else {
                mSoundPoolUtil.playSound(1);
            }
            if (++index >= molecule) {
                index = 0;
            }
            try {
                Thread.sleep(60 * 1000 / speed / (deno / 4));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (isPlaying);
    }
}