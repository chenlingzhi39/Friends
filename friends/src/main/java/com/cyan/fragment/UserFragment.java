package com.cyan.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cyan.adapter.UserAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.User;
import com.cyan.community.R;
import com.cyan.module.user.presenter.UserPresenter;
import com.cyan.module.user.presenter.UserPresenterImpl;
import com.cyan.module.user.view.GetUserView;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import java.util.List;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/3/30.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_list)
public class UserFragment extends BaseFragment implements GetUserView{
    private EasyRecyclerView userList;
    private UserPresenter<User> userPresenter;
    private BmobQuery<User> query;
    private UserAdapter userAdapter;
    public static UserFragment newInstance(String key){
        UserFragment userFragment=new UserFragment();
        Bundle bundle=new Bundle();
        bundle.putString("key", key);
        userFragment.setArguments(bundle);
        return userFragment;
    }
    @Override
    protected void initView(View fragmentRootView) {
       userList=(EasyRecyclerView)fragmentRootView.findViewById(R.id.list);
        userPresenter=new UserPresenterImpl(getActivity(),this);
        query=new BmobQuery<>();
        query.addWhereContains("username", getArguments().getString("key"));
        getUsers();
    }
    private void getUsers(){
        userList.setRefreshEnabled(false);
        userList.setLayoutManager(new LinearLayoutManager(getActivity()));
        userAdapter=new UserAdapter(getActivity());
        userPresenter.getUser(query);
    }

    @Override
    public void addUsers(List<User> list) {
        if(list.size()>0)
            if(userAdapter.getData().size()==0){
                userAdapter.addAll(list);
                userList.setAdapter(userAdapter);
            }else{
                userAdapter.addAll(list);
            }

    }



    @Override
    public void showEmpty() {
userList.showEmpty();
    }

    @Override
    public void showError() {
userList.showError();
    }

    @Override
    public void showProgress(Boolean b) {
       if(b)userList.showProgress();
       else userList.setHeaderRefreshing(true);
    }

    @Override
    public void showRecycler() {
      userList.showRecycler();
    }

    @Override
    public void stopLoadmore() {
      userList.setFooterRefreshing(false);
    }

    @Override
    public void stopRefresh() {
      userList.setHeaderRefreshing(false);
    }
}
