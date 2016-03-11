package com.cyan.module.post.view;

import com.cyan.bean.Post;

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
