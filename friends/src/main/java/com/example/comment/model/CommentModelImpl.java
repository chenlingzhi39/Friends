package com.example.comment.model;

import android.content.Context;

import com.example.bean.Comment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/2/25.
 */
public class CommentModelImpl implements CommentModel{
    @Override
    public void loadComment(Context context,BmobQuery query, final LoadCommentListener loadCommentListener) {
        query.setLimit(10);
        query.order("-id");
        query.include("author,comment,comment.author");
        query.findObjects(context, new FindListener() {
            @Override
            public void onSuccess(List list) {
                loadCommentListener.onSuccess(list);
            }

            @Override
            public void onError(int i, String s) {
               loadCommentListener.onError(i,s);
            }

            @Override
            public void onFinish() {
                loadCommentListener.onFinish();
            }
        });
    }

    @Override
    public void sendComment(Context context, Comment comment, SendCommentListener sendCommentListener) {

    }

    public interface LoadCommentListener {
        void onSuccess(List<Comment> list);
        void onError(int i,String s);
        void onFinish();
    }
    public interface SendCommentListener {
        void onSuccess(Comment comment);
        void onFailure(int i,String s);
        void onFinish();
    }
}
