package com.example.module.user.model;

import android.content.Context;

import com.example.bean.User;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/3/4.
 */
public class UserModelImpl implements UserModel {
    @Override
    public void signUp(Context context, User user, SaveListener saveListener) {
        user.signUp(context,saveListener);
    }

    @Override
    public void login(Context context, User user, SaveListener saveListener) {
        user.login(context,saveListener);

    }
}
