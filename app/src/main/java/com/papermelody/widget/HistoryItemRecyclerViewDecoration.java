package com.papermelody.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by 潘宇杰 on 2017-6-14 0014.
 */

public class HistoryItemRecyclerViewDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public HistoryItemRecyclerViewDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (parent.getChildAdapterPosition(view) != 0)
            outRect.top = space;
    }
}