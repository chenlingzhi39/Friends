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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.example.listener.OnItemClickListener;
import com.example.refreshlayout.RefreshLayout;
import com.example.util.SimpleHandler;
import com.example.util.Utils;
import com.example.widget.recyclerview.FastScroller;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

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
    @InjectView(R.id.fast_scroller)
    FastScroller fastScroller;
    private ActionBarDrawerToggle mDrawerToggle;
    public static String APPID = "9245da2bae59a43d2932e1324875137a";
    public static String TAG = "bmob";
    User myUser;
    public static final int SAVE_OK = 2;
    public static final int SUBMIT_OK = 3;
    public static final int LOGOUT = 4;
    public static final int REFRESH_PRAISE = 5;
    public static final int REFRESH_COLLECTION = 6;
    public final static int REFRESH_COMMENT=7;
    ImageView head;
    TextView username;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Post> posts;
    PostAdapter postAdpater;
    private int mToolbarHeight;
    private int firstid, lastid;


    private View footerView;
    private Boolean hasNavigationBar;
    private SparseArray<Boolean> is_praised;
    private SparseArray<Boolean> is_collected;
    private int select_index = 0;
    private MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        ButterKnife.inject(this);
        Bmob.initialize(getApplicationContext(), APPID);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, APPID);
        setSupportActionBar(toolbar);
        initRefreshLayout();
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
        hasNavigationBar = Utils.checkDeviceHasNavigationBar(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setPadding(0, Utils.getStatusBarHeight(getApplicationContext()), 0, 0);

        if (hasNavigationBar) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) submit.getLayoutParams();
            lp.setMargins(32, 32, 32, 32 + Utils.getNavigationBarHeight(MainActivity.this));
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
        is_collected = new SparseArray<Boolean>();
        fastScroller.attachToRecyclerView(contentList);

        refreshQuery();












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

            } else {
                head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            }
        } else {
            head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
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
                Bundle bundle = data.getExtras();
                User user = (User) bundle.get("user");
                username.setText(user.getUsername());
                if (user.getHead() != null) {
                    imageLoader.displayImage(user.getHead().getFileUrl(getApplicationContext()), head);
                } else {
                    head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                }
                if (posts.size() != 0) {
                    setPraise(posts);
                    setCollection(posts);
                    postAdpater.notifyDataSetChanged();
                } else {


                }

                Log.i("userId",user.getObjectId());
                refreshInstalllation(user.getObjectId());
                break;
            case LOGOUT:
                username.setText("请登录");
                head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                is_collected.clear();
                is_praised.clear();
                postAdpater.notifyDataSetChanged();
                MyApplication.getInstance().clearCurrentUser();
                break;
            case SAVE_OK:
                testGetCurrentUser();
                initHead();
                break;
            case SUBMIT_OK:
                refreshLayout.setHeaderRefreshing(true);
                refreshQuery();
                break;
            case REFRESH_PRAISE:
                boolean praised = data.getBooleanExtra("is_praised", false);
                if (praised)
                    posts.get(select_index).setPraise_count(posts.get(select_index).getPraise_count() + 1);
                else
                    posts.get(select_index).setPraise_count(posts.get(select_index).getPraise_count() - 1);
                is_praised.put(posts.get(select_index).getId(), praised);
                postAdpater.notifyDataSetChanged();
                break;
            case REFRESH_COLLECTION:
                boolean collected = data.getBooleanExtra("is_collected", false);
                is_collected.put(posts.get(select_index).getId(), collected);
                postAdpater.notifyDataSetChanged();
                break;
            case REFRESH_COMMENT:
                posts.get(select_index).setComment_count(posts.get(select_index).getComment_count()+1);
                postAdpater.notifyDataSetChanged();
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
            //toast("本地用户为null,请登录。");
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
        {query.addWhereGreaterThan("id", posts.get(0).getId());
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);}
        else query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
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
                        postAdpater.notifyDataSetChanged();


                    } else {
                        posts = (ArrayList<Post>) list;
                        postAdpater = new PostAdapter(posts, is_praised, is_collected, getApplicationContext(), hasNavigationBar);
                        flush(list);
//                        if (MyApplication.getInstance().getCurrentUser() != null) {
//                            setPraise(posts);
//                            setCollection(posts);
//                            // list = DatabaseUtil.getInstance(getApplicationContext()).setPraise(list);
//                        }
                        postAdpater.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onClick(View view, Object item) {

                                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("post", posts.get((Integer) item));
                                intent.putExtra("isPraised", is_praised.get(posts.get((Integer) item).getId()));
                                intent.putExtra("isCollected", is_collected.get(posts.get((Integer) item).getId()));
                                startActivityForResult(intent, 0);
                            }
                        });

                        if (hasNavigationBar) {
                            footerView = getLayoutInflater().inflate(R.layout.footer, null);
                            footerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.getNavigationBarHeight(MainActivity.this)));
                            postAdpater.setFooterView(footerView);
                        }
                        contentList.setAdapter(postAdpater);
                        SimpleHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        postAdpater.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onClick(View view, Object item) {

                                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                                select_index = (Integer) item;
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
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            query.addWhereLessThan("id", posts.get(posts.size() - 1).getId());
            query.setLimit(10);
            query.order("-id");
            query.include("author");
            query.findObjects(this, new FindListener<Post>() {
                @Override
                public void onSuccess(final List<Post> list) {
                    if (list.size() != 0) {


                        posts.addAll((ArrayList<Post>) list);
                        postAdpater.notifyDataSetChanged();
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
                            postAdpater.notifyDataSetChanged();
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
                postAdpater.notifyDataSetChanged();
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
       menuItem=menu.findItem(R.id.action_refresh);
       menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
           menu.performIdentifierAction(R.id.action_refresh,0);
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
    private void startRefreshIconAnimation(MenuItem menuItem){
        Animation rotation= AnimationUtils.loadAnimation(this,R.anim.refresh_icon_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        menuItem.getActionView().startAnimation(rotation);
        menuItem.getActionView().setClickable(false);
    }
    private void stopRefreshIconAnimation(MenuItem menuItem){
        if(menuItem!=null){
        menuItem.getActionView().clearAnimation();
        menuItem.getActionView().setClickable(true);}
    }
   private void refreshInstalllation(final String userId){
       BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
       query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(this));
       query.findObjects(this, new FindListener<MyBmobInstallation>() {

           @Override
           public void onSuccess(List<MyBmobInstallation> object) {
               // TODO Auto-generated method stub
               if(object.size() > 0){
                   MyBmobInstallation mbi = object.get(0);
                   mbi.setUid(userId);
                   mbi.update(MainActivity.this, new UpdateListener() {

                       @Override
                       public void onSuccess() {
                           // TODO Auto-generated method stub
                           // 使用推送服务时的初始化操作
                           BmobInstallation.getCurrentInstallation(MainActivity.this).save();
                           Log.i("bmob", "设备信息更新成功");
                       }

                       @Override
                       public void onFailure(int code, String msg) {
                           // TODO Auto-generated method stub
                           Log.i("bmob", "设备信息更新失败:" + msg);
                       }
                   });
               }else{
               }
           }

           @Override
           public void onError(int code, String msg) {
               // TODO Auto-generated method stub
           }
       });
   }
}
