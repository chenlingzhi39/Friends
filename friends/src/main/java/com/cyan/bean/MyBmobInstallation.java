package com.cyan.bean;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by Administrator on 2016/1/13.
 */
public class MyBmobInstallation extends BmobInstallation{
    String uid;
    public MyBmobInstallation(Context context) {
        super(context);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
