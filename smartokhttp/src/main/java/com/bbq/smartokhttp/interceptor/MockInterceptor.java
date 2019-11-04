package com.bbq.smartokhttp.interceptor;

import com.bbq.smartokhttp.HttpClient;
import com.bbq.smartokhttp.util.CommonUtil;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * balabala..
 * Created by bangbang.qiu on 2019/11/4.
 */
public class MockInterceptor implements Interceptor {

    public Response intercept(Chain chain) throws IOException {
        Response response;
        Request request = null;
        OkHttpClient client = HttpClient.getHttpClient();

        Call call;
        try {
            request = chain.request();
            // TODO: 2019/11/4 assetManager
            String mockResponse = CommonUtil.makeMockData(request.url(), null);
            if (CommonUtil.isEmpty(mockResponse)) {
                call = client.newCall(request);
                response = call.execute();
            } else {
                response = (new okhttp3.Response.Builder()).request(request).protocol(Protocol.get("http/1.1")).code(200).message("").body(ResponseBody.create(mockResponse, request.body().contentType())).build();
            }
        } catch (Exception var6) {
            call = client.newCall(request);
            response = call.execute();
        }

        return response;
    }
}
