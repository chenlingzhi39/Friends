package com.example.ui;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.administrator.myapplication.R;
import com.example.listener.ScrollViewListener;
import com.example.util.Utils;
import com.example.widget.AlphaView;
import com.example.widget.ObservableScrollView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/9/25.
 */
public class UserInfoActivity extends AppCompatActivity implements ScrollViewListener {


    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.content)
    RelativeLayout content;
    @InjectView(R.id.scrollView)
    ObservableScrollView scrollView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.toolbar_background)
    AlphaView toolbarBackground;
    @InjectView(R.id.buttons)
    LinearLayout buttons;

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
        scrollView.setScrollViewListener(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getStatusBarHeight(this)+Utils.getToolbarHeight(this));
        toolbarBackground.setLayoutParams(lp);
        image.post(new Runnable() {
            @Override
            public void run() {
                toolbarBackground.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background));
            }
        });

    }

    @Override
    public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {

        if (y >= 0 && y <= (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this)-buttons.getHeight()))

            toolbar.setBackgroundColor(Color.argb(255 * y / (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this)), 0, 0, 0));
        if(y>=(image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this)-2*buttons.getHeight())&&y<=(image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this)-buttons.getHeight()))


            toolbarBackground.setAlpha(255 * (y - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - buttons.getHeight()) / 2*buttons.getHeight());
    }

}
