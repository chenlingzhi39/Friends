package com.cyan.common;

import android.os.Environment;

/**
 * Created by Administrator on 2015/12/24.
 */
public class Constants {
    // 日志输出标志
    public static final String TAG = "tuya";

    // 手机SD卡的路径
    public static final String SDCARD = Environment
            .getExternalStorageDirectory().getPath();

    public static final String PIC_STORE_PATH = SDCARD + "/tuya/";

}