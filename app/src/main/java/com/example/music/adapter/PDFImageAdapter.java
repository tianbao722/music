package com.example.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.bean.PDFImageBean;

import java.util.ArrayList;

public class PDFImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<PDFImageBean> list;
    private Context mContext;

    public PDFImageAdapter(ArrayList<PDFImageBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_image_rupianyuepu_image, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mName.setText(list.get(position).getName());
        viewHolder.mYeShu.setText("大小：" + list.get(position).getSize());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItmeClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null && list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mYeShu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.tv_image_name);
            mYeShu = itemView.findViewById(R.id.tv_yeshu);
        }
    }

    public void setData(ArrayList<PDFImageBean> data) {
        list.clear();
        list = data;
        notifyDataSetChanged();
    }

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(PDFImageAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItmeClick(int position);
    }
}
