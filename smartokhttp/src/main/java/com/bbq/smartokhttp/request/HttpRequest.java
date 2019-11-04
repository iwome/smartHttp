package com.bbq.smartokhttp.request;

import androidx.collection.ArrayMap;

import com.bbq.smartokhttp.BitmapCallback;
import com.bbq.smartokhttp.FileCallback;
import com.bbq.smartokhttp.HttpCallback;
import com.bbq.smartokhttp.HttpCallbackImpl;
import com.bbq.smartokhttp.HttpClient;
import com.bbq.smartokhttp.HttpStatus;
import com.bbq.smartokhttp.config.HttpConfig;
import com.bbq.smartokhttp.response.HttpResponse;
import com.bbq.smartokhttp.util.CommonUtil;
import com.bbq.smartokhttp.util.ExSingleThread;
import com.bbq.smartokhttp.util.ThreadUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by bangbang.qiu on 2019/10/28.
 */
public class HttpRequest {
    private OkHttpClient mClient;
    private int requestMethod = RequestMethod.POST_JSON;
    private ArrayMap<String, String> headers;
    private ArrayMap<String, Object> params;
    private String url;
    private Object what;
    private HttpConfig httpConfig;
    private MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");//post_file时必须
    private File file;
    private Object body;//post_stream 作为stream
    private HttpCallback httpCallback;
    private boolean hasNet = true;
    private int maxCacheAge = 60 * 60; // 1h
    private boolean needCache = false;
    private Call call;

    private HttpRequest() {
    }

