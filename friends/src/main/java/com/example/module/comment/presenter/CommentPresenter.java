package com.example.module.comment.presenter;

import com.example.bean.Comment;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/25.
 */
public interface CommentPresenter {
    void loadComment(BmobQuery query);
    void sendComment(Comment comment);
}
