package com.example.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.PostAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.example.bean.User;
import com.example.refreshlayout.RefreshLayout;
import com.example.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import static com.example.administrator.myapplication.R.layout.activity_main;

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
    private ActionBarDrawerToggle mDrawerToggle;
    public static String APPID = "9245da2bae59a43d2932e1324875137a";
    public static String TAG = "bmob";
    User myUser;
    public static final int SAVE_OK = 2;
    public static final int SUBMIT_OK = 3;
    ImageView head;
    TextView username;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Post> posts;
    PostAdapter postAdpater;
    private int mToolbarHeight;
    private int firstid, lastid;
    public final static int REFRESH_START = 4;
    public final static int REFRESH_FINISH = 5;
    public final static int LOAD_MORE_START = 6;
    public final static int LOAD_MORE_FINISH = 7;
    private View footerView;
    private Boolean hasNavigationBar;
    private SparseArray<Boolean> is_praised;
    private SparseArray<Boolean> is_collected;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case REFRESH_START:
                    refreshLayout.setHeaderRefreshing(true);
                    break;
                case REFRESH_FINISH:
                    refreshLayout.setHeaderRefreshing(false);
                    break;
                case LOAD_MORE_START:
                    refreshLayout.setFooterRefreshing(true);
                    break;
                case LOAD_MORE_FINISH:
                    refreshLayout.setFooterRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        ButterKnife.inject(this);
        Bmob.initialize(getApplicationContext(), APPID);
        setSupportActionBar(toolbar);
        initRefreshLayout();
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
        hasNavigationBar = checkDeviceHasNavigationBar(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setPadding(0, getStatusBarHeight(getApplicationContext()), 0, 0);

        if (hasNavigationBar) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) submit.getLayoutParams();
            lp.setMargins(32, 32, 32, 32 + getNavigationBarHeight(MainActivity.this));
            submit.setLayoutParams(lp);
        }
        posts = new ArrayList<Post>();
        initHead();
        /* int paddingTop = Utils.getToolbarHeight(this);
        contentList.setPadding(contentList.getPaddingLeft(), paddingTop, contentList.getPaddingRight(), contentList.getPaddingBottom());*/
        mLayoutManager = new LinearLayoutManager(this);
        contentList.setLayoutManager(mLayoutManager);

        mToolbarHeight = Utils.getToolbarHeight(this);
        is_praised = new SparseArray<Boolean>();
        is_collected=new SparseArray<Boolean>();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                refreshQuery();
            }
        };
        handler.post(runnable);











 /*contentList.addOnScrollListener(new HidingScrollListener(this) {

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

        });*/


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

        idNvMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:

                }
                drawerLayout.closeDrawers();
                return false;
            }
        });
        head = (ImageView) idNvMenu.findViewById(R.id.head);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = User.getCurrentUser(MainActivity.this, User.class);
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        });

        username = (TextView) idNvMenu.findViewById(R.id.id_username);
        myUser = testGetCurrentUser();
        if (myUser != null) {
            username.setText(myUser.getUsername());
            if (myUser.getHead() != null) {
                imageLoader.displayImage(myUser.getHead().getFileUrl(getApplicationContext()), head, MyApplication.getInstance().getOptions());

            }else{
                head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            }
        }else{ head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));}
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
                Bundle bundle = data.getExtras();
                User user = (User) bundle.get("user");
                username.setText(user.getUsername());
                if (user.getHead() != null) {
                    imageLoader.displayImage(user.getHead().getFileUrl(getApplicationContext()), head);
                } else {
                    head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                }
                setPraise(posts);
                setCollection(posts);
                postAdpater.notifyDataSetChanged();
                break;
            case RESULT_CANCELED:
                username.setText("请登录");
                head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                is_collected.clear();
                is_praised.clear();
                postAdpater.notifyDataSetChanged();
                break;
            case SAVE_OK:
                testGetCurrentUser();
                initHead();
                break;
            case SUBMIT_OK:
                Message msg = new Message();
                msg.arg1 = REFRESH_START;
                handler.sendMessage(msg);
                refreshQuery();
                break;
            default:
                break;
        }

    }

    /**
     * 获取本地用户
     */
    private User testGetCurrentUser() {
        User myUser = BmobUser.getCurrentUser(this, User.class);
        if (myUser != null) {
            Log.i("life", "本地用户信息:objectId = " + myUser.getObjectId() + ",name = " + myUser.getUsername()
            );
        } else {
            toast("本地用户为null,请登录。");
        }
        return myUser;
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHead();
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
        query.setLimit(10);
        query.order("-id");
        query.include("author");
        final Message msg = new Message();
        query.findObjects(this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list.size() != 0) {
                    if (posts.size() > 0) {


                        if (MyApplication.getInstance().getCurrentUser() != null) {
                            setPraise(list);
                            setCollection(list);
                            // list = DatabaseUtil.getInstance(getApplicationContext()).setPraise(list);
                        }
                        posts.addAll(0, (ArrayList<Post>) list);
                        postAdpater.notifyDataSetChanged();


                    } else {
                        posts = (ArrayList<Post>) list;
                        postAdpater = new PostAdapter(posts, is_praised, is_collected, getApplicationContext(), hasNavigationBar);
                        if (MyApplication.getInstance().getCurrentUser() != null) {
                            setPraise(list);
                            setCollection(list);
                            // list = DatabaseUtil.getInstance(getApplicationContext()).setPraise(list);
                        }


                        if (hasNavigationBar) {
                            footerView = getLayoutInflater().inflate(R.layout.footer, null);
                            footerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getNavigationBarHeight(MainActivity.this)));
                            postAdpater.setFooterView(footerView);
                        }
                        contentList.setAdapter(postAdpater);
                       refreshLayout.setVisibility(View.VISIBLE);
                       progressBar.setVisibility(View.GONE);
                    }
                }
                refreshLayout.setHeaderRefreshing(false);
                toast("查询成功：共" + list.size() + "条数据。");
            }

            @Override
            public void onError(int i, String s) {
                refreshLayout.setHeaderRefreshing(false);
                toast("查询失败：" + s);
            }
        });
    }

    public void loadMoreQuery() {
        if (posts.size() > 0) {
            BmobQuery<Post> query = new BmobQuery<Post>();
            query.addWhereLessThan("id", posts.get(posts.size() - 1).getId());
            query.setLimit(10);
            query.order("-id");
            query.include("author");
            final Message msg = new Message();
            query.findObjects(this, new FindListener<Post>() {
                @Override
                public void onSuccess(final List<Post> list) {
                    if (list.size() != 0) {

                        if (MyApplication.getInstance().getCurrentUser() != null) {
                                    setPraise(list);
                                    setCollection(list);


                            //list = DatabaseUtil.getInstance(getApplicationContext()).setPraise(list);
                        }
                        posts.addAll((ArrayList<Post>) list);
                        postAdpater.notifyDataSetChanged();

                    }
                    refreshLayout.setFooterRefreshing(false);
                    toast("查询成功：共" + list.size() + "条数据。");
                }

                @Override
                public void onError(int i, String s) {
                    refreshLayout.setFooterRefreshing(false);
                }
            });
        }
        Message msg = new Message();
        msg.arg1 = LOAD_MORE_FINISH;
        handler.sendMessage(msg);
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
                            //postAdpater.notifyDataSetChanged();
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
        List<String> collect_post_id=new ArrayList<String>();
        collect_post_id = MyApplication.getInstance().getCurrentUser().getCollect_post_id();
        if( collect_post_id!=null)
        { for (final Post post : list) {
            if (collect_post_id.contains(post.getObjectId()))
                is_collected.append(post.getId(), true);
            else
                is_collected.append(post.getId(), false);
           // postAdpater.notifyDataSetChanged();
        }
       }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshLayout.setHeaderRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        refreshLayout.setHeaderRefreshing(false);
                        Toast.makeText(MainActivity.this, "Refresh Finished!", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    public static boolean checkDeviceHasNavigationBar(Context activity) {

        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏

            return true;
        }
        return false;
    }

    public static int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public void initRefreshLayout() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setFooterColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        refreshLayout.setHeaderColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshLayout.setHeaderRefreshing(true);

    }

}
