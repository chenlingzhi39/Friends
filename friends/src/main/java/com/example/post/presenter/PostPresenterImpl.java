package com.example.post.presenter;

import android.content.Context;

import com.example.bean.Post;
import com.example.post.model.PostModel;
import com.example.post.model.PostModelImpl;
import com.example.post.view.LoadPostView;
import com.example.post.view.SendPostView;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/2/24.
 */
public class PostPresenterImpl implements PostPresenter, PostModelImpl.LoadPostListener {
    private LoadPostView loadPostView;
    private SendPostView sendPostView;
    private PostModel postModel;
    private Context context;
    private boolean first=true;
    private PostModelImpl.SendFileListener fileListener;
    private PostModelImpl.SendPostListener sendPostListener;

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
        if (first)
        loadPostView.showProgress();
        postModel.loadPost(context,query, this);

    }



    @Override
    public void sendPost(File file, final Post post) {
        sendPostListener = new PostModelImpl.SendPostListener() {
            @Override
            public void onSuccess(Post post) {
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
        };
        fileListener = new PostModelImpl.SendFileListener() {
            @Override
            public void onSuccess(BmobFile imageFile) {
                post.setImage(imageFile);
                sendPostView.toastUploadSuccess();
                sendPostView.showCircleDialog();
                postModel.sendPost(context,post,sendPostListener);
            }

            @Override
            public void onFailure(int i, String s) {
                sendPostView.toastUploadFailure();
            }

            @Override
            public void onProgress(Integer progress) {
                sendPostView.updateHorizonalDialog(progress);
            }

            @Override
            public void onFinish() {
                sendPostView.dismissHorizonalDialog();

            }
        };
        if(file!=null){sendPostView.showHorizonalDialog();
           postModel.sendFile(context,file,fileListener);}
         else { sendPostView.showCircleDialog();
            postModel.sendPost(context,post,sendPostListener);}


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
}
