package com.cyan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.cyan.adapter.FansAdapter;
import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.Focus;
import com.cyan.bean.User;
import com.cyan.community.R;
import com.cyan.module.focus.presenter.FocusPresenter;
import com.cyan.module.focus.presenter.FocusPresenterImpl;
import com.cyan.module.focus.view.FocusView;
import com.cyan.widget.recyclerview.DividerItemDecoration;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by Administrator on 2016/2/2.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_list,
        toolbarTitle = R.string.fans
)
public class FansActivity extends BaseActivity implements RecyclerArrayAdapter.OnLoadMoreListener ,FocusView{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.list)
    EasyRecyclerView focusList;
    private User user;
    private FansAdapter focusAdapter;
    private int fans_num;
    private BmobQuery<Focus> query;
    private FocusPresenter focusPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

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
        if(fans_num>10)
            focusAdapter.pauseMore();
        else focusList.showError();
    }

    @Override
    public void showEmpty() {
        focusList.showEmpty();
    }

    @Override
    public void stopLoadmore() {

    }

    @Override
    public void addFocus(List<Focus> list) {
        if(list.size()>0)
            if(focusAdapter.getData().size()==0){
                focusAdapter.addAll(list);
                focusList.setAdapter(focusAdapter);
            }else{
                focusAdapter.addAll(list);
            }

    }

    public void init() {
        user = (User) getIntent().getExtras().get("user");
        fans_num = getIntent().getIntExtra("fans_num", 0);
        focusAdapter = new FansAdapter(this);
        if (fans_num > 10) {
            focusAdapter.setMore(R.layout.view_more, this);
            focusAdapter.setNoMore(R.layout.view_nomore).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    focusAdapter.resumeMore();
                }
            });
            focusAdapter.setError(R.layout.view_error);
        }
        focusAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(FansActivity.this, UserInfoActivity.class);
                intent.putExtra("user", focusAdapter.getData().get(position).getUser());
                startActivity(intent);
            }
        });
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
        if (focusAdapter.getData().size() > 0)
            query.addWhereLessThan("id", focusAdapter.getData().get(focusAdapter.getData().size() - 1).getId());
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
