package com.bbq.smartokhttp;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This prints events for a single in-flight call. It won't work for multiple concurrent calls
 * because we don't know what callStartNanos refers to.
 */
public final class PrintEventsNonConcurrent extends EventListener {

    private long callStartNanos;

    private void printEvent(String name) {
        long nowNanos = System.nanoTime();
        if (name.equals("callStart")) {
            callStartNanos = nowNanos;
        }
        long elapsedNanos = nowNanos - callStartNanos;
        Log.d("Qbb_PrintEvents: ", String.format("%.3f %s%n", elapsedNanos / 1000000000d, name));
    }

    @Override
    public void callStart(Call call) {
        printEvent("callStart" + System.nanoTime());
    }

    @Override
    public void proxySelectStart(Call call, HttpUrl url) {
        printEvent("proxySelectStart" + System.nanoTime());
    }

    @Override
    public void proxySelectEnd(Call call, HttpUrl url, List<Proxy> proxies) {
        printEvent("proxySelectEnd" + System.nanoTime());
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        printEvent("dnsStart" + System.nanoTime());
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        printEvent("dnsEnd" + System.nanoTime());
    }

    @Override
    public void connectStart(
            Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        printEvent("connectStart" + System.nanoTime());
    }

    @Override
    public void secureConnectStart(Call call) {
        printEvent("secureConnectStart" + System.nanoTime());
    }

    @Override
    public void secureConnectEnd(Call call, Handshake handshake) {
        printEvent("secureConnectEnd： " + System.nanoTime());
    }

    private String charToString(byte[] encoded) {
        return new String(encoded, StandardCharsets.UTF_8);
    }

    @Override
    public void connectEnd(
            Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        printEvent("connectEnd" + System.nanoTime());
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy,
                              Protocol protocol, IOException ioe) {
        printEvent("connectFailed" + System.nanoTime());
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        printEvent("connectionAcquired" + System.nanoTime());
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        printEvent("connectionReleased" + System.nanoTime());
    }

    @Override
    public void requestHeadersStart(Call call) {
        printEvent("requestHeadersStart" + System.nanoTime());
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        printEvent("requestHeadersEnd" + System.nanoTime());
    }

    @Override
    public void requestBodyStart(Call call) {
        printEvent("requestBodyStart" + System.nanoTime());
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        printEvent("requestBodyEnd" + System.nanoTime());
    }

    @Override
    public void requestFailed(Call call, IOException ioe) {
        printEvent("requestFailed" + System.nanoTime());
    }

    @Override
    public void responseHeadersStart(Call call) {
        printEvent("responseHeadersStart" + System.nanoTime());
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        printEvent("responseHeadersEnd" + System.nanoTime());
    }

    @Override
    public void responseBodyStart(Call call) {
        printEvent("responseBodyStart" + System.nanoTime());
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        printEvent("responseBodyEnd" + System.nanoTime());
    }

    @Override
    public void responseFailed(Call call, IOException ioe) {
        printEvent("responseFailed" + System.nanoTime());
    }

    @Override
    public void callEnd(Call call) {
        printEvent("callEnd" + System.nanoTime());
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        printEvent("callFailed" + System.nanoTime());
    }
}