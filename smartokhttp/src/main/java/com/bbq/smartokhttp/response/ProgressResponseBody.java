package com.bbq.smartokhttp.response;

import com.bbq.smartokhttp.HttpCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by bangbang.qiu on 2019/11/1.
 */
public class ProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private HttpCallback httpCallback;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, HttpCallback httpCallback) {
        this.responseBody = responseBody;
        this.httpCallback = httpCallback;
    }

    @Override
    public MediaType contentType() {
        return this.responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return this.responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (this.bufferedSource == null) {
            this.bufferedSource = Okio.buffer(this.source(this.responseBody.source()));
        }

        return this.bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                this.totalBytesRead += bytesRead != -1L ? bytesRead : 0L;
                ProgressResponseBody.this.httpCallback.onProgress(this.totalBytesRead, ProgressResponseBody.this.responseBody.contentLength(), bytesRead == -1L);
                return bytesRead;
            }
        };
    }
}
