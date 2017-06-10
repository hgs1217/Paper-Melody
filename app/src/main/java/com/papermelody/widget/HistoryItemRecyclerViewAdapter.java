package com.papermelody.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.papermelody.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 潘宇杰 on 2017-6-10 0010.
 */


public class HistoryItemRecyclerViewAdapter extends RecyclerView.Adapter<HistoryItemRecyclerViewAdapter.ViewHolder> {

    public String[] datas = null;
    private Context context;
    // private LayoutInflater layoutInflater;
    private mOnItemClickListener onItemClickListener;


    public HistoryItemRecyclerViewAdapter(String[] xdatas) {
        this.datas = xdatas;
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
        viewHolder.setView(datas[position]);
        viewHolder.itemView.setOnClickListener((View view) ->
                {
                    int pos = viewHolder.getLayoutPosition();
                    //
                }
        );
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.length;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_history_title)
        TextView itemTitle;
        @BindView(R.id.item_history_caption)
        TextView itemCaption;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void setView(String datas) {
            itemTitle.setText(datas);
        }
    }

    public void setOnItemClickListener(mOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface mOnItemClickListener {
        void OnItemClick();
    }
}