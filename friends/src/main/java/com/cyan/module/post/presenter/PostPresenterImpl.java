package com.cyan.module.post.presenter;

import android.content.Context;

import com.cyan.bean.Post;
import com.cyan.module.post.model.PostModel;
import com.cyan.module.post.model.PostModelImpl;
import com.cyan.module.post.view.LoadPostView;
import com.cyan.module.post.view.SendPostView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/2/24.
 */
public class PostPresenterImpl implements PostPresenter<Post> {
    private LoadPostView loadPostView;
    private SendPostView sendPostView;
    private PostModel postModel;
    private Context context;
    private boolean first=true;
    public PostPresenterImpl(Context context, LoadPostView loadPostView) {
        this.context = context;
        postModel = new PostModelImpl();
        this.loadPostView = loadPostView;
    }
    public PostPresenterImpl(Context context,SendPostView sendPostView) {
        this.context = context;
        postModel = new PostModelImpl();
        this.sendPostView = sendPostView;
    }
    @Override
    public void loadPost(BmobQuery query) {

        postModel.loadPost(context, query, new FindListener<Post>() {
            @Override
            public void onStart() {
                loadPostView.showProgress(first);
            }

            @Override
            public void onError(int i, String s) {
                loadPostView.showError();
            }

            @Override
            public void onFinish() {
                loadPostView.stopLoadmore();
                loadPostView.stopRefresh();
            }

            @Override
            public void onSuccess(List<Post> list) {
                loadPostView.addPosts(list);
                if (first && list.size() == 0)
                    loadPostView.showEmpty();
                if (first)
                {loadPostView.showRecycler();
                    first=false;}
            }
        });

    }



    @Override
    public void sendPost( final Post post) {

            postModel.sendPost(context, post, new SaveListener() {
                @Override
                public void onStart() {
                    sendPostView.showCircleDialog();
                }

                @Override
                public void onSuccess() {
                    sendPostView.refresh(post);
                    sendPostView.toastSendSuccess();
                }

                @Override
                public void onFailure(int i, String s) {
                    sendPostView.toastSendFailure();
                }

                @Override
                public void onFinish() {
                    sendPostView.dismissCircleDialog();
                }

            });}





}
