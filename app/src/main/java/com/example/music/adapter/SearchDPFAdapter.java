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
import com.example.music.bean.PDFImageBean;

import java.util.ArrayList;
import java.util.List;

public class SearchDPFAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<PDFImageBean> beans;//所有数据
    private ArrayList<PDFImageBean> list;//显示数据||搜索到的数据
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SearchDPFAdapter(Context mContext, ArrayList<PDFImageBean> beans) {
        this.mContext = mContext;
        this.beans = beans;
        this.list = beans;
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
        viewHolder.mTvPage.setText("大小：" + list.get(position).getSize());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
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
                    for (PDFImageBean bean : beans) {
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
    public int getItemCount() {
        return list.size();
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

    public interface onItemClickListener {
        void onItemClick(int position);
    }

}
