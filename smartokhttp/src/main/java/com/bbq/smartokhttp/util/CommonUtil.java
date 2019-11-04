package com.bbq.smartokhttp.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.SparseArray;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okio.Buffer;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created by bangbang.qiu on 2019/10/31.
 */
public class CommonUtil {

    public static boolean isEmpty(String input) {
        return TextUtils.isEmpty(input);
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    public static boolean isEmpty(int obj) {
        return obj == 0;
    }

    public static boolean isEmpty(float obj) {
        return obj == 0.0F;
    }

    public static boolean isEmpty(long obj) {
        return obj == 0L;
    }

    public static boolean isEmpty(SparseArray obj) {
        return obj == null || obj.size() == 0;
    }

    public static boolean isEmpty(CharSequence obj) {
        return TextUtils.isEmpty(obj);
    }

    public static String getFileContentType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(file.getName());
        return type;
    }

    public static Bitmap bytesToBitmap(byte[] b) {
        return b.length == 0 ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static boolean saveFile(byte[] fileData, String filePath, String fileName) {
        File folder = new File(filePath);
        folder.mkdirs();
        File file = new File(folder, fileName);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
            Source source = Okio.source(new ByteArrayInputStream(fileData));
            Sink sink = Okio.sink(file);
            Buffer buffer = new Buffer();
            long length;
            while ((length = source.read(buffer, 1024)) != -1) {
                sink.write(buffer, length);
                sink.flush();
            }
            sink.close();
            source.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static String makeMockData(HttpUrl url, AssetManager am) {
        List<String> paths = url.pathSegments();
        String query = "mock/" + paths.get(paths.size() - 1);
        return readFileFromAssets(query, am);
    }

    private static String readFileFromAssets(String name, AssetManager am) {
        InputStream is = null;

        try {
            is = am.open(name);
        } catch (Exception var4) {
        }

        return inputStreamToStr(is, Charset.defaultCharset());
    }

    private static String inputStreamToStr(InputStream in, Charset encoding) {
        InputStreamReader input = new InputStreamReader(in, encoding);
        StringWriter output = new StringWriter();
        String result = null;

        try {
            char[] buffer = new char[4096];

            int n;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }

            result = output.toString();
        } catch (IOException var20) {
            var20.printStackTrace();
        } finally {
            output.flush();

            try {
                output.close();
            } catch (IOException var19) {
                var19.printStackTrace();
            }

            try {
                input.close();
            } catch (IOException var18) {
                var18.printStackTrace();
            }

        }

        return result;
    }

}
