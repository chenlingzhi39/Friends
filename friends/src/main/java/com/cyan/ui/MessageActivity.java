package com.cyan.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cyan.app.MyApplication;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.Comment;
import com.cyan.bean.Post;
import com.cyan.bean.RefreshData;
import com.cyan.bean.User;
import com.cyan.community.R;
import com.cyan.fragment.CommentFragment;
import com.cyan.fragment.ReplyFragment;
import com.cyan.module.comment.presenter.CommentPresenter;
import com.cyan.module.comment.presenter.CommentPresenterImpl;
import com.cyan.module.comment.view.SendCommentView;
import com.cyan.util.ActivityUtil;
import com.cyan.util.RxBus;
import com.cyan.util.StringUtils;
import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.daoexample.CommentToMe;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Record;
import de.greenrobot.daoexample.RecordDao;
import de.greenrobot.daoexample.ReplyToMe;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/1/13.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_message,
        toolbarTitle = R.string.message
)
public class MessageActivity extends BaseActivity implements SendCommentView{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.layout_tab)
    TabLayout layoutTab;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    Observable<CommentToMe> mCommentToMeObservable;
    @InjectView(R.id.content)
    EditText content;
    @InjectView(R.id.submit)
    Button submit;
    @InjectView(R.id.reply_container)
    LinearLayout replyContainer;
    Observable<ReplyToMe> mReplyToMeObservable;
    CommentPresenter commentPresenter;
    Comment replyComment;
    Post post;
    private SQLiteDatabase db;
    private DaoSession daoSession;
    private RecordDao recordDao;
    private DaoMaster daoMaster;
    private ReplyToMe reply;
    private CommentToMe comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        commentPresenter=new CommentPresenterImpl(this,null,this);
        viewpager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this));
        layoutTab.setupWithViewPager(viewpager);
        if (getIntent().getStringExtra("mode") != null)
            viewpager.setCurrentItem(getIntent().getStringExtra("mode").equals("comment") ? 0 : 1);
        mReplyToMeObservable = RxBus.get().register("replyContainer", ReplyToMe.class);
        mCommentToMeObservable = RxBus.get().register("commentContainer", CommentToMe.class);
        mReplyToMeObservable.subscribe(new Action1<ReplyToMe>() {
            @Override
            public void call(ReplyToMe replyToMe) {
                Log.i("container","reply");
                replyContainer.setVisibility(View.VISIBLE);
                ActivityUtil.showKeyboard(content);
                content.setFocusable(true);
                content.setHint("回复" + replyToMe.getUser_name());
                replyComment=new Comment();
                replyComment.setObjectId(replyToMe.getComment_id());
                replyComment.setContent(replyToMe.getComment_content());
                User user=new User();
                user.setObjectId(replyToMe.getUserid());
                replyComment.setAuthor(user);
                post=new Post();
                post.setObjectId(replyToMe.getPostid());
                reply = new ReplyToMe();
                if (MyApplication.getInstance().getCurrentUser().getHead() != null)
                    reply.setHead(MyApplication.getInstance().getCurrentUser().getHead().getFileUrl(getApplicationContext()));
                reply.setPost_content(replyToMe.getPost_content());
                reply.setUserid(MyApplication.getInstance().getCurrentUser().getObjectId());
                reply.setPostid(replyToMe.getPostid());
                reply.setUser_name(MyApplication.getInstance().getCurrentUser().getUsername());
                reply.setYourid(replyToMe.getUserid());
                reply.setReply_content(replyToMe.getComment_content());
                reply.setPost_author_id(replyToMe.getPost_author_id());
                reply.setPost_author_name(replyToMe.getPost_author_name());
            }
        });
        mCommentToMeObservable.subscribe(new Action1<CommentToMe>() {
            @Override
            public void call(CommentToMe commentToMe) {
                Log.i("container","comment");
                replyContainer.setVisibility(View.VISIBLE);
                ActivityUtil.showKeyboard(content);
                content.setFocusable(true);
                content.setHint("回复" + commentToMe.getUser_name());
                replyComment=new Comment();
                replyComment.setObjectId(commentToMe.getComment_id());
                replyComment.setContent(commentToMe.getComment_content());
                User user=new User();
                user.setObjectId(commentToMe.getUserid());
                replyComment.setAuthor(user);
                post=new Post();
                post.setObjectId(commentToMe.getPostid());
                reply = new ReplyToMe();
                if (MyApplication.getInstance().getCurrentUser().getHead() != null)
                    reply.setHead(MyApplication.getInstance().getCurrentUser().getHead().getFileUrl(getApplicationContext()));
                reply.setPost_content(commentToMe.getPost_content());
                reply.setUserid(MyApplication.getInstance().getCurrentUser().getObjectId());
                reply.setPostid(commentToMe.getPostid());
                reply.setUser_name(MyApplication.getInstance().getCurrentUser().getUsername());
                reply.setYourid(commentToMe.getUserid());
                reply.setReply_content(commentToMe.getComment_content());
                reply.setPost_author_id(MyApplication.getInstance().getCurrentUser().getObjectId());
                reply.setPost_author_name(MyApplication.getInstance().getCurrentUser().getUsername());
            }
        });
    }
    @OnClick(R.id.submit)
    public void submit() {
        if (MyApplication.getInstance().getCurrentUser() != null) {
            if (content.getText().toString().trim().equals("")) {
                Toast.makeText(this, "内容不能为空!", Toast.LENGTH_SHORT).show();
                return;
            }
            Comment comment = new Comment();
            if (replyComment != null)
                comment.setComment(replyComment);
            comment.setPost(post);
            comment.setContent(content.getText().toString());
            comment.setAuthor(MyApplication.getInstance().getCurrentUser());
            commentPresenter.sendComment(comment);
        } else {
            toast("请登录");
        }
    }
    @Override
    public void dismissDialog() {
        pd.dismiss();
    }

    @Override
    public void refresh(final Comment comment) {
        // TODO Auto-generated method stub
        post.increment("comment_count", 1);
        post.update(getApplicationContext(), new UpdateListener() {
            @Override
            public void onSuccess() {
                BmobPushManager bmobPush = new BmobPushManager(MessageActivity.this);
                BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
                Gson gson = new Gson();
                    reply.setComment_content(((Comment) comment).getContent());
                    reply.setComment_id(comment.getObjectId());
                    reply.setCreate_time(StringUtils.toDate(comment.getCreatedAt()));
                    String message = "{\"replyToMe\":" + gson.toJson(reply) + "}";
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

                  /*  Intent intent = new Intent();
                    intent.putExtra("post_id", post.getObjectId());
                    setResult(MainActivity.REFRESH_COMMENT, intent);*/
                RefreshData data=new RefreshData(post.getObjectId(),"comment",null);
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
                content.setText("");
                content.setHint("");
                replyContainer.setVisibility(View.GONE);
                replyComment=null;
            }
        });
    }

    @Override
    public void showDialog() {
        pd= ProgressDialog.show(MessageActivity.this, null, "正在发送");
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

    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"评论我的", "回复我的"};
        private Context context;

        public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;

        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CommentFragment.newInstance();

                case 1:
                    return ReplyFragment.newInstance();

                default:
                    return null;

            }

        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("tag", "onNewINtent执行了");
        setIntent(intent);
        viewpager.setCurrentItem(getIntent().getStringExtra("mode").equals("comment") ? 0 : 1);
        getIntent().putExtras(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister("commentContainer", mCommentToMeObservable);
        RxBus.get().unregister("replyContainer",mReplyToMeObservable);
    }
}
