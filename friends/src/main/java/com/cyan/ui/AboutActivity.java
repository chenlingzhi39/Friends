package com.cyan.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.community.R;
import com.cyan.fragment.AboutFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/2/29.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_about,
        toolbarTitle = R.string.about
)
public class AboutActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.fragment)
    FrameLayout fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, AboutFragment.newInstance());
        transaction.commit();
    }

}
