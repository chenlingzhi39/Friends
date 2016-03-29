package com.cyan.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.Glide;
import com.cyan.App.MyApplication;
import com.cyan.adapter.CommentAdapter;
import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.Comment;
import com.cyan.bean.Post;
import com.cyan.bean.RefreshData;
import com.cyan.bean.User;
import com.cyan.community.R;
import com.cyan.module.comment.presenter.CommentPresenter;
import com.cyan.module.comment.presenter.CommentPresenterImpl;
import com.cyan.module.comment.view.LoadCommentView;
import com.cyan.module.comment.view.SendCommentView;
import com.cyan.widget.refreshlayout.RefreshLayout;
import com.cyan.util.RxBus;
import com.cyan.util.StringUtils;
import com.cyan.widget.recyclerview.DividerItemDecoration;
import com.cyan.widget.recyclerview.EasyRecyclerView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.daoexample.CommentToMe;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Record;
import de.greenrobot.daoexample.RecordDao;
import de.greenrobot.daoexample.ReplyToMe;

/**
 * Created by Administrator on 2016/1/4.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_content,
        toolbarTitle = R.string.content
)
public class ContentActivity extends BaseActivity implements RefreshLayout.OnRefreshListener,LoadCommentView,SendCommentView {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.comment_list)
    EasyRecyclerView commentList;
    @InjectView(R.id.content)
    EditText content;
    @InjectView(R.id.submit)
    Button submit;
    private Post post;
    private boolean is_praised;
    private boolean is_collected;
    private LinearLayoutManager mLayoutManager;
    private CommentAdapter commentAdapter;
    private View headerView;
    private Comment replyComment;
    private CommentToMe commentToMe;
    private ReplyToMe replyToMe;
    private String message;
    private boolean is_reply = false;
    TextView praise;
    ImageButton collect;
    private SQLiteDatabase db;
    private DaoSession daoSession;
    private RecordDao recordDao;
    private DaoMaster daoMaster;
    private CommentPresenter commentPresenter;
    private BmobQuery<Comment> query;
    ClipboardManager myClipboard;
    private boolean isRefresh=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        commentPresenter=new CommentPresenterImpl(this,this,this);
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.showProgress();
        post = (Post) getIntent().getExtras().get("post");
        if(post!=null){
        is_praised = getIntent().getBooleanExtra("isPraised", false);
        is_collected = getIntent().getBooleanExtra("isCollected", false);
        init();}
        else{
            BmobQuery<Post> query=new BmobQuery<>();
            if(getIntent().getExtras().get("parent_id")!=null)
            query.addWhereEqualTo("objectId",getIntent().getExtras().get("parent_id"));
            else  query.addWhereEqualTo("objectId",getIntent().getExtras().get("object_id"));
            query.include("author");
            query.findObjects(this, new FindListener<Post>() {
                @Override
                public void onSuccess(List<Post> list) {
                    if(list.size()!=0){
                        post=list.get(0);
                        setCollection(post);
                        setPraise(post);
                        init();
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }

    }


    @Override
    public void dismissDialog() {
    pd.dismiss();
    }

    @Override
    public void refresh(final Comment comment) {
        query=new BmobQuery<>();
        if(commentAdapter.getData().size()>0)
            query.addWhereGreaterThan("id", commentAdapter.getData().get(0).getId());
        query.addWhereEqualTo("post", new BmobPointer(post));
        commentPresenter.loadComment(query);
            // TODO Auto-generated method stub
            post.increment("comment_count", 1);
            post.update(getApplicationContext(), new UpdateListener() {
                @Override
                public void onSuccess() {
                    post.setComment_count(post.getComment_count() + 1);
                    BmobPushManager bmobPush = new BmobPushManager(ContentActivity.this);
                    BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
                    Gson gson = new Gson();
                    if (!is_reply) {
                        commentToMe = new CommentToMe();
                        commentToMe.setYourid(post.getAuthor().getObjectId());
                        commentToMe.setPostid(post.getObjectId());
                        commentToMe.setUserid(MyApplication.getInstance().getCurrentUser().getObjectId());
                        commentToMe.setComment_id(comment.getObjectId());
                        commentToMe.setUser_name(MyApplication.getInstance().getCurrentUser().getUsername());
                        if (MyApplication.getInstance().getCurrentUser().getHead() != null)
                            commentToMe.setHead(MyApplication.getInstance().getCurrentUser().getHead().getFileUrl(getApplicationContext()));
                        commentToMe.setPost_content(post.getContent());
                        commentToMe.setComment_content(((Comment) comment).getContent());
                        commentToMe.setCreate_time(StringUtils.toDate(comment.getCreatedAt()));
                        message = "{\"commentToMe\":" + gson.toJson(commentToMe) + "}";
                        if (!post.getAuthor().getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
                            query.addWhereEqualTo("uid", post.getAuthor().getObjectId());
                            bmobPush.setQuery(query);
                            bmobPush.pushMessage(message);
                        }
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "records-db", null);
                        db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        recordDao = daoSession.getRecordDao();
                        Record record = new Record();
                        record.setType("comment");
                        record.setContent(((Comment) comment).getContent());
                        record.setUser_id(MyApplication.getInstance().getCurrentUser().getObjectId());
                        record.setAdd_time(StringUtils.toDate(comment.getCreatedAt()));
                        record.setObject_id(comment.getObjectId());
                        record.setParent_id(post.getObjectId());
                        recordDao.insert(record);

                    } else {
                        replyToMe = new ReplyToMe();
                        replyToMe.setComment_content(((Comment) comment).getContent());
                        replyToMe.setComment_id(comment.getObjectId());
                        if (MyApplication.getInstance().getCurrentUser().getHead() != null)
                            replyToMe.setHead(MyApplication.getInstance().getCurrentUser().getHead().getFileUrl(getApplicationContext()));
                        replyToMe.setPost_content(post.getContent());
                        replyToMe.setUserid(MyApplication.getInstance().getCurrentUser().getObjectId());
                        replyToMe.setPostid(post.getObjectId());
                        replyToMe.setUser_name(MyApplication.getInstance().getCurrentUser().getUsername());
                        replyToMe.setCreate_time(StringUtils.toDate(comment.getCreatedAt()));
                        replyToMe.setYourid(replyComment.getAuthor().getObjectId());
                        replyToMe.setReply_content(replyComment.getContent());
                        replyToMe.setPost_author_id(post.getAuthor().getObjectId());
                        replyToMe.setPost_author_name(post.getAuthor().getUsername());
                        message = "{\"replyToMe\":" + gson.toJson(replyToMe) + "}";
                        if (!replyComment.getAuthor().getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
                            query.addWhereEqualTo("uid", replyComment.getAuthor().getObjectId());
                            bmobPush.setQuery(query);
                            bmobPush.pushMessage(message);
                            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "records-db", null);
                            db = helper.getWritableDatabase();
                            daoMaster = new DaoMaster(db);
                            daoSession = daoMaster.newSession();
                            recordDao = daoSession.getRecordDao();
                            Record record = new Record();
                            record.setType("reply");
                            record.setContent(((Comment) comment).getContent());
                            record.setUser_id(MyApplication.getInstance().getCurrentUser().getObjectId());
                            record.setAdd_time(StringUtils.toDate(comment.getCreatedAt()));
                            record.setObject_id(comment.getObjectId());
                            record.setParent_id(post.getObjectId());
                            recordDao.insert(record);
                        }

                    }
                    Log.i("message", message);
                  /*  Intent intent = new Intent();
                    intent.putExtra("post_id", post.getObjectId());
                    setResult(MainActivity.REFRESH_COMMENT, intent);*/
                    RefreshData data = new RefreshData(post.getObjectId(), "comment", null);
                    RxBus.get().post("refresh", data);
                }

                @Override
                public void onFailure(int i, String s) {

                }

                @Override
                public void onFinish() {
                    InputMethodManager inputManager =

                            (InputMethodManager) content.getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(content.getWindowToken(), 0);
                    is_reply = false;
                    content.setText("");
                    content.setHint("");
                }
            });


        }


    @Override
    public void showDialog() {
     pd=ProgressDialog.show(ContentActivity.this,null,"正在发送");
    }

    @Override
    public void toastSendFailure() {
    toast("发送失败");
    }

    @Override
    public void toastSendSuccess() {
        toast("发送成功");
    }

    @Override
    public void addComments(List<Comment> list) {
        if (list.size() > 0)
            if (commentAdapter.getData().size() == 0) {
                commentAdapter.addAll(0, (ArrayList) list);
            } else {
                if(commentAdapter.getData().get(0).getId()>list.get(0).getId())
                commentAdapter.addAll(list);else
                    commentAdapter.addAll(0,(ArrayList) list);

            }


    }

    @Override
    public void showEmpty() {
    commentAdapter.setNoMore(R.layout.view_nomore);
    commentAdapter.stopMore();
    commentList.setFooterEnabled(false);
    }

    @Override
    public void showError() {
     commentAdapter.pauseMore();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void showRecycler() {

    }

    @Override
    public void stopLoadmore() {
    commentList.setFooterRefreshing(false);
    }

    @Override
    public void stopRefresh() {
    commentList.setHeaderRefreshing(false);
    }
    @Override
    public void onFooterRefresh() {
        query=new BmobQuery<>();
        if(commentAdapter.getData().size()>0)
            query.addWhereLessThan("id",commentAdapter.getData().get(commentAdapter.getData().size()-1).getId());
        query.addWhereEqualTo("post", new BmobPointer(post));
        commentPresenter.loadComment(query);

    }

    @Override
    public void onHeaderRefresh() {
        query=new BmobQuery<>();
        if(commentAdapter.getData().size()>0)
            query.addWhereGreaterThan("id", commentAdapter.getData().get(0).getId());
        query.addWhereEqualTo("post", new BmobPointer(post));
        commentPresenter.loadComment(query);

    }
    public void setPraise(Post post) {


            if (MyApplication.getInstance().getCurrentUser() != null) {
                BmobQuery<Post> query = new BmobQuery<Post>();


                String[] praise_user_id = {MyApplication.getInstance().getCurrentUser().getObjectId()};

                query.addWhereEqualTo("objectId", post.getObjectId());
                query.addWhereContainsAll("praise_user_id", Arrays.asList(praise_user_id));

                query.findObjects(getApplicationContext(), new FindListener<Post>() {
                    @Override
                    public void onSuccess(List<Post> list) {
                        if (list.size() > 0) {
                            is_praised = getIntent().getBooleanExtra("isPraised", true);
                            praise.setTextColor(getApplicationContext().getResources().getColor(R.color.material_blue_500));
                        } else {
                            is_praised = getIntent().getBooleanExtra("isPraised", false);
                            praise.setTextColor(getApplicationContext().getResources().getColor(android.R.color.black));
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });




            //DatabaseUtil.getInstance(getApplicationContext()).insertPraise(post);
        }

    }

    public void setCollection(Post post) {
        List<String> collect_post_id = new ArrayList<String>();
        collect_post_id = MyApplication.getInstance().getCurrentUser().getCollect_post_id();
        if (collect_post_id != null) {

            if (collect_post_id.contains(post.getObjectId()))
                is_collected = getIntent().getBooleanExtra("isCollected", true);
            else
                is_collected = getIntent().getBooleanExtra("isCollected", false);
        }
        }

    @OnClick(R.id.submit)
    public void submit() {
        if (MyApplication.getInstance().getCurrentUser() != null) {
            if (content.getText().toString().trim().equals("")) {
                Toast.makeText(this, "内容不能为空!", Toast.LENGTH_SHORT).show();
                return;
            }
            Comment comment = new Comment();
            if (replyComment != null&&is_reply)
            comment.setComment(replyComment);
            comment.setPost(post);
            comment.setContent(content.getText().toString());
            comment.setAuthor(MyApplication.getInstance().getCurrentUser());
            commentPresenter.sendComment(comment);
        } else {
           toast("请登录");
        }
    }



    public void init() {
        if(headerView==null)
        headerView = getLayoutInflater().inflate(R.layout.item_content, null);
        commentList.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL_LIST));
        commentList.setHeaderRefreshingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        commentList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        commentAdapter = new CommentAdapter(this);
        commentAdapter.setError(R.layout.view_error);
        commentAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {

                return headerView;
            }

            @Override
            public void onBindView(View headerView) {
                final Drawable drawable=getResources().getDrawable(R.drawable.ic_action_love_selected);
                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                final Drawable drawable1=getResources().getDrawable(R.drawable.ic_action_love);
                drawable1.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                TextView userName = (TextView) headerView.findViewById(R.id.user_name);
                ImageView userHead = (ImageView) headerView.findViewById(R.id.user_head);
                ImageView contentImage = (ImageView) headerView.findViewById(R.id.content_image);
                TextView contentText = (TextView) headerView.findViewById(R.id.content_text);
                TextView time = (TextView) headerView.findViewById(R.id.time);
                ImageView share = (ImageView) headerView.findViewById(R.id.share);
                praise = (TextView) headerView.findViewById(R.id.praise);
                collect = (ImageButton) headerView.findViewById(R.id.collect);
               contentImage.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(ContentActivity.this, PhotoActivity.class);
                       intent.putExtra("photo", post.getImage().getFileUrl(getApplicationContext()));
                       startActivity(intent);
                   }
               });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(post.getImage()!=null) {
                            Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
                            intent.setType("text/plain"); // 分享发送的数据类型
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "图文社区分享"); // 分享的主题
                            intent.putExtra(Intent.EXTRA_TEXT, post.getImage().getFileUrl(ContentActivity.this)); // 分享的内容
                            startActivity(Intent.createChooser(intent, "选择分享"));// 目标应用选择对话框的标题*/
                        }else toast("没有图片不能分享");


                    }
                });
                userHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyApplication.getInstance().getCurrentUser() != null) {
                            Intent intent = new Intent(ContentActivity.this, UserInfoActivity.class);
                            intent.putExtra("user", post.getAuthor());
                            startActivityForResult(intent, 0);
                        }
                    }
                });
                praise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyApplication.getInstance().getCurrentUser() != null) {
                            praise.setClickable(false);

                            if (is_praised) {
                                post.increment("praise_count", -1);
                                post.removeAll("praise_user_id", Arrays.asList(MyApplication.getInstance().getCurrentUser().getObjectId()));
                            } else {

                                post.increment("praise_count", 1);
                                post.addUnique("praise_user_id", MyApplication.getInstance().getCurrentUser().getObjectId());
                            }


                            post.update(getApplicationContext(), new UpdateListener() {

                                @Override
                                public void onSuccess() {
                                    // TODO Auto-generated method stub
                                    if (is_praised) {
                                        is_praised = false;
                                        post.setPraise_count(post.getPraise_count() - 1);
                                        praise.setCompoundDrawables(drawable1, null, null, null);
                                    } else {
                                        is_praised = true;
                                        post.setPraise_count(post.getPraise_count() + 1);
                                        praise.setCompoundDrawables(drawable, null, null, null);
                                    }
                                    //DatabaseUtil.getInstance(context).deletePraise(entity);
                                    praise.setClickable(true);
                                    praise.setText(post.getPraise_count() + "");
                                  /*  Intent intent = new Intent();
                                    intent.putExtra("post_id", post.getObjectId());
                                    intent.putExtra("is_praised", is_praised);
                                    setResult(MainActivity.REFRESH_PRAISE, intent);*/
                                    RefreshData data=new RefreshData(post.getObjectId(),"praise",is_praised);
                                    RxBus.get().post("refresh", data);

                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    // TODO Auto-generated method stub

                                    // praise.setClickable(true);
                                }

                                @Override
                                public void postOnFailure(int code, String msg) {
                                    super.postOnFailure(code, msg);
                                    praise.setClickable(true);
                                }
                            });

                        }
                    }


                });
                collect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyApplication.getInstance().getCurrentUser() != null) {
                            collect.setClickable(false);
                            User user = new User();
                            if (is_collected)
                                user.removeAll("collect_post_id", Arrays.asList(post.getObjectId()));
                            else
                                user.addUnique("collect_post_id", post.getObjectId());
                            user.update(getApplicationContext(), MyApplication.getInstance().getCurrentUser().getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    if (is_collected) {
                                        is_collected = false;
                                        collect.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_fav_normal));
                                    } else {
                                        is_collected = true;
                                        collect.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_fav_selected));
                                    }
                                    collect.setClickable(true);

                             /*       Intent intent = new Intent();
                                    intent.putExtra("post_id", post.getObjectId());
                                    intent.putExtra("is_collected", is_collected);*/
                                    MyApplication.getInstance().getCurrentUser().getCollect_post_id().remove(post.getObjectId());
                             /*       setResult(MainActivity.REFRESH_COLLECTION, intent);*/
                                    RefreshData data=new RefreshData(post.getObjectId(),"collection",is_collected);
                                    RxBus.get().post("refresh", data);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    // collect.setClickable(true);

                                }

                                @Override
                                public void postOnFailure(int code, String msg) {
                                    super.postOnFailure(code, msg);
                                    collect.setClickable(true);
                                }
                            });
                        }
                    }
                });
                userName.setText(post.getAuthor().getUsername());
                if (post.getAuthor().getHead() != null)
                    Glide.with(getApplicationContext()).load(post.getAuthor().getHead().getFileUrl(getApplicationContext())).into(userHead);
                contentText.setText(post.getContent());
                time.setText(StringUtils.friendly_time(post.getCreatedAt()));
                if (post.getImage() != null)
                    Glide.with(getApplicationContext()).load(post.getImage().getFileUrl(getApplicationContext())).into(contentImage);
                else contentImage.setVisibility(View.GONE);
                praise.setText(post.getPraise_count() + "");
                if (is_praised)
                    praise.setCompoundDrawables(drawable, null, null, null);
                else
                    praise.setCompoundDrawables(drawable1, null, null, null);
                if (is_collected)
                    collect.setImageDrawable(ContentActivity.this.getResources().getDrawable(R.drawable.ic_action_fav_selected));
                else
                    collect.setImageDrawable(ContentActivity.this.getResources().getDrawable(R.drawable.ic_action_fav_normal));

            }
        });

        commentList.setAdapter(commentAdapter);
        commentList.setRefreshListener(this);
        commentAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showReplyDialog(commentAdapter.getData().get(position));
            }
        });
        commentAdapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemClick(int position) {
                showReplyDialog(commentAdapter.getData().get(position));
                return false;
            }


        });
       commentList.showRecycler();
        query=new BmobQuery<>();
        query.addWhereEqualTo("post", new BmobPointer(post));
       commentPresenter.loadComment(query);
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

    public void showReplyDialog(Comment comment) {
        ReplyDialogHelper helper = new ReplyDialogHelper();
        Dialog dialog = new AlertDialog.Builder(this)
                .setView(helper.getView())
                .setOnDismissListener(helper)
                .create();

        replyComment = comment;
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
                    is_reply = true;
                    InputMethodManager inputManager =

                            (InputMethodManager) content.getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);

                    inputManager.showSoftInput(content, 0);
                    content.setHint("回复 " + replyComment.getAuthor().getUsername() + ":");
                    break;
                case R.id.copy:
                    ClipData myClip;
                    myClip = ClipData.newPlainText("text", replyComment.getContent());
                    myClipboard.setPrimaryClip(myClip);
                    toast("已复制到剪贴板");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case MainActivity.REFRESH_PRAISE:
                Log.i("refresh","praise"+data.getStringExtra("post_id"));
                setResult(resultCode,data);
                if(data.getStringExtra("post_id").equals(post.getObjectId()))
                    if(data.getBooleanExtra("is_praised", false))
                    { praise.setTextColor(this.getResources().getColor(R.color.material_blue_500));
                    post.setPraise_count(post.getPraise_count() + 1);
                        praise.setText(post.getPraise_count()+"");
                    }
                    else {praise.setTextColor(this.getResources().getColor(android.R.color.black));
                        post.setPraise_count(post.getPraise_count() - 1);
                        praise.setText(post.getPraise_count()+"");}
                break;
            case MainActivity.REFRESH_COLLECTION:
                Log.i("refresh", "collection");
                setResult(resultCode,data);
                if(data.getStringExtra("post_id").equals(post.getObjectId()))
                if (data.getBooleanExtra("is_collected", false))
                    collect.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_fav_selected));
                else
                    collect.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_fav_normal));
                break;
            case MainActivity.REFRESH_COMMENT:
                setResult(resultCode, data);
                break;
            default:
                break;
        }}
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("tag", "onNewINtent执行了");
        setIntent(intent);
        init();
    }


}
