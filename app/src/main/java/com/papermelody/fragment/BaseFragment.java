package com.papermelody.fragment;

import android.support.v4.app.Fragment;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class BaseFragment extends Fragment {
    /**
     * 所有Fragment的基类，用于放置所有Fragment冗余的共有部分
     */

    /* compositeSubscription用于持有该页面的subscription，在生命周期结束的时候取消订阅，从而防止Observable
     * 持有Context时导致的内存泄漏 */
    private CompositeSubscription compositeSubscription;

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
    public void onDestroy() {
        super.onDestroy();
        if (this.compositeSubscription != null) {
            this.compositeSubscription.unsubscribe();
        }
    }
}
