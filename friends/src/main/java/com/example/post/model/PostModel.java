package com.example.post.model;

import android.content.Context;

import com.example.bean.Post;

import java.io.File;

/**
 * Created by Administrator on 2016/2/24.
 */
public interface PostModel {
void loadPost(Context context,int size,Integer head,Integer footer,PostModelImpl.LoadPostListener loadPostListener);
void sendPost(Context context,Post post,PostModelImpl.SendPostListener sendPostListener);
void sendFile(Context context,File file,PostModelImpl.SendFileListener sendFileListener);
}
