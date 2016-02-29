package com.example.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapter.CommentToMeAdapter;
import com.example.adapter.RecyclerArrayAdapter;
import com.example.administrator.myapplication.R;
import com.example.ui.ContentActivity;
import com.example.ui.MyApplication;
import com.example.widget.recyclerview.DividerItemDecoration;
import com.example.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;

import de.greenrobot.daoexample.CommentToMe;
import de.greenrobot.daoexample.CommentToMeDao;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;

/**
 * Created by Administrator on 2016/1/14.
 */
public class CommentFragment extends Fragment {
    private ArrayList<CommentToMe> commentToMes;
    private CommentToMeAdapter commentToMeAdapter;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private CommentToMeDao commentToMeDao;

    private Cursor cursor;
    public static CommentFragment newInstance(){
     CommentFragment commentFragment=new CommentFragment();
    return commentFragment;
}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        EasyRecyclerView commentList=(EasyRecyclerView)view.findViewById(R.id.comment_list);
        commentToMes=new ArrayList<>();
        getCommentToMes();
         //commentToMes = DatabaseUtil.getInstance(getActivity()).queryCommentToMe(MyApplication.getInstance().getCurrentUser().getObjectId());
        commentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentList.setRefreshEnabled(false);
        commentList.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        commentList.showProgress();
      if (commentToMes.size() > 0) {
            commentToMeAdapter = new CommentToMeAdapter(getActivity());
          commentToMeAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
              @Override
              public void onItemClick(int position) {
                  Intent intent=new Intent(getActivity(),ContentActivity.class);
                  intent.putExtra("type","reply");
                  intent.putExtra("object_id",commentToMes.get(position).getComment_id());
                  intent.putExtra("parent_id",commentToMes.get(position).getPostid());
                  startActivityForResult(intent, 0);
              }
          });
            commentToMeAdapter.addAll(commentToMes);
            commentList.setAdapter(commentToMeAdapter);
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
               commentToMeDao.delete(commentToMes.get(position));
               commentToMes.remove(position);
               commentToMeAdapter.remove(position);
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
       Log.i("oncreate","oncreate");
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    private void getCommentToMes(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "messages-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        commentToMeDao = daoSession.getCommentToMeDao();
        String textColumn = CommentToMeDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        String where= CommentToMeDao.Properties.Yourid.columnName+" = '" +MyApplication.getInstance().getCurrentUser().getObjectId() + "'";
        cursor = db.query(commentToMeDao.getTablename(),commentToMeDao.getAllColumns(),where, null, null, null, orderBy);
        if (cursor == null) {
            return;
        }
        commentToMes = new ArrayList<CommentToMe>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            CommentToMe commentToMe = new CommentToMe();
         commentToMeDao.readEntity(cursor,commentToMe,0);
            commentToMes.add(commentToMe);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getCommentToMes();
    }
}
