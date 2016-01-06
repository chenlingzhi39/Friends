package com.example.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.listener.OnItemClickListener;
import com.example.listener.OnItemLongClickListener;
import com.example.widget.recyclerview.SparseArrayViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
public abstract class BaseAdapter<T,VH extends SparseArrayViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * click listener
     */
    protected OnItemClickListener mOnItemClickListener;
    /**
     * long click listener
     */
    protected OnItemLongClickListener mOnItemLongClickListener;
    /**
     * data
     */
    protected List<T> mList;
    protected int mLayoutId;
    /**
     * @param list the datas to attach the adapter
     */
    public BaseAdapter(List<T> list,int layoutId) {
        mList = list;
        mLayoutId=layoutId;
    }

    /**
     * get a item by index
     *
     * @param position
     * @return
     */
    protected T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * set a long click listener
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    /**
     * set a click listener
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * inflate a view by viewgroup ,id ,etc
     *
     * @param viewGroup
     * @param layoutId
     * @return
     */
    protected View inflateItemView(ViewGroup viewGroup, int layoutId) {
        return inflateItemView(viewGroup, layoutId, false);
    }

    /**
     * inflate a view by viewgroup ,id ,etc
     *
     * @param viewGroup
     * @param layoutId
     * @param attach
     * @return
     */
    protected View inflateItemView(ViewGroup viewGroup, int layoutId, boolean attach) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, attach);
    }


    /**
     * a final function to avoid you override
     * use template design pattern
     *
     * @param vh
     * @param position
     */
    @Override
    public final void onBindViewHolder(VH vh, int position) {
        final T item = getItem(position);
        bindDataToItemView(vh, item);
        bindItemViewClickListener(vh, item);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        return (VH)new SparseArrayViewHolder(inflateItemView(parent,mLayoutId));
    }

    /**
     * bind data to itemview
     *
     * @param vh   viewholder
     * @param item item
     */
    protected abstract void bindDataToItemView(VH vh, T item);


    /**
     * bind click listner to itemview
     *
     * @param vh   viewholder
     * @param item item
     */
    protected final void bindItemViewClickListener(VH vh, final T item) {
        if (mOnItemClickListener != null) {
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(view, item);
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onLongClick(v, item);
                    return true;
                }
            });
        }
    }
    public abstract void convert(VH helper, T item);


}
