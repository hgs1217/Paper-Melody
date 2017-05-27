package com.papermelody.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.model.ImgBanner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HgS_1217_ on 2017/5/27.
 */

public class MainCycleViewPager extends FrameLayout implements ViewPager.OnPageChangeListener {
    /* 用于轮播图的显示 */
    /* 参考资料：http://www.jianshu.com/p/c052ae42fbc9 */

    private final int WHEEL = 100;
    private final int WHEEL_WAIT = 101;
    private final int REPEAT_TIMES = 10;

    private Context mContext;
    private ViewPager mViewPager;
    private TextView mTitle;
    private LinearLayout mIndicatorLayout; // 指示器布局
    private ImageCycleViewListener mImageCycleViewListener;
    private ViewPagerAdapter mAdapter;
    private int mIndicatorSelected;
    private int mIndicatorUnselected;
    private ArrayList<View> mViews = new ArrayList<>(); // 需要轮播的views
    private List<ImgBanner> mBanners;  // 数据集合
    private ImageView[] mIndicators; //指示器小圆点
    private Handler handler; // 执行切换的handler
    private int mCurrentPosition = 0; // 轮播当前位置
    private long releaseTime = 0; // 手指松开后页面不滚动时间，防止手指松开后短时间进行切换
    private int delay = 4000; // 默认轮播切换时间
    private boolean isWheel = true; // 是否轮播
    private boolean isCycle = true; // 是否循环
    private boolean isScrolling = false; // 滚动框是否正在滚动

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mContext != null && isWheel) {
                long now = System.currentTimeMillis();
                // 检测上次滑动到现在中间是否有触击操作，若有则等待下一次切换
                if (now - releaseTime > delay - 500) {
                    handler.sendEmptyMessage(WHEEL);
                } else {
                    handler.sendEmptyMessage(WHEEL_WAIT);
                }
            }
        }
    };

    public MainCycleViewPager(Context context) {
        this(context, null);
    }

    public MainCycleViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainCycleViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.item_cycle_banner, this, true);
        mViewPager = (ViewPager) findViewById(R.id.cycle_view_pager);
        mTitle = (TextView) findViewById(R.id.text_cycle_title);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.cycle_indicator);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mViews.size() > 0) {
                    if (msg.what == WHEEL) {
                        if (!isScrolling) {
                            /* 当前为非滚动状态，切换到下一页 */
                            int position = (mCurrentPosition + 1) % mViews.size();
                            mViewPager.setCurrentItem(position, true);
                        }
                        releaseTime = System.currentTimeMillis();
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, delay);
                    } else if (msg.what == WHEEL_WAIT) {
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, delay);
                    }
                }
            }
        };
    }

    /* 设置指示器的样式ID */
    public void setIndicatorsSelected (int selected, int unselected) {
        mIndicatorSelected = selected;
        mIndicatorUnselected = unselected;
    }

    public void setData (List<ImgBanner> banners, ImageCycleViewListener listener) {
        mCurrentPosition = banners.size() * (REPEAT_TIMES / 2);
        setData(banners, listener, mCurrentPosition);
    }

    public void setData (List<ImgBanner> banners, ImageCycleViewListener listener, int showPosition) {
        if (banners.size() > 0) {
            mViews.clear();
            mBanners = banners;
            if (isCycle) {
                /* 添加轮播图view，数量为集合数+2 */
                // 先添加最后一张图，然后来回添加中间n张图，使得总图数很大，最后添加第一张图。总图数很大就能一定程度上解决卡顿的问题
                mViews.add(getImageView(mContext, mBanners.get(mBanners.size()-1).getResId()));
                for (int j=0; j<REPEAT_TIMES; ++j) {
                    for (int i=0; i<mBanners.size(); ++i) {
                        // 暂时存在反复加载相同图片浪费流量的问题
                        mViews.add(getImageView(mContext, mBanners.get(i).getResId()));
                    }
                }
                mViews.add(getImageView(mContext, mBanners.get(0).getResId()));
                Log.d("TEST", String.valueOf(mViews.size()));
            } else {
                // 不循环，只添加n张图
                for (int i=0; i<mBanners.size(); ++i) {
                    mViews.add(getImageView(mContext, mBanners.get(i).getResId()));
                }
            }
            if (mViews.size() > 0) {
                mImageCycleViewListener = listener;
                // 设置指示器
                if (isCycle) {
                    mIndicators = new ImageView[mBanners.size()];
                } else {
                    mIndicators = new ImageView[mBanners.size()];
                }
                mIndicatorLayout.removeAllViews();
                for (int i=0; i<mIndicators.length; ++i) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(10, 0, 10, 0);
                    mIndicators[i] = new ImageView(mContext);
                    mIndicators[i].setLayoutParams(layoutParams);
                    mIndicatorLayout.addView(mIndicators[i]);
                }
                mAdapter = new ViewPagerAdapter();
                // 默认指向第一项
                setIndicator(0);
                mViewPager.setOffscreenPageLimit(3);
                mViewPager.setOnPageChangeListener(this);
                mViewPager.setAdapter(mAdapter);
                if (showPosition < 0 || showPosition >= mViews.size()) {
                    showPosition = 0;
                }
                if (isCycle) {
                    showPosition += 1;
                }
                mViewPager.setCurrentItem(showPosition);
                setWheel(true); // 设置轮播
            } else {
                /* 没有view时隐藏整个布局 */
                this.setVisibility(View.GONE);
            }
        } else {
            /* 没有数据时隐藏整个布局 */
            this.setVisibility(View.GONE);
        }
    }

    private View getImageView(Context context, int resId) {
        /* 设置某一页面的view */
        RelativeLayout relativeLayout = new RelativeLayout(context);
        ImageView imageView = new ImageView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(layoutParams);
        // 设置一个半透明的黑色背景，防止白字受白色图片干扰而无法显示
        ImageView background = new ImageView(context);
        background.setLayoutParams(layoutParams);
        background.setBackgroundResource(R.color.colorCycleViewBannerBackground);
        relativeLayout.addView(imageView);
        relativeLayout.addView(background);
        Picasso.with(context).load(resId).into(imageView);
        return relativeLayout;
    }

    private void setIndicator(int selectedPosition) {
        setText(mTitle, mBanners.get(selectedPosition).getTitle());
        try {
            for (int i=0; i < mIndicators.length; ++i) {
                if (i == selectedPosition) {
                    mIndicators[i].setBackgroundResource(mIndicatorSelected);
                } else {
                    mIndicators[i].setBackgroundResource(mIndicatorUnselected);
                }
            }
        } catch (Exception e) {
            Log.e("CycleBanner", "指示器路径不正确");
        }
    }

    private void setText(TextView textView, String text) {
        if (text != null && textView != null) {
            textView.setText(text);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int index) {
        int maximum = mViews.size() - 1;
        int position = index;
        mCurrentPosition = index;
        if (isCycle) {
            if (index == 0) {
                // 滚动到mViews的第一个，将mCurrentPosition设为max-1
                mCurrentPosition = maximum - 1;
            } else if (index == maximum) {
                // 滚动到mViews的最后一个，将mCurrentPosition设为1
                mCurrentPosition = 1;
            }

            if (mCurrentPosition == 0) {
                Log.w("CycleViewPager", "mCurrentPosition == 0 warning");
                position = mBanners.size() - 1;
            } else {
                position = (mCurrentPosition - 1) % mBanners.size();
            }
        }
        setIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 1) { // viewPager在滚动
            isScrolling = true;
            return;
        } else if (state == 0) { // viewPager滚动结束
            releaseTime = System.currentTimeMillis();
            // 跳转到第mCurrentPosition个页面
            mViewPager.setCurrentItem(mCurrentPosition, false);
        }
        isScrolling = false;
    }

    public boolean isCycle() {
        return isCycle;
    }

    public void setCycle(boolean cycle) {
        isCycle = cycle;
    }

    public void setWheel(boolean isWheel) {
        this.isWheel = isWheel;
        isCycle = true;
        if (isWheel) {
            handler.postDelayed(runnable, delay);
        }
    }

    public void refreshData() {
        /* 当外部视图更新后，通知刷新数据 */
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public boolean isWheel() {
        return isWheel;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View view = mViews.get(position);
            if (mImageCycleViewListener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mImageCycleViewListener.onImageClick(mBanners.get((mCurrentPosition - 1 + mBanners.size()) % mBanners.size()),
                                mCurrentPosition, view);
                    }
                });
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public interface ImageCycleViewListener {
        /* 单击图片事件 */
        public void onImageClick (ImgBanner banner, int position, View imageView);
    }
}

