package com.example.music.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.bean.BenDiYuePuBean;

import java.util.ArrayList;

public class DaoRuQuPuAdaper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<BenDiYuePuBean> list;
    private final static int ITEM_VIEWTYPE_ONE = 1;
    private final static int ITEM_VIEWTYPE_TWO = 2;
    private onItemClickListener onItemClickListener;
    private onEndItemClickListener onEndItemClickListener;

    public void setOnEndItemClickListener(DaoRuQuPuAdaper.onEndItemClickListener onEndItemClickListener) {
        this.onEndItemClickListener = onEndItemClickListener;
    }

    public void setOnItemClickListener(DaoRuQuPuAdaper.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public DaoRuQuPuAdaper(Context mContext, ArrayList<BenDiYuePuBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View titleInflate = LayoutInflater.from(mContext).inflate(R.layout.item_daoruqupu_title, parent, false);
            ViewHolder viewHolder = new ViewHolder(titleInflate);
            return viewHolder;
        } else if (viewType == 2) {
            View imageInflate = LayoutInflater.from(mContext).inflate(R.layout.item_daoruqupu_image, parent, false);
            ViewoHolder1 viewoHolder1 = new ViewoHolder1(imageInflate);
            return viewoHolder1;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 1) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mTitle.setText(list.get(position).getTitle());
            if (list.get(position).isSelected()) {
                viewHolder.mTitle.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                viewHolder.mTitle.setTextColor(mContext.getResources().getColor(R.color.black));
            }
            viewHolder.mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        } else if (itemViewType == 2) {
            ViewoHolder1 viewoHolder1 = (ViewoHolder1) holder;
            viewoHolder1.mTianJia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEndItemClickListener.onEndItemClick();
                }
            });
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
    public int getItemCount() {
        if (list == null || list.size() <= 0) {
            return 1;
        } else {
            return list.size() + 1;
        }
    }

    public void setData(ArrayList<BenDiYuePuBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_daoruqupu_title);
        }
    }

    class ViewoHolder1 extends RecyclerView.ViewHolder {
        private ImageView mTianJia;

        public ViewoHolder1(@NonNull View itemView) {
            super(itemView);
            mTianJia = itemView.findViewById(R.id.tv_tianjia);
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public interface onEndItemClickListener {
        void onEndItemClick();
    }
}
