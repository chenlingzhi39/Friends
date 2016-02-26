package com.example.focus.model;

import android.content.Context;

import com.example.bean.Focus;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/26.
 */
public interface FocusModel {
    void loadFocus(Context context, BmobQuery<Focus> query, FocusModelImpl.LoadFocusListener loadFocusListener);
    void sendFocus(Context context, Focus focus, FocusModelImpl.SendFocusListener sendFocusListener);
}
