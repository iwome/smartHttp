package com.bbq.smartokhttp.response;

import androidx.collection.ArrayMap;

import com.bbq.smartokhttp.util.CommonUtil;

/**
 * Created by bangbang.qiu on 2019/10/31.
 */
public class HttpResponse {
    private String url;
    private byte[] responseBody;
    private int code;
    private ArrayMap<String,String> responseHeader;
    private String contentType;
    public Object what;

    public HttpResponse(String url, int code, byte[] responseBody, String contentType, Object what) {
        this.url = url;
        this.code = code;
        this.what = what;
        this.contentType = contentType;
        if (!CommonUtil.isEmpty(responseBody)) {
            this.responseBody = responseBody;
        }

    }

    public String url() {
        return this.url;
    }

    public byte[] getResponseBody() {
        if (CommonUtil.isEmpty(this.responseBody)) {
            this.responseBody = new byte[0];
        }

        return this.responseBody;
    }

    public int getCode() {
        return this.code;
    }

    public ArrayMap<String, String> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(ArrayMap<String, String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String getContentType() {
        return this.contentType;
    }
}
