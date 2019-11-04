package com.bbq.smartokhttp.interceptor;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 注意Cache-Control是给客户端使用的，所有response的header都是给客户端用的
 * Created by bangbang.qiu on 2019/11/4.
 */
public class NetCacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originResponse = chain.proceed(request);

        if (!TextUtils.isEmpty(request.header("Cache-Control"))) {
            originResponse = originResponse.newBuilder()
                    .removeHeader("pragma")
                    .header("Cache-Control", request.header("Cache-Control"))
                    .build();
        }

        return originResponse;
    }
}
