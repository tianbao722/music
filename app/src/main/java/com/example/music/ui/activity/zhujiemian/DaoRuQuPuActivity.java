package com.example.music.ui.activity.zhujiemian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private int position;//当前选中的文件夹的下标
    private boolean isDown = true;
    private ArrayList<ImageYuePuImageBean> Titlelist;
    private boolean isNameEqual = false;
    private Intent intent;
    private String type;
    private String text;

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
        if (TextUtils.isEmpty(type)) {
            type = "2";
        }
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
        mRecDaoRuQuPu = findViewById(R.id.rec_daoruqupu);
        mEdDaoruqupu = findViewById(R.id.ed_daoruqupu);
        mivBack = findViewById(R.id.iv_back);
        mRecDaoRuQuPuImg = findViewById(R.id.rec_daoruqupu_img);
        mTvEnterDaoRuQuPu = findViewById(R.id.tv_enter_daoruyuepu);
        mivBack.setOnClickListener(this);
        mTvEnterDaoRuQuPu.setOnClickListener(this);
        list = SPBeanUtile.getTuPianQuPuFileList();
        if (list == null) {
            list = new ArrayList<BenDiYuePuBean>();
        }
        daoRuQuPuAdaper = new DaoRuQuPuAdaper(mContext, list);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mRecDaoRuQuPu.addItemDecoration(new SpaceItemDecoration(dp2px(5)));
        mRecDaoRuQuPu.setLayoutManager(flowLayoutManager);
        mRecDaoRuQuPu.setAdapter(daoRuQuPuAdaper);
        imageDaoRuQuPuAdapter = new ImageDaoRuQuPuAdapter(imagelist, mContext);
//        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
//        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 30);//右间距
//        mRecDaoRuQuPuImg.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        mRecDaoRuQuPuImg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRecDaoRuQuPuImg.setAdapter(imageDaoRuQuPuAdapter);
//        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new MyItemTouchHelper());
//        mItemTouchHelper.attachToRecyclerView(mRecDaoRuQuPuImg);
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
                        List<File> files1 = FileUtils.listFilesInDir(files.get(j).getPath());
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
                                daoRuQuPuAdaper.setData(list);
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
                            daoRuQuPuAdaper.setData(list);
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

    //加载中loading动画
    private AlertDialog alertDialogLoading;

    private void showAleartDialogLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        alertDialogLoading = builder.create();
        alertDialogLoading.show();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_loading, null);
        alertDialogLoading.setContentView(inflate);
        alertDialogLoading.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialogLoading.setCanceledOnTouchOutside(false);
        ImageView mIvLoading = inflate.findViewById(R.id.iv_loading);
        Glide.with(mContext).load(R.mipmap.loading).into(mIvLoading);
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
                text = mEdDaoruqupu.getText().toString();
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
                        isDown = false;
                        MyAsyncTask myAsyncTask = new MyAsyncTask();
                        myAsyncTask.execute(text);
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
                        String path = getPath(mContext, uri);
                        File file = new File(path);
                        if (file.exists()) {
                            int exifOrientation = getExifOrientation(path);
                            Bitmap bitmap = null;
                            if (exifOrientation == 0) {
                                bitmap = openImage(path);
                            } else {
                                bitmap = rotateBitmap(bitmap, 270);
                            }
                            if (bitmap != null) {
                                imagelist.add(new ImageDaoRuQuPuBean(null, bitmap, null));
                            }
                            imageDaoRuQuPuAdapter.notifyDataSetChanged();
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(mContext, "文件不存在", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                break;
        }
    }

    //获取图片的选择度数
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            Log.d("异常", "获取图片的旋转度数" + ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
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
            } else if (uri == null && bitmap == null && !TextUtils.isEmpty(url)) {
                b = DownLoadUtile.newThread(url, MyApplication.getTuPianYuePuFile().getPath() + "/" + list.get(position).getTitle() + "/" + text, i);
            }
        }
        return b;
    }

    /**
     * 旋转bitmap
     *
     * @param bitmap
     * @param degress
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    /**
     * uri转path
     *
     * @param context
     * @param uri
     * @return
     */
    private String getPath(Context context, Uri uri) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return path;
    }

    /**
     * 将本地图片转成Bitmap
     *
     * @param path 已有图片的路径
     * @return
     */
    public Bitmap openImage(String path) {
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程
            if (alertDialogLoading != null && !alertDialogLoading.isShowing()) {
                alertDialogLoading.show();
            } else {
                showAleartDialogLoading();
            }
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //这是在后台子线程中执行的
            boolean b = addFile(params[0]);
            return b;
        }

        @Override
        protected void onCancelled() {
            //当任务被取消时回调
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //更新进度
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            //当任务执行完成是调用,在UI线程
            if (b) {
                PreferenceUtil.getInstance().remove(Constants.webImage);
                Toast.makeText(mContext, "导入成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, BenDiQuPuActivity.class);
                startActivity(intent);
                setResult(3, intent);
                PreferenceUtil.getInstance().remove(Constants.webImage);
                setResult(3, intent);
                DaoRuQuPuActivity.this.finish();
            } else {
                String path = MyApplication.getTuPianYuePuFile().getPath() + "/" + list.get(position).getTitle() + "/" + text;
                FileUtils.delete(path);
                Toast.makeText(mContext, "导入失败,请检查网络", Toast.LENGTH_SHORT).show();
            }
            if (alertDialogLoading != null && alertDialogLoading.isShowing()) {
                alertDialogLoading.dismiss();
            }
            isDown = true;
        }
    }

}