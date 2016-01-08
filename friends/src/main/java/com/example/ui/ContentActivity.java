package com.example.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.adapter.CommentAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Comment;
import com.example.bean.Post;
import com.example.manager.SystemBarTintManager;
import com.example.refreshlayout.RefreshLayout;
import com.example.widget.recyclerview.EasyRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/1/4.
 */
public class ContentActivity extends AppCompatActivity implements RefreshLayout.OnRefreshListener{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.comment_list)
    EasyRecyclerView commentList;
    @InjectView(R.id.content)
    EditText content;
    @InjectView(R.id.submit)
    Button submit;


    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Post post;
    private boolean is_praised;
    private boolean is_collected;
    private LinearLayoutManager mLayoutManager;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("作品");
        comments=new ArrayList<Comment>();
        Comment comment=new Comment();
        comment.setAuthor(MyApplication.getInstance().getCurrentUser());
        comment.setContent("我是评论");

        for(int i=0;i<10;i++)
        {comments.add(comment);}

        init();


    }

    public void init() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.material_blue_500));
        commentList.setProgressView(R.layout.view_progress);
        commentList.setEmptyView(R.layout.view_empty);
        commentList.setErrorView(R.layout.view_error);
        commentList.setLayoutManager(new LinearLayoutManager(this));
commentList.setHeaderRefreshingColor(android.R.color.holo_blue_bright,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light);
commentList.setFooterRefreshingColor(android.R.color.holo_blue_bright,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light);
        commentList.setAdapter(commentAdapter=new CommentAdapter(this));
        commentAdapter.addAll(comments);
        commentList.setRefreshListener(this);


        post = (Post) getIntent().getExtras().get("post");
        is_praised = getIntent().getBooleanExtra("isPraised", false);
        is_collected = getIntent().getBooleanExtra("isCollected", false);
       /* userName.setText(post.getAuthor().getUsername());
        if (post.getAuthor().getHead() != null)
            imageLoader.displayImage(post.getAuthor().getHead().getFileUrl(getApplicationContext()), userHead);
        contentText.setText(post.getContent());
        time.setText(StringUtils.friendly_time(post.getCreatedAt()));
        if (post.getImage() != null)
            imageLoader.displayImage(post.getImage().getFileUrl(getApplicationContext()), contentImage);
        praise.setText(post.getPraise_count() + "");
        if (is_praised)
            praise.setTextColor(this.getResources().getColor(R.color.material_blue_500));
        else praise.setTextColor(this.getResources().getColor(android.R.color.black));
        if (is_collected)
            collect.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_fav_selected));
        else
            collect.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_fav_normal));*/
    }

    @Override
    public void onFooterRefresh() {

    }

    @Override
    public void onHeaderRefresh() {

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
