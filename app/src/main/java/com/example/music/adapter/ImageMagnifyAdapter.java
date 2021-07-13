package com.example.music.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageMagnifyAdapter extends PagerAdapter {
    private Context context;
    private List<String> data;
    private onCallBack callBack;

    public ImageMagnifyAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    public void setCallBack(onCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);
        Glide.with(context).load(data.get(position)).into(photoView);
        container.addView(photoView);

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (callBack != null) {
                    callBack.onItemClick();
                }
            }

            @Override
            public void onOutsidePhotoTap() {
                if (callBack != null) {
                    callBack.onItemClick();
                }
            }
        });

        return photoView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public interface onCallBack {
        void onItemClick();
    }
}
