package com.example.module.focus.presenter;

import com.example.bean.Focus;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/26.
 */
public interface FocusPresenter {
    void loadFocus(BmobQuery<Focus> query);
    void sendFocus(Focus focus);
}
