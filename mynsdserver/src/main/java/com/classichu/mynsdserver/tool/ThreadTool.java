package com.classichu.mynsdserver.tool;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by louisgeek on 2016/7/12.
 */
public class ThreadTool {
    public static final void runOnUiThread(Runnable runnable) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        //通过查看Thread类的当前线程
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }
}
