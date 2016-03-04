package com.example.module.comment.model;

import android.content.Context;

import com.example.bean.Comment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/2/25.
 */
public class CommentModelImpl implements CommentModel<Comment>{
    @Override
    public void loadComment(Context context,BmobQuery<Comment> query, FindListener<Comment> findListener) {
        query.setLimit(10);
        query.order("-id");
        query.include("author,comment,comment.author");
        query.findObjects(context, findListener);
    }

    @Override
    public void sendComment(Context context, final Comment comment, SaveListener saveListener) {
    comment.save(context, saveListener);
    }


}
