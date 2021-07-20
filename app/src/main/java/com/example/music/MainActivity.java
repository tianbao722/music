package com.example.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.music.ui.activity.zhujiemian.BenDiQuPuActivity;
import com.example.music.ui.activity.zhujiemian.BenDiYinYueActivity;
import com.example.music.ui.activity.zhujiemian.DaoRuQuPuActivity;
import com.example.music.ui.activity.zhujiemian.DownloadTheSongActivity;
import com.example.music.ui.activity.zhujiemian.DongTaiPuActivity;
import com.example.music.ui.activity.zhujiemian.LianXiGuJiActivity;
import com.example.music.ui.activity.zhujiemian.MianFeiJiaoXueActivity;
import com.example.music.ui.activity.zhujiemian.ShiYongShuoMingActivity;
import com.example.music.ui.activity.zhujiemian.XiTongSheZhiActivity;
import com.example.music.ui.activity.zhujiemian.XiaZaiYinYueActivity;
import com.example.music.utils.StatusBarUtil;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mTvXiaZai;
    private LinearLayout mTvBenDiYinYue;
    private LinearLayout mLLBenDiQuPu;
    private LinearLayout mLLXiaZaiQuPu;
    private LinearLayout mTvShiYongShuoMing;
    private LinearLayout mLLDaoRuYuePu;
    private LinearLayout mLLWenJianGuanLi;
    private LinearLayout mLLJiePaiQi;
    private LinearLayout mLLMianFeiJiaoXue;
    private LinearLayout mLLLianXiGuJi;
    private LinearLayout mTvSystemSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.transparencyBar(this);
        initView();
    }

    private void initView() {
        mTvXiaZai = findViewById(R.id.ll_xiazaiyinyue);
        mTvBenDiYinYue = findViewById(R.id.ll_bendiyinyue);
        mLLBenDiQuPu = findViewById(R.id.ll_bendiqupu);
        mLLXiaZaiQuPu = findViewById(R.id.ll_xiazaiqupu);
        mLLDaoRuYuePu = findViewById(R.id.ll_daoruyuepu);
        mLLWenJianGuanLi = findViewById(R.id.ll_wenjianguanli);
        mTvShiYongShuoMing = findViewById(R.id.tv_caozuoshuoming);
        mLLJiePaiQi = findViewById(R.id.ll_dongtaipu);
        mLLMianFeiJiaoXue = findViewById(R.id.ll_mianfeijiaoxue);
        mLLLianXiGuJi = findViewById(R.id.ll_lianxiguji);
        mTvSystemSetting = findViewById(R.id.tv_systemsetting);
        mTvXiaZai.setOnClickListener(this);
        mTvBenDiYinYue.setOnClickListener(this);
        mLLBenDiQuPu.setOnClickListener(this);
        mLLXiaZaiQuPu.setOnClickListener(this);
        mLLDaoRuYuePu.setOnClickListener(this);
        mLLMianFeiJiaoXue.setOnClickListener(this);
        mLLWenJianGuanLi.setOnClickListener(this);
        mLLJiePaiQi.setOnClickListener(this);
        mLLLianXiGuJi.setOnClickListener(this);
        mTvSystemSetting.setOnClickListener(this);
        String[] strings = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,};
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                Log.i("TAG", "permissionGranted: " + "权限请求成功");
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                Log.i("TAG", "permissionDenied: " + "权限请求失败");
            }
        }, strings);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_xiazaiqupu://下载曲谱
                Intent intent5 = new Intent(MainActivity.this, DownloadTheSongActivity.class);
                startActivity(intent5);
                break;
            case R.id.ll_bendiyinyue://本地音乐
                Intent intent1 = new Intent(MainActivity.this, BenDiYinYueActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_bendiqupu://本地曲谱
                Intent intent2 = new Intent(MainActivity.this, BenDiQuPuActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_xiazaiyinyue://下载音乐
                Intent intent3 = new Intent(MainActivity.this, XiaZaiYinYueActivity.class);
                startActivity(intent3);
                break;
            case R.id.ll_daoruyuepu://导入曲谱
                Intent intent4 = new Intent(MainActivity.this, DaoRuQuPuActivity.class);
                startActivity(intent4);
                break;
            case R.id.ll_mianfeijiaoxue://免费教学
                Intent intent9 = new Intent(MainActivity.this, MianFeiJiaoXueActivity.class);
                startActivity(intent9);
                break;
            case R.id.ll_wenjianguanli://文件管理
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //系统调用Action属性
                intent.setType("*/*");
                //设置文件类型
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // 添加Category属性
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "没有正确打开文件管理器", Toast.LENGTH_SHORT).show();
                }
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setType("application/pdf");
//                startActivityForResult(intent, 1);
                break;
            case R.id.ll_dongtaipu://节拍器
                Intent intent6 = new Intent(MainActivity.this, DongTaiPuActivity.class);
                startActivity(intent6);
                break;
            case R.id.ll_lianxiguji://练习鼓机
                Intent intent7 = new Intent(MainActivity.this, LianXiGuJiActivity.class);
                startActivity(intent7);
                break;
            case R.id.tv_systemsetting://系统设置
                Intent intent8 = new Intent(MainActivity.this, XiTongSheZhiActivity.class);
                startActivity(intent8);
                break;
            case R.id.tv_caozuoshuoming://系统设置
                Intent intent10 = new Intent(MainActivity.this, ShiYongShuoMingActivity.class);
                startActivity(intent10);
                break;
        }
    }
}