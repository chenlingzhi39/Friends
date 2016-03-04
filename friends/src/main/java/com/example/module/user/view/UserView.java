package com.example.module.user.view;

import com.example.bean.Post;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface UserView {
    void showCircleDialog();
    void dismissCircleDialog();
    void toastSendFailure(int code, String msg);
    void toastSendSuccess();
}
