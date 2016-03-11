package com.cyan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;

import com.cyan.adapter.PostAdapter;
import com.cyan.community.R;
import com.cyan.bean.Post;
import com.cyan.listener.OnItemClickListener;
import com.cyan.module.post.presenter.PostPresenter;
import com.cyan.module.post.presenter.PostPresenterImpl;
import com.cyan.module.post.view.LoadPostView;
import com.cyan.refreshlayout.RefreshLayout;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/1/15.
 */
public class CollectionActivity extends BaseActivity implements LoadPostView,RefreshLayout.OnRefreshListener{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.list)
    EasyRecyclerView collectionList;
    private ArrayList<Post> posts;
    private SparseArray<Boolean> is_praised;
    private SparseArray<Boolean> is_collected;
    private PostAdapter postAdapter;
    private int j=0;
    private PostPresenter postPresenter;
    private BmobQuery<Post> query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("收藏");
        collectionList.setLayoutManager(new LinearLayoutManager(this));
        collectionList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        collectionList.showProgress();
        collectionList.setRefreshListener(this);
        posts=new ArrayList<>();
        is_praised=new SparseArray<>();
        is_collected=new SparseArray<>();
        postPresenter=new PostPresenterImpl(this,this);
        collectionList.getmErrorView().setOnClickListener(new View.OnClickListener() {
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
            if(posts.size()==0){
                posts = (ArrayList<Post>) list;
                postAdapter = new PostAdapter(posts, is_praised, is_collected, CollectionActivity.this);
                PFhelper.flush(this, is_praised, is_collected, list, postAdapter);
                collectionList.setAdapter(postAdapter);
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
                PFhelper.flush(this, is_praised, is_collected, list, postAdapter);
                posts.addAll((ArrayList<Post>) list);
                postAdapter.notifyDataSetChanged();

            }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case  MainActivity.REFRESH_PRAISE:
                boolean praised = data.getBooleanExtra("is_praised", false);

                for(Post post:posts) {
                    if(post.getObjectId().equals(data.getStringExtra("post_id")))
                    { if (praised)
                        post.setPraise_count(post.getPraise_count() + 1);
                    else
                        post.setPraise_count(post.getPraise_count() - 1);
                        is_praised.put(post.getId(), praised);
                        postAdapter.notifyDataSetChanged();
                        setResult(resultCode,data);
                        break;
                    }

                }

                break;
            case MainActivity.REFRESH_COLLECTION:
                boolean collected = data.getBooleanExtra("is_collected", false);
                for(Post post:posts) {
                    if(post.getObjectId().equals(data.getStringExtra("post_id"))) {
                        is_collected.put(post.getId(), collected);
                        postAdapter.notifyDataSetChanged();
                        setResult(resultCode, data);
                        break;}

                }

                break;
            case MainActivity.REFRESH_COMMENT:
                if (data.getExtras() != null)
                {
                    for(Post post:posts) {
                        if(post.getObjectId().equals(data.getStringExtra("post_id"))){
                            post.setComment_count(post.getComment_count() + 1);
                            postAdapter.notifyDataSetChanged();
                            setResult(resultCode,data);
                            break;
                        }
                    } }
                break;
            default:
                break;
        }

    }}
