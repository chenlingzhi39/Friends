package com.cyan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.cyan.app.MyApplication;
import com.cyan.adapter.PostAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.Post;
import com.cyan.community.R;
import com.cyan.listener.OnItemClickListener;
import com.cyan.module.post.presenter.PostPresenter;
import com.cyan.module.post.presenter.PostPresenterImpl;
import com.cyan.module.post.view.LoadPostView;
import com.cyan.widget.refreshlayout.RefreshLayout;
import com.cyan.util.PraiseUtils;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/1/15.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_list,
        toolbarTitle = R.string.collection
)
public class CollectionActivity extends RefreshActivity implements LoadPostView,RefreshLayout.OnRefreshListener{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.list)
    EasyRecyclerView collectionList;
    private int j=0;
    private PostPresenter<Post> postPresenter;
    private BmobQuery<Post> query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        collectionList.setLayoutManager(new LinearLayoutManager(this));
        collectionList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        collectionList.showProgress();
        collectionList.setRefreshListener(this);
        postPresenter=new PostPresenterImpl(this,this,subscription);
        collectionList.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQuery();
            }
        });
        if(MyApplication.getInstance().getCurrentUser().getCollect_post_id()!=null)
        initQuery();
        else collectionList.showEmpty();
    }

    @Override
    public void addPosts(List<Post> list) {
        if (list.size()>0)
        {PraiseUtils.flush(this, is_praised, is_collected, list);
            if(posts.size()==0){
                posts = (ArrayList<Post>) list;
                postAdapter = new PostAdapter(posts, is_praised, is_collected, CollectionActivity.this);
                postAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onClick(View view, Object item) {
                        Intent intent = new Intent(CollectionActivity.this, ContentActivity.class);
                        intent.putExtra("post", posts.get((Integer) item));
                        intent.putExtra("isPraised", is_praised.get(posts.get((Integer) item).getId()));
                        intent.putExtra("isCollected", is_collected.get(posts.get((Integer) item).getId()));
                        startActivityForResult(intent, 0);
                    }
                });
            }else{
                PraiseUtils.flush(this, is_praised, is_collected, list);
                posts.addAll((ArrayList<Post>) list);
            }}
    }

    @Override
    public void notifyDataSetChanged(boolean b) {
        if(b)
            collectionList.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmpty() {
        collectionList.showEmpty();
    }

    @Override
    public void showError() {
        collectionList.showError();
    }

    @Override
    public void showProgress(Boolean b) {
        if(b)collectionList.showProgress();
    }

    @Override
    public void showRecycler() {
        collectionList.showRecycler();
    }

    @Override
    public void stopLoadmore() {
        collectionList.setFooterRefreshing(false);
    }

    @Override
    public void stopRefresh() {
        collectionList.setHeaderRefreshing(false);
    }


    @Override
    public void onFooterRefresh() {
        initQuery();
    }

    @Override
    public void onHeaderRefresh() {

    }

    public void initQuery() {
        query=new BmobQuery<>();
        List<BmobQuery<Post>> queries = new ArrayList<BmobQuery<Post>>();
        Log.i("collection_size",MyApplication.getInstance().getCurrentUser().getCollect_post_id().size()+"");
        if(MyApplication.getInstance().getCurrentUser().getCollect_post_id().size() <= 10)
        {
        collectionList.setRefreshEnabled(false);
        j=MyApplication.getInstance().getCurrentUser().getCollect_post_id().size();
        }
        else {if(posts.size()==0)

        {collectionList.setHeaderEnabled(false);
        collectionList.setFooterEnabled(true);
            j=10;
        }
        else if(MyApplication.getInstance().getCurrentUser().getCollect_post_id().size()-posts.size()<=10)
            {collectionList.setRefreshEnabled(false);
            j=MyApplication.getInstance().getCurrentUser().getCollect_post_id().size()-posts.size();
            }
        else j=j+10;}
        for(int i=posts.size();i< posts.size()+j ;i++) {
            BmobQuery<Post> eq = new BmobQuery<Post>();
            eq.addWhereEqualTo("objectId", MyApplication.getInstance().getCurrentUser().getCollect_post_id().get(MyApplication.getInstance().getCurrentUser().getCollect_post_id().size()-i-1));
            queries.add(eq);
        }
        query.or(queries);
        postPresenter.loadPost(query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    }
