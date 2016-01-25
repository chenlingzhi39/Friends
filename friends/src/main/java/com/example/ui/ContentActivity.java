package com.example.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.CommentAdapter;
import com.example.adapter.RecyclerArrayAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Comment;
import com.example.bean.Post;
import com.example.bean.User;
import com.example.manager.SystemBarTintManager;
import com.example.refreshlayout.RefreshLayout;
import com.example.util.StringUtils;
import com.example.widget.recyclerview.EasyRecyclerView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

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
    private Comment replyComment;
    @Override
    public void start() {
        pd = ProgressDialog.show(ContentActivity.this, null, dialog_content);
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
        commentList.showProgress();
        commentList.setRefreshEnabled(true);
        post = (Post) getIntent().getExtras().get("post");
        is_praised = getIntent().getBooleanExtra("isPraised", false);
        is_collected = getIntent().getBooleanExtra("isCollected", false);
        initHeader();
        init();


    }

    @OnClick(R.id.submit)
    public void submit() {
        Log.i("submit","submit");
        if (MyApplication.getInstance().getCurrentUser() != null) {
            if (content.getText().toString().trim().equals("")) {
                Toast.makeText(this, "内容不能为空!", Toast.LENGTH_SHORT).show();
                return;
            }
            setDialogContent("正在提交");
            handler.sendEmptyMessage(START);
            Comment comment = new Comment();
            if(replyComment!=null)
            comment.setComment(replyComment);
            comment.setPost(post);
            comment.setContent(content.getText().toString());
            comment.setAuthor(MyApplication.getInstance().getCurrentUser());
            insertObject(comment);
        } else {
            Intent intent = new Intent(ContentActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void initHeader() {
        headerView = getLayoutInflater().inflate(R.layout.content_item, null);
        TextView userName = (TextView) headerView.findViewById(R.id.user_name);
        ImageView userHead = (ImageView) headerView.findViewById(R.id.user_head);
        ImageView contentImage = (ImageView) headerView.findViewById(R.id.content_image);
        TextView contentText = (TextView) headerView.findViewById(R.id.content_text);
        TextView time = (TextView) headerView.findViewById(R.id.time);
        final TextView praise = (TextView) headerView.findViewById(R.id.praise);
        final ImageButton collect = (ImageButton) headerView.findViewById(R.id.collect);
        praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getCurrentUser() != null) {
                    praise.setClickable(false);

                    if (is_praised) {
                        post.increment("praise_count", -1);
                        post.removeAll("praise_user_id", Arrays.asList(MyApplication.getInstance().getCurrentUser().getObjectId()));
                        post.update(getApplicationContext(), new UpdateListener() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                is_praised = false;
                                post.setPraise_count(post.getPraise_count() - 1);
                                praise.setTextColor(getApplicationContext().getResources().getColor(android.R.color.black));
                                //DatabaseUtil.getInstance(context).deletePraise(entity);
                                praise.setClickable(true);
                                praise.setText(post.getPraise_count() + "");
                                Intent intent = new Intent();
                                intent.putExtra("post_id", post.getId());
                                intent.putExtra("is_praised", is_praised);
                                setResult(MainActivity.REFRESH_PRAISE, intent);
                                Log.i("bmob", "删除点赞成功");
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Log.i("bmob", "删除点赞失败：" + msg);
                                // praise.setClickable(true);
                            }

                            @Override
                            public void postOnFailure(int code, String msg) {
                                super.postOnFailure(code, msg);
                                praise.setClickable(true);
                            }
                        });
                    } else {
                        post.increment("praise_count", 1);
                        post.addUnique("praise_user_id", MyApplication.getInstance().getCurrentUser().getObjectId());
                        post.update(getApplicationContext(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                is_praised = true;
                                post.setPraise_count(post.getPraise_count() + 1);
                                praise.setTextColor(getApplicationContext().getResources().getColor(R.color.material_blue_500));
                                praise.setClickable(true);
                                praise.setText(post.getPraise_count() + "");
                                Intent intent = new Intent();
                                intent.putExtra("post_id", post.getId());
                                intent.putExtra("is_praised", is_praised);
                                setResult(MainActivity.REFRESH_PRAISE, intent);
                                //DatabaseUtil.getInstance(context).insertPraise(entity);
                                Log.i("bmob", "添加点赞成功");
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Log.i("bmob", "添加点赞失败：" + msg);
                                //praise.setClickable(true);
                            }

                            @Override
                            public void postOnFailure(int code, String msg) {
                                super.postOnFailure(code, msg);
                                praise.setClickable(true);
                            }
                        });
                    }

                }

            }
        });
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getCurrentUser() != null) {
                    collect.setClickable(false);
                    User user = new User();
                    user.setObjectId(MyApplication.getInstance().getCurrentUser().getObjectId());
                    if (is_collected) {
                        user.removeAll("collect_post_id", Arrays.asList(post.getObjectId()));
                        user.update(getApplicationContext(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                is_collected = false;
                                collect.setClickable(true);
                                collect.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_fav_normal));
                                Intent intent = new Intent();
                                intent.putExtra("post_id", post.getId());
                                intent.putExtra("is_collected", is_collected);
                                setResult(MainActivity.REFRESH_COLLECTION, intent);
                                Log.i("bmob", "删除收藏成功");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                // collect.setClickable(true);
                                Log.i("bmob", "删除收藏失败" + s);
                            }

                            @Override
                            public void postOnFailure(int code, String msg) {
                                super.postOnFailure(code, msg);
                                collect.setClickable(true);
                            }
                        });
                    } else {
                        user.addUnique("collect_post_id", post.getObjectId());
                        user.update(getApplicationContext(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                is_collected = true;
                                collect.setClickable(true);
                                collect.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_fav_selected));
                                Intent intent = new Intent();
                                intent.putExtra("post_id", post.getId());
                                intent.putExtra("is_collected", is_collected);
                                setResult(MainActivity.REFRESH_COLLECTION, intent);
                                Log.i("bmob", "添加收藏成功");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                // collect.setClickable(true);
                                Log.i("bmob", "添加收藏失败" + s);
                            }

                            @Override
                            public void postOnFailure(int code, String msg) {
                                super.postOnFailure(code, msg);
                                collect.setClickable(true);
                            }
                        });
                    }

                }
            }
        });
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

        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.setHeaderRefreshingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        commentList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        commentAdapter = new CommentAdapter(this);
        commentAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {

                return headerView;
            }

            @Override
            public void onBindView(View headerView) {

            }
        });
        commentList.showRecycler();
        commentList.setAdapter(commentAdapter);
        commentList.setRefreshListener(this);
        commentAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showReplyDialog(comments.get(position));
            }
        });
        commentAdapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemClick(int position) {
                showReplyDialog(comments.get(position));
                return false;
            }


        });

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
        if (comments.size() > 0) {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.addWhereGreaterThan("id", comments.get(0).getId());
            Log.i("size", comments.size() + "");
        }//else query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.addWhereEqualTo("post", new BmobPointer(post));
        query.setLimit(10);
        query.order("-id");
        query.include("author,comment,comment.author");
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                if (list.size() != 0) {
                    if (comments.size() > 0) {
                        comments.addAll(0, (ArrayList) list);
                        commentAdapter.clear();
                        commentAdapter.addAll(comments);
                    } else {
                        comments = (ArrayList) list;
                        commentAdapter.addAll(comments);
                    }

                }
                commentList.showRecycler();
                commentList.setHeaderRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                Log.i("onError", s);
                commentList.setHeaderRefreshing(false);
            }

        });

    }

    @Override
    public void loadMoreQuery() {
        if (comments.size() > 0) {
            BmobQuery<Comment> query = new BmobQuery<>();
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.addWhereLessThan("id", comments.get(comments.size() - 1).getId());
            query.addWhereEqualTo("post", new BmobPointer(post));
            query.setLimit(10);
            query.order("-id");
            query.include("author,comment,comment.author");

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
            });
        }
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
                post.increment("comment_count", 1);
                post.update(getApplicationContext(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        post.setPraise_count(post.getComment_count() + 1);
                        User user = new User();
                        user.setObjectId(MyApplication.getInstance().getCurrentUser().getObjectId());
                        user.setUsername(MyApplication.getInstance().getCurrentUser().getUsername());
                        user.setHead(MyApplication.getInstance().getCurrentUser().getHead());
                        User user1 = new User();
                        user1.setObjectId(post.getAuthor().getObjectId());
                        Post post1 = new Post();
                        post1.setAuthor(user1);
                        post1.setObjectId(post.getObjectId());
                        post1.setContent(post.getContent());
                        Comment pushComment = new Comment();
                        pushComment = (Comment) obj;
                        pushComment.setAuthor(user);
                        pushComment.setPost(post1);
                        Gson gson = new Gson();
                        String message = gson.toJson(pushComment);
                        Log.i("message", message);
                        BmobPushManager bmobPush = new BmobPushManager(ContentActivity.this);
                        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
                        query.addWhereEqualTo("uid", MyApplication.getInstance().getCurrentUser().getObjectId());
                        bmobPush.setQuery(query);
                        bmobPush.pushMessage(message);
                        Intent intent = new Intent();
                        setResult(MainActivity.REFRESH_COMMENT, intent);
                        content.setText("");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
                InputMethodManager inputManager =

                        (InputMethodManager) content.getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(content.getWindowToken(), 0);
                content.setText("");
                replyComment=null;
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showToast("-->创建数据失败：" + arg0 + ",msg = " + arg1);
                handler.sendEmptyMessage(FAIL);
                replyComment=null;
            }

            @Override
            public void postOnFailure(int code, String msg) {
                super.postOnFailure(code, msg);
                replyComment=null;
            }
        });
    }

    public void showReplyDialog(Comment comment) {
        ReplyDialogHelper helper = new ReplyDialogHelper();
        Dialog dialog = new AlertDialog.Builder(this)
                .setView(helper.getView())
                .setOnDismissListener(helper)
                .create();
       replyComment=comment;
        helper.setDialog(dialog);
        dialog.show();

    }

    private class ReplyDialogHelper implements
            DialogInterface.OnDismissListener, View.OnClickListener {

        private Dialog mDialog;
        private View mView;

        private ReplyDialogHelper() {
            @SuppressLint("InflateParams")
            LinearLayout linear = (LinearLayout) ContentActivity.this
                    .getLayoutInflater().inflate(R.layout.dialog_reply, null);
            Button reply = (Button) linear.findViewById(R.id.reply);
            Button copy = (Button) linear.findViewById(R.id.copy);
            Button info = (Button) linear.findViewById(R.id.info);
            reply.setOnClickListener(this);
            copy.setOnClickListener(this);
            info.setOnClickListener(this);
            mView = linear;
        }



        public View getView() {
            return mView;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reply:
                    InputMethodManager inputManager =

                            (InputMethodManager) content.getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);

                    inputManager.showSoftInput(content, 0);
                   content.setHint("回复 "+replyComment.getAuthor().getUsername()+":");
                    break;
                case R.id.copy:
                    break;
                case R.id.info:
                    break;
                default:
                    break;
            }
            mDialog.dismiss();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            mDialog = null;
        }


    }

}
