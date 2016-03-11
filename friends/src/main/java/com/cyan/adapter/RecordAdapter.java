package com.cyan.adapter;

import android.content.Context;
import android.view.ViewGroup;

import de.greenrobot.daoexample.Record;

/**
 * Created by Administrator on 2016/2/15.
 */
public class RecordAdapter extends RecyclerArrayAdapter<Record>{
    public RecordAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordViewHolder(parent);
    }
}
