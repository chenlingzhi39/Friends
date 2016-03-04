package com.example.module.comment.presenter;

import android.content.Context;
import android.util.Log;

import com.example.bean.Comment;
import com.example.module.comment.model.CommentModel;
import com.example.module.comment.model.CommentModelImpl;
import com.example.module.comment.view.LoadCommentView;
import com.example.module.comment.view.SendCommentView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/2/25.
 */
public class CommentPresenterImpl implements CommentPresenter<Comment>{
    private Context context;
    private CommentModel commentModel;
    private LoadCommentView loadCommentView;
    private SendCommentView sendCommentView;
    private boolean first=true;

    public CommentPresenterImpl(Context context, LoadCommentView loadCommentView,SendCommentView sendCommentView) {
        this.context = context;
        this.loadCommentView = loadCommentView;
        this.sendCommentView = sendCommentView;
        commentModel=new CommentModelImpl();
    }



    @Override
    public void loadComment(BmobQuery query) {

        commentModel.loadComment(context, query, new FindListener<Comment>() {
            @Override
            public void onStart() {
                if (first) loadCommentView.showProgress();
            }

            @Override
            public void onSuccess(List<Comment> list) {
                loadCommentView.addComments(list);
                if (first && list.size() == 0 || list.size() < 10) {
                    Log.i("show", "empty");
                    loadCommentView.showEmpty();
                }
                if (first) {
                    loadCommentView.showRecycler();
                    first = false;
                }

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
        });
    }

    @Override
    public void sendComment(final Comment comment) {
        commentModel.sendComment(context, comment, new SaveListener() {
        @Override
        public void onStart() {
            sendCommentView.showDialog();
        }

        @Override
        public void onSuccess() {
            sendCommentView.toastSendSuccess();
            sendCommentView.refresh(comment);
        }

        @Override
        public void onFailure(int i, String s) {
            sendCommentView.toastSendFailure();
        }

        @Override
        public void onFinish() {
            sendCommentView.dismissDialog();
        }});
    }


}
