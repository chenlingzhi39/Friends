package com.example.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.PostAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.MyBmobInstallation;
import com.example.bean.Post;
import com.example.bean.User;
import com.example.listener.HidingScrollListener;
import com.example.listener.OnItemClickListener;
import com.example.refreshlayout.RefreshLayout;
import com.example.util.SimpleHandler;
import com.example.util.Utils;
import com.example.widget.recyclerview.FastScroller;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by Administrator on 2015/9/21.
 */
public class MainActivity extends AppCompatActivity implements RefreshLayout.OnRefreshListener {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.id_nv_menu)
    NavigationView idNvMenu;
    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.content_list)
    RecyclerView contentList;
    @InjectView(R.id.refresh_layout)
    RefreshLayout refreshLayout;
    @InjectView(R.id.submit)
    FloatingActionButton submit;
    @InjectView(R.id.mToolbarContainer)
    LinearLayout mToolbarContainer;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.fast_scroller)
    FastScroller fastScroller;
    @InjectView(R.id.hide)
    View hide;
    private ActionBarDrawerToggle mDrawerToggle;

    public static String TAG = "bmob";
    User myUser;
    public static final int SAVE_OK = 2;
    public static final int SUBMIT_OK = 3;
    public static final int LOGOUT = 4;
    public static final int REFRESH_PRAISE = 5;
    public static final int REFRESH_COLLECTION = 6;
    public final static int REFRESH_COMMENT = 7;
    ImageView head, background;
    TextView username;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Post> posts;
    PostAdapter postAdapter;
    private int mToolbarHeight;
    private View footerView;
    private Boolean hasNavigationBar;
    private SparseArray<Boolean> is_praised;
    private SparseArray<Boolean> is_collected;
    private MenuItem menuItem, messages, records, collection, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        initRefreshLayout();
        // setFullTouch();
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);

        hasNavigationBar = Utils.checkDeviceHasNavigationBar(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setPadding(0, Utils.getStatusBarHeight(getApplicationContext()), 0, 0);
        else hide.setVisibility(View.GONE);
        if (hasNavigationBar) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) submit.getLayoutParams();
            lp.setMargins(32, 32, 32, 32 + Utils.getNavigationBarHeight(MainActivity.this));
            submit.setLayoutParams(lp);
        }
        posts = new ArrayList<Post>();
        initHead();
        hide.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, Utils.getStatusBarHeight(this)));
        int paddingTop = Utils.getToolbarHeight(this) + Utils.getStatusBarHeight(this);
        if (Build.VERSION.SDK_INT >= 21)
            contentList.setPadding(contentList.getPaddingLeft(), paddingTop, contentList.getPaddingRight(), contentList.getPaddingBottom());
        else
            contentList.setPadding(contentList.getPaddingLeft(), Utils.getToolbarHeight(this), contentList.getPaddingRight(), contentList.getPaddingBottom());
        mLayoutManager = new LinearLayoutManager(this);
        contentList.setLayoutManager(mLayoutManager);
        mToolbarHeight = Utils.getToolbarHeight(this);
        is_praised = new SparseArray<Boolean>();
        is_collected = new SparseArray<Boolean>();
        fastScroller.attachToRecyclerView(contentList);

        refreshQuery();


        contentList.addOnScrollListener(new HidingScrollListener(this) {

            @Override
            public void onMoved(int distance) {
                mToolbarContainer.setTranslationY(-distance);

            }

            @Override
            public void onShow() {
                mToolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            }

            @Override
            public void onHide() {
                mToolbarContainer.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();

            }

        });


    }

    @Override
    public void onFooterRefresh() {

        loadMoreQuery();


    }

    @Override
    public void onHeaderRefresh() {

        refreshQuery();


    }

    public void initHead() {

        if (messages == null) {
            settings = idNvMenu.getMenu().getItem(3);
            messages = idNvMenu.getMenu().getItem(1);
            records = idNvMenu.getMenu().getItem(2);
            collection = idNvMenu.getMenu().getItem(0);
            idNvMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_collection:

                            Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                            startActivityForResult(intent, 0);

                            break;
                        case R.id.nav_messages:
                            intent = new Intent(MainActivity.this, MessageActivity.class);
                            startActivityForResult(intent, 0);
                            break;
                        case R.id.nav_records:
                            intent = new Intent(MainActivity.this, RecordActivity.class);
                            startActivityForResult(intent, 0);
                            break;
                        case R.id.nav_settings:
                            intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivityForResult(intent, 0);
                            break;
                    }
                    //drawerLayout.closeDrawers();
                    return false;
                }
            });
            head = (ImageView) idNvMenu.findViewById(R.id.head);
            head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyApplication.getInstance().getCurrentUser() != null) {
                        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                        startActivityForResult(intent, 0);
                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, 0);
                    }
                }
            });
            background = (ImageView) idNvMenu.findViewById(R.id.image);
            username = (TextView) idNvMenu.findViewById(R.id.id_username);
        }
        myUser = MyApplication.getInstance().getCurrentUser();
        if (myUser != null) {
            messages.setEnabled(true);
            records.setEnabled(true);
            collection.setEnabled(true);
            username.setText(myUser.getUsername());
            if (myUser.getHead() != null) {
                imageLoader.displayImage(myUser.getHead().getFileUrl(getApplicationContext()), head, MyApplication.getInstance().getOptions());
            } else {
                head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            }
            if (myUser.getBackground() != null)
                imageLoader.displayImage(myUser.getBackground().getFileUrl(this), background);
        } else {
            messages.setEnabled(false);
            records.setEnabled(false);
            collection.setEnabled(false);
            username.setText("请登录");
            head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            background.setImageDrawable(getResources().getDrawable(R.drawable.background));
        }
    }

    static class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                initHead();
                if (posts.size() != 0) {
                    setPraise(posts);
                    setCollection(posts);
                    postAdapter.notifyDataSetChanged();
                }
                Log.i("userId", myUser.getObjectId());
                refreshInstalllation(myUser.getObjectId());
                break;
            case LOGOUT:
                MyApplication.getInstance().clearCurrentUser();
                initHead();
                is_collected.clear();
                is_praised.clear();
                postAdapter.notifyDataSetChanged();
                refreshInstalllation(null);
                BmobInstallation.getCurrentInstallation(MyApplication.getInstance()).delete(MyApplication.getInstance());
                break;
            case SAVE_OK:
                initHead();
                for (Post post : posts) {
                    if (post.getAuthor().getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId()))
                        post.setAuthor(MyApplication.getInstance().getCurrentUser());
                }
                postAdapter.notifyDataSetChanged();
                break;
            case SUBMIT_OK:
                refreshLayout.setHeaderRefreshing(true);
                refreshQuery();
                break;
            case REFRESH_PRAISE:
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
            case REFRESH_COLLECTION:
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
            case REFRESH_COMMENT:
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
            default:
                break;
        }

    }


    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }


    @OnClick(R.id.submit)
    public void submit() {
        User user = User.getCurrentUser(MainActivity.this, User.class);
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivityForResult(intent, 0);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    public void refreshQuery() {
        BmobQuery<Post> query = new BmobQuery<>();
        if (posts.size() > 0)
            query.addWhereGreaterThan("id", posts.get(0).getId());
        //query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setLimit(10);
        query.order("-id");
        query.include("author");
        query.findObjects(this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list.size() != 0) {
                    if (posts.size() > 0) {


                        flush(list);
                        posts.addAll(0, (ArrayList<Post>) list);
                        postAdapter.notifyDataSetChanged();


                    } else {
                        posts = (ArrayList<Post>) list;
                        postAdapter = new PostAdapter(posts, is_praised, is_collected, MainActivity.this);
                        flush(list);
//                        if (MyApplication.getInstance().getCurrentUser() != null) {
//                            setPraise(posts);
//                            setCollection(posts);
//                            // list = DatabaseUtil.getInstance(getApplicationContext()).setPraise(list);
//                        }


                        if (hasNavigationBar) {
                            footerView = getLayoutInflater().inflate(R.layout.footer, null);
                            footerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.getNavigationBarHeight(MainActivity.this)));
                            postAdapter.setFooterView(footerView);
                        }
                        contentList.setAdapter(postAdapter);
                        SimpleHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        postAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onClick(View view, Object item) {

                                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                                intent.putExtra("post", posts.get((Integer) item));
                                intent.putExtra("isPraised", is_praised.get(posts.get((Integer) item).getId()));
                                intent.putExtra("isCollected", is_collected.get(posts.get((Integer) item).getId()));
                                startActivityForResult(intent, 0);
                            }
                        });


                    }
                } else {
                    SimpleHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }

                toast("查询成功：共" + list.size() + "条数据。");
            }

            @Override
            public void onError(int i, String s) {

                toast("查询失败：" + s);
            }
        });
        refreshLayout.setHeaderRefreshing(false);
        stopRefreshIconAnimation(menuItem);
    }

    public void loadMoreQuery() {
        if (posts.size() > 0) {
            BmobQuery<Post> query = new BmobQuery<Post>();
            //query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.addWhereLessThan("id", posts.get(posts.size() - 1).getId());
            query.setLimit(10);
            query.order("-id");
            query.include("author");
            query.findObjects(this, new FindListener<Post>() {
                @Override
                public void onSuccess(final List<Post> list) {
                    if (list.size() != 0) {


                        posts.addAll((ArrayList<Post>) list);
                        postAdapter.notifyDataSetChanged();
                        flush(list);

                        //list = DatabaseUtil.getInstance(getApplicationContext()).setPraise(list);

                    }

                    toast("查询成功：共" + list.size() + "条数据。");
                }

                @Override
                public void onError(int i, String s) {

                }
            });
            refreshLayout.setFooterRefreshing(false);
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.action_refresh);
        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(R.id.action_refresh, 0);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                startRefreshIconAnimation(item);
                refreshQuery();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void initRefreshLayout() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        refreshLayout.setProgressViewOffset(false, Utils.getStatusBarHeight(this) + Utils.getToolbarHeight(this) + 64, (int) (Utils.getStatusBarHeight(this) + Utils.getToolbarHeight(this) + 64 * dm.density));
        refreshLayout.setProgressViewEndTarget(false, Utils.getStatusBarHeight(this) + Utils.getToolbarHeight(this) + 64);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setFooterColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        refreshLayout.setHeaderColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    private void startRefreshIconAnimation(MenuItem menuItem) {
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_icon_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        menuItem.getActionView().startAnimation(rotation);
        menuItem.getActionView().setClickable(false);
    }

    private void stopRefreshIconAnimation(MenuItem menuItem) {
        if (menuItem != null) {
            menuItem.getActionView().clearAnimation();
            menuItem.getActionView().setClickable(true);
        }
    }

    private void refreshInstalllation(final String userId) {
        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(MyApplication.getInstance()));
        query.findObjects(this, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                // TODO Auto-generated method stub
                if (object.size() > 0) {
                    MyBmobInstallation mbi = object.get(0);
                    mbi.setUid(userId);
                    mbi.update(MainActivity.this, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            // 使用推送服务时的初始化操作
                            Log.i("bmob", "设备信息更新成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "设备信息更新失败:" + msg);
                        }
                    });
                } else {
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        BmobQuery.clearAllCachedResults(this);

    }

    private void setFullTouch() {
        Field mDragger = null;
        try {
            mDragger = drawerLayout.getClass().getDeclaredField(
                    "mLeftDragger"); //mRightDragger for right obviously
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mDragger.setAccessible(true);
        ViewDragHelper draggerObj = null;
        try {
            draggerObj = (ViewDragHelper) mDragger
                    .get(drawerLayout);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Field mEdgeSize = null;
        try {
            mEdgeSize = draggerObj.getClass().getDeclaredField(
                    "mEdgeSize");
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mEdgeSize.setAccessible(true);
        int edge = 0;
        try {
            edge = mEdgeSize.getInt(draggerObj);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            Log.i("widthpixels", dm.widthPixels + "");
            Log.i("heightpixels", dm.heightPixels + "");
            Log.i("density", dm.density + "");
            Log.i("densityDpi", dm.densityDpi + "");
            mEdgeSize.setInt(draggerObj, dm.widthPixels); //optimal value as for me, you may set any constant in dp
            //You can set it even to the value you want like mEdgeSize.setInt(draggerObj, 150); for 150dp
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
