package com.example.music.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.bean.ImageDaoRuQuPuBean;

import java.util.ArrayList;

public class ImageDaoRuQuPuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ImageDaoRuQuPuBean> list;
    private Context mContext;
    private final static int ITEM_VIEWTYPE_ONE = 1;
    private final static int ITEM_VIEWTYPE_TWO = 2;
    private onItemClickListener onItemClickListener;
    private onEndItemClickListener onEndItemClickListener;

    public void setOnItemClickListener(ImageDaoRuQuPuAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnEndItemClickListener(ImageDaoRuQuPuAdapter.onEndItemClickListener onEndItemClickListener) {
        this.onEndItemClickListener = onEndItemClickListener;
    }

    public ImageDaoRuQuPuAdapter(ArrayList<ImageDaoRuQuPuBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_image_daoruqupu, parent, false);
            ViewHolder viewHolder = new ViewHolder(inflate);
            return viewHolder;
        } else {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_addimage_daoruqupu, parent, false);
            ViewHolder1 viewHolder1 = new ViewHolder1(inflate);
            return viewHolder1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list == null || list.size() <= 0) {
            return 2;
        } else {
            if (position == list.size()) {
                return 2;
            } else {
                return 1;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 1) {
            ViewHolder viewHolder = (ViewHolder) holder;
            if (TextUtils.isEmpty(list.get(position).getUrl()) && list.get(position).getBitmap() != null && list.get(position).getUri() == null) {
                viewHolder.mIvImage.setImageBitmap(list.get(position).getBitmap());
            } else if (!TextUtils.isEmpty(list.get(position).getUrl()) && list.get(position).getBitmap() == null && list.get(position).getUri() == null) {
                Glide.with(mContext).load(list.get(position).getUrl()).into(viewHolder.mIvImage);
            } else if (TextUtils.isEmpty(list.get(position).getUrl()) && list.get(position).getBitmap() == null && list.get(position).getUri() != null) {
                Glide.with(mContext).load(list.get(position).getUri()).into(viewHolder.mIvImage);
            }
            viewHolder.mIvCha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        } else {
            ViewHolder1 viewHolder1 = (ViewHolder1) holder;
            viewHolder1.mIvJia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEndItemClickListener.onEndItemClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list == null || list.size() <= 0) {
            return 1;
        } else {
            return list.size() + 1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvImage;
        private ImageView mIvCha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvImage = itemView.findViewById(R.id.iv_daoruqupu_image);
            mIvCha = itemView.findViewById(R.id.iv_cha);
        }
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        private ImageView mIvJia;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            mIvJia = itemView.findViewById(R.id.iv_jia);
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public interface onEndItemClickListener {
        void onEndItemClick();
    }
}
