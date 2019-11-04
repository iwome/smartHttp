package com.bbq.smartokhttp;

import android.text.TextUtils;

import com.bbq.smartokhttp.config.HttpConfig;
import com.bbq.smartokhttp.interceptor.GzipRequestInterceptor;
import com.bbq.smartokhttp.interceptor.LoggingInterceptor;
import com.bbq.smartokhttp.interceptor.MockInterceptor;
import com.bbq.smartokhttp.interceptor.NetCacheInterceptor;
import com.bbq.smartokhttp.interceptor.ProgressInterceptor;
import com.bbq.smartokhttp.secure.SSLHelper;
import com.bbq.smartokhttp.util.CommonUtil;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * Created by bangbang.qiu on 2019/10/30.
 */
public class HttpClient {

    private static OkHttpClient mClient;
    private static HttpConfig mHttpConfig;

    private HttpClient() {

    }

    public static OkHttpClient getHttpClient(HttpConfig httpConfig) {
        if (mClient == null) {
            if (httpConfig == null) httpConfig = new HttpConfig();
            mHttpConfig = httpConfig;
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(httpConfig.connectTimeout, TimeUnit.SECONDS)
                    .readTimeout(httpConfig.readTimeout, TimeUnit.SECONDS)
                    .writeTimeout(httpConfig.writeTimeout, TimeUnit.SECONDS)
                    .eventListener(new PrintEventsNonConcurrent())
                    .connectionPool(new ConnectionPool(httpConfig.maxIdleConnections, httpConfig.keepAliveDuration, TimeUnit.SECONDS));
            if (!CommonUtil.isEmpty(httpConfig.cacheDirectory)) {
                builder.cache(new Cache(new File(httpConfig.cacheDirectory), httpConfig.maxCacheSize))
                        .addNetworkInterceptor(new NetCacheInterceptor());
            }
            if (httpConfig.certificates != null) {
                SSLHelper sslHelper = SSLHelper.getSslSocketFactory(httpConfig.certificates, null, null);
                builder.sslSocketFactory(sslHelper.sSLSocketFactory, sslHelper.trustManager);
            }
            if (httpConfig.enableProxy && !TextUtils.isEmpty(httpConfig.proxyIp))
                builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpConfig.proxyIp, httpConfig.proxyPort)));

            if (httpConfig.hasNetLog)
                builder.addInterceptor(new LoggingInterceptor());

            if (httpConfig.needProgress)
                builder.addInterceptor(new ProgressInterceptor(httpConfig.httpCallback));

            if (httpConfig.gZip)
                builder.addInterceptor(new GzipRequestInterceptor());

            if (httpConfig.mock)
                builder.addInterceptor(new MockInterceptor());
            mClient = builder.build();
        }
        return mClient;
    }

    public static OkHttpClient rebuildHttpClient(HttpConfig httpConfig) {
        mClient = null;
        return getHttpClient(httpConfig);
    }

    public static OkHttpClient getHttpClient() {
        return getHttpClient(mHttpConfig);
    }

    public static HttpConfig getHttpConfig() {
        return mHttpConfig;
    }
}
