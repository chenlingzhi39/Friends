package com.example.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapter.CommentToMeAdapter;
import com.example.adapter.RecyclerArrayAdapter;
import com.example.administrator.myapplication.R;
import com.example.ui.ContentActivity;
import com.example.ui.ItemHelper;
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<CommentToMe>(commentToMeDao,commentToMes,commentToMeAdapter).mCallback);
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
