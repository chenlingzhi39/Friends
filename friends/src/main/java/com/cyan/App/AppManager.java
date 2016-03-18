package com.cyan.App;

import android.util.SparseArray;

/**
 * Created by Administrator on 2016/3/14.
 */
public class AppManager {
    private static volatile AppManager instance;

    private static SparseArray<String> mActivityOrder;

    private AppManager() {
    }

    public static AppManager getAppManager() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }
    public Class getLastNavActivity() throws ClassNotFoundException {
        if (mActivityOrder == null || mActivityOrder.size() == 0 || mActivityOrder
                .size() == 1) {
            return null;
        }
        return Class.forName(mActivityOrder.get(mActivityOrder.size() - 2));
    }
    public Class getCurrentActivity() throws ClassNotFoundException {
        if (mActivityOrder == null || mActivityOrder.size() == 0) {
            return null;
        }
        return Class.forName(mActivityOrder.get(mActivityOrder.size() - 1));
    }
    public void orderActivity(String className, boolean backPress) {
        if (mActivityOrder == null) {
            mActivityOrder = new SparseArray<>();
        }
        final int index = mActivityOrder.indexOfValue(className);
        if (!backPress) {
            if (index == -1) {
                mActivityOrder.put(mActivityOrder.size(), className);
            } else {
                for (int i = index + 1; i < mActivityOrder.size()-1; i++) {
                    mActivityOrder.remove(i);
                }

            }
        } else {
            mActivityOrder.remove(mActivityOrder.size()-1);
        }


    }

}
