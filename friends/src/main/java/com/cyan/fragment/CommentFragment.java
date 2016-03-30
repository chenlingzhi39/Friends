package com.cyan.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cyan.adapter.CommentToMeAdapter;
import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.app.MyApplication;
import com.cyan.community.R;
import com.cyan.ui.ContentActivity;
import com.cyan.widget.recyclerview.DividerItemDecoration;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import de.greenrobot.daoexample.CommentToMe;
import de.greenrobot.daoexample.CommentToMeDao;

/**
 * Created by Administrator on 2016/1/14.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_list)
public class CommentFragment extends BaseFragment {
    private CommentToMeAdapter commentToMeAdapter;
    private EasyRecyclerView commentList;
    public static CommentFragment newInstance(){
     CommentFragment commentFragment=new CommentFragment();
    return commentFragment;
}

    @Override
    protected void initView(View fragmentRootView) {
        commentList=(EasyRecyclerView)fragmentRootView.findViewById(R.id.comment_list);
        getCommentToMes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    private void getCommentToMes(){
        commentList.setRefreshEnabled(false);
        commentList.showProgress();
        CommentToMeDao commentToMeDao =  MyApplication.getInstance().getDaoSession().getCommentToMeDao();
        String textColumn = CommentToMeDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        String where= CommentToMeDao.Properties.Yourid.columnName+" = '" + MyApplication.getInstance().getCurrentUser().getObjectId() + "'";
        Cursor cursor =  MyApplication.getInstance().getDb().query(commentToMeDao.getTablename(),commentToMeDao.getAllColumns(),where, null, null, null, orderBy);
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
       /* ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<CommentToMe>(commentToMeDao,commentToMeAdapter));
        itemTouchHelper.attachToRecyclerView(commentList.getRecyclerView());*/
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
