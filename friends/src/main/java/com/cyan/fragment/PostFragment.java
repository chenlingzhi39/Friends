package com.cyan.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.Post;
import com.cyan.community.R;
import com.cyan.module.post.presenter.PostPresenter;
import com.cyan.widget.recyclerview.EasyRecyclerView;

/**
 * Created by Administrator on 2016/3/30.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_list)
public class PostFragment extends BaseFragment{
    PostPresenter<Post> postPresenter;
    private EasyRecyclerView postList;
    public static PostFragment newInstance(){
        PostFragment postFragment=new PostFragment();
        return postFragment;
    }
    @Override
    protected void initView(View fragmentRootView) {
       postList=(EasyRecyclerView)fragmentRootView.findViewById(R.id.list);
        getPosts();
    }
    private void getPosts(){
        postList.setRefreshEnabled(false);
        postList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
