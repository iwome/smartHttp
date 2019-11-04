package com.bbq.smartokhttp.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by bangbang.qiu on 2019/11/1.
 */
public class ExSingleThread {

    private final ExecutorService mExecutor;
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    private ExSingleThread() {
        this.mExecutor = Executors.newSingleThreadExecutor(new ExSingleThread.AppThreadFactory());
    }

    public static ExSingleThread getInstance() {
        return ExSingleThread.ThreadHolder.est;
    }

    public void execute(Runnable runnable) {
        this.mExecutor.execute(runnable);
    }

    public void submit(Callable task) {
        this.mExecutor.submit(task);
    }

    public void execute(final ThreadUtils.OperationTask task) {
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

    public void execute(final ThreadUtils.OperationTask task, final ThreadUtils.TaskCallback callback) {
        this.mExecutor.execute(new Runnable() {
            public void run() {
                try {
                    final Object obj = task.execute();
                    ExSingleThread.mHandler.post(new Runnable() {
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

    private class AppThreadFactory implements ThreadFactory {
        private AppThreadFactory() {
        }

        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("app_thread" + thread.getId());
            thread.setPriority(1);
            return thread;
        }
    }

    private static class ThreadHolder {
        private static final ExSingleThread est = new ExSingleThread();

        private ThreadHolder() {
        }
    }
}
