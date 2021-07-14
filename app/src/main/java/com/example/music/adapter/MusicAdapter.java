package com.example.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.bean.MusicBean;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MusicBean> list;
    private Context mContext;

    public MusicAdapter(ArrayList<MusicBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
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
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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
        void onItemClick(int position);
    }
}
