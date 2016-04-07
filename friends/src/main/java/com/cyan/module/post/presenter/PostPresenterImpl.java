package com.cyan.module.post.presenter;

import android.content.Context;
import android.util.Log;

import com.cyan.bean.Post;
import com.cyan.module.post.model.PostModel;
import com.cyan.module.post.model.PostModelImpl;
import com.cyan.module.post.view.LoadPostView;
import com.cyan.module.post.view.SendPostView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/2/24.
 */
public class PostPresenterImpl implements PostPresenter<Post> {
    private LoadPostView loadPostView;
    private SendPostView sendPostView;
    private PostModel<Post> postModel;
    private Context context;
    private boolean first=true;
    private Subscription subscription;
    public PostPresenterImpl(Context context, LoadPostView loadPostView,Subscription subscription) {
        this.context = context;
        postModel = new PostModelImpl();
        this.loadPostView = loadPostView;
        this.subscription=subscription;
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
                Log.i("error",s);
            }

            @Override
            public void onFinish() {
                loadPostView.stopRefresh();
                loadPostView.stopLoadMore();
            }

            @Override
            public void onSuccess(final List<Post> list) {
                if (first && list.size() == 0)
                {loadPostView.showEmpty();
                    return;
                }
                subscription= Observable.create(new Observable.OnSubscribe<List<Post>>() {
                    @Override
                    public void call(Subscriber<? super List<Post>> subscriber) {
                        loadPostView.addPosts(list);
                        subscriber.onNext(list);
                    }}).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Post>>() {
                    @Override
                    public void call(List<Post> posts) {
                        loadPostView.notifyDataSetChanged(first);
                            first=false;
                    }
                });
                loadPostView.showRecycler();
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

    public void setFirst(boolean first) {
        this.first = first;
    }
}
