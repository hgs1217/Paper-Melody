package com.papermelody.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by HgS_1217_ on 2017/3/18.
 */

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 所有Activity的基类，用于放置所有Activity冗余的共有部分
     */

    /* 静态调用opencv library */
    static{ System.loadLibrary("opencv_java3"); }

    /* compositeSubscription用于持有该页面的subscription，在生命周期结束的时候取消订阅，从而防止Observable
     * 持有Context时导致的内存泄漏 */
    private CompositeSubscription compositeSubscription;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        setStatusBarTransparent();
    }

    abstract protected int getContentViewId();

    private void setStatusBarTransparent() {
        /**
         * 设置手机状态栏为透明，注意activity的layout需设置fitsSystemWindows参数为true，否则界面会占用状态栏
         */
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
    }

    public CompositeSubscription getCompositeSubscription() {
        if (this.compositeSubscription == null) {
            this.compositeSubscription = new CompositeSubscription();
        }

        return this.compositeSubscription;
    }


    public void addSubscription(Subscription s) {
        if (this.compositeSubscription == null) {
            this.compositeSubscription = new CompositeSubscription();
        }

        this.compositeSubscription.add(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.compositeSubscription != null) {
            this.compositeSubscription.unsubscribe();
        }
    }
}
