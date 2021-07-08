package com.example.music.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music.R;
import com.example.music.adapter.DaoRuQuPuAdaper;
import com.example.music.adapter.FlowLayoutManager;
import com.example.music.adapter.ImageDaoRuQuPuAdapter;
import com.example.music.adapter.SpaceItemDecoration;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageDaoRuQuPuBean;
import com.example.music.utils.SPBeanUtile;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.util.ArrayList;

public class DaoRuQuPuActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecDaoRuQuPu;
    private RecyclerView mRecDaoRuQuPuImg;
    private EditText mEdDaoruqupu;
    private TextView mTvEnterDaoRuQuPu;
    private ImageView mivBack;
    private ArrayList<BenDiYuePuBean> list;
    private Context mContext;
    private DaoRuQuPuAdaper daoRuQuPuAdaper;
    private boolean classify = false;
    private ArrayList<ImageDaoRuQuPuBean> imagelist;
    private ImageDaoRuQuPuAdapter imageDaoRuQuPuAdapter;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dao_ru_qu_pu);
        this.mContext = this;
        initView();
        initListener();
    }

    private void initView() {
        mRecDaoRuQuPu = findViewById(R.id.rec_daoruqupu);
        mEdDaoruqupu = findViewById(R.id.ed_daoruqupu);
        mivBack = findViewById(R.id.iv_back);
        mRecDaoRuQuPuImg = findViewById(R.id.rec_daoruqupu_img);
        mTvEnterDaoRuQuPu = findViewById(R.id.tv_enter_daoruyuepu);
        mivBack.setOnClickListener(this);
        list = SPBeanUtile.getSPList();
        if (list == null) {
            list = new ArrayList<BenDiYuePuBean>();
        }
        daoRuQuPuAdaper = new DaoRuQuPuAdaper(mContext, list);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mRecDaoRuQuPu.addItemDecoration(new SpaceItemDecoration(dp2px(5)));
        mRecDaoRuQuPu.setLayoutManager(flowLayoutManager);
        mRecDaoRuQuPu.setAdapter(daoRuQuPuAdaper);

        imagelist = new ArrayList<>();
        imageDaoRuQuPuAdapter = new ImageDaoRuQuPuAdapter(imagelist, mContext);
        mRecDaoRuQuPuImg.addItemDecoration(new SpaceItemDecoration(dp2px(10)));
        mRecDaoRuQuPuImg.setLayoutManager(new GridLayoutManager(mContext, 4));
        mRecDaoRuQuPuImg.setAdapter(imageDaoRuQuPuAdapter);
    }

    private void initListener() {
        //选择title监听
        daoRuQuPuAdaper.setOnItemClickListener(new DaoRuQuPuAdaper.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < list.size(); i++) {
                    BenDiYuePuBean benDiYuePuBean = list.get(i);
                    benDiYuePuBean.setSelected(false);
                    list.set(i, benDiYuePuBean);
                }
                BenDiYuePuBean benDiYuePuBean = list.get(position);
                benDiYuePuBean.setSelected(true);
                list.set(position, benDiYuePuBean);
                daoRuQuPuAdaper.notifyDataSetChanged();
            }
        });
        //添加title监听
        daoRuQuPuAdaper.setOnEndItemClickListener(new DaoRuQuPuAdaper.onEndItemClickListener() {
            @Override
            public void onEndItemClick() {
                showAlertDerlog();
            }
        });
        //图片选中删除监听
        imageDaoRuQuPuAdapter.setOnItemClickListener(new ImageDaoRuQuPuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        //图片添加监听
        imageDaoRuQuPuAdapter.setOnEndItemClickListener(new ImageDaoRuQuPuAdapter.onEndItemClickListener() {
            @Override
            public void onEndItemClick() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                alertDialog = builder.create();
                alertDialog.show();
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_xiangce_xiangji, null);
                alertDialog.setContentView(inflate);
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                alertDialog.setCanceledOnTouchOutside(true);
                TextView mTvXiangJi = inflate.findViewById(R.id.tv_xiangji);
                TextView mTvXiangCe = inflate.findViewById(R.id.tv_xiangce);
                //相机的点击监听
                mTvXiangJi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//用来打开相机的Intent
                        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
                            startActivityForResult(takePhotoIntent, 1);//启动相机
                        }
                    }
                });
                //相册的点击监听
                mTvXiangCe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_album = new Intent(Intent.ACTION_PICK);
                        intent_album.setType("image/*");
                        startActivityForResult(intent_album, 2);
                    }
                });
            }
        });
    }

    private void showAlertDerlog() {
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
                    if (list.size() != 0) {
                        for (int i = 0; i < list.size(); i++) {
                            String title = list.get(i).getTitle();
                            if (title.equals(text)) {
                                classify = false;
                                break;
                            } else {
                                classify = true;
                            }
                        }
                        if (classify) {
                            for (int i = 0; i < list.size(); i++) {
                                BenDiYuePuBean benDiYuePuBean = list.get(i);
                                benDiYuePuBean.setSelected(false);
                                list.set(i, benDiYuePuBean);
                            }
                            BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                            list.add(0, benDiYuePuBean);
                            daoRuQuPuAdaper.notifyDataSetChanged();
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                            SPBeanUtile.setSPList(list);
                        } else {
                            Toast.makeText(mContext, "分类已经存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                        list.add(0, benDiYuePuBean);
                        daoRuQuPuAdaper.notifyDataSetChanged();
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                        }
                        SPBeanUtile.setSPList(list);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                DaoRuQuPuActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    if (imagelist != null && imageDaoRuQuPuAdapter != null) {
                        imagelist.add(new ImageDaoRuQuPuBean(null, photo, null));
                        imageDaoRuQuPuAdapter.notifyDataSetChanged();
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                        }
                    }
//                mIvImage.setImageBitmap(photo);
                }
                break;
            case 2:
                if (data != null) {
                    Uri uri = data.getData();
                    if (imagelist != null && imageDaoRuQuPuAdapter != null) {
                        imagelist.add(new ImageDaoRuQuPuBean(null, null, uri));
                        imageDaoRuQuPuAdapter.notifyDataSetChanged();
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                        }
                    }
//                mIvImage.setImageURI(uri);
                }
                break;
        }
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}