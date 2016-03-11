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

import com.cyan.adapter.CommentToMeAdapter;
import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.community.R;
import com.cyan.ui.ContentActivity;
import com.cyan.ui.ItemHelper;
import com.cyan.ui.MyApplication;
import com.cyan.widget.recyclerview.DividerItemDecoration;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import de.greenrobot.daoexample.CommentToMe;
import de.greenrobot.daoexample.CommentToMeDao;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;

/**
 * Created by Administrator on 2016/1/14.
 */
public class CommentFragment extends Fragment {
    private CommentToMeAdapter commentToMeAdapter;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private CommentToMeDao commentToMeDao;
    private Cursor cursor;
    private EasyRecyclerView commentList;
    public static CommentFragment newInstance(){
     CommentFragment commentFragment=new CommentFragment();
    return commentFragment;
}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
       commentList=(EasyRecyclerView)view.findViewById(R.id.comment_list);

        getCommentToMes();
         //commentToMes = DatabaseUtil.getInstance(getActivity()).queryCommentToMe(MyApplication.getInstance().getCurrentUser().getObjectId());

       Log.i("oncreate","oncreate");
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    private void getCommentToMes(){
        commentList.setRefreshEnabled(false);
        commentList.showProgress();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "messages-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        commentToMeDao = daoSession.getCommentToMeDao();
        String textColumn = CommentToMeDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        String where= CommentToMeDao.Properties.Yourid.columnName+" = '" + MyApplication.getInstance().getCurrentUser().getObjectId() + "'";
        cursor = db.query(commentToMeDao.getTablename(),commentToMeDao.getAllColumns(),where, null, null, null, orderBy);
        if (cursor == null) {
            commentList.showEmpty();
            return;
        }
        commentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentList.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        commentToMeAdapter=new CommentToMeAdapter(getActivity());
        commentToMeAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent=new Intent(getActivity(),ContentActivity.class);
                intent.putExtra("type","reply");
                intent.putExtra("object_id", commentToMeAdapter.getData().get(position).getComment_id());
                intent.putExtra("parent_id", commentToMeAdapter.getData().get(position).getPostid());
                startActivityForResult(intent,0);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<CommentToMe>(commentToMeDao,commentToMeAdapter));
        itemTouchHelper.attachToRecyclerView(commentList.getRecyclerView());
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            CommentToMe commentToMe = new CommentToMe();
            commentToMeDao.readEntity(cursor,commentToMe,0);
            commentToMeAdapter.addAll(commentToMe);
        }
        if (cursor != null) {
            commentList.showRecycler();
            commentList.setAdapter(commentToMeAdapter);
            cursor.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getCommentToMes();
    }
}
