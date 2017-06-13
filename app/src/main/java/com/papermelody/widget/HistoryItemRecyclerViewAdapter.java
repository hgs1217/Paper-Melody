package com.papermelody.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.model.HistoryMusic;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 潘宇杰 on 2017-6-10 0010.
 */


public class HistoryItemRecyclerViewAdapter extends RecyclerView.Adapter<HistoryItemRecyclerViewAdapter.ViewHolder> {

    public String[] datas = null;
    private List<HistoryMusic> musics;
    private Context context;
    // private LayoutInflater layoutInflater;
    private mOnItemClickListener onItemClickListener;


    public HistoryItemRecyclerViewAdapter(String[] xdatas, List<HistoryMusic> musics) {
        this.datas = xdatas;
        this.musics = musics;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history_music, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (position >= datas.length)
            viewHolder.setView("Caption Fetch Failed", musics.get(position));
        else
            viewHolder.setView(datas[position], musics.get(position));
        viewHolder.itemView.setOnClickListener((View view) ->
                {
                    int pos = viewHolder.getLayoutPosition();
                    //// TODO: 2017-6-14 0014 after click
                }
        );
    }

    //获取传入RecyclerView数据的数量
    @Override
    public int getItemCount() {
        return musics.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_history_title)
        TextView itemTitle;
        @BindView(R.id.item_history_caption)
        TextView itemCaption;
        @BindView(R.id.item_history_fileSize)
        TextView itemSize;
        @BindView(R.id.item_history_time)
        TextView itemModifiedTime;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        private void setView(String datas, HistoryMusic music) {
            itemTitle.setText(music.getName());
            itemCaption.setText(datas);
            itemModifiedTime.setText(timeLongToString(music.getCreateTime()));
            itemSize.setText(fileSizeToString(music.getSize()));
        }

        private String timeLongToString(long m) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
            String time = sdf.format(new Date(m));
            return time;
        }

        private String fileSizeToString(long m) {
            Integer label = 0;
            /**  label:
             * 0-Bytes
             * 1-KB
             * 2-MB
             */
            double res = (double) m;
            while (res > 1024) {
                if (label > 2)
                    break;
                res /= 1024.0;
                label++;
            }
            DecimalFormat df = new DecimalFormat("#.00");
            String str = df.format(res);
            switch (label) {
                case 0:
                    return Long.toString(m) + " Bytes";
                case 1:
                    return str + " KB";
                case 2:
                    return str + " MB";
                default:
                    return str;
            }
        }
    }

    public void setOnItemClickListener(mOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface mOnItemClickListener {
        void OnItemClick();
    }
}
