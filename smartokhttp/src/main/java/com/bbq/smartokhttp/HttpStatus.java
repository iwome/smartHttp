package com.bbq.smartokhttp;

/**
 * Created by bangbang.qiu on 2019/10/31.
 */
public class HttpStatus {
    public static final int STATUS_OK = 200;
    public static final int STATUS_NO_CONTENT = 204;
    public static final int STATUS_PARTIAL_CONTENT = 206;
    public static final int STATUS_NOT_MODIFIED = 304; // 服务器发现数据notModify,返回空数据和304，本地取缓存
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_REQUEST_TIMEOUT = 408;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    public static final int STATUS_SERVICE_UNAVAILABLE = 503;
    public static final int STATUS_NOT_LOCAL_CACHE = 504; // 强制使用本地缓存，但本地无缓存可用，返回504
    public static final int STATUS_NO = -1;
    public static final int STATUS_CANCEL = -1000;
    public static final int STATUS_NO_NET = -100;
    public static final int STATUS_CACHE = 800;
    public static final int STATUS_CERTIFICATE_FAILED = 801;

    public HttpStatus() {
    }
}
