package com.example.music.ui.activity.zhujiemian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.example.music.Constants;
import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.adapter.DaoRuQuPuAdaper;
import com.example.music.adapter.FlowLayoutManager;
import com.example.music.adapter.ImageDaoRuQuPuAdapter;
import com.example.music.adapter.RecyclerViewSpacesItemDecoration;
import com.example.music.adapter.SpaceItemDecoration;
import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.ImageDaoRuQuPuBean;
import com.example.music.bean.ImageYuePuImageBean;
import com.example.music.bean.UrlImageListBean;
import com.example.music.utils.DownLoadUtile;
import com.example.music.utils.PreferenceUtil;
import com.example.music.utils.SPBeanUtile;
import com.example.music.utils.StatusBarUtil;
import com.example.music.zview.MaxHeightRecyclerView;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DaoRuQuPuActivity extends AppCompatActivity implements View.OnClickListener {
    private MaxHeightRecyclerView mRecDaoRuQuPu;
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
    private ImageView mIvLoading;
    private int position;//当前选中的文件夹的下标
    private boolean isDown = true;
    private ArrayList<ImageYuePuImageBean> Titlelist;
    private boolean isNameEqual = false;
    private int time = 3000;
    private Intent intent;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dao_ru_qu_pu);
        StatusBarUtil.transparencyBar(this);
        this.mContext = this;
        initView();
        initListener();
    }

    private void initView() {
        imagelist = new ArrayList<>();
        Titlelist = new ArrayList<>();
        intent = getIntent();
        type = intent.getStringExtra("type");
        String json = PreferenceUtil.getInstance().getString(Constants.webImage, null);
        if (!TextUtils.isEmpty(json)) {
            UrlImageListBean urlImageListBean = new Gson().fromJson(json, UrlImageListBean.class);
            ArrayList<String> list = urlImageListBean.getList();
            if (list != null && list.size() > 0) {
                for (String imageUrl : list) {
                    imagelist.add(new ImageDaoRuQuPuBean(imageUrl, null, null));
                }
            }
        }
        if (imagelist.size() <= 2) {
            time = 3000;
        } else if (imagelist.size() > 2 && imagelist.size() <= 5) {
            time = 5000;
        } else if (imagelist.size() >= 6) {
            time = 8000;
        } else {
            time = 2000;
        }
        mRecDaoRuQuPu = findViewById(R.id.rec_daoruqupu);
        mEdDaoruqupu = findViewById(R.id.ed_daoruqupu);
        mIvLoading = findViewById(R.id.iv_loading);
        mivBack = findViewById(R.id.iv_back);
        mRecDaoRuQuPuImg = findViewById(R.id.rec_daoruqupu_img);
        mTvEnterDaoRuQuPu = findViewById(R.id.tv_enter_daoruyuepu);
        mivBack.setOnClickListener(this);
        mTvEnterDaoRuQuPu.setOnClickListener(this);
        list = SPBeanUtile.getTuPianQuPuFileList();
        if (list == null) {
            list = new ArrayList<BenDiYuePuBean>();
        }
        Glide.with(mContext).load(R.mipmap.loading).into(mIvLoading);
        mIvLoading.setVisibility(View.GONE);
        daoRuQuPuAdaper = new DaoRuQuPuAdaper(mContext, list);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mRecDaoRuQuPu.addItemDecoration(new SpaceItemDecoration(dp2px(5)));
        mRecDaoRuQuPu.setLayoutManager(flowLayoutManager);
        mRecDaoRuQuPu.setAdapter(daoRuQuPuAdaper);
        imageDaoRuQuPuAdapter = new ImageDaoRuQuPuAdapter(imagelist, mContext);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 30);//右间距
        mRecDaoRuQuPuImg.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        mRecDaoRuQuPuImg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRecDaoRuQuPuImg.setAdapter(imageDaoRuQuPuAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new MyItemTouchHelper());
        mItemTouchHelper.attachToRecyclerView(mRecDaoRuQuPuImg);
    }

    private void getImageFileList() {
        ArrayList<BenDiYuePuBean> benDiYuePuBeans = SPBeanUtile.getTuPianQuPuFileList();
        Titlelist.clear();
        String path;
        path = MyApplication.getTuPianYuePuFile().getPath();
        if (benDiYuePuBeans != null) {
            for (int i = 0; i < benDiYuePuBeans.size(); i++) {
                String currentPath = path + "/" + benDiYuePuBeans.get(i).getTitle();
                List<File> files = FileUtils.listFilesInDir(currentPath);
                if (files != null && files.size() > 0) {
                    for (int j = 0; j < files.size(); j++) {
                        String name = files.get(j).getName();
                        List<File> files1 = FileUtils.listFilesInDir(files.get(i).getPath());
                        ImageYuePuImageBean imageYuePuImageBean = new ImageYuePuImageBean(name, files1, files1.size());
                        Titlelist.add(imageYuePuImageBean);
                    }
                }
            }
        }
    }

    private void initListener() {
        //选择title监听
        daoRuQuPuAdaper.setOnItemClickListener(new DaoRuQuPuAdaper.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DaoRuQuPuActivity.this.position = position;
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
                imagelist.remove(position);
                imageDaoRuQuPuAdapter.notifyDataSetChanged();
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
                TextView mTvUrlImage = inflate.findViewById(R.id.tv_urlimage);
                if (type.equals("1")) {
                    mTvUrlImage.setVisibility(View.GONE);
                } else {
                    mTvUrlImage.setVisibility(View.VISIBLE);
                }
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
                //继续选择曲谱点击监听
                mTvUrlImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(2, intent);
                        DaoRuQuPuActivity.this.finish();
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
                            boolean tuPiQuPuFile = SPBeanUtile.createTuPiQuPuFile(text);
                            if (tuPiQuPuFile) {
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
                                Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "分类已经存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        boolean tuPiQuPuFile = SPBeanUtile.createTuPiQuPuFile(text);
                        if (tuPiQuPuFile) {
                            BenDiYuePuBean benDiYuePuBean = new BenDiYuePuBean(text, true);
                            list.add(0, benDiYuePuBean);
                            daoRuQuPuAdaper.notifyDataSetChanged();
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                            Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                        }
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
                PreferenceUtil.getInstance().remove(Constants.webImage);
                setResult(3, intent);
                DaoRuQuPuActivity.this.finish();
                break;
            case R.id.tv_enter_daoruyuepu://确定
                getImageFileList();
                String text = mEdDaoruqupu.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    for (int i = 0; i < Titlelist.size(); i++) {
                        String name = Titlelist.get(i).getName();
                        if (name.equals(text)) {
                            isNameEqual = true;
                            break;
                        } else {
                            isNameEqual = false;
                        }
                    }
                    if (!isNameEqual) {
                        boolean b = addFile(text);
                        mIvLoading.setVisibility(View.VISIBLE);
                        isDown = false;
                        mTvEnterDaoRuQuPu.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                boolean b = addFile(text);
                                if (b) {
                                    PreferenceUtil.getInstance().remove(Constants.webImage);
                                    Toast.makeText(mContext, "导入成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext, BenDiQuPuActivity.class);
                                    startActivity(intent);
                                    setResult(3, intent);
                                } else {
                                    Toast.makeText(mContext, "导入失败,请检查网络", Toast.LENGTH_SHORT).show();
                                }
                                mIvLoading.setVisibility(View.GONE);
                                isDown = true;
                            }
                        }, time);
                    } else {
                        Toast.makeText(mContext, "该乐谱名称已存在，请更换乐谱名称", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "请输入乐谱名字", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isDown) {
            return super.dispatchTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PreferenceUtil.getInstance().remove(Constants.webImage);
        setResult(3, intent);
        return super.onKeyDown(keyCode, event);
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
                }
                break;
        }
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private ItemTouchHelper mItemTouchHelper;

    public class MyItemTouchHelper extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            //得到当拖拽的viewHolder的Position
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            if (toPosition == imagelist.size() || fromPosition == imagelist.size()) {
                imageDaoRuQuPuAdapter.notifyItemMoved(fromPosition, toPosition);
            } else {
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(imagelist, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(imagelist, i, i - 1);
                    }
                }
                imageDaoRuQuPuAdapter.notifyItemMoved(fromPosition, toPosition);
            }
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }


        /**
         * 长按选中Item的时候开始调用
         *
         * @param viewHolder
         * @param actionState
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 手指松开的时候还原
         *
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
//            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        }

        /**
         * 重写拖拽不可用
         *
         * @return
         */
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
    }

    public boolean addFile(String text) {
        boolean b = false;
        for (int i = 0; i < imagelist.size(); i++) {
            ImageDaoRuQuPuBean imageDaoRuQuPuBean = imagelist.get(i);
            String url = imageDaoRuQuPuBean.getUrl();
            Uri uri = imageDaoRuQuPuBean.getUri();
            Bitmap bitmap = imageDaoRuQuPuBean.getBitmap();
            if (TextUtils.isEmpty(url) && uri == null && bitmap != null) {//保存bitmap
                b = DownLoadUtile.SavaImage(bitmap, MyApplication.getTuPianYuePuFile().getPath() + "/" + list.get(position).getTitle() + "/" + text, i);
            } else if (TextUtils.isEmpty(url) && bitmap == null && uri != null) {//保存uri
                try {
                    Bitmap bitmap1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    b = DownLoadUtile.SavaImage(bitmap1, MyApplication.getTuPianYuePuFile().getPath() + "/" + list.get(position).getTitle() + "/" + text, i);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (uri == null && bitmap == null && !TextUtils.isEmpty(url)) {
                b = DownLoadUtile.newThread(url, MyApplication.getTuPianYuePuFile().getPath() + "/" + list.get(position).getTitle() + "/" + text, i);
            }
        }
        return b;
    }
}