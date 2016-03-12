package com.cyan.adapter;

import android.content.Context;
import android.view.ViewGroup;

import de.greenrobot.daoexample.Draft;

/**
 * Created by Administrator on 2016/3/11.
 */
public class DraftAdapter extends RecyclerArrayAdapter<Draft>{
    public DraftAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new DraftViewHolder(parent);
    }
}
