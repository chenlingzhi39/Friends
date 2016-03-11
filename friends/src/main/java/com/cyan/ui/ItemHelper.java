package com.cyan.ui;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.cyan.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by Administrator on 2016/3/10.
 */
public class ItemHelper<T> {
    private AbstractDao<T,Long> abstractDao;
    private ArrayList<T> ts;
    private RecyclerArrayAdapter<T> recyclerArrayAdapter;
    public static ItemTouchHelper.Callback mCallback;
    public ItemHelper(final AbstractDao<T, Long> abstractDao,final ArrayList<T> ts,final RecyclerArrayAdapter<T> recyclerArrayAdapter) {
        this.abstractDao = abstractDao;
        this.ts = ts;
        this.recyclerArrayAdapter=recyclerArrayAdapter;
        mCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            /**
             * @param recyclerView
             * @param viewHolder 拖动的ViewHolder
             * @param target 目标位置的ViewHolder
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                abstractDao.delete(ts.get(position));
                ts.remove(position);
               recyclerArrayAdapter.remove(position);
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float)viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
        };
    }

}
