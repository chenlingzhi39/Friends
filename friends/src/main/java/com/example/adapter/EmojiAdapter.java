package com.example.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.Emoji;
import com.example.administrator.myapplication.R;
import com.example.widget.recyclerview.SimpleHolder;

/**
 * Created by Administrator on 2015/12/23.
 */
public class EmojiAdapter extends RecyclerView.Adapter<SimpleHolder>{
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickLitener)
    {
        this.mOnItemClickListener = mOnItemClickLitener;
    }
    @Override
    public void onBindViewHolder(final SimpleHolder holder, int position) {
        ((TextView) holder.itemView).setText(Emoji.EMOJI_NAME[position]);
     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int pos = holder.getLayoutPosition();
             mOnItemClickListener.onItemClick(holder.itemView, pos);
         }
     });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = holder.getLayoutPosition();
                mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                return false;
            }
        });
    }

    @Override
    public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoji, null));
    }


    @Override
    public int getItemCount() {

        return Emoji.COUNT;
    }
}
