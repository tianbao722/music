package com.example.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.bean.BanZouBean;

import java.util.ArrayList;

public class BanZouAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<BanZouBean> list;

    public BanZouAdapter(Context mContext, ArrayList<BanZouBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setData(ArrayList<BanZouBean> data) {
        list.clear();
        list = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_banzou, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        BanZouBean banZouBean = list.get(position);
        if (banZouBean != null) {
            viewHolder.mName.setText(banZouBean.getName());
        } else {
            return;
        }
        //点击条目进行播放
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
        //点击删除
        viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDeleteClickListener.onItemDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mYinFu;
        TextView mName;
        ImageView mDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mYinFu = itemView.findViewById(R.id.banzou_music_item_yinfu);
            mName = itemView.findViewById(R.id.banzou_music_item_name);
            mDelete = itemView.findViewById(R.id.banzou_music_item_tv_delete);
        }
    }

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(BanZouAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    private onItemDeleteClickListener onItemDeleteClickListener;

    public void setOnItemDeleteClickListener(BanZouAdapter.onItemDeleteClickListener onItemDeleteClickListener) {
        this.onItemDeleteClickListener = onItemDeleteClickListener;
    }

    public interface onItemDeleteClickListener {
        void onItemDelete(int position);
    }
}
