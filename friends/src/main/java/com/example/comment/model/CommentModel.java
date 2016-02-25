package com.example.comment.model;

import android.content.Context;

import com.example.bean.Comment;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/25.
 */
public interface CommentModel {
    void loadComment(Context context,BmobQuery query,CommentModelImpl.LoadCommentListener loadCommentListener);
    void sendComment(Context context,Comment comment,CommentModelImpl.SendCommentListener sendCommentListener);
}
