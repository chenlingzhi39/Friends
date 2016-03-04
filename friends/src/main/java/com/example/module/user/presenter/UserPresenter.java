package com.example.module.user.presenter;

import com.example.bean.User;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface UserPresenter<T> {
    void login(T t);
    void signUp(T t);
    void update(T t);
}
