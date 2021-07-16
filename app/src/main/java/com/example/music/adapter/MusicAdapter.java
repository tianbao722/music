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

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private ArrayList<MusicBean> list;
    private ArrayList<MusicBean> beans;
    private Context mContext;

    public MusicAdapter(ArrayList<MusicBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        this.beans = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_music_rv_list, parent, false);
        ViewHolcer viewHolcer = new ViewHolcer(inflate);
        return viewHolcer;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolcer viewHolcer = (ViewHolcer) holder;
        viewHolcer.mTvName.setText(list.get(position).getName());
        viewHolcer.mTvSize.setText("大小：" + list.get(position).getSize());
        viewHolcer.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, list.get(position));
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

    class ViewHolcer extends RecyclerView.ViewHolder {
        TextView mTvName;
        TextView mTvSize;

        public ViewHolcer(@NonNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_music_name);
            mTvSize = itemView.findViewById(R.id.tv_music_size);
        }
    }

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(MusicAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(int position, MusicBean musicBean);
    }

    public void setData(ArrayList<MusicBean> data) {
        list.clear();
        beans.clear();
        list = data;
        beans = data;
        notifyDataSetChanged();
    }
}
