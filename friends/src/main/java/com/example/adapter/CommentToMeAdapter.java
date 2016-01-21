package com.example.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.example.bean.CommentToMe;

/**
 * Created by Administrator on 2016/1/20.
 */
public class CommentToMeAdapter extends RecyclerArrayAdapter<CommentToMe>{
    public CommentToMeAdapter(Context context) {
        super(context);
    }
    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentToMeViewHolder(parent);
    }

}
