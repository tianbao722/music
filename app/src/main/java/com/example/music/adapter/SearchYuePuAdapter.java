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
import com.example.music.bean.ImageYuePuImageBean;

import java.util.ArrayList;
import java.util.List;

public class SearchYuePuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<ImageYuePuImageBean> beans;//所有数据
    private ArrayList<ImageYuePuImageBean> list;//显示数据||搜索到的数据

    public SearchYuePuAdapter(Context mContext, ArrayList<ImageYuePuImageBean> list) {
        this.mContext = mContext;
        this.beans = list;
        this.list = list;
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
        viewHolder.mTvTitle.setText(list.get(position).getName());
        viewHolder.mTvPage.setText("页数：" + list.get(position).getContent());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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
                    for (ImageYuePuImageBean bean : beans) {
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<ImageYuePuImageBean> data) {
        list.clear();
        beans.clear();
        list = data;
        beans = data;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTitle;
        TextView mTvPage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_image_name);
            mTvPage = itemView.findViewById(R.id.tv_yeshu);
        }
    }

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(int position, ImageYuePuImageBean imageYuePuImageBean);
    }
}
