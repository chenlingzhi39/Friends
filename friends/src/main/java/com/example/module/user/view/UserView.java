package com.example.module.user.view;

/**
 * Created by Administrator on 2016/3/5.
 */
public interface UserView {
    void showCircleDialog();
    void dismissCircleDialog();
    void toastSendFailure(int code, String msg);
    void toastSendSuccess();
}
