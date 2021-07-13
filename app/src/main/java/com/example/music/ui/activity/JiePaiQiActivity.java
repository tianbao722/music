package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.music.R;
import com.example.music.utils.StatusBarUtil;

public class JiePaiQiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jie_pai_qi);
        StatusBarUtil.transparencyBar(this);

    }
}