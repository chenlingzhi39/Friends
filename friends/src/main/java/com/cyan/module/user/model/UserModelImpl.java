package com.cyan.module.user.model;

import android.content.Context;

import com.cyan.bean.User;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/3/4.
 */
public class UserModelImpl implements UserModel<User> {
    @Override
    public void signUp(Context context, User user, SaveListener saveListener) {
        user.signUp(context,saveListener);
    }

    @Override
    public void login(Context context, User user, SaveListener saveListener) {
        user.login(context,saveListener);

    }

    @Override
    public void update(Context context, User user, UpdateListener updateListener) {
        user.update(context,updateListener);
    }

    @Override
    public void getUser(Context context, BmobQuery<User> query, FindListener<User> findListener) {
        query.findObjects(context,findListener);
    }
}
