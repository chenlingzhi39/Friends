package com.example.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/1/4.
 */
public class ContentActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.time)
    TextView time;
    @InjectView(R.id.content_text)
    TextView contentText;
    @InjectView(R.id.content_image)
    ImageView contentImage;
    @InjectView(R.id.user_head)
    ImageView userHead;
    @InjectView(R.id.collect)
    ImageView collect;
    @InjectView(R.id.share)
    TextView share;
    @InjectView(R.id.praise)
    TextView praise;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

    }

}
