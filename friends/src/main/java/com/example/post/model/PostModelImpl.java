package com.example.post.model;

import android.content.Context;
import android.util.Log;

import com.example.bean.Post;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2016/2/24.
 */
public class PostModelImpl implements PostModel{
    @Override
    public void loadPost(Context context,BmobQuery<Post> query, final LoadPostListener loadPostListener) {

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
               Log.i("error",s);
            }

            @Override
            public void onFinish() {
                loadPostListener.onFinish();
            }
        });

    }

    @Override
    public void sendPost(Context context,final Post post, final SendPostListener sendPostListener) {
     post.save(context, new SaveListener() {
         @Override
         public void onSuccess() {
             sendPostListener.onSuccess(post);
         }

         @Override
         public void onFailure(int i, String s) {
            sendPostListener.onFailure(i,s);
         }

         @Override
         public void onFinish() {
           sendPostListener.onFinish();
         }
     });
    }

    @Override
    public void sendFile(Context context, File file, final SendFileListener sendFileListener) {
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.upload(context, new UploadFileListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
              sendFileListener.onSuccess(bmobFile);
            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub
                Log.i("life", "uploadMovoieFile-->onProgress:" + arg0);
             sendFileListener.onProgress(arg0);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
            sendFileListener.onFailure(arg0,arg1);
            }

            @Override
            public void onFinish() {
                sendFileListener.onFinish();
            }
        });

    }

    public interface LoadPostListener {
        void onSuccess(List<Post> list);
        void onError(int i,String s);
        void onFinish();
    }
    public interface SendPostListener {
        void onSuccess(Post post);
        void onFailure(int i,String s);
        void onFinish();
    }
    public interface SendFileListener {
        void onSuccess(BmobFile imageFile);
        void onFailure(int i,String s);
        void onProgress(Integer progress);
        void onFinish();
    }
}
