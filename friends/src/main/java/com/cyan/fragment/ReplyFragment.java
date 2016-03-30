package com.cyan.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.adapter.ReplyToMeAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.app.MyApplication;
import com.cyan.community.R;
import com.cyan.ui.ContentActivity;
import com.cyan.widget.recyclerview.DividerItemDecoration;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import de.greenrobot.daoexample.CommentToMeDao;
import de.greenrobot.daoexample.ReplyToMe;
import de.greenrobot.daoexample.ReplyToMeDao;

/**
 * Created by Administrator on 2016/1/14.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_list)
public class ReplyFragment extends BaseFragment{
    private ReplyToMeAdapter replyToMeAdapter;
    private EasyRecyclerView commentList;
    public static ReplyFragment newInstance(){
        ReplyFragment replyFragment=new ReplyFragment();
        return replyFragment;
    }

    @Override
    protected void initView(View fragmentRootView) {
        commentList=(EasyRecyclerView)fragmentRootView.findViewById(R.id.comment_list);
        getReplyToMes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    private void getReplyToMes(){
        commentList.setRefreshEnabled(false);
        commentList.showProgress();
        ReplyToMeDao replyToMeDao = MyApplication.getInstance().getDaoSession().getReplyToMeDao();
        String textColumn = CommentToMeDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        String where= CommentToMeDao.Properties.Yourid.columnName+" = '" + MyApplication.getInstance().getCurrentUser().getObjectId() + "'";
        Cursor cursor = MyApplication.getInstance().getDb().query(replyToMeDao.getTablename(), replyToMeDao.getAllColumns(), where, null, null, null, orderBy);
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
      /*  ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<ReplyToMe>(replyToMeDao,replyToMeAdapter));
        itemTouchHelper.attachToRecyclerView(commentList.getRecyclerView());*/
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
