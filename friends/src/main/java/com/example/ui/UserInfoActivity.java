package com.example.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.administrator.myapplication.R;
import com.example.listener.ScrollViewListener;
import com.example.util.Utils;
import com.example.widget.ObservableScrollView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/9/25.
 */
public class UserInfoActivity extends AppCompatActivity implements ScrollViewListener{


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.scrollView)
    ObservableScrollView scrollView;
     DisplayMetrics dm;


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
        dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        scrollView.setScrollViewListener(this);


    }

    @Override
    public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {

        if(y>=0&&y<=image.getHeight()-Utils.getStatusBarHeight(this)-Utils.getNavigationBarHeight(this))
            toolbar.setBackgroundColor(Color.argb(255*y/(image.getHeight()-Utils.getStatusBarHeight(this)-Utils.getNavigationBarHeight(this)),0,0,0));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
