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
            ToastUtil.showShort("Error code " + ((HttpException) e).code());
        } else {
            if (TextUtils.isEmpty(e.getMessage())) {
                ToastUtil.showShort(R.string.network_failure);
            } else {
                ToastUtil.showShort(e.getMessage());
            }
        }
        e.printStackTrace();
    }

    static public void onLogInError(Throwable e) {
        if (e instanceof HttpException) {
            int code = ((HttpException) e).code();
            switch (code) {
                case 403:   ToastUtil.showShort(R.string.wrong_password);   break;
                case 404:   ToastUtil.showShort(R.string.user_not_exist);  break;
                case 409:   ToastUtil.showShort(R.string.user_already_exist);  break;
                default:    ToastUtil.showShort(R.string.network_failure); break;
            }
        } else {
            if (TextUtils.isEmpty(e.getMessage())) {
                ToastUtil.showShort(R.string.network_failure);
            } else {
                ToastUtil.showShort(e.getMessage());
            }
        }
        e.printStackTrace();
    }

    static public void onUploadError(Throwable e) {
        if (e instanceof HttpException) {
            int code = ((HttpException) e).code();
            switch (code) {
                case 409:   ToastUtil.showShort(R.string.name_already_exist);  break;
                default:    ToastUtil.showShort(R.string.network_failure); break;
            }
        } else {
            if (TextUtils.isEmpty(e.getMessage())) {
                ToastUtil.showShort(R.string.network_failure);
            } else {
                ToastUtil.showShort(e.getMessage());
            }
        }
        e.printStackTrace();
    }

    static public final Action1<Throwable> basicErrorHandler = throwable -> onError(throwable);
    static public final Action1<Throwable> loginErrorHandler = throwable -> onLogInError(throwable);
    static public final Action1<Throwable> uploadErrorHandler = throwable -> onUploadError(throwable);

    static public final Func1<HttpResponse, Observable<HttpResponse>> httpFailureFilter =
            httpResponse -> {
                if (httpResponse.getError() == 0) {
                    return Observable.just(httpResponse);
                } else {
                    ToastUtil.showShort(httpResponse.getMsg());
                    return Observable.error(new Exception(httpResponse.getMsg()));
                }
            };
}
