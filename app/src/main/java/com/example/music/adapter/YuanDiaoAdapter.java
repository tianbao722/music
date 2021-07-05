package com.example.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.bean.YinDiaoBean;

import java.util.ArrayList;

public class YuanDiaoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<YinDiaoBean> yinDiaoBeans;
    private Context mContext;
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(YuanDiaoAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public YuanDiaoAdapter(ArrayList<YinDiaoBean> yinDiaoBeans, Context mContext) {
        this.yinDiaoBeans = yinDiaoBeans;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_yindiao, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mTvYinDiaoZhi.setText(yinDiaoBeans.get(position).getYindiao());
        if (yinDiaoBeans.get(position).isSelecte()) {
            viewHolder.mTvYinDiaoZhi.setTextColor(mContext.getResources().getColor(R.color.white));
            viewHolder.mTvYinDiaoZhi.setBackground(mContext.getResources().getDrawable(R.drawable.shape_yindiaoselect));
        } else {
            viewHolder.mTvYinDiaoZhi.setTextColor(mContext.getResources().getColor(R.color.black));
            viewHolder.mTvYinDiaoZhi.setBackground(mContext.getResources().getDrawable(R.drawable.shape_yindiao));
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return yinDiaoBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvYinDiaoZhi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvYinDiaoZhi = itemView.findViewById(R.id.tv_yindiaozhi);
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
}
