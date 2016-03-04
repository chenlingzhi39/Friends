package com.example.module.post.view;

import com.example.bean.Post;

/**
 * Created by Administrator on 2016/2/25.
 */
public interface SendPostView {
    void showCircleDialog();
    void dismissCircleDialog();
    void toastSendFailure();
    void toastSendSuccess();
    void refresh(Post post);
}
