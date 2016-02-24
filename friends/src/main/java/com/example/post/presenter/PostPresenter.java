package com.example.post.presenter;

import android.content.Context;

import com.example.post.model.PostModelImpl;

/**
 * Created by Administrator on 2016/2/24.
 */
public interface PostPresenter {
    void loadPost(Context context,int size,Integer head,Integer footer);
}
