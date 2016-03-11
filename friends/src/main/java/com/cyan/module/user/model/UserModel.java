package com.cyan.module.user.model;

import android.content.Context;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface UserModel<T> {
void signUp(Context context,T t,SaveListener saveListener);
    void login(Context context,T t,SaveListener saveListener);
void update(Context context,T t,UpdateListener updateListener);
}
