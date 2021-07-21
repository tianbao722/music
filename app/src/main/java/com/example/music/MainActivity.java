package com.example.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.music.ui.activity.zhujiemian.BenDiQuPuActivity;
import com.example.music.ui.activity.zhujiemian.BenDiYinYueActivity;
import com.example.music.ui.activity.zhujiemian.DaoRuQuPuActivity;
import com.example.music.ui.activity.zhujiemian.DownloadTheSongActivity;
import com.example.music.ui.activity.zhujiemian.DongTaiPuActivity;
import com.example.music.ui.activity.zhujiemian.LianXiGuJiActivity;
import com.example.music.ui.activity.zhujiemian.MianFeiJiaoXueActivity;
import com.example.music.ui.activity.zhujiemian.ShiYongShuoMingActivity;
import com.example.music.ui.activity.zhujiemian.XiaZaiYinYueActivity;
import com.example.music.utils.StatusBarUtil;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mTvXiaZai;
    private LinearLayout mTvBenDiYinYue;
    private LinearLayout mLLBenDiQuPu;
    private LinearLayout mLLXiaZaiQuPu;
    private LinearLayout mTvShiYongShuoMing;
    private LinearLayout mLLDaoRuYuePu;
    private LinearLayout mLLWenJianGuanLi;
    private LinearLayout mLLJiePaiQi;
    private LinearLayout mLLZengZhiFuWu;
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
        mLLZengZhiFuWu = findViewById(R.id.ll_zengzhifuwu);
        mTvXiaZai.setOnClickListener(this);
        mTvBenDiYinYue.setOnClickListener(this);
        mLLBenDiQuPu.setOnClickListener(this);
        mLLZengZhiFuWu.setOnClickListener(this);
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
                Manifest.permission.INTERNET,
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
                String aPackage = getPackage();
                openAppWithPackageName(aPackage);
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
                Intent intent8 = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent8);
//                Intent intent8 = new Intent(MainActivity.this, XiTongSheZhiActivity.class);
//                startActivity(intent8);
                break;
            case R.id.tv_caozuoshuoming://系统设置
                Intent intent10 = new Intent(MainActivity.this, ShiYongShuoMingActivity.class);
                startActivity(intent10);
                break;
            case R.id.ll_zengzhifuwu://增值服务
                Intent intent11 = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                startActivity(intent11);
//                Intent intent11 = new Intent(MainActivity.this, ZengZhiFuWuActivity.class);
//                startActivity(intent11);
                break;
        }
    }

    public String getPackage() {
        String BaoMing = null;
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            CharSequence charSequence = info.activityInfo.loadLabel(packageManager);
            String name1 = String.valueOf(charSequence);
            if (name1.contains(Constants.BaoMing) || name1.contains(Constants.BaoMing1)) {
                BaoMing = info.activityInfo.applicationInfo.packageName;
                break;
            }
            Log.e("包名", info.activityInfo.loadLabel(packageManager) + " 包名 "
                    + info.activityInfo.applicationInfo.packageName + " 类名 " + info.activityInfo.name);
        }
        return BaoMing;
    }

    private void openAppWithPackageName(String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        if (!resolveinfoList.iterator().hasNext()) {
            return;
        }
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//重点是加这个

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            startActivity(intent);
        }
    }
}