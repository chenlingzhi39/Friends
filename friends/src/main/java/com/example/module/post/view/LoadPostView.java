package com.example.module.post.view;

import com.example.bean.Post;

import java.util.List;

/**
 * Created by Administrator on 2016/2/24.
 */
public interface LoadPostView {
    void showRecycler();
    void showProgress();
    void showError();
    void showEmpty();
    void stopRefresh();
    void stopLoadmore();
    void addPosts(List<Post> list);
;
}
