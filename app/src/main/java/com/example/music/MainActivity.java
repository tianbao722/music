package com.example.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.music.ui.activity.BenDiYinYueActivity;
import com.example.music.ui.activity.DownloadTheSongActivity;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvXiaZai;
    private TextView mTvBenDiYinYue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTvXiaZai = findViewById(R.id.tv_xiazai);
        mTvBenDiYinYue = findViewById(R.id.tv_bendiyinyue);
        mTvXiaZai.setOnClickListener(this);
        mTvBenDiYinYue.setOnClickListener(this);
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                Log.i("TAG", "permissionGranted: " + "权限请求成功");
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                Log.i("TAG", "permissionDenied: " + "权限请求失败");
            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_xiazai:
                Intent intent = new Intent(MainActivity.this, DownloadTheSongActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_bendiyinyue:
                Intent intent1 = new Intent(MainActivity.this, BenDiYinYueActivity.class);
                startActivity(intent1);
                break;
        }
    }
}