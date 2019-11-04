package com.bbq.smartHttp;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bbq.smartokhttp.BitmapCallback;
import com.bbq.smartokhttp.request.HttpRequest;
import com.bbq.smartokhttp.request.RequestMethod;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private TextView tvResult;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = findViewById(R.id.tv_result);
        imageView = findViewById(R.id.iv);
    }


    public void requestPost(View view) {
        smartHttpTest();
    }

    HttpRequest request = null;

    public void smartHttpTest() {
        request = new HttpRequest.Builder()
                .url("https://img-blog.csdn.net/20180820101853555?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM2Mzc1OTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70")
                .setRequestMethod(RequestMethod.GET)
//                .setHasNet(isNetworkConnect())
                .needProgress(true)
                .setNeedCache(true)
                .setCacheDirector(new File(getExternalCacheDir(), "qbb_cache").getAbsolutePath())
                .setHttpCallback(new BitmapCallback() {
                    @Override
                    public void onRequestStart(Object what) {
                        Toast.makeText(MainActivity.this, "onRequestStart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSucceed(Object what, final Bitmap data) {
                       imageView.setImageBitmap(data);
                    }

                    @Override
                    public void onResponseFinish(Object what) {
                        Toast.makeText(MainActivity.this, "onResponseFinish", Toast.LENGTH_SHORT).show();
                        request.cancel();
                    }

                    @Override
                    public void onFailed(Object what, int code, String errorMsg) {
                        Toast.makeText(MainActivity.this, "onFailed: " + errorMsg + " code: " + code, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(final long progress, final long length, final boolean done) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText(String.format("progress %s length %s done %s", progress / 1024, length / 1024, done));
                            }
                        });
                    }
                }).build().request();

    }

    public boolean isNetworkConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}
