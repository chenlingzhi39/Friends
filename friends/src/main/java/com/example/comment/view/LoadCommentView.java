package com.example.comment.view;

import com.example.bean.Comment;

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
    void stopLoadmore();
    void addPosts(List<Comment> comment);
}
