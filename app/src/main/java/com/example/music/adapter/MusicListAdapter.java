package com.example.music.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.music.R;
import com.example.music.bean.Song;
import com.example.music.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private List<Song> mFilteredList;
    private List<Song> musicList;
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(MusicListAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MusicListAdapter(List<Song> musicList) {
        this.mFilteredList = musicList;
        this.musicList = musicList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_music_rv_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //给控件赋值
        ViewHolder viewHolder = (ViewHolder) holder;
        Song item = mFilteredList.get(position);
        int duration = item.getDuration();
        String time = MusicUtils.formatTime(duration);
        viewHolder.mName.setText(item.getSong().trim());//歌曲名称
        viewHolder.mChagZhe.setText(item.getSinger() + " - " + item.getAlbum());//歌手 - 专辑
        viewHolder.mTime.setText(time);//歌曲时间
        //歌曲序号，因为getAdapterPosition得到的位置是从0开始，故而加1，
        //是因为位置和1都是整数类型，直接赋值给TextView会报错，故而拼接了""
        viewHolder.mXuHao.setText(position + 1 + "");
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
                    mFilteredList = musicList;
                } else {
                    mFilteredList = new ArrayList<>();
                    for (Song music : musicList) {
                        if (music.getSong().contains(constraint) || music.getSinger().contains(constraint)) {
                            mFilteredList.add(music);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = mFilteredList;
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
        return mFilteredList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mXuHao;
        private TextView mChagZhe;
        private TextView mTime;

        public ViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.tv_song_name);
            mXuHao = view.findViewById(R.id.tv_position);
            mChagZhe = view.findViewById(R.id.tv_singer);
            mTime = view.findViewById(R.id.tv_duration_time);
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
}
