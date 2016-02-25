package com.example.comment.view;

import com.example.bean.Comment;

/**
 * Created by Administrator on 2016/2/25.
 */
public interface SendCommentView {
    void showDialog();
    void dismissDialog();
    void toastSendFailure();
    void toastSendSuccess();
    void refresh(Comment comment);
}
