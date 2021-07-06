package com.example.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.bean.BenDiYuePuBean;

import java.util.ArrayList;

public class TuPianYuePuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<BenDiYuePuBean> list;
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(TuPianYuePuAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TuPianYuePuAdapter(Context mContext, ArrayList<BenDiYuePuBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_tupianyuepuleft, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mTvTitle.setText(list.get(position).getTitle());
        if (list.get(position).isSelected()) {
            viewHolder.mLLItemBenDiYuePu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_bendiqupu));
        } else {
            viewHolder.mLLItemBenDiYuePu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_white));
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;
        private LinearLayout mLLItemBenDiYuePu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_bendiyueputitle);
            mLLItemBenDiYuePu = itemView.findViewById(R.id.ll_item_bendiyuepu);
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
}
