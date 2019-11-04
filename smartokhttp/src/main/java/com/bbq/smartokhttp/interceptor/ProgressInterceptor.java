package com.bbq.smartokhttp.interceptor;

import com.bbq.smartokhttp.HttpCallback;
import com.bbq.smartokhttp.response.ProgressResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by bangbang.qiu on 2019/11/1.
 */
public class ProgressInterceptor implements Interceptor {

    private HttpCallback httpCallback;

    public ProgressInterceptor(HttpCallback httpCallback) {
        this.httpCallback = httpCallback;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), this.httpCallback)).build();
    }
}
