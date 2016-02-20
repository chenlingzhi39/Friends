package com.example.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.adapter.PostAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Focus;
import com.example.bean.Post;
import com.example.bean.User;
import com.example.listener.OnItemClickListener;
import com.example.listener.ScrollViewListener;
import com.example.refreshlayout.RefreshLayout;
import com.example.util.SimpleHandler;
import com.example.util.Utils;
import com.example.widget.AlphaView;
import com.example.widget.ObservableScrollView;
import com.example.widget.recyclerview.EasyRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2015/9/25.
 */
public class UserInfoActivity extends AppCompatActivity implements ScrollViewListener,RefreshLayout.OnRefreshListener {


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
    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.post_num)
    TextView postNum;
    @InjectView(R.id.btn_post)
    LinearLayout btnPost;
    @InjectView(R.id.focus_num)
    TextView focusNum;
    @InjectView(R.id.btn_focus)
    LinearLayout btnFocus;
    @InjectView(R.id.fans_num)
    TextView fansNum;
    @InjectView(R.id.btn_fans)
    LinearLayout btnFans;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.edit)
    Button edit;
    @InjectView(R.id.list)
    EasyRecyclerView collectionList;
    private User user;
    private static final int NOT_FOCUS = 0;
    private static final int BE_FOCUSED = 1;
    private static final int FOCUS = 2;
    private static final int FOCUS_EACH = 3;
    private int state = 0;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private int fans_num;
    private int focus_num;
    private int post_num;
    private String objectId;
    private ProgressDialog dialog;
    private ArrayList<Post> posts;
    private SparseArray<Boolean> is_praised;
    private SparseArray<Boolean> is_collected;
    private PostAdapter postAdapter;
    private int j=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.inject(this);
        collectionList.setRefreshEnabled(false);
        collectionList.setLayoutManager(new LinearLayoutManager(this));
        collectionList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        collectionList.showProgress();
        posts=new ArrayList<>();
        is_praised=new SparseArray<>();
        is_collected=new SparseArray<>();
        init();
        initQuery();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("用户");
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            );
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            toolbar.setPadding(0, Utils.getStatusBarHeight(this), 0, 0);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getStatusBarHeight(this) + Utils.getToolbarHeight(this));
            toolbarBackground.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getToolbarHeight(this));
            toolbarBackground.setLayoutParams(lp);
        }
        scrollView.setScrollViewListener(this);


    }

    @Override
    public void onFooterRefresh() {

    }

    @Override
    public void onHeaderRefresh() {
initQuery();
    }

    @OnClick(R.id.image)
    public void image() {
        if (user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
            Intent intent = new Intent(UserInfoActivity.this, SelectPicPopupWindow.class);
            intent.putExtra("isCrop", false);
            startActivityForResult(intent, 0);
        }
    }

    @OnClick(R.id.btn_post)
    public void btn_post() {
        Intent intent = new Intent(UserInfoActivity.this, PostListActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("post_num", post_num);
        startActivityForResult(intent, 0);

    }

    @OnClick(R.id.btn_focus)
    public void btn_focus() {
        Intent intent = new Intent(UserInfoActivity.this, FocusActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("focus_num", focus_num);
        startActivity(intent);
    }

    @OnClick(R.id.btn_fans)
    public void btn_fans() {
        Intent intent = new Intent(UserInfoActivity.this, FansActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("fans_num", fans_num);
        startActivity(intent);
    }

    @OnClick(R.id.edit)
    public void edit() {
        Log.i("status", state + "");
        if (!user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
            final Focus focus = new Focus();
            focus.setUser(MyApplication.getInstance().getCurrentUser());
            focus.setFocusUser(user);
            edit.setClickable(false);
            switch (state) {
                case NOT_FOCUS:
                case BE_FOCUSED:
                    focus.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            if (state == BE_FOCUSED) state = FOCUS_EACH;
                            else state = FOCUS;
                            edit.setText("已关注");
                            fans_num = fans_num + 1;
                            fansNum.setText(fans_num + "");
                            objectId = focus.getObjectId();
                            edit.setClickable(true);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            edit.setClickable(true);
                        }
                    });
                    break;
                case FOCUS:
                case FOCUS_EACH:
                    focus.setObjectId(objectId);
                    focus.delete(this, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            if (state == FOCUS) {
                                state = NOT_FOCUS;
                                edit.setText("+关注");
                            } else {
                                state = BE_FOCUSED;
                                edit.setText("互相关注");
                            }
                            fans_num = fans_num - 1;
                            fansNum.setText(fans_num + "");
                            Log.i("delete", "success");
                            edit.setClickable(true);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.i("delete", s);
                            edit.setClickable(true);
                        }
                    });
                    break;
            }
        } else {
            Intent intent = new Intent(UserInfoActivity.this, UserActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    private void init() {
        if (getIntent().getExtras() != null)
            user = (User) getIntent().getExtras().get("user");
        else user = MyApplication.getInstance().getCurrentUser();
        if (user.getHead() != null) {
            imageLoader.displayImage(user.getHead().getFileUrl(this), head);
        } else {
            head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        }
        image.post(new Runnable() {
            @Override
            public void run() {
                toolbarBackground.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background), image.getWidth(), image.getHeight());
            }
        });
        if (user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
            edit.setText("编辑");
            edit.setClickable(true);
        }
        if (user.getBackground() != null) {
            imageLoader.displayImage(user.getBackground().getFileUrl(this), image, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    FadeInBitmapDisplayer.animate(image, 1000);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap;
                            bitmap = imageLoader.loadImageSync(user.getBackground().getFileUrl(getApplicationContext()));
                            SimpleHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    toolbarBackground.setBitmap(bitmap, image.getWidth(), image.getHeight());
                                }
                            });
                        }
                    };
                    new Thread(runnable).start();
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });


        } else {
            image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background));
            image.post(new Runnable() {
                @Override
                public void run() {
                    toolbarBackground.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background), image.getWidth(), image.getHeight());
                }
            });

        }
        queryFocus();
        userName.setText(user.getUsername());
    }

    @Override
    public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {

        if (y >= 0 && y <= (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - buttons.getHeight())) {
            toolbarBackground.setAlpha(0);
            toolbar.setBackgroundColor(Color.argb(255 * y / (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this)), 0, 0, 0));
        }
        if (y >= (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - 2 * buttons.getHeight()) && y <= (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - buttons.getHeight()))

        {
            Log.i("long", (y - (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - 2 * buttons.getHeight())) + "");
            toolbarBackground.setAlpha(255 * (y - (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - 2 * buttons.getHeight())) / buttons.getHeight());
        }
        if (y > (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - buttons.getHeight())) {
            toolbarBackground.setAlpha(255);
        }
        if (y > (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this))) {
            toolbar.setBackgroundColor(Color.argb(255 * (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - buttons.getHeight()) / (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this)), 0, 0, 0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    public void queryFocus() {
        BmobQuery<Focus> query = new BmobQuery<>();
        final User user0 = new User();
        user0.setObjectId(user.getObjectId());
        if (!user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
            query.addWhereEqualTo("user", new BmobPointer(MyApplication.getInstance().getCurrentUser()));
            query.addWhereEqualTo("focus_user", new BmobPointer(user));
            query.findObjects(this, new FindListener<Focus>() {
                @Override
                public void onSuccess(List list) {
                    if (list.size() != 0) {
                        objectId = ((Focus) list.get(0)).getObjectId();
                        BmobQuery<Focus> query = new BmobQuery<>();
                        query.addWhereEqualTo("focus_user", new BmobPointer(MyApplication.getInstance().getCurrentUser()));
                        query.addWhereEqualTo("user", new BmobPointer(user0));
                        query.findObjects(getApplicationContext(), new FindListener<Focus>() {
                            @Override
                            public void onSuccess(List list) {
                                if (list.size() != 0)
                                    state = FOCUS_EACH;
                                else
                                    state = FOCUS;
                                edit.setText("已关注");

                                edit.setClickable(true);
                            }

                            @Override
                            public void onError(int i, String s) {
                            }
                        });
                    } else {

                        BmobQuery<Focus> query = new BmobQuery<>();
                        query.addWhereEqualTo("focus_user", new BmobPointer(MyApplication.getInstance().getCurrentUser()));
                        query.addWhereEqualTo("user", new BmobPointer(user0));
                        query.findObjects(getApplicationContext(), new FindListener<Focus>() {
                            @Override
                            public void onSuccess(List list) {
                                if (list.size() != 0) {
                                    state = BE_FOCUSED;
                                    edit.setText("相互关注");
                                } else {
                                    state = NOT_FOCUS;
                                    edit.setText("+关注");
                                }
                                edit.setClickable(true);
                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
        query = new BmobQuery<>();
        query.addWhereEqualTo("focus_user", new BmobPointer(user0));
        query.count(this, Focus.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                fans_num = i;
                fansNum.setText(fans_num + "");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(user0));
        query.count(this, Focus.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                focus_num = i;
                focusNum.setText(focus_num + "");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        BmobQuery<Post> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("author", new BmobPointer(user0));
        query1.count(this, Post.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                post_num = i;
                postNum.setText(post_num + "");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case MainActivity.REFRESH_PRAISE:
                Log.i("refresh", "praise");
                boolean praised = data.getBooleanExtra("is_praised", false);
                for (Post post : posts) {
                    if (post.getObjectId().equals(data.getStringExtra("post_id"))) {
                        if (praised)
                            post.setPraise_count(post.getPraise_count() + 1);
                        else
                            post.setPraise_count(post.getPraise_count() - 1);
                        is_praised.put(post.getId(), praised);
                        postAdapter.notifyDataSetChanged();
                        break;

                    }

                }

                break;
            case MainActivity.REFRESH_COLLECTION:
                Log.i("refresh", "collection");
                boolean collected = data.getBooleanExtra("is_collected", false);
                for (Post post : posts) {
                    if (post.getObjectId().equals(data.getStringExtra("post_id"))) {
                        is_collected.put(post.getId(), collected);
                        postAdapter.notifyDataSetChanged();
                        break;
                    }

                }

                break;
            case MainActivity.REFRESH_COMMENT:
                if (data.getExtras() != null) {
                    for (Post post : posts) {
                        if (post.getObjectId().equals(data.getStringExtra("post_id"))) {
                            post.setComment_count(post.getComment_count() + 1);
                            postAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }

                break;
            case RESULT_OK:
                dialog = new ProgressDialog(this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setTitle("上传中...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                final String path = data.getStringExtra("path");

                imageLoader.displayImage("file://" + path, image);
                File file = new File(path);
                final BmobFile bmobFile = new BmobFile(file);
                bmobFile.upload(this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        User user0 = new User();
                        user0.setBackground(bmobFile);
                        user0.update(getApplicationContext(), user.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                imageLoader.displayImage("file://" + path, image);
                                MyApplication.getInstance().setCurrentUser();
                                toolbarBackground.setBitmap(BitmapFactory.decodeFile(path), image.getWidth(), image.getHeight());
                                dialog.dismiss();
                                setResult(MainActivity.SAVE_OK);
                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }

                    @Override
                    public void onProgress(Integer value) {
                        dialog.setProgress(value);
                    }
                });
                break;
            case MainActivity.SAVE_OK:
                init();
                setResult(MainActivity.SAVE_OK);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("tag", "onNewINtent执行了");
        setIntent(intent);
        getIntent().putExtras(intent);
        init();
    }
    public void initQuery() {

        BmobQuery<Post> query=new BmobQuery<>();
        query.order("-id");
        query.include("author");
        query.setLimit(10);
        List<BmobQuery<Post>> queries = new ArrayList<BmobQuery<Post>>();
        if(user.getCollect_post_id().size() <= 10)
        {
            collectionList.setRefreshEnabled(false);
            j=user.getCollect_post_id().size();
        }
        else if(posts.size()==0)
        {
            collectionList.setHeaderEnabled(false);
            collectionList.setFooterEnabled(true);
            j=10;
        }
        else if(posts.size()!=0)
            if(user.getCollect_post_id().size()-posts.size()<=10)
            {collectionList.setRefreshEnabled(false);
                j=user.getCollect_post_id().size()-posts.size();
            }
        for(int i=posts.size();i< posts.size()+j ;i++) {
            BmobQuery<Post> eq = new BmobQuery<Post>();
            eq.addWhereEqualTo("objectId", user.getCollect_post_id().get(i));
            queries.add(eq);
        }

        query.or(queries);
        query.findObjects(this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list.size() > 0) {
                    flush(list);
                    if(posts.size()==0){
                        posts = (ArrayList<Post>) list;
                        postAdapter = new PostAdapter(posts, is_praised, is_collected, UserInfoActivity.this, false);
                        collectionList.setAdapter(postAdapter);}else{
                        posts.addAll(list);
                        postAdapter.notifyDataSetChanged();
                    }

                    postAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onClick(View view, Object item) {
                            Intent intent = new Intent(UserInfoActivity.this, ContentActivity.class);
                            intent.putExtra("post", posts.get((Integer) item));
                            intent.putExtra("isPraised", is_praised.get(posts.get((Integer) item).getId()));
                            intent.putExtra("isCollected", is_collected.get(posts.get((Integer) item).getId()));
                            startActivityForResult(intent, 0);

                        }
                    });
                }
                collectionList.showRecycler();
                Log.i("list_size",list.size()+"");
            }

            @Override
            public void onError(int i, String s) {
                collectionList.showError();
                Log.i("onerror",  "error");
            }
        });
    }
    public void flush(final List<Post> posts) {
        if (MyApplication.getInstance().getCurrentUser() != null) {
            SimpleHandler.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    setPraise(posts);
                    setCollection(posts);
                }
            });
        }
    }
    public void setPraise(List<Post> list) {

        for (final Post post : list) {
            if (MyApplication.getInstance().getCurrentUser() != null) {
                BmobQuery<Post> query = new BmobQuery<Post>();


                String[] praise_user_id = {MyApplication.getInstance().getCurrentUser().getObjectId()};

                query.addWhereEqualTo("objectId", post.getObjectId());
                query.addWhereContainsAll("praise_user_id", Arrays.asList(praise_user_id));

                query.findObjects(getApplicationContext(), new FindListener<Post>() {
                    @Override
                    public void onSuccess(List<Post> list) {
                        if (list.size() > 0) {
                            is_praised.append(post.getId(), true);
                            Log.i("objectid", post.getId() + "");
                            postAdapter.notifyDataSetChanged();
                        } else {

                            is_praised.append(post.getId(), false);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });


            }

            //DatabaseUtil.getInstance(getApplicationContext()).insertPraise(post);
        }

    }


    public void setCollection(List<Post> list) {
        List<String> collect_post_id = new ArrayList<String>();
        collect_post_id = MyApplication.getInstance().getCurrentUser().getCollect_post_id();
        if (collect_post_id != null) {
            for (final Post post : list) {
                if (collect_post_id.contains(post.getObjectId()))
                    is_collected.append(post.getId(), true);
                else
                    is_collected.append(post.getId(), false);
                postAdapter.notifyDataSetChanged();
            }
        }

    }

}
