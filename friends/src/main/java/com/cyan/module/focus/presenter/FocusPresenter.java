package com.cyan.module.focus.presenter;

import com.cyan.bean.Focus;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/26.
 */
public interface FocusPresenter {
    void loadFocus(BmobQuery<Focus> query);

}
