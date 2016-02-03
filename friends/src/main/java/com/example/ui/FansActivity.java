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
import com.example.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/2/2.
 */
public class FansActivity extends BaseActivity implements RecyclerArrayAdapter.OnLoadMoreListener{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.list)
    EasyRecyclerView focusList;
    private User user;
    private FansAdapter focusAdapter;
    private ArrayList<Focus> focuses;
    private int fans_num;
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
        user=(User)getIntent().getExtras().get("user");
        fans_num=getIntent().getIntExtra("fans_num",0);
        focusAdapter=new FansAdapter(this);
        if(fans_num>10)
        {focusAdapter.setMore(R.layout.view_more, this);
            focusAdapter.setNoMore(R.layout.view_nomore);
        }
        focusAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(FansActivity.this, UserInfoActivity.class);
                intent.putExtra("user",focuses.get(position).getFocusUser());
                startActivity(intent);
            }
        });
        focuses=new ArrayList<>();
        if(fans_num>0)
        queryFocus();
        else focusList.showEmpty();
    }

    @Override
    public void onLoadMore() {
        queryFocus();
    }
    private void queryFocus(){
        BmobQuery<Focus> query=new BmobQuery<Focus>();
        if(focuses.size()>0)
            query.addWhereLessThan("id",focuses.get(focuses.size()-1).getId());
        query.addWhereEqualTo("focus_user",new BmobPointer(user));
        query.setLimit(10);
        query.order("-id");
        query.include("user,focus_user");
        query.findObjects(this, new FindListener<Focus>() {
            @Override
            public void onSuccess(List list) {
                if (list.size() != 0) {
                    if (focuses.size() == 0) {
                        focuses=(ArrayList<Focus>)list;
                        focusAdapter.addAll(list);
                        focusList.setAdapter(focusAdapter);
                    } else {
                        focuses.addAll((ArrayList<Focus>) list);
                        focusAdapter.addAll(list);

                    }

                }
                Log.i("focus","success");
                focusList.showRecycler();
            }

            @Override
            public void onError(int i, String s) {
               Log.i("focus",s);
            }
        });
    }
}