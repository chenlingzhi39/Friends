package com.example.util;
import android.content.Context;

import com.example.administrator.myapplication.R;

/**
 * Created by Administrator on 2015/12/31.
 */
public class LayoutUtils {
    /**
     * dp conversion to pix
     *
     * @param context The context
     * @param dp The value you want to conversion
     * @return value in pix
     */
    public static int dp2pix(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * pix conversion to dp
     *
     * @param context The context
     * @param pix The value you want to conversion
     * @return value in dp
     */
    public static float pix2dp(Context context, int pix) {
        return pix / context.getResources().getDisplayMetrics().density;
    }

    /**
     * sp conversion to pix
     *
     * @param sp The value you want to conversion
     * @return value in pix
     */
    public static int sp2pix(Context context, float sp) {
        return (int) (sp * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    /**
     * pix conversion to sp
     *
     * @param pix The value you want to conversion
     * @return value in sp
     */
    public static float pix2sp(Context context, float pix) {
        return pix / context.getResources().getDisplayMetrics().scaledDensity;
    }


    /**
     * Is this device is table.
     * For sw600dp is true
     *
     * @param context the context
     * @return true for table
     */
    public static boolean isTable(Context context) {
        return context.getResources().getBoolean(R.bool.is_table);
    }
}
