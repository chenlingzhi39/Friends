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
import com.example.adapter.RecyclerArrayAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.example.bean.User;
import com.example.listener.OnItemClickListener;
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
 * Created by Administrator on 2016/2/2.
 */
public class PostListActivity extends BaseActivity implements RecyclerArrayAdapter.OnLoadMoreListener{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.list)
    EasyRecyclerView postList;
    private ArrayList<Post> posts;
    private SparseArray<Boolean> is_praised;
    private SparseArray<Boolean> is_collected;
    private PostAdapter postAdapter;
    private User user;
    private int select_index;
    private int post_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.inject(this);
        user=(User)getIntent().getExtras().get("user");
        post_num=getIntent().getIntExtra("post_num",0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("动态");
        postList.setRefreshEnabled(false);
        postList.setLayoutManager(new LinearLayoutManager(this));
        postList.showProgress();
        posts=new ArrayList<>();
        is_praised=new SparseArray<>();
        is_collected=new SparseArray<>();

        initQuery();

    }

    @Override
    public void onLoadMore() {

    }

    public void initQuery() {
        BmobQuery<Post> query=new BmobQuery<>();
        query.addWhereEqualTo("author",user);
        query.order("-id");
        query.include("author");
        query.findObjects(this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list.size() > 0) {
                    flush(list);
                    posts = (ArrayList<Post>) list;
                    postAdapter = new PostAdapter(posts, is_praised, is_collected, PostListActivity.this, false);
                    postList.setAdapter(postAdapter);

                    postAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onClick(View view, Object item) {
                            Intent intent = new Intent(PostListActivity.this, ContentActivity.class);
                            select_index = (Integer) item;
                            intent.putExtra("post", posts.get((Integer) item));
                            intent.putExtra("isPraised", is_praised.get(posts.get((Integer) item).getId()));
                            intent.putExtra("isCollected", is_collected.get(posts.get((Integer) item).getId()));
                            startActivityForResult(intent, 0);

                        }
                    });
                }
                postList.showRecycler();
                Log.i("list_size", list.size() + "");
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
            case MainActivity.REFRESH_PRAISE:
                boolean praised = data.getBooleanExtra("is_praised", false);
                if (praised)
                    posts.get(select_index).setPraise_count(posts.get(select_index).getPraise_count() + 1);
                else
                    posts.get(select_index).setPraise_count(posts.get(select_index).getPraise_count() - 1);
                is_praised.put(posts.get(select_index).getId(), praised);
                postAdapter.notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra("post_id", posts.get(select_index).getId());
                intent.putExtra("is_praised", praised);
                setResult(MainActivity.REFRESH_PRAISE, intent);
                break;
            case MainActivity.REFRESH_COLLECTION:
                boolean collected = data.getBooleanExtra("is_collected", false);
                is_collected.put(posts.get(select_index).getId(), collected);
                postAdapter.notifyDataSetChanged();
                intent = new Intent();
                intent.putExtra("post_id", posts.get(select_index).getId());
                intent.putExtra("is_collected", collected);
                setResult(MainActivity.REFRESH_COLLECTION, intent);
                break;
            case MainActivity.REFRESH_COMMENT:
                posts.get(select_index).setComment_count(posts.get(select_index).getComment_count()+1);
                postAdapter.notifyDataSetChanged();
                intent = new Intent();
                intent.putExtra("post",posts.get(select_index));
                setResult(MainActivity.REFRESH_COMMENT, intent);
                break;
            default:
                break;
        }

    }}
