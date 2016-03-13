package com.cyan.ui;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cyan.adapter.PostAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.Focus;
import com.cyan.bean.Post;
import com.cyan.bean.User;
import com.cyan.community.R;
import com.cyan.listener.OnItemClickListener;
import com.cyan.module.file.presenter.FilePresenter;
import com.cyan.module.file.presenter.FilePresenterImpl;
import com.cyan.module.file.view.SendFileView;
import com.cyan.module.post.presenter.PostPresenter;
import com.cyan.module.post.presenter.PostPresenterImpl;
import com.cyan.module.post.view.LoadPostView;
import com.cyan.module.user.presenter.UserPresenter;
import com.cyan.module.user.presenter.UserPresenterImpl;
import com.cyan.module.user.view.UserView;
import com.cyan.refreshlayout.RefreshLayout;
import com.cyan.util.Utils;
import com.cyan.widget.AlphaView;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2015/9/25.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_userinfo,
        toolbarTitle = R.string.user
)
public class UserInfoActivity extends BaseActivity implements RefreshLayout.OnRefreshListener, View.OnClickListener, LoadPostView, SendFileView, UserView {
    ImageView image;
    LinearLayout buttons;
    CircleImageView head;
    TextView postNum;
    LinearLayout btnPost;
    TextView focusNum;
    LinearLayout btnFocus;
    TextView fansNum;
    LinearLayout btnFans;
    TextView userName;
    Button edit;
    TextView title;
    RelativeLayout content;
    @InjectView(R.id.list)
    EasyRecyclerView collectionList;
    @InjectView(R.id.toolbar_background)
    AlphaView toolbarBackground;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    private User user;
    private static final int NOT_FOCUS = 0;
    private static final int BE_FOCUSED = 1;
    private static final int FOCUS = 2;
    private static final int FOCUS_EACH = 3;
    private int state = 0;
    private int fans_num;
    private int focus_num;
    private int post_num;
    private String objectId;
    private ProgressDialog dialog, pd;
    private ArrayList<Post> posts;
    private SparseArray<Boolean> is_praised;
    private SparseArray<Boolean> is_collected;
    private PostAdapter postAdapter;
    private int j = 0;
    private View headerView;
    private View footerView;
    private DisplayMetrics displayMetrics;
    private BmobQuery<Post> query;
    private PostPresenter postPresenter;
    private UserPresenter userPresenter;
    private FilePresenter filePresenter;
    private String path;
    private int y = 0;
    private int hideHeight;
    private RelativeLayout.LayoutParams lp,lp1;
    private float yDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        collectionList.setLayoutManager(new LinearLayoutManager(this));
        collectionList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        collectionList.setRefreshListener(this);

