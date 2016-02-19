package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;

import com.example.adapter.PostAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.example.listener.OnItemClickListener;
import com.example.refreshlayout.RefreshLayout;
import com.example.util.SimpleHandler;
import com.example.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/1/15.
 */
public class CollectionActivity extends BaseActivity implements RefreshLayout.OnRefreshListener{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.list)
    EasyRecyclerView collectionList;
    private ArrayList<Post> posts;
    private SparseArray<Boolean> is_praised;
    private SparseArray<Boolean> is_collected;
    private PostAdapter postAdapter;
    private int select_index;
    int j=0,k;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("收藏");
        collectionList.setRefreshEnabled(false);
        collectionList.setLayoutManager(new LinearLayoutManager(this));
        collectionList.showProgress();
        posts=new ArrayList<>();
        is_praised=new SparseArray<>();
        is_collected=new SparseArray<>();
         initQuery();

    }

    @Override
    public void onFooterRefresh() {

    }

    @Override
    public void onHeaderRefresh() {

    }

    public void initQuery() {
        BmobQuery<Post> query=new BmobQuery<>();
        query.order("-id");
        query.include("author");
        query.setLimit(10);
        List<BmobQuery<Post>> queries = new ArrayList<BmobQuery<Post>>();
        k=j;
        if(MyApplication.getInstance().getCurrentUser().getCollect_post_id().size()<=10)
        {j=MyApplication.getInstance().getCurrentUser().getCollect_post_id().size();
        collectionList.setRefreshEnabled(false);}
        else if(posts.size()==0)
        {j=10;
        collectionList.setHeaderEnabled(false);
        collectionList.setFooterEnabled(true);
        collectionList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        }
        else if(posts.size()!=0)
            if(MyApplication.getInstance().getCurrentUser().getCollect_post_id().size()-j>10)
                j+=10;
           else
            { j+=MyApplication.getInstance().getCurrentUser().getCollect_post_id().size()-j;
              collectionList.setRefreshEnabled(false);}
        for(int i=k;i< j;i++) {
            BmobQuery<Post> eq = new BmobQuery<Post>();
            eq.addWhereEqualTo("objectId", MyApplication.getInstance().getCurrentUser().getCollect_post_id().get(i));
            queries.add(eq);
        }

        query.or(queries);
        query.findObjects(this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list.size() > 0) {
                    flush(list);
                    if(posts.size()==0){
                    posts = (ArrayList<Post>) list;
                    postAdapter = new PostAdapter(posts, is_praised, is_collected, CollectionActivity.this, false);
                    collectionList.setAdapter(postAdapter);}else{
                        posts.addAll(list);
                        postAdapter.notifyDataSetChanged();
                    }

                    postAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onClick(View view, Object item) {
                            Intent intent = new Intent(CollectionActivity.this, ContentActivity.class);
                            select_index = (Integer) item;
                            intent.putExtra("post", posts.get((Integer) item));
                            intent.putExtra("isPraised", is_praised.get(posts.get((Integer) item).getId()));
                            intent.putExtra("isCollected", is_collected.get(posts.get((Integer) item).getId()));
                            startActivityForResult(intent, 0);

                        }
                    });
                }
                collectionList.showRecycler();
                Log.i("list_size",list.size()+"");
            }

            @Override
            public void onError(int i, String s) {
                Log.i("onerror",  "error");
            }
        });
    }
    public void flush(final List<Post> posts) {
        if (MyApplication.getInstance().getCurrentUser() != null) {
            SimpleHandler.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    setPraise(posts);
                    setCollection(posts);
                }
            });
        }
    }
    public void setPraise(List<Post> list) {

        for (final Post post : list) {
            if (MyApplication.getInstance().getCurrentUser() != null) {
                BmobQuery<Post> query = new BmobQuery<Post>();


                String[] praise_user_id = {MyApplication.getInstance().getCurrentUser().getObjectId()};

                query.addWhereEqualTo("objectId", post.getObjectId());
                query.addWhereContainsAll("praise_user_id", Arrays.asList(praise_user_id));

                query.findObjects(getApplicationContext(), new FindListener<Post>() {
                    @Override
                    public void onSuccess(List<Post> list) {
                        if (list.size() > 0) {
                            is_praised.append(post.getId(), true);
                            Log.i("objectid", post.getId() + "");
                            postAdapter.notifyDataSetChanged();
                        } else {

                            is_praised.append(post.getId(), false);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });


            }

            //DatabaseUtil.getInstance(getApplicationContext()).insertPraise(post);
        }

    }


    public void setCollection(List<Post> list) {
        List<String> collect_post_id = new ArrayList<String>();
        collect_post_id = MyApplication.getInstance().getCurrentUser().getCollect_post_id();
        if (collect_post_id != null) {
            for (final Post post : list) {
                if (collect_post_id.contains(post.getObjectId()))
                    is_collected.append(post.getId(), true);
                else
                    is_collected.append(post.getId(), false);
                postAdapter.notifyDataSetChanged();
            }
        }

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
