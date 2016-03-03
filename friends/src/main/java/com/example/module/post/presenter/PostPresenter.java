package com.example.module.post.presenter;

import com.example.bean.Post;

import java.io.File;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/24.
 */
public interface PostPresenter {
    void loadPost(BmobQuery<Post> query);
    void sendPost(File file,Post post);
}
