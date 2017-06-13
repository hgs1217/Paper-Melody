package com.papermelody.widget;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by HgS_1217_ on 2017/6/13.
 */

public class TutorialViewPagerAdapter extends PagerAdapter {
    /**
     * 教程页面的Adapter
     */

    // 界面列表
    private List<View> views;

    public TutorialViewPagerAdapter(List<View> views) {
        this.views = views;
    }

    //加载viewpager的每个item
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position), 0);
        return views.get(position);
    }

    //删除ViewPager的item
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    // 获得当前界面数
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
