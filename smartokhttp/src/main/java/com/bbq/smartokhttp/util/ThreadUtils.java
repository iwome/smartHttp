package com.bbq.smartokhttp.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by bangbang.qiu on 2019/10/23.
 */
public class ThreadUtils {
    private final ExecutorService mExecutor;
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    private ThreadUtils() {
        this.mExecutor = Executors.newFixedThreadPool(3, new ThreadUtils.AppThreadFactory());
    }

    public static final ThreadUtils getInstance() {
        return ThreadUtils.ThreadHolder.et;
    }

    public ExecutorService getExecutor() {
        return this.mExecutor;
    }

    public void execute(final OperationTask task) {
        this.mExecutor.execute(new Runnable() {
            public void run() {
                try {
                    task.execute();
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        });
    }

    public void execute(final OperationTask task, final TaskCallback callback) {
        this.mExecutor.execute(new Runnable() {
            public void run() {
                try {
                    final Object obj = task.execute();
                    ThreadUtils.mHandler.post(new Runnable() {
                        public void run() {
                            callback.callback(obj);
                        }
                    });
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        });
    }

    public void executeByUI(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void executeByUIADelay(Runnable runnable, long delayMillis) {
        mHandler.postDelayed(runnable, delayMillis);
    }

    public void cancelByUI(Runnable runnable) {
        mHandler.removeCallbacks(runnable);
    }

    private class AppThreadFactory implements ThreadFactory {
        private AppThreadFactory() {
        }

        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("app_thread" + thread.getId());
            thread.setPriority(5);
            return thread;
        }
    }

    private static class ThreadHolder {
        private static final ThreadUtils et = new ThreadUtils();

        private ThreadHolder() {
        }
    }

    public interface TaskCallback {
        void callback(Object var1);
    }

    public interface OperationTask {
        Object execute();
    }

}
