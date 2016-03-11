package com.cyan.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.cyan.bean.Focus;

/**
 * Created by Administrator on 2016/2/1.
 */
public class FansAdapter extends RecyclerArrayAdapter<Focus>{
    public FansAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new FansViewHolder(parent);
    }
}
