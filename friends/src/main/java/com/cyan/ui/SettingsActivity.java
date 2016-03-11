package com.cyan.ui;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.cyan.community.R;
import com.cyan.bean.User;
import com.cyan.fragment.SettingsFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/31.
 */
public class SettingsActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.logout)
    Button logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);
        if (MyApplication.getInstance().getCurrentUser() != null)
            logout.setVisibility(View.VISIBLE);
        else logout.setVisibility(View.GONE);
        setSupportActionBar(toolbar);


      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle("设置");
        FragmentManager fragmentManager = getFragmentManager();

         FragmentTransaction transaction = fragmentManager.beginTransaction();


         transaction.replace(R.id.fragment,  SettingsFragment.newInstance());

         transaction.commit();

    }

    @OnClick(R.id.logout)
    public void logout() {
        User.logOut(this);
        setResult(MainActivity.LOGOUT);
        finish();
    }


}
