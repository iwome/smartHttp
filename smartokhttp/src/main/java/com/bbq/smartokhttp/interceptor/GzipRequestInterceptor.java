package com.bbq.smartokhttp.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * Created by bangbang.qiu on 2019/11/1.
 */
public class GzipRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() != null && originalRequest.header("Content-Encoding") == null) {
            Request compressedRequest = originalRequest.newBuilder().header("Content-Encoding", "gzip").method(originalRequest.method(), this.gzip(originalRequest.body())).build();
            return chain.proceed(compressedRequest);
        } else {
            return chain.proceed(originalRequest);
        }
    }

    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            public MediaType contentType() {
                return body.contentType();
            }

            public long contentLength() {
                return -1L;
            }

            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}
