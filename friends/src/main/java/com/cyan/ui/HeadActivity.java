package com.cyan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cyan.common.Constants;
import com.cyan.community.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/4/6.
 */
public class HeadActivity extends Activity {
    @InjectView(R.id.head)
    ImageView head;
    @InjectView(R.id.change)
    Button change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head);
        ButterKnife.inject(this);
        if(!getIntent().getBooleanExtra("self",false))
            change.setVisibility(View.GONE);
        if(getIntent().getStringExtra("head")!=null)
            Glide.with(this).load(getIntent().getStringExtra("head")).into(head);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Constants.CHANGE_HEAD);
                finish();
            }
        });
    }
}
