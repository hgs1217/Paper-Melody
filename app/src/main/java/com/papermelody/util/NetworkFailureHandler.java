package com.papermelody.util;

import android.text.TextUtils;

import com.papermelody.R;
import com.papermelody.model.response.HttpResponse;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by HgS_1217_ on 2017/5/2.
 */

public class NetworkFailureHandler {
    /**
     * 处理网络故障的控制类
     */

    static public void onError(Throwable e) {
        if (e instanceof HttpException) {
            ToastUtils.showShort("Error code " + ((HttpException) e).code());
        } else {
            if (TextUtils.isEmpty(e.getMessage())) {
                ToastUtils.showShort(R.string.network_failure);
            } else {
                ToastUtils.showShort(e.getMessage());
            }
        }
        e.printStackTrace();
    }

    static public final Action1<Throwable> basicErrorHandler = throwable -> onError(throwable);

    static public final Func1<HttpResponse, Observable<HttpResponse>> httpFailureFilter =
            httpResponse -> {
                if (httpResponse.getError() == 0) return Observable.just(httpResponse);
                else
                    return Observable.error(new Exception(httpResponse.getMsg()));
            };
}