    public HttpRequest request() {
        if (httpRequestExit())
            return this;
        if (httpConfig == null) httpConfig = new HttpConfig();
        if (httpConfig.needProgress)
            httpConfig.httpCallback = httpCallback;
        if (!httpConfig.equals(HttpClient.getHttpConfig()))
            mClient = HttpClient.rebuildHttpClient(httpConfig);
        if (httpCallback == null)
            httpCallback = new HttpCallbackImpl();
        RequestQueue.getInstance().add(HttpRequest.this);
        httpCallback.onRequestStart(what);
        ExSingleThread.getInstance().execute(new ThreadUtils.OperationTask() {
            @Override
            public Object execute() {
                HttpResponse httpResponse;
                try {
                    httpResponse = performRequest();
                } catch (Exception e) {
                    RequestQueue.getInstance().remove(HttpRequest.this);
                    return new HttpResponse(url, HttpStatus.STATUS_CANCEL, null, "", what);
                }
                return httpResponse;
            }
        }, new ThreadUtils.TaskCallback() {
            @Override
            public void callback(Object result) {
                try {
                    HttpResponse response = (HttpResponse) result;
                    switch (response.getCode()) {
                        case HttpStatus.STATUS_CANCEL:
                        case HttpStatus.STATUS_PARTIAL_CONTENT:
                            break;
                        case HttpStatus.STATUS_NO_NET:
                            httpCallback.onFailed(what, response.getCode(), "网络连接失败");
                            break;
                        case HttpStatus.STATUS_BAD_REQUEST:
                        case HttpStatus.STATUS_UNAUTHORIZED:
                        case HttpStatus.STATUS_FORBIDDEN:
                        case HttpStatus.STATUS_NOT_FOUND:
                        case HttpStatus.STATUS_REQUEST_TIMEOUT:
                        case HttpStatus.STATUS_INTERNAL_SERVER_ERROR:
                        case HttpStatus.STATUS_SERVICE_UNAVAILABLE:
                        case HttpStatus.STATUS_NO:
                            httpCallback.onFailed(what, response.getCode(), "请求异常");
                            break;
                        case HttpStatus.STATUS_OK:
                        case HttpStatus.STATUS_NOT_MODIFIED:
                        case HttpStatus.STATUS_CACHE:
                            if (httpCallback instanceof BitmapCallback)
                                httpCallback.onSucceed(what, CommonUtil.bytesToBitmap(response.getResponseBody()));
                            else if (httpCallback instanceof FileCallback)
                                httpCallback.onSucceed(what, response.getResponseBody());
                            else
                                httpCallback.onSucceed(what, new String(response.getResponseBody(), Charset.forName("UTF-8")));
                            break;
                        case HttpStatus.STATUS_NO_CONTENT:
                            httpCallback.onSucceed(what, null);
                            break;
                        case HttpStatus.STATUS_CERTIFICATE_FAILED:
                            httpCallback.onFailed(what, response.getCode(), "CERTIFICATE_FAILED");
                            break;
                        default:
                            httpCallback.onFailed(what, response.getCode(), "请求异常");
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    RequestQueue.getInstance().remove(HttpRequest.this);
                    httpCallback.onResponseFinish(what);
                }

            }
        });
        return this;
    }

    private HttpResponse performRequest() {
        Request request;
        try {
            request = buildRequest();
        } catch (Exception e) {
            return new HttpResponse(url, HttpStatus.STATUS_NO, null, "", what);
        }
        if (request == null)
            return new HttpResponse(url, HttpStatus.STATUS_NO, null, "", what);
        if (!hasNet)
            return new HttpResponse(url, HttpStatus.STATUS_NO_NET, null, "", what);
        try {
            call = mClient.newCall(request);
            Response response = call.execute();
            if (call.isCanceled()) {
                return new HttpResponse(url, HttpStatus.STATUS_CANCEL, null, "", what);
            } else {
                HttpResponse httpResponse = buildHttpResponse(response);
                response.close();
                return httpResponse;
            }
        } catch (SocketTimeoutException e) {
            return new HttpResponse(url, HttpStatus.STATUS_REQUEST_TIMEOUT, null, "", what);
        } catch (SSLHandshakeException e) {
            return new HttpResponse(url, HttpStatus.STATUS_CERTIFICATE_FAILED, null, "", what);
        } catch (IOException e) {
            return "Canceled".equals(e.getMessage()) ? new HttpResponse(url, HttpStatus.STATUS_CANCEL, null, "", what)
                    : new HttpResponse(url, HttpStatus.STATUS_NO, null, "", what);
        } catch (Exception e) {
            return new HttpResponse(url, HttpStatus.STATUS_NO, null, "", what);
        }
    }

    private HttpResponse buildHttpResponse(Response response) {
        HttpResponse httpResponse = null;

        try {
            String contentType = response.header("Content-Type");
            httpResponse = new HttpResponse(url, response.code(), response.body().bytes(), contentType, what);
            ArrayMap<String, String> responseHeader = new ArrayMap<>();

            for (String key : response.headers().names()) {
                responseHeader.put(key, response.header(key));
            }

            httpResponse.setResponseHeader(responseHeader);
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        return httpResponse;
    }

    private Request buildRequest() {
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null)
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                builder.addHeader(key, value);
            }
        CacheControl.Builder cacheBuilder = new CacheControl.Builder();
        if (needCache) cacheBuilder.maxAge(maxCacheAge, TimeUnit.SECONDS);
        else cacheBuilder.noCache();
        builder.cacheControl(cacheBuilder.build());
        if (params == null) params = new ArrayMap<>();
        switch (requestMethod) {
            case RequestMethod.GET:
                return builder.build();
            case RequestMethod.POST_JSON:
                return builder.post(RequestBody.create(new JSONObject(params).toString(), MediaType.get("application/json; charset=utf-8")))
                        .build();
            case RequestMethod.POST_FORM:
                FormBody.Builder formBody = new FormBody.Builder();
                for (String key : params.keySet()) {
                    Object value = params.get(key);
                    formBody.add(key, value.toString());
                }
                return builder.post(formBody.build()).build();
            case RequestMethod.POST_FILE:
                if (file == null)
                    throw new IllegalArgumentException("request create failed,check your Builder.file");
                return builder.post(RequestBody.create(file, mediaType))
                        .build();
            case RequestMethod.POST_MULTIPART:
                final MultipartBody.Builder multiBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                for (String key : params.keySet()) {
                    Object value = params.get(key);
                    if (value instanceof File) {
                        File file = (File) value;
                        String contentType = CommonUtil.getFileContentType(file);
                        String fileName = file.getName();
                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                        multiBuilder.addFormDataPart(key, UUID.randomUUID() + "." + suffix, RequestBody.create(file, MediaType.parse(contentType)));
                    } else if (value instanceof File[]) {
                        File[] files = (File[]) value;

                        for (File file : files) {
                            String contentType = CommonUtil.getFileContentType(file);
                            String fileName = file.getName();
                            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                            multiBuilder.addFormDataPart(key, UUID.randomUUID() + "." + suffix, RequestBody.create(MediaType.parse(contentType), file));
                        }
                    } else {
                        multiBuilder.addFormDataPart(key, value.toString());
                    }
                }
                return builder.post(multiBuilder.build()).build();
            case RequestMethod.POST_STREAM:
                if (body == null)
                    throw new IllegalArgumentException("request create failed,check your Builder.body");
                RequestBody requestBody = new RequestBody() {
                    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                        if (body instanceof String) {
                            bufferedSink.writeUtf8(body.toString());
                        } else if (body instanceof byte[]) {
                            bufferedSink.write((byte[]) body);
                        }
                    }

                    public MediaType contentType() {
                        return MediaType.parse("application/octet-stream; charset=utf-8");
                    }
                };

                return builder.post(requestBody).build();
            case RequestMethod.POST_STRING:
                if (body == null)
                    throw new IllegalArgumentException("request create failed,check your Builder.body");
                if (body instanceof String)
                    return builder.post(RequestBody.create(body.toString(), mediaType)).build();
            default:
                return null;

        }
    }

    public void cancel() {
        if (call != null) call.cancel();
    }

    public void cancelByUrl(String url) {
        ConcurrentLinkedQueue<HttpRequest> queueList = RequestQueue.getInstance().getRequestQueue();
        Iterator<HttpRequest> requestIterator = queueList.iterator();
        while (requestIterator.hasNext()) {
            HttpRequest httpRequest = requestIterator.next();
            if (httpRequest.url.equals(url)) {
                httpRequest.cancel();
                requestIterator.remove();
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public ArrayMap<String, Object> getParams() {
        return params;
    }

    public Object getBody() {
        return body;
    }

    private boolean httpRequestExit() {
        switch (requestMethod) {
            case RequestMethod.GET:
                return RequestQueue.getInstance().requestExist(url);
            case RequestMethod.POST_JSON:
            case RequestMethod.POST_FORM:
            case RequestMethod.POST_MULTIPART:
                return RequestQueue.getInstance().requestExist(url, params);
            case RequestMethod.POST_FILE:
                return RequestQueue.getInstance().requestExist(url, file);
            case RequestMethod.POST_STREAM:
                return RequestQueue.getInstance().requestExist(url, body);
            case RequestMethod.POST_STRING:
                return RequestQueue.getInstance().requestExist(url, body);
            default:
                return false;

        }


    }

    public static class Builder {
        HttpRequest httpRequest;

        public Builder() {
            this.httpRequest = new HttpRequest();
        }

        public Builder setHasNet(boolean hasNet) {
            httpRequest.hasNet = hasNet;
            return this;
        }


        public Builder setHttpCallback(HttpCallback httpCallback) {
            httpRequest.httpCallback = httpCallback;
            return this;
        }


        public Builder setBody(Object body) {
            httpRequest.body = body;
            return this;
        }


        public Builder setFile(File file) {
            httpRequest.file = file;
            return this;
        }


        public Builder setMediaType(MediaType mediaType) {
            httpRequest.mediaType = mediaType;
            return this;
        }


        public Builder setHttpConfig(HttpConfig httpConfig) {
            httpRequest.httpConfig = httpConfig;
            return this;
        }


        public Builder setHeaders(ArrayMap<String, String> headers) {
            httpRequest.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (CommonUtil.isEmpty(httpRequest.headers))
                httpRequest.headers = new ArrayMap<>();
            httpRequest.headers.put(key, value);
            return this;
        }

        public Builder addParam(String key, Object value) {
            if (CommonUtil.isEmpty(httpRequest.params))
                httpRequest.params = new ArrayMap<>();
            httpRequest.params.put(key, value);
            return this;
        }

        public Builder setParams(ArrayMap<String, Object> params) {
            httpRequest.params = params;
            return this;
        }


        public Builder setWhat(Object what) {
            httpRequest.what = what;
            return this;
        }


        public Builder setRequestMethod(@RequestMethod.METHOD int requestMethod) {
            httpRequest.requestMethod = requestMethod;
            return this;
        }


        public Builder url(String url) {
            httpRequest.url = url;
            return this;
        }

        public Builder isMock(boolean mock) {
            if (httpRequest.httpConfig == null) httpRequest.httpConfig = new HttpConfig();
            httpRequest.httpConfig.mock = mock;
            return this;
        }

        public Builder needProgress(boolean needProgress) {
            if (httpRequest.httpConfig == null) httpRequest.httpConfig = new HttpConfig();
            httpRequest.httpConfig.needProgress = needProgress;
            return this;
        }

        public Builder isGZip(boolean gZip) {
            if (httpRequest.httpConfig == null) httpRequest.httpConfig = new HttpConfig();
            httpRequest.httpConfig.gZip = gZip;
            return this;
        }

        public Builder setCacheDirector(String path) {
            if (httpRequest.httpConfig == null) httpRequest.httpConfig = new HttpConfig();
            httpRequest.httpConfig.cacheDirectory = path;
            return this;
        }

        public Builder setMaxCacheAge(int maxCacheAge) {
            httpRequest.maxCacheAge = maxCacheAge;
            return this;
        }

        public Builder setNeedCache(boolean needCache) {
            httpRequest.needCache = needCache;
            return this;
        }

        public HttpRequest build() {
            return httpRequest;
        }
    }
}
