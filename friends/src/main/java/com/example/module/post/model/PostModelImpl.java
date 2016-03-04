package com.example.module.post.model;

import android.content.Context;

import com.example.bean.Post;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/2/24.
 */
public class PostModelImpl implements PostModel<Post>{
    @Override
    public void loadPost(Context context,BmobQuery query, FindListener findListener) {
        //query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setLimit(10);
        query.order("-id");
        query.include("author");
        query.findObjects(context, findListener);

    }

    @Override
    public void sendPost(Context context,final Post post,SaveListener saveListener) {
     post.save(context,saveListener);
    }



    public interface LoadPostListener {
        void onStart();
        void onSuccess(List<Post> list);
        void onError(int i,String s);
        void onFinish();
    }
    public interface SendPostListener {
        void onStart();
        void onSuccess(Post post);
        void onFailure(int i,String s);
        void onFinish();
    }


}
