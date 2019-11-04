package com.bbq.smartokhttp.config;

import com.bbq.smartokhttp.BuildConfig;
import com.bbq.smartokhttp.HttpCallback;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by bangbang.qiu on 2019/10/29.
 */
public class HttpConfig {
    public int readTimeout = 15;
    public int writeTimeout = 15;
    public int connectTimeout = 15;
    public int maxIdleConnections = 5;
    public int keepAliveDuration = 45;
    public String cacheDirectory = "";
    public int maxCacheSize = 100 * 1024 * 1024;//100MB
    public int threadPoolSize = 3;
    public boolean enableProxy = false;
    public boolean hasNetLog = BuildConfig.DEBUG;
    public String proxyIp;
    public int proxyPort;
    public InputStream[] certificates;
    public InputStream[] bksFile;
    public String authorityPassword;

    public boolean gZip = false;
    public boolean needProgress = false;
    public boolean mock = false;
    public HttpCallback httpCallback;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpConfig)) return false;
        HttpConfig that = (HttpConfig) o;
        return readTimeout == that.readTimeout &&
                writeTimeout == that.writeTimeout &&
                connectTimeout == that.connectTimeout &&
                maxIdleConnections == that.maxIdleConnections &&
                keepAliveDuration == that.keepAliveDuration &&
                maxCacheSize == that.maxCacheSize &&
                threadPoolSize == that.threadPoolSize &&
                enableProxy == that.enableProxy &&
                hasNetLog == that.hasNetLog &&
                proxyPort == that.proxyPort &&
                gZip == that.gZip &&
                needProgress == that.needProgress &&
                mock == that.mock &&
                Objects.equals(cacheDirectory, that.cacheDirectory) &&
                Objects.equals(proxyIp, that.proxyIp) &&
                Arrays.equals(certificates, that.certificates) &&
                Arrays.equals(bksFile, that.bksFile) &&
                Objects.equals(authorityPassword, that.authorityPassword) &&
                Objects.equals(httpCallback, that.httpCallback);
    }
}
