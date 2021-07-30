package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music.R;

public class CaoZuoShuoMingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shi_yong_shuo_ming);
        initView();
    }

    private void initView() {
        ImageView mIvBack = findViewById(R.id.iv_back);
        TextView mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText("操作说明");
        mIvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                CaoZuoShuoMingActivity.this.finish();
                break;
        }
    }
}