package com.cyan.fragment;


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

import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.adapter.ReplyToMeAdapter;
import com.cyan.community.R;
import com.cyan.ui.ContentActivity;
import com.cyan.ui.ItemHelper;
import com.cyan.ui.MyApplication;
import com.cyan.widget.recyclerview.DividerItemDecoration;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import de.greenrobot.daoexample.CommentToMeDao;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.ReplyToMe;
import de.greenrobot.daoexample.ReplyToMeDao;

/**
 * Created by Administrator on 2016/1/14.
 */
public class ReplyFragment extends Fragment{
    private ReplyToMeAdapter replyToMeAdapter;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ReplyToMeDao replyToMeDao;
    private Cursor cursor;
    private EasyRecyclerView commentList;
    public static ReplyFragment newInstance(){
        ReplyFragment replyFragment=new ReplyFragment();
        return replyFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        commentList=(EasyRecyclerView)view.findViewById(R.id.comment_list);
        getReplyToMes();
        //commentToMes = DatabaseUtil.getInstance(getActivity()).queryCommentToMe(MyApplication.getInstance().getCurrentUser().getObjectId());
        Log.i("oncreate", "oncreate");
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    private void getReplyToMes(){
        commentList.setRefreshEnabled(false);
        commentList.showProgress();
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
            commentList.showEmpty();
            return;
        }
        commentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentList.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
       replyToMeAdapter=new ReplyToMeAdapter(getActivity());
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ReplyToMe replyToMe = new ReplyToMe();
            replyToMeDao.readEntity(cursor,replyToMe,0);
           replyToMeAdapter.addAll(replyToMe);
        }
        replyToMeAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent=new Intent(getActivity(),ContentActivity.class);
                intent.putExtra("type","reply");
                intent.putExtra("object_id", replyToMeAdapter.getData().get(position).getComment_id());
                intent.putExtra("parent_id", replyToMeAdapter.getData().get(position).getPostid());
                startActivityForResult(intent,0);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<ReplyToMe>(replyToMeDao,replyToMeAdapter));
        itemTouchHelper.attachToRecyclerView(commentList.getRecyclerView());
        if (cursor != null) {
            commentList.showRecycler();
            commentList.setAdapter(replyToMeAdapter);
            cursor.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getReplyToMes();
    }
}
