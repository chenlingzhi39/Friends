package com.cyan.ui;

import android.os.Bundle;

import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.community.R;

/**
 * Created by Administrator on 2016/3/30.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_search
)
public class SearchActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
}
