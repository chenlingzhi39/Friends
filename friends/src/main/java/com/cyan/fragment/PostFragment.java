package com.cyan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;

import com.cyan.adapter.PostAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.Post;
import com.cyan.community.R;
import com.cyan.listener.OnItemClickListener;
import com.cyan.module.post.presenter.PostPresenter;
import com.cyan.module.post.presenter.PostPresenterImpl;
import com.cyan.module.post.view.LoadPostView;
import com.cyan.ui.ContentActivity;
import com.cyan.util.PraiseUtils;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/3/30.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_list)
public class PostFragment extends BaseFragment implements LoadPostView{
    PostPresenter<Post> postPresenter;
    private EasyRecyclerView postList;
    Subscription subscription;
    ArrayList<Post> posts;
    PostAdapter postAdapter;
    SparseArray<Boolean> is_praised;
    SparseArray<Boolean> is_collected;
    BmobQuery<Post> query;
    public static PostFragment newInstance(String key){
        PostFragment postFragment=new PostFragment();
        Bundle bundle=new Bundle();
        bundle.putString("key",key);
        postFragment.setArguments(bundle);
        return postFragment;
    }
    @Override
    protected void initView(View fragmentRootView) {
       postList=(EasyRecyclerView)fragmentRootView.findViewById(R.id.list);
        postList.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPresenter.loadPost(query);
            }
        });
        postPresenter=new PostPresenterImpl(getActivity(),this,subscription);
        posts = new ArrayList<>();
        is_praised = new SparseArray<>();
        is_collected = new SparseArray<>();
        getPosts();
       mReSearchObservable.subscribe(new Action1<String>() {
           @Override
           public void call(String s) {
               posts.clear();
               is_praised.clear();
               is_collected.clear();
               query = new BmobQuery<>();
               query.addWhereContains("content", s);
               postPresenter = new PostPresenterImpl(getActivity(), PostFragment.this, subscription);
               postPresenter.loadPost(query);
           }
       });
    }
    private void getPosts(){
        postList.setRefreshEnabled(false);
        postList.setLayoutManager(new LinearLayoutManager(getActivity()));
        query = new BmobQuery<>();
        query.addWhereContains("content",getArguments().getString("key"));
        postPresenter.loadPost(query);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subscription!=null)
        subscription.unsubscribe();
    }

    @Override
    public void addPosts(List<Post> list) {


    PraiseUtils.flush(getActivity(), is_praised, is_collected, list);
    if (list.size() > 0)
            if (posts.size() == 0) {
        posts = (ArrayList<Post>) list;
        postAdapter = new PostAdapter(posts, is_praised, is_collected,getActivity());
        postAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, Object item) {
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.putExtra("post", posts.get((Integer) item));
                intent.putExtra("isPraised", is_praised.get(posts.get((Integer) item).getId()));
                intent.putExtra("isCollected", is_collected.get(posts.get((Integer) item).getId()));
                startActivityForResult(intent, 0);
            }
        });

    } else {
        if (posts.get(0).getId() < list.get(0).getId()) {
            posts.addAll(0, list);
        } else {
            posts.addAll(list);
        }

    }}
    @Override
    public void notifyDataSetChanged(boolean b) {
        if (b) postList.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmpty() {
       postList.showEmpty();
    }

    @Override
    public void showError() {
       postList.showError();
    }

    @Override
    public void showProgress(Boolean b) {
        if(b)postList.showProgress();
        else postList.setHeaderRefreshing(true);
    }

    @Override
    public void showRecycler() {
      postList.showRecycler();
    }

    @Override
    public void stopLoadmore() {
        postList.setFooterRefreshing(false);
    }

    @Override
    public void stopRefresh() {
        postList.setHeaderRefreshing(false);
    }
}
