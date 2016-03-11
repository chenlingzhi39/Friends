package com.cyan.module.post.model;

import android.content.Context;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/2/24.
 */
public interface PostModel<T> {
void loadPost(Context context,BmobQuery<T> query,FindListener<T> findListener);
void sendPost(Context context,T t,SaveListener saveListener);

}
