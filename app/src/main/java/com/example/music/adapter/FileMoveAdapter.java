package com.example.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.bean.BenDiYuePuBean;

import java.util.ArrayList;

public class FileMoveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<BenDiYuePuBean> mList;

    public FileMoveAdapter(Context mContext, ArrayList<BenDiYuePuBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_file_move, parent, false);
        ViewHolcer viewHolcer = new ViewHolcer(inflate);
        return viewHolcer;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolcer viewHolcer = (ViewHolcer) holder;
        viewHolcer.mTvName.setText(mList.get(position).getTitle());
        if (mList.get(position).isSelected()) {
            viewHolcer.mTvName.setBackground(mContext.getResources().getDrawable(R.drawable.search_tab_bg_move_file_select));
        } else {
            viewHolcer.mTvName.setBackground(mContext.getResources().getDrawable(R.drawable.search_tab_bg_focused));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(ArrayList<BenDiYuePuBean> strings) {
        mList = strings;
        notifyDataSetChanged();
    }

    class ViewHolcer extends RecyclerView.ViewHolder {
        private TextView mTvName;

        public ViewHolcer(@NonNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_file_move_name);
        }
    }

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(FileMoveAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
}
