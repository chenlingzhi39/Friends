package com.example.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.CommentAdapter;
import com.example.adapter.RecyclerArrayAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Comment;
import com.example.bean.Post;
import com.example.manager.SystemBarTintManager;
import com.example.refreshlayout.RefreshLayout;
import com.example.util.StringUtils;
import com.example.widget.recyclerview.EasyRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/1/4.
 */
public class ContentActivity extends BasicActivity implements RefreshLayout.OnRefreshListener {
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
    private final int SUBMIT_START = 0;
    private final int SUBMIT_SUCCEED = 1;
    private final int SUBMIT_FAIL = 2;
    private View headerView;

    @Override
    public void start() {
        pd=ProgressDialog.show(ContentActivity.this,null,dialog_content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("作品");
        comments = new ArrayList<Comment>();
     /*   Comment comment = new Comment();
        comment.setAuthor(MyApplication.getInstance().getCurrentUser());
        comment.setContent("我是评论");

        for (int i = 0; i < 10; i++) {
            comments.add(comment);
        }*/
        post = (Post) getIntent().getExtras().get("post");
        is_praised = getIntent().getBooleanExtra("isPraised", false);
        is_collected = getIntent().getBooleanExtra("isCollected", false);
        initHeader();
        init();


    }
    @OnClick(R.id.submit)
    public void submit(){
        if (content.getText().toString().equals("")) {
            Toast.makeText(this, "内容不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        setDialogContent("正在提交");
        handler.sendEmptyMessage(START);
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content.getText().toString());
        comment.setAuthor(MyApplication.getInstance().getCurrentUser());
        insertObject(comment);
    }
    public void initHeader(){
        headerView=getLayoutInflater().inflate(R.layout.content_item,null);
        TextView userName=(TextView)headerView.findViewById(R.id.user_name);
        ImageView userHead=(ImageView)headerView.findViewById(R.id.user_head);
        ImageView contentImage=(ImageView)headerView.findViewById(R.id.content_image);
        TextView contentText=(TextView)headerView.findViewById(R.id.content_text);
        TextView time=(TextView)headerView.findViewById(R.id.time);
        TextView praise=(TextView)headerView.findViewById(R.id.praise);
        ImageView collect=(ImageView)headerView.findViewById(R.id.collect);
        userName.setText(post.getAuthor().getUsername());
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
            collect.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_fav_normal));
    }
    public void init() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.material_blue_500));

       /* commentList.setProgressView(R.layout.view_progress);
        commentList.setEmptyView(R.layout.view_empty);
        commentList.setErrorView(R.layout.view_error);*/
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.setHeaderRefreshingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        commentList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        commentList.setAdapter(commentAdapter = new CommentAdapter(this));
       commentAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {

                return headerView;
            }

            @Override
            public void onBindView(View headerView) {

            }
        });
        commentAdapter.addAll(comments);
        commentList.setRefreshListener(this);



        refreshQuery();
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
        loadMoreQuery();
    }

    @Override
    public void onHeaderRefresh() {

        refreshQuery();
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


    @Override
    public void refreshQuery() {
        BmobQuery<Comment> query = new BmobQuery<>();
        if (comments.size() > 0)
            query.addWhereGreaterThan("id", comments.get(0).getId());
        query.addWhereEqualTo("post", new BmobPointer(post));
        query.setLimit(10);
        query.order("-id");
        query.include("author");
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                if (list.size() != 0) {
                    if (comments.size() > 0) {
                        comments.addAll(0, (ArrayList) list);
                        commentAdapter.notifyDataSetChanged();
                        // commentList.showRecycler();
                    } else {
                        comments = (ArrayList) list;
                        commentAdapter.addAll(comments);
                    }
                } else {

                    //commentList.showEmpty();
                }
                commentList.setHeaderRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                commentList.setHeaderRefreshing(false);
                commentList.showError();
            }
        });
    }

    @Override
    public void loadMoreQuery() {
        if(comments.size()>0){
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereLessThan("id", comments.get(comments.size() - 1).getId());
        query.addWhereEqualTo("post", new BmobPointer(post));
        query.setLimit(10);
        query.order("-id");
        query.include("author");
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                if (list.size() != 0) {
                    comments.addAll((ArrayList) list);
                    commentAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });}
        commentList.setFooterRefreshing(false);
    }

    public void insertObject(final BmobObject obj) {

        obj.save(getApplicationContext(), new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                showToast("-->创建数据成功：" + obj.getObjectId());
                refreshQuery();
                handler.sendEmptyMessage(SUCCEED);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showToast("-->创建数据失败：" + arg0 + ",msg = " + arg1);
                handler.sendEmptyMessage(FAIL);
            }
        });
    }

}
