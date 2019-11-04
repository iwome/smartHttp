package com.bbq.smartokhttp;

/**
 * Created by bangbang.qiu on 2019/10/31.
 */
public interface HttpCallback<T> {
    void onRequestStart(Object what);

    void onSucceed(Object what, T data);

    void onResponseFinish(Object what);

    void onFailed(Object what, int code, String errorMsg);

    void onProgress(long progress, long length, boolean done);
}
