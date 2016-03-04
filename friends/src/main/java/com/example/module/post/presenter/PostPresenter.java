package com.example.module.post.presenter;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/24.
 */
public interface PostPresenter<T> {
    void loadPost(BmobQuery<T> query);
    void sendPost(T t);
}
