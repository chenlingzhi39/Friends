package com.cyan.adapter;

import android.content.Context;
import android.view.ViewGroup;

import de.greenrobot.daoexample.QuickSearch;

/**
 * Created by Administrator on 2016/3/30.
 */
public class QuickSearchAdapter extends RecyclerArrayAdapter<QuickSearch>{
    public QuickSearchAdapter(Context context) {
        super(context);
    }
    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new QuickSearchViewHolder(parent);
    }
}
