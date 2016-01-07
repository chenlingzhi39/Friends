package com.example.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * M为这个itemView对应的model�?
 * 使用RecyclerArrayAdapter就一定要用这个ViewHolder�?
 * 这个ViewHolder将ItemView与Adapter解�?��??
 * 推荐子类继承第二个构造函数�?�并将子类的构�?�函数设为一个ViewGroup parent�?
 * 然后这个ViewHolder就完全独立�?�adapter在new的时候只�?将parentView传进来�?�View的生成与管理由ViewHolder执行�?
 * 实现setData来实现UI修改。Adapter会在onCreateViewHolder里自动调用�??
 *
 * 在一些特殊情况下，只能在setData里设置监听�??
 * @param <M>
 */
abstract public class BaseViewHolder<M> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public BaseViewHolder(ViewGroup parent, @LayoutRes int res) {
        super(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
    }

    public void setData(M data) {
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) itemView.findViewById(id);
    }

    protected Context getContext(){
        return itemView.getContext();
    }

}