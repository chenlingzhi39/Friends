package com.example.post.view;

import com.example.bean.Post;

/**
 * Created by Administrator on 2016/2/25.
 */
public interface SendPostView {
    void showCircleDialog();
    void showHorizonalDialog();
    void updateHorizonalDialog(Integer i);
    void dismissHorizonalDialog();
    void dismissCircleDialog();
    void toastUploadFailure();
    void toastUploadSuccess();
    void toastSendFailure();
    void toastSendSuccess();
    void refresh(Post post);
}
