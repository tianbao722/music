package com.example.music.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music.R;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.utils.SPBeanUtile;

import java.util.ArrayList;

public class DaoRuQuPuActivity extends AppCompatActivity {
    private RecyclerView mRecDaoRuQuPu;
    private RecyclerView mRecDaoRuQuPuImg;
    private EditText mEdDaoruqupu;
    private TextView mTvEnterDaoRuQuPu;
    private ImageView mivBack;
    private ArrayList<BenDiYuePuBean> lsit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dao_ru_qu_pu);
        initView();
    }

    private void initView() {
        mRecDaoRuQuPu = findViewById(R.id.rec_daoruqupu);
        mEdDaoruqupu = findViewById(R.id.ed_daoruqupu);
        mivBack = findViewById(R.id.iv_back);
        mRecDaoRuQuPuImg = findViewById(R.id.rec_daoruqupu_img);
        mTvEnterDaoRuQuPu = findViewById(R.id.tv_enter_daoruyuepu);
        lsit = SPBeanUtile.getSPList();

    }
}