package com.bbq.smartokhttp.request;

import android.util.ArrayMap;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 正在请求的request队列
 * Created by bangbang.qiu on 2019/10/30.
 */
public class RequestQueue {
    private static final int size = 10;
    private ConcurrentLinkedQueue<HttpRequest> requestQueue = new ConcurrentLinkedQueue<>();

    private static final RequestQueue queue = new RequestQueue();

    private RequestQueue() {
    }

    public static RequestQueue getInstance() {
        return queue;
    }

    public ConcurrentLinkedQueue<HttpRequest> getRequestQueue() {
        return requestQueue;
    }

    public void add(HttpRequest request) {
        if (requestQueue.size() < size) {
            requestQueue.add(request);
        }
    }

    public void remove(HttpRequest request) {
        requestQueue.remove(request);
    }

    public void removeByUrl(String url) {
        Iterator<HttpRequest> iterator = requestQueue.iterator();
        while (iterator.hasNext()) {
            HttpRequest httpRequest = iterator.next();
            if (url.equals(httpRequest.getUrl())) {
                iterator.remove();
            }
        }
    }

    public boolean requestExist(@NotNull String url, @NotNull ArrayMap param) {
        synchronized (queue) {
            for (HttpRequest request : requestQueue) {
                if (url.equals(request.getUrl()) && param.equals(request.getParams()))
                    return true;
            }
            return false;
        }
    }

    public boolean requestExist(@NotNull String url, @NotNull Object body) {
        synchronized (queue) {
            for (HttpRequest request : requestQueue) {
                if (url.equals(request.getUrl()) && body.equals(request.getBody()))
                    return true;
            }
            return false;
        }
    }

    public boolean requestExist(@NotNull String url) {
        synchronized (queue) {
            for (HttpRequest request : requestQueue) {
                if (url.equals(request.getUrl()))
                    return true;
            }
            return false;
        }
    }


}
