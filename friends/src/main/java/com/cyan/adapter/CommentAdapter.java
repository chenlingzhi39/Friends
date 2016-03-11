package com.cyan.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.cyan.bean.Comment;

/**
 * Created by Administrator on 2016/1/8.
 */
public class CommentAdapter extends RecyclerArrayAdapter<Comment>{
    public CommentAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(parent);
    }
}
