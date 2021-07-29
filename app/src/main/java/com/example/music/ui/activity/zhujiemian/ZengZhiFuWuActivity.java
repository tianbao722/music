package com.example.music.ui.activity.zhujiemian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.music.Constants;
import com.example.music.R;
import com.example.music.adapter.ZengZhiFuWuAdapter;
import com.example.music.bean.ZengZhiFuWuBean;

import java.util.ArrayList;
import java.util.List;

public class ZengZhiFuWuActivity extends AppCompatActivity {

    private RecyclerView mRec;
    private Context mContext;
    private ArrayList<ZengZhiFuWuBean> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zeng_zhi_fu_wu);
        mContext = this;
        initView();
    }

    private void initView() {
        mRec = findViewById(R.id.rec_zengzhifuwu);
        mList = getPackage();
        mRec.setLayoutManager(new GridLayoutManager(mContext, 4));
        ZengZhiFuWuAdapter zengZhiFuWuAdapter = new ZengZhiFuWuAdapter(mContext, mList);
        mRec.setAdapter(zengZhiFuWuAdapter);
        zengZhiFuWuAdapter.setOnItemClickListener(new ZengZhiFuWuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openAppWithPackageName(mList.get(position).getPickName());
            }
        });
    }

    //获取文件管理或资源管理的包名
    public ArrayList<ZengZhiFuWuBean> getPackage() {
        ArrayList<ZengZhiFuWuBean> zengZhiFuWuBeans = new ArrayList<>();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            Drawable drawable = info.loadIcon(packageManager);
            CharSequence charSequence = info.activityInfo.loadLabel(packageManager);
            String name1 = String.valueOf(charSequence);
            String packageName = info.activityInfo.applicationInfo.packageName;
            zengZhiFuWuBeans.add(new ZengZhiFuWuBean(name1, packageName, drawable));
            Log.e("包名", info.activityInfo.loadLabel(packageManager) + " 包名 "
                    + info.activityInfo.applicationInfo.packageName + " 类名 " + info.activityInfo.name
                    + "图标" + info.activityInfo.applicationInfo.icon);
        }
        return zengZhiFuWuBeans;
    }

    //通过包名跳转到指定的应用
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