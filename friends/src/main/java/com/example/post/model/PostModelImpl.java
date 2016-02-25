package com.example.post.model;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapter.PostAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.example.listener.OnItemClickListener;
import com.example.ui.ContentActivity;
import com.example.ui.MyApplication;
import com.example.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/2/24.
 */
public class PostModelImpl implements PostModel{
    @Override
    public void loadPost(Context context,int size,Integer head, Integer footer, final LoadPostListener loadPostListener) {
        BmobQuery<Post> query = new BmobQuery<>();
        if (size > 0)
            if(head!=null)
            query.addWhereGreaterThan("id", head);
            else
            query.addWhereLessThan("id",footer);
        //query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setLimit(10);
        query.order("-id");
        query.include("author");
        query.findObjects(context, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
               loadPostListener.onSuccess(list);
            }

            @Override
            public void onError(int i, String s) {
               loadPostListener.onError(i,s);

            }

            @Override
            public void onFinish() {
                loadPostListener.onFinish();
            }
        });

    }

    @Override
    public void sendPost(Context context,Post post,SendPostListener sendPostListener) {

    }

    @Override
    public void sendFile(Context context, File file, SendFileListener sendFileListener) {

    }

    public interface LoadPostListener {
        void onSuccess(List<Post> list);
        void onError(int i,String s);
        void onFinish();
    }
    public interface SendPostListener {
        void onSuccess(List<Post> list);
        void onError(int i,String s);
        void onFinish();
    }
    public interface SendFileListener {
        void onSuccess(List<Post> list);
        void onError(int i,String s);
        void onFinish();
    }
}
