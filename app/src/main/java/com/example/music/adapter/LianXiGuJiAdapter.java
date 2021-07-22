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
import com.example.music.bean.LianXiGuJiBean;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LianXiGuJiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<LianXiGuJiBean> list;
    private Context mContext;

    public LianXiGuJiAdapter(ArrayList<LianXiGuJiBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_guji, parent, false);
            ViewHolder viewHolder = new ViewHolder(inflate);
            return viewHolder;
        } else {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_guji2, parent, false);
            ViewHolderTwo viewHolder = new ViewHolderTwo(inflate);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 2) {
            ViewHolderTwo viewHolderTwo = (ViewHolderTwo) holder;
            viewHolderTwo.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mTvName.setText(list.get(position).getName());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public void setData(ArrayList<LianXiGuJiBean> mList) {
        list = mList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvImage;
        TextView mTvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvImage = itemView.findViewById(R.id.iv_guji_image);
            mTvName = itemView.findViewById(R.id.item_guji_tv_name);
        }
    }

    class ViewHolderTwo extends RecyclerView.ViewHolder {
        private ImageView mIvImage;

        public ViewHolderTwo(@NonNull View itemView) {
            super(itemView);
            mIvImage = itemView.findViewById(R.id.iv_tianjiaguji);
        }
    }

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(LianXiGuJiAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
}
