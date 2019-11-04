package com.bbq.smartokhttp;

import android.graphics.Bitmap;

/**
 * Created by bangbang.qiu on 2019/10/31.
 */
public class BitmapCallback implements HttpCallback<Bitmap> {
    @Override
    public void onRequestStart(Object what) {

    }

    @Override
    public void onSucceed(Object what, Bitmap data) {

    }

    @Override
    public void onResponseFinish(Object what) {

    }

    @Override
    public void onFailed(Object what,int code, String errorMsg) {

    }

    @Override
    public void onProgress(long progress, long length, boolean done) {

    }
}
