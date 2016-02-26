package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.adapter.FansAdapter;
import com.example.adapter.RecyclerArrayAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Focus;
import com.example.bean.User;
import com.example.focus.presenter.FocusPresenter;
import com.example.focus.presenter.FocusPresenterImpl;
import com.example.focus.view.FocusView;
import com.example.widget.recyclerview.DividerItemDecoration;
import com.example.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by Administrator on 2016/2/2.
 */
public class FansActivity extends BaseActivity implements RecyclerArrayAdapter.OnLoadMoreListener ,FocusView{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.list)
    EasyRecyclerView focusList;
    private User user;
    private FansAdapter focusAdapter;
    private ArrayList<Focus> focuses;
    private int fans_num;
    private BmobQuery<Focus> query;
    private FocusPresenter focusPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("粉丝");
        focusList.setRefreshEnabled(false);
        focusList.setLayoutManager(new LinearLayoutManager(this));
        focusList.showProgress();
        focusList.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL_LIST));
        focusPresenter=new FocusPresenterImpl(this,this);
        init();
    }

    @Override
    public void showProgress() {
        focusList.showProgress();
    }

    @Override
    public void showRecycler() {
        focusList.showRecycler();
    }

    @Override
    public void showError() {
        focusAdapter.setError(R.id.error);
        focusList.showError();
    }

    @Override
    public void showEmpty() {
        focusList.showEmpty();
    }

    @Override
    public void stopLoadmore() {
        if(focuses.size()>=10)
        {focusAdapter.setNoMore(R.layout.view_nomore);
            focusAdapter.stopMore();}
    }

    @Override
    public void addFocus(List<Focus> list) {
        if(list.size()>0)
            if(focuses.size()==0){focuses=(ArrayList<Focus>) list;
                focusAdapter.addAll(list);
                focusList.setAdapter(focusAdapter);
            }else{
                focuses.addAll(list);
                focusAdapter.addAll(list);
            }

    }

    public void init() {
        user = (User) getIntent().getExtras().get("user");
        fans_num = getIntent().getIntExtra("fans_num", 0);
        focusAdapter = new FansAdapter(this);
        if (fans_num > 10) {
            focusAdapter.setMore(R.layout.view_more, this);
            focusAdapter.setNoMore(R.layout.view_nomore);
        }
        focusAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(FansActivity.this, UserInfoActivity.class);
                intent.putExtra("user", focuses.get(position).getUser());
                startActivity(intent);
            }
        });
        focuses = new ArrayList<>();
        if (fans_num > 0)
            queryFocus();
        else focusList.showEmpty();
    }

    @Override
    public void onLoadMore() {
        queryFocus();
    }

    private void queryFocus() {
        query = new BmobQuery<Focus>();
        if (focuses.size() > 0)
            query.addWhereLessThan("id", focuses.get(focuses.size() - 1).getId());
        query.addWhereEqualTo("focus_user", new BmobPointer(user));
        focusPresenter.loadFocus(query);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("tag", "onNewINtent执行了");
        setIntent(intent);
        getIntent().putExtras(intent);
        init();
    }
}
