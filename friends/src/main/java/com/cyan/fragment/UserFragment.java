package com.cyan.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.community.R;
import com.cyan.widget.recyclerview.EasyRecyclerView;

/**
 * Created by Administrator on 2016/3/30.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_list)
public class UserFragment extends BaseFragment{
    private EasyRecyclerView userList;
    public static UserFragment newInstance(){
        UserFragment userFragment=new UserFragment();
        return userFragment;
    }
    @Override
    protected void initView(View fragmentRootView) {
       userList=(EasyRecyclerView)fragmentRootView.findViewById(R.id.list);
        getUsers();
    }
    private void getUsers(){
        userList.setRefreshEnabled(false);
        userList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
