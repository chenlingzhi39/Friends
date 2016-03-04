package com.example.module.comment.model;

import android.content.Context;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/2/25.
 */
public interface CommentModel<T>{
    void loadComment(Context context,BmobQuery<T> query,FindListener<T> findListener);
    void sendComment(Context context,T t,SaveListener saveListener);
}
