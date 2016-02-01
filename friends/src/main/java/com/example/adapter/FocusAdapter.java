package com.example.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.example.bean.Focus;

/**
 * Created by Administrator on 2016/2/1.
 */
public class FocusAdapter extends RecyclerArrayAdapter<Focus>{
    public FocusAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new FocusViewHolder(parent);
    }
}
