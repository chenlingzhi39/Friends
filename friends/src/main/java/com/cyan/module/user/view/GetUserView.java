package com.cyan.module.user.view;

import com.cyan.bean.User;

import java.util.List;

/**
 * Created by Administrator on 2016/3/31.
 */
public interface GetUserView {
    void showRecycler();
    void showProgress(Boolean b);
    void showError();
    void showEmpty();
    void stopRefresh();
    void stopLoadmore();
    void addUsers(List<User> list);
}
