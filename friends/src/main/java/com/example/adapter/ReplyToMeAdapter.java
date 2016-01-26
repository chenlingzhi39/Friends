package com.example.adapter;

import android.content.Context;
import android.view.ViewGroup;

import de.greenrobot.daoexample.ReplyToMe;


/**
 * Created by Administrator on 2016/1/26.
 */
public class ReplyToMeAdapter extends RecyclerArrayAdapter<ReplyToMe>{
    public ReplyToMeAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReplyToMeViewHolder(parent);
    }
}
