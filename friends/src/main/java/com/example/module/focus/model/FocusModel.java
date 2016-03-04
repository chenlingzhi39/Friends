package com.example.module.focus.model;

import android.content.Context;

import com.example.bean.Focus;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/2/26.
 */
public interface FocusModel<T> {
    void loadFocus(Context context, BmobQuery<T> query,FindListener<T> findListener);
}
