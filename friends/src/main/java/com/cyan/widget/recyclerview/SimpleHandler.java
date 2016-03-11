package com.cyan.widget.recyclerview;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 2016/1/7.
 */
public class SimpleHandler extends Handler {

    private static Handler sInstance;

    public static Handler getInstance() {
        if (sInstance == null) {
            sInstance = new SimpleHandler(Looper.getMainLooper());
        }
        return sInstance;
    }

    public SimpleHandler(Looper mainLooper) {
        super(mainLooper);
    }
}
