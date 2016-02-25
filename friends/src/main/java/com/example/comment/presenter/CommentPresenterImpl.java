package com.example.comment.presenter;

import android.content.Context;

import com.example.bean.Comment;
import com.example.comment.model.CommentModel;
import com.example.comment.model.CommentModelImpl;
import com.example.comment.view.LoadCommentView;
import com.example.comment.view.SendCommentView;

import java.util.List;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/25.
 */
public class CommentPresenterImpl implements CommentPresenter,CommentModelImpl.LoadCommentListener{
    private Context context;
    private CommentModel commentModel;
    private LoadCommentView loadCommentView;
    private SendCommentView sendCommentView;
    private boolean first=true;

    public CommentPresenterImpl(Context context, LoadCommentView loadCommentView) {
        this.context = context;
        this.loadCommentView = loadCommentView;
        commentModel=new CommentModelImpl();
    }

    public CommentPresenterImpl(Context context, SendCommentView sendCommentView) {
        this.context = context;
        this.sendCommentView = sendCommentView;
        commentModel=new CommentModelImpl();
    }

    @Override
    public void loadComment(BmobQuery query) {
    if(first) loadCommentView.showProgress();
        commentModel.loadComment(context,query,this);
    }

    @Override
    public void sendComment(Comment comment) {

    }

    @Override
    public void onSuccess(List<Comment> list) {
        loadCommentView.addPosts(list);
        if (first)
        {loadCommentView.showRecycler();
            first=false;}
        if (first && list.size() == 0)
            loadCommentView.showEmpty();
    }

    @Override
    public void onError(int i, String s) {
        loadCommentView.showError();
    }

    @Override
    public void onFinish() {
        loadCommentView.stopLoadmore();
        loadCommentView.stopRefresh();
    }
}
