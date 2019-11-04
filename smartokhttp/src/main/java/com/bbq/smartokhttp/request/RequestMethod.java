package com.bbq.smartokhttp.request;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by bangbang.qiu on 2019/10/28.
 */
public class RequestMethod {
    public static final int GET = 0x01;
    public static final int POST_STRING = 0x02; //字符串
    public static final int POST_STREAM = 0x03; //数据流
    public static final int POST_FILE = 0x04; //文件
    public static final int POST_FORM = 0x05; //表单
    public static final int POST_MULTIPART = 0x06; //表单 + 文件
    public static final int POST_JSON = 0x07;

    @IntDef({GET, POST_STRING, POST_STREAM, POST_FILE, POST_FORM, POST_MULTIPART, POST_JSON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface METHOD {
    }
}
