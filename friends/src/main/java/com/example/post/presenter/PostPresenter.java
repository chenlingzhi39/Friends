package com.example.post.presenter;

import android.content.Context;

import com.example.bean.Post;
import com.example.post.model.PostModelImpl;

import java.io.File;

/**
 * Created by Administrator on 2016/2/24.
 */
public interface PostPresenter {
    void loadPost(Context context,int size,Integer head,Integer footer);
    void sendPost(Context context,Post post);
    void sendFile(Context context,File file);
}
