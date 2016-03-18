package com.cyan.module.post.view;

import com.cyan.bean.Post;

import java.util.List;

/**
 * Created by Administrator on 2016/2/24.
 */
public interface LoadPostView {
    void showRecycler();
    void showProgress(Boolean b);
    void showError();
    void showEmpty();
    void stopRefresh();
    void stopLoadmore();
    void addPosts(List<Post> list);
    void notifyDataSetChanged(boolean b);
}
