package com.example.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by Administrator on 2016/1/4.
 */
public class SparseArrayViewHolder extends RecyclerView.ViewHolder{
    private final SparseArray<View> views;
    public SparseArrayViewHolder(View itemView) {
        super(itemView);
        views=new SparseArray<View>();
    }

}
