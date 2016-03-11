package com.cyan.module.comment.presenter;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/25.
 */
public interface CommentPresenter<T> {
    void loadComment(BmobQuery query);
    void sendComment(T t);
}
