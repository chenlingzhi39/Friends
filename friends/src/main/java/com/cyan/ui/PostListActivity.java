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
import com.cyan.bean.User;
import com.cyan.listener.OnItemClickListener;
import com.cyan.refreshlayout.RefreshLayout;
import com.cyan.util.SimpleHandler;
import com.cyan.widget.recyclerview.EasyRecyclerView;

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
public class PostListActivity extends BaseActivity implements RefreshLayout.OnRefreshListener {
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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("动态");
        postList.setRefreshListener(this);
        postList.setLayoutManager(new LinearLayoutManager(this));
        postList.showProgress();


        init();

    }

    @Override
    public void onFooterRefresh() {
      initQuery();
    }

    @Override
    public void onHeaderRefresh() {

    }

    public void init() {
        user = (User) getIntent().getExtras().get("user");
        post_num = getIntent().getIntExtra("post_num", 0);
        if (post_num > 10) {
            postList.setHeaderEnabled(false);
            postList.setFooterEnabled(true);
           postList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                   android.R.color.holo_green_light,
                   android.R.color.holo_orange_light,
                   android.R.color.holo_red_light);

        } else
            postList.setRefreshEnabled(false);

        posts = new ArrayList<>();
        is_praised = new SparseArray<>();
        is_collected = new SparseArray<>();
        if (post_num > 0)
            initQuery();
        else postList.showEmpty();
    }

    public void initQuery() {
        BmobQuery<Post> query = new BmobQuery<>();
        if(posts.size()>0)
            query.addWhereLessThan("id",posts.get(posts.size() - 1).getId());
        query.addWhereEqualTo("author", user);
        query.order("-id");
        query.setLimit(10);
        query.include("author");
        query.findObjects(this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list.size() > 0) {
                    if (posts.size() == 0) {
                        flush(list);
                        posts = (ArrayList<Post>) list;
                        postAdapter = new PostAdapter(posts, is_praised, is_collected, PostListActivity.this);
                        postList.setAdapter(postAdapter);
                    } else {
                        posts.addAll(list);
                        postAdapter.notifyDataSetChanged();
                    }

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
                } else {
                    postList.setEnabled(false);
                }

                postList.showRecycler();
                Log.i("list_size", list.size() + "");
            }

            @Override
            public void onError(int i, String s) {
                Log.i("onerror", "error");
                postList.showError();
            }

            @Override
            public void onFinish() {
                postList.setFooterRefreshing(false);
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
                    }
                }

                break;
            default:
                break;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("tag", "onNewINtent执行了");
        setIntent(intent);
        getIntent().putExtras(intent);
        user = (User) getIntent().getExtras().get("user");

        init();
    }
}
