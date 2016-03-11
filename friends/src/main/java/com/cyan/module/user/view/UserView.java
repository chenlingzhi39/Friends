package com.cyan.module.user.view;

/**
 * Created by Administrator on 2016/3/7.
 */
public interface UserView {
    void showCircleDialog();
    void toastSendSuccess();
    void toastSendFailure(int code, String msg);
    void dismissCircleDialog();
}
