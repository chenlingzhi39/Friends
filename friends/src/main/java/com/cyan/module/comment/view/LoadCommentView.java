package com.cyan.module.comment.view;

import com.cyan.bean.Comment;

import java.util.List;

/**
 * Created by Administrator on 2016/2/25.
 */
public interface LoadCommentView {
    void showRecycler();
    void showProgress();
    void showError();
    void showEmpty();
    void stopRefresh();
    void stopLoadMore();
    void addComments(List<Comment> comment);
}
