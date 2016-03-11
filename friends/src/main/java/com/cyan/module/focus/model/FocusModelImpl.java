package com.cyan.module.focus.model;

import android.content.Context;

import com.cyan.bean.Focus;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/2/26.
 */
public class FocusModelImpl implements FocusModel<Focus>{
    @Override
    public void loadFocus(Context context, BmobQuery<Focus> query, FindListener<Focus> findListener ) {
        query.setLimit(10);
        query.order("-id");
        query.include("user,focus_user");
       query.findObjects(context,findListener);
    }




}
