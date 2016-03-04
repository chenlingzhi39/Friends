package com.example.module.comment.model;

import android.content.Context;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/25.
 */
public interface CommentModel<T>{
    void loadComment(Context context,BmobQuery<T> query,CommentModelImpl.LoadCommentListener loadCommentListener);
    void sendComment(Context context,T t,CommentModelImpl.SendCommentListener sendCommentListener);
}
