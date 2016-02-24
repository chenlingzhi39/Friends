package com.example.post.model;

import android.content.Context;

/**
 * Created by Administrator on 2016/2/24.
 */
public interface PostModel {
void loadPost(Context context,int size,Integer head,Integer footer,PostModelImpl.LoadPostListener loadPostListener);
}
