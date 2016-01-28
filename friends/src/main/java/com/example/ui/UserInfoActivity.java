package com.example.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.administrator.myapplication.R;
import com.example.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/9/25.
 */
public class UserInfoActivity extends AppCompatActivity implements ScrollView.OnScrollChangeListener{


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.scrollView)
    ScrollView scrollView;
    @InjectView(R.id.content)
    RelativeLayout content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("用户");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        toolbar.setPadding(0, Utils.getStatusBarHeight(this), 0, 0);
        scrollView.setOnScrollChangeListener(this);
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if(scrollX>=0&&scrollX<=320)
        toolbar.setBackgroundColor(Color.argb(255*scrollX/320,0,0,0));
    }
}
