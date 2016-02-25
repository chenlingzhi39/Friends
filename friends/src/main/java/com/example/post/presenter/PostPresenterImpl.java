package com.example.post.presenter;

import android.content.Context;

import com.example.bean.Post;
import com.example.post.model.PostModel;
import com.example.post.model.PostModelImpl;
import com.example.post.view.PostView;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/2/24.
 */
public class PostPresenterImpl implements PostPresenter, PostModelImpl.LoadPostListener {
    private PostView postView;
    private PostModel postModel;
    private Context context;
    private int size = 0;
    private PostModelImpl.SendFileListener fileListener;
    private PostModelImpl.SendPostListener sendPostListener;

    public PostPresenterImpl(Context context, PostView postView) {
        this.context = context;
        postModel = new PostModelImpl();
        this.postView = postView;
    }

    @Override
    public void loadPost(Context context, int size, Integer head, Integer footer) {
        if (size == 0)
            postView.showProgress();
        postModel.loadPost(context, size, head, footer, this);
        this.size = size;
    }

    @Override
    public void sendFile(Context context, File file) {
        fileListener = new PostModelImpl.SendFileListener() {
            @Override
            public void onSuccess(List<Post> list) {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onFinish() {

            }
        };

    }

    @Override
    public void sendPost(Context context, Post post) {
        sendPostListener = new PostModelImpl.SendPostListener() {
            @Override
            public void onSuccess(List<Post> list) {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onFinish() {

            }
        };
    }

    @Override
    public void onError(int i, String s) {
        postView.showError();
    }

    @Override
    public void onFinish() {
        postView.stopLoadmore();
        postView.stopRefresh();
    }

    @Override
    public void onSuccess(List<Post> list) {
        postView.addPosts(list);
        if (size == 0)
            postView.showRecycler();
        if (size == 0 && list.size() == 0)
            postView.showEmpty();
    }
}
