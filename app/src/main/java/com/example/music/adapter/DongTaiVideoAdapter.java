package com.example.music.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.bean.MusicBean;

import java.util.ArrayList;
import java.util.List;

public class DongTaiVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private ArrayList<MusicBean> list;
    private ArrayList<MusicBean> beans;
    private Context mContext;

    public DongTaiVideoAdapter(ArrayList<MusicBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        this.beans = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_dong_video, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mTvName.setText(list.get(position).getName());
        viewHolder.mTvSize.setText(list.get(position).getSize());
        MusicBean musicBean = list.get(position);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(musicBean);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onItemLongClick(list, position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(ArrayList<MusicBean> imageFileList) {
        this.list = imageFileList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (TextUtils.isEmpty(constraint)) {
                    list = beans;
                } else {
                    list = new ArrayList<>();
                    for (MusicBean bean : beans) {
                        if (bean.getName().contains(constraint) || bean.getName().contains(constraint)) {
                            list.add(bean);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = list;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values instanceof List) {
                    notifyDataSetChanged();
                }
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName;
        private TextView mTvSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.dong_tv_video_name);
            mTvSize = itemView.findViewById(R.id.dong_tv_video_size);
        }
    }

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(DongTaiVideoAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(MusicBean musicBean);
    }

    private onItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(DongTaiVideoAdapter.onItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface onItemLongClickListener {
        void onItemLongClick(ArrayList<MusicBean> list, int position);
    }
}
