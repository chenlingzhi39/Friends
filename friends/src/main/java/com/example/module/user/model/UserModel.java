package com.example.module.user.model;

import android.content.Context;

import com.example.bean.User;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface UserModel {
void signUp(Context context,User user,SaveListener saveListener);
    void login(Context context,User user,SaveListener saveListener);

}
