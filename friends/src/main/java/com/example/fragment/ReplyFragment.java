package com.example.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import com.example.adapter.RecyclerArrayAdapter;
import com.example.adapter.ReplyToMeAdapter;
import com.example.administrator.myapplication.R;
import com.example.ui.ContentActivity;
import com.example.ui.MyApplication;
import com.example.widget.recyclerview.DividerItemDecoration;
import com.example.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;

import de.greenrobot.daoexample.CommentToMeDao;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.ReplyToMe;
import de.greenrobot.daoexample.ReplyToMeDao;

/**
 * Created by Administrator on 2016/1/14.
 */
public class ReplyFragment extends Fragment{
    private ArrayList<ReplyToMe> replyToMes;
    private ReplyToMeAdapter replyToMeAdapter;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ReplyToMeDao replyToMeDao;
    private Cursor cursor;
    public static ReplyFragment newInstance(){
        ReplyFragment replyFragment=new ReplyFragment();
        return replyFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        EasyRecyclerView commentList=(EasyRecyclerView)view.findViewById(R.id.comment_list);
        replyToMes=new ArrayList<>();
        getReplyToMes();
        //commentToMes = DatabaseUtil.getInstance(getActivity()).queryCommentToMe(MyApplication.getInstance().getCurrentUser().getObjectId());
        commentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentList.setRefreshEnabled(false);
        commentList.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        commentList.showProgress();

        if (replyToMes.size() > 0) {
            replyToMeAdapter = new ReplyToMeAdapter(getActivity());
            replyToMeAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent=new Intent(getActivity(),ContentActivity.class);
                    intent.putExtra("type","reply");
                    intent.putExtra("object_id",replyToMes.get(position).getComment_id());
                    intent.putExtra("parent_id",replyToMes.get(position).getPostid());
                    startActivityForResult(intent,0);
                }
            });
            replyToMeAdapter.addAll(replyToMes);
            commentList.setAdapter(replyToMeAdapter);
            commentList.showRecycler();
        } else commentList.showEmpty();
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
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
                replyToMeDao.delete(replyToMes.get(position));
                replyToMes.remove(position);
                replyToMeAdapter.remove(position);
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
            }};
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(commentList.getRecyclerView());
        Log.i("oncreate", "oncreate");
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    private void getReplyToMes(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "messages-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
         replyToMeDao = daoSession.getReplyToMeDao();
        String textColumn = CommentToMeDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        String where= CommentToMeDao.Properties.Yourid.columnName+" = '" + MyApplication.getInstance().getCurrentUser().getObjectId() + "'";
        cursor = db.query(replyToMeDao.getTablename(),replyToMeDao.getAllColumns(),where, null, null, null, orderBy);
        if (cursor == null) {
            return;
        }
        replyToMes = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ReplyToMe replyToMe = new ReplyToMe();
            replyToMeDao.readEntity(cursor,replyToMe,0);
            replyToMes.add(replyToMe);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getReplyToMes();
    }
}