        if (Utils.checkDeviceHasNavigationBar(getApplicationContext())) {
            footerView = getLayoutInflater().inflate(R.layout.footer, null);
            footerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.getNavigationBarHeight(UserInfoActivity.this)));
        }
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            );
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.argb(100, 0, 0, 0));
            toolbar.setPadding(0, Utils.getStatusBarHeight(this), 0, 0);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getStatusBarHeight(this) + Utils.getToolbarHeight(this));
            toolbarBackground.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getToolbarHeight(this));
            toolbarBackground.setLayoutParams(lp);
        }

        postPresenter = new PostPresenterImpl(this, this);
        userPresenter = new UserPresenterImpl(this, this);
        filePresenter = new FilePresenterImpl(this, this);
        collectionList.getmErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
                initQuery();
            }
        });
        init();
        initQuery();

    }


    @Override
    public void toastSendSuccess() {
        toast("更新成功");
        setResult(MainActivity.SAVE_OK);
        MyApplication.getInstance().setCurrentUser();
        toolbarBackground.setBitmap(BitmapFactory.decodeFile(path), displayMetrics.widthPixels/2 , displayMetrics.heightPixels/2 );
    }

    @Override
    public void toastSendFailure(int code, String msg) {
        toast("更新失败");
    }

    @Override
    public void showCircleDialog() {
        pd = ProgressDialog.show(UserInfoActivity.this, null, "正在提交");
    }

    @Override
    public void dismissCircleDialog() {
        pd.dismiss();
    }

    @Override
    public void dismissHorizonalDialog() {
        dialog.dismiss();
    }

    @Override
    public void updateHorizonalDialog(Integer i) {
        dialog.setProgress(i);
    }

    @Override
    public void toastUploadSuccess() {
        toast("上传成功");
    }

    @Override
    public void toastUploadFailure(int i, String s) {
        toast("上传失败" + s);
    }

    @Override
    public void showHorizonalDialog() {
        dialog = new ProgressDialog(UserInfoActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("上传中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


    }

    @Override
    public void getFile(BmobFile bmobFile) {
        user.setBackground(bmobFile);
        userPresenter.update(user);
    }

    @Override
    public void addPosts(List<Post> list) {
        if (list.size() > 0)
            if (posts.size() == 0) {
                posts = (ArrayList<Post>) list;
                postAdapter = new PostAdapter(posts, is_praised, is_collected, UserInfoActivity.this);
                PFhelper.flush(this, is_praised, is_collected, list, postAdapter);
                postAdapter.setHeaderView(headerView);
                if (footerView != null)
                    postAdapter.setFooterView(footerView);
                collectionList.setAdapter(postAdapter);
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
            } else {
                PFhelper.flush(this, is_praised, is_collected, list, postAdapter);
                posts.addAll((ArrayList<Post>) list);
                postAdapter.notifyDataSetChanged();

            }
    }

    @Override
    public void stopLoadmore() {
        collectionList.setFooterRefreshing(false);
    }

    @Override
    public void stopRefresh() {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showError() {
        collectionList.showError();
    }

    @Override
    public void showProgress(Boolean b) {
        if (b)
            collectionList.showProgress();
    }

    @Override
    public void showRecycler() {
        collectionList.showRecycler();
    }

    @Override
    public void onFooterRefresh() {
        initQuery();
    }

    @Override
    public void onHeaderRefresh() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:
                if (user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
                    Intent intent = new Intent(UserInfoActivity.this, SelectPicPopupWindow.class);
                    intent.putExtra("isCrop", false);
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.btn_post:
                Intent intent = new Intent(UserInfoActivity.this, PostListActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("post_num", post_num);
                startActivityForResult(intent, 0);
                break;
            case R.id.btn_focus:
                intent = new Intent(UserInfoActivity.this, FocusActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("focus_num", focus_num);
                startActivity(intent);
                break;
            case R.id.btn_fans:
                intent = new Intent(UserInfoActivity.this, FansActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("fans_num", fans_num);
                startActivity(intent);
                break;
            case R.id.edit:
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
                    intent = new Intent(UserInfoActivity.this, UserActivity.class);
                    startActivityForResult(intent, 0);
                }
                break;
        }
    }


    private void init() {
        if (headerView == null) {
            headerView = getLayoutInflater().inflate(R.layout.image_header, null,true);
            head = (CircleImageView) headerView.findViewById(R.id.head);
            image = (ImageView) headerView.findViewById(R.id.image);
            edit = (Button) headerView.findViewById(R.id.edit);
            userName = (TextView) headerView.findViewById(R.id.user_name);
            buttons = (LinearLayout) headerView.findViewById(R.id.buttons);
            postNum = (TextView) headerView.findViewById(R.id.post_num);
            btnPost = (LinearLayout) headerView.findViewById(R.id.btn_post);
            focusNum = (TextView) headerView.findViewById(R.id.focus_num);
            btnFocus = (LinearLayout) headerView.findViewById(R.id.btn_focus);
            fansNum = (TextView) headerView.findViewById(R.id.fans_num);
            btnFans = (LinearLayout) headerView.findViewById(R.id.btn_fans);
            title = (TextView) headerView.findViewById(R.id.title);
            content=(RelativeLayout)headerView.findViewById(R.id.content);
            image.setOnClickListener(this);
            btnFocus.setOnClickListener(this);
            btnFans.setOnClickListener(this);
            btnPost.setOnClickListener(this);
            edit.setOnClickListener(this);
        }
        posts = new ArrayList<>();
        is_praised = new SparseArray<>();
        is_collected = new SparseArray<>();
        postAdapter = new PostAdapter(posts, is_praised, is_collected, UserInfoActivity.this);
        postAdapter.setHeaderView(headerView);
        if (footerView != null)
            postAdapter.setFooterView(footerView);
        collectionList.setAdapter(postAdapter);
        collectionList.showRecycler();
        if (getIntent().getExtras() != null)
            user = (User) getIntent().getExtras().get("user");
        else user = MyApplication.getInstance().getCurrentUser();
        if (user.getHead() != null) {
            Glide.with(this).load(user.getHead().getFileUrl(this)).into(head);
        } else {
            head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        }
        if (user.getCollect_post_id() != null)
            title.setText("收藏的作品" + " (" + user.getCollect_post_id().size() + ")");
        else title.setText("收藏的作品" + " (0)");
        if (user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
            edit.setText("编辑");
            edit.setClickable(true);
        }


        if (user.getBackground() != null) {
            Glide.with(this).load(user.getBackground().getFileUrl(this)).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Log.i("bitmap_width", resource.getWidth() + "");
                    Log.i("height", 2 * displayMetrics.heightPixels / 3 + "");
                    if (resource.getHeight() * displayMetrics.widthPixels / resource.getWidth() > displayMetrics.heightPixels / 2 && resource.getHeight() * displayMetrics.widthPixels / resource.getWidth() < 2 * displayMetrics.heightPixels / 3) {
                        Log.i("height", resource.getHeight() * displayMetrics.widthPixels / resource.getWidth() + "");
                        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, resource.getHeight() * displayMetrics.widthPixels / resource.getWidth());
                        lp.setMargins(0, (displayMetrics.widthPixels / 2 - resource.getHeight() * displayMetrics.widthPixels / resource.getWidth()) / 2, 0, (displayMetrics.widthPixels / 2 - resource.getHeight() * displayMetrics.widthPixels / resource.getWidth()) / 2);
                        hideHeight = (displayMetrics.widthPixels / 2 - resource.getHeight() * displayMetrics.widthPixels / resource.getWidth()) / 2;
                        image.setLayoutParams(lp);
                        lp1=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, resource.getHeight() * displayMetrics.widthPixels / resource.getWidth());
                        lp1.setMargins(0,displayMetrics.widthPixels / 2 - resource.getHeight() * displayMetrics.widthPixels / resource.getWidth(),0,0);
                        content.setLayoutParams(lp1);
                    }
                    if (resource.getHeight() * displayMetrics.widthPixels / resource.getWidth() > 2 * displayMetrics.heightPixels / 3) {
                        Log.i("height", resource.getHeight() * displayMetrics.widthPixels / resource.getWidth() + "");
                        lp = new  RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 2 * displayMetrics.heightPixels / 3);
                        lp.setMargins(0, -displayMetrics.heightPixels / 12, 0,-displayMetrics.heightPixels / 12);
                        hideHeight = -displayMetrics.heightPixels / 12;
                        image.setLayoutParams(lp);
                        lp1=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  2 * displayMetrics.heightPixels / 3);
                        lp1.setMargins(0, -displayMetrics.heightPixels / 6,0,0);
                        content.setLayoutParams(lp1);
                    }
                    image.setImageBitmap(resource);
                    image.post(new Runnable() {
                        @Override
                        public void run() {

                            toolbarBackground.setBitmap(resource, displayMetrics.widthPixels, displayMetrics.heightPixels / 2);
                        }
                    });

                }
            });
        } else {
            image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background));
            image.post(new Runnable() {
                @Override
                public void run() {
                    toolbarBackground.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background), displayMetrics.widthPixels, displayMetrics.heightPixels / 2);
                }
            });

        }
        final LinearLayoutManager lm = (LinearLayoutManager) collectionList.getRecyclerView().getLayoutManager();
        collectionList.setOnScrollListener(
                new RecyclerView.OnScrollListener() {


                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        y += dy;
                        Log.i("re", y + "");
                        Log.i("buttons_height", buttons.getHeight() + "");
                        Log.i("toolbar", Utils.getToolbarHeight(UserInfoActivity.this) + "");
                        Log.i("status", Utils.getStatusBarHeight(UserInfoActivity.this) + "");
                        if (y >= 0 && y <= (displayMetrics.heightPixels/2 - Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this) - buttons.getHeight())) {
                            toolbarBackground.setAlpha(0);
                            toolbar.setBackgroundColor(Color.argb(255 * y / (displayMetrics.heightPixels/2 - Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this)), 0, 0, 0));
                        }
                        if (y >= (displayMetrics.heightPixels/2- Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this) - 2 * buttons.getHeight()) && y <= (displayMetrics.heightPixels/2 - Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this) - buttons.getHeight()))

                        {
                            Log.i("long", (y - (displayMetrics.heightPixels/2  - Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this) - 2 * buttons.getHeight())) + "");
                            toolbarBackground.setAlpha(255 * (y - (displayMetrics.heightPixels/2 - Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this) - 2 * buttons.getHeight())) / buttons.getHeight());
                        }
                        if (y > (displayMetrics.heightPixels/2  - Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this) - buttons.getHeight())) {
                            toolbarBackground.setAlpha(255);
                        }
                        if (y > (displayMetrics.heightPixels/2  - Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this))) {
                            toolbar.setBackgroundColor(Color.argb(255 * (displayMetrics.heightPixels/2  - Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this) - buttons.getHeight()) / (displayMetrics.heightPixels/2  - Utils.getStatusBarHeight(UserInfoActivity.this) - Utils.getToolbarHeight(UserInfoActivity.this)), 0, 0, 0));
                        }
                    }
                });
        collectionList.setOnTouchListener(new View.OnTouchListener() {
            boolean IS_DOWN=true;
            boolean IS_PULL = false;
            boolean IS_RELEASE=false;
            int distance=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("isfirst",(lm.findViewByPosition(lm.findFirstVisibleItemPosition()).getTop() == 0 && lm.findFirstVisibleItemPosition() == 0)+"");
                if (lm.findViewByPosition(lm.findFirstVisibleItemPosition()).getTop() == 0 && lm.findFirstVisibleItemPosition() == 0||IS_PULL) {
                    Log.i("touchevent",event.getAction()+"");
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            yDown = event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(!IS_RELEASE)
                            { Log.i("hideheight",hideHeight+"");
                            Log.i("distance",distance+"");
                            Log.i("ydown",yDown+"");
                            if(IS_DOWN)yDown=event.getRawY();
                            IS_DOWN=false;
                            float yMove = event.getRawY();
                            Log.i("ymove",yMove+"");
                            distance = (int) (yMove - yDown);
                            if (distance <= 0||distance/2>=-hideHeight) {
                                return false;
                            }
                            IS_PULL = true;
                            lp.setMargins(0,(distance / 2) + hideHeight,0,(distance / 2) + hideHeight);
                                lp1.setMargins(0,distance+hideHeight*2,0,0);
                          // lp.bottomMargin=lp.topMargin=(distance / 2) + hideHeight;
                            image.setLayoutParams(lp);
                            content.setLayoutParams(lp1);}
                            break;
                        case MotionEvent.ACTION_UP:
                        default:
                            if (IS_PULL)
                            {IS_RELEASE=true;
                                ValueAnimator  mAnimator = ValueAnimator.ofInt(lp.topMargin,  hideHeight);
                            mAnimator.setDuration(100);
                            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    lp.setMargins(0,(int)animation.getAnimatedValue(),0, (int)animation.getAnimatedValue());
                                    // lp.bottomMargin=lp.topMargin= integer;
                                    if ((int)animation.getAnimatedValue() <= hideHeight) {
                                        IS_PULL = false;
                                        IS_DOWN=true;
                                        IS_RELEASE=false;
                                    }
                                    image.setLayoutParams(lp);
                                    lp1.setMargins(0,2*lp.topMargin,0,0);
                                    content.setLayoutParams(lp1);
                                }

                            });
                            mAnimator.start();
                            /*   Observable.create(new Observable.OnSubscribe<Integer>() {
                                @Override
                                public void call(Subscriber<? super Integer> subscriber) {
                                    int topMargin = lp.topMargin;
                                    while (true) {
                                        topMargin = topMargin - 1;
                                        if (topMargin <= hideHeight) {
                                            topMargin = hideHeight;
                                            //lp.setMargins(0,hideHeight,0, hideHeight);
                                            // lp.bottomMargin=lp.topMargin= hideHeight;
                                            //image.setLayoutParams(lp);
                                            subscriber.onNext(topMargin);
                                            IS_PULL = false;
                                            IS_DOWN=true;
                                            break;
                                        }
                                        try {
                                            Thread.sleep(10);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        subscriber.onNext(topMargin);


                                    }
                                }
                            }).observeOn(Schedulers.newThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                                    @Override
                                    public void call(Integer integer) {
                                        lp.setMargins(0,integer,0, integer);
                                       // lp.bottomMargin=lp.topMargin= integer;
                                        image.setLayoutParams(lp);

                                    }
                                });*/

                                return true;}
                            break;
                    }

                }
                return false;
           }
        });
        queryFocus();
        userName.setText(user.getUsername());
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
                    setResult(resultCode, data);
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
                setResult(resultCode, data);
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
                setResult(resultCode, data);
                break;
            case RESULT_OK:
                path = data.getStringExtra("path");
                Glide.with(this).load("file://" + path).into(image);
                filePresenter.sendFile(new File(path));
                break;
            case MainActivity.SAVE_OK:
                init();
                setResult(resultCode);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("tag", "onNewINtent执行了");
        if (intent.getExtras().get("user") != user) {
            setIntent(intent);
            getIntent().putExtras(intent);
            posts = new ArrayList<>();
            y = 0;
            toolbar.setBackgroundColor(0x0000000);
            init();
            initQuery();
        }
    }

    public void initQuery() {

        if (user.getCollect_post_id() == null) {
            collectionList.setRefreshEnabled(false);
            return;
        }
        if (user.getCollect_post_id().size() <= 10) {
            collectionList.setRefreshEnabled(false);
            j = user.getCollect_post_id().size();
        } else {
            if (posts.size() == 0) {
                collectionList.setHeaderEnabled(false);
                collectionList.setFooterEnabled(true);
                j = 10;
            } else {
                if (user.getCollect_post_id().size() - posts.size() <= 10) {
                    collectionList.setRefreshEnabled(false);
                    j = user.getCollect_post_id().size() - posts.size();
                } else {
                    j = j + 10;
                }
            }
        }
        BmobQuery<Post> query = new BmobQuery<>();
        query.order("-id");
        query.include("author");
        query.setLimit(10);
        List<BmobQuery<Post>> queries = new ArrayList<BmobQuery<Post>>();
        for (int i = posts.size(); i < posts.size() + j; i++) {
            BmobQuery<Post> eq = new BmobQuery<Post>();
            eq.addWhereEqualTo("objectId", user.getCollect_post_id().get(i));
            queries.add(eq);
        }
        query.or(queries);
        postPresenter.loadPost(query);
    }


}

