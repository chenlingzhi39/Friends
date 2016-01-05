package com.example.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.example.manager.SystemBarTintManager;
import com.nostra13.universalimageloader.core.ImageLoader;

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
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Post post;
    private boolean is_praised;
    private boolean is_collected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("作品");
        init();


    }
public  void init(){
    SystemBarTintManager tintManager = new SystemBarTintManager(this);
    // enable status bar tint
    tintManager.setStatusBarTintEnabled(true);
    tintManager.setStatusBarTintColor(getResources().getColor(R.color.material_blue_500));
    post=(Post)getIntent().getExtras().get("post");
    is_praised=getIntent().getBooleanExtra("isPraised",false);
    is_collected=getIntent().getBooleanExtra("isCollected",false);
    userName.setText(post.getAuthor().getUsername());
    if(post.getAuthor().getHead()!=null)
    imageLoader.displayImage(post.getAuthor().getHead().getFileUrl(getApplicationContext()), userHead);
    contentText.setText(post.getContent());
    time.setText(post.getCreatedAt());
    if(post.getImage()!=null)
    imageLoader.displayImage(post.getImage().getFileUrl(getApplicationContext()), contentImage);
    praise.setText(post.getPraise_count()+"");
    if(is_praised)
    praise.setTextColor(this.getResources().getColor(R.color.material_blue_500));
    else praise.setTextColor(this.getResources().getColor(android.R.color.black));
    if(is_collected)
        collect.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_fav_selected));
    else collect.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_fav_normal));
}
}
