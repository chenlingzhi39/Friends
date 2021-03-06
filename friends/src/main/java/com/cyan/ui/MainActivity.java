package com.cyan.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cyan.adapter.PostAdapter;
import com.cyan.adapter.QuickSearchAdapter;
import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.app.MyApplication;
import com.cyan.bean.MyBmobInstallation;
import com.cyan.bean.Post;
import com.cyan.bean.User;
import com.cyan.common.Constants;
import com.cyan.community.R;
import com.cyan.listener.InputWindowListener;
import com.cyan.listener.OnItemClickListener;
import com.cyan.manager.MyLayoutManager;
import com.cyan.module.post.presenter.PostPresenter;
import com.cyan.module.post.presenter.PostPresenterImpl;
import com.cyan.module.post.view.LoadPostView;
import com.cyan.util.InitiateSearch;
import com.cyan.util.PraiseUtils;
import com.cyan.util.SPUtils;
import com.cyan.widget.recyclerview.EasyRecyclerView;
import com.cyan.widget.refreshlayout.RefreshLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import de.greenrobot.daoexample.QuickSearch;
import de.greenrobot.daoexample.QuickSearchDao;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2015/9/21.
 */

@ActivityFragmentInject(
        contentViewId = R.layout.activity_main,
        toolbarTitle = R.string.app_name
)
public class MainActivity extends RefreshActivity implements RefreshLayout.OnRefreshListener, LoadPostView {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.mToolbarContainer)
    AppBarLayout mToolbarContainer;
    @InjectView(R.id.list)
    EasyRecyclerView contentList;
    @InjectView(R.id.submit)
    FloatingActionButton submit;
    @InjectView(R.id.id_nv_menu)
    NavigationView idNvMenu;
    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.view_search)
    RelativeLayout viewSearch;
    @InjectView(R.id.image_search_back)
    ImageView imageSearchBack;
    @InjectView(R.id.edit_text_search)
    EditText editTextSearch;
    @InjectView(R.id.clearSearch)
    ImageView clearSearch;
    @InjectView(R.id.linearLayout_search)
    LinearLayout linearLayoutSearch;
    @InjectView(R.id.line_divider)
    View lineDivider;
    @InjectView(R.id.listView)
    RecyclerView listView;
    @InjectView(R.id.card_search)
    CardView cardSearch;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<QuickSearch> quickSearches;
    public static String TAG = "bmob";
    User myUser;
    ImageView head, background;
    TextView username;
    private RecyclerView.LayoutManager mLayoutManager;
    private MenuItem menuItem, messages, records, collection;
    private long firstclick;
    private PostPresenter<Post> postPresenter;
    private BmobQuery<Post> query;
    private InitiateSearch initiateSearch;
    private QuickSearchAdapter quickSearchAdapter;
    private QuickSearchDao quickSearchDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        BmobUpdateAgent.update(this);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                // TODO Auto-generated method stub
                Log.i("updatestatus", updateStatus + "");
            }
        });
        initRefreshLayout();
        // setFullTouch();
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
        initHead();
        mLayoutManager = new LinearLayoutManager(this);
        contentList.setLayoutManager(mLayoutManager);
        postPresenter = new PostPresenterImpl(this, this, subscription);
        query = new BmobQuery<>();
        postPresenter.loadPost(query);
        contentList.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = new BmobQuery<>();
                postPresenter.loadPost(query);
            }
        });

        InitiateSearch();
        HandleSearch();
    }

    @Override
    public void addPosts(final List<Post> list) {
        PraiseUtils.flush(MainActivity.this, is_praised, is_collected, list);
        if (list.size() > 0)
            if (posts.size() == 0) {
                posts = (ArrayList<Post>) list;
                postAdapter = new PostAdapter(posts, is_praised, is_collected, MainActivity.this);
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

            } else {
                if (posts.get(0).getId() < list.get(0).getId()) {
                    posts.addAll(0, list);
                } else {
                    posts.addAll(list);
                }

            }
    }

    @Override
    public void notifyDataSetChanged(boolean b) {
        if (b) contentList.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmpty() {
        contentList.showEmpty();
    }

    @Override
    public void showError() {
        contentList.showError();
        stopRefreshIconAnimation(menuItem);
    }

    @Override
    public void showProgress(Boolean b) {
        if (b) {
            if (menuItem != null)
                startRefreshIconAnimation(menuItem);
            contentList.showProgress();
        }
    }

    @Override
    public void showRecycler() {
        contentList.showRecycler();

    }

    @Override
    public void stopLoadMore() {
        contentList.setFooterRefreshing(false);
    }

    @Override
    public void stopRefresh() {
        contentList.setHeaderRefreshing(false);
        stopRefreshIconAnimation(menuItem);
    }

    @Override
    public void onFooterRefresh() {
        query = new BmobQuery<>();
        if (posts.size() > 0)
            query.addWhereLessThan("id", posts.get(posts.size() - 1).getId());
        postPresenter.loadPost(query);

    }

    @Override
    public void onHeaderRefresh() {
        query = new BmobQuery<>();
        if (posts.size() > 0)
            query.addWhereGreaterThan("id", posts.get(0).getId());
        postPresenter.loadPost(query);


    }

    public void initHead() {

        if (messages == null) {
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
                        case R.id.nav_about:
                            intent = new Intent(MainActivity.this, AboutActivity.class);
                            startActivity(intent);
                            break;
                    }
                    //drawerLayout.closeDrawers();
                    return false;
                }
            });
            View headerView = idNvMenu.getHeaderView(0);
            head = (ImageView) headerView.findViewById(R.id.head);
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
            background = (ImageView) headerView.findViewById(R.id.image);
            username = (TextView) headerView.findViewById(R.id.id_username);
        }
        myUser = MyApplication.getInstance().getCurrentUser();
        if (myUser != null) {
            messages.setEnabled(true);
            records.setEnabled(true);
            collection.setEnabled(true);
            username.setText(myUser.getUsername());
            if (myUser.getHead() != null) {
                Glide.with(this).load(myUser.getHead().getFileUrl(getApplicationContext())).into(head);
            } else {
                head.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            }
            if (myUser.getBackground() != null)
                Glide.with(this).load(myUser.getBackground().getFileUrl(this)).into(background);
        } else {
            messages.setEnabled(false);
            records.setEnabled(false);
            collection.setEnabled(false);
            username.setText("请登陆");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                initHead();
                if (posts.size() != 0) {
                    sub = Observable.create(new Observable.OnSubscribe<PostAdapter>() {
                        @Override
                        public void call(Subscriber<? super PostAdapter> subscriber) {
                            PraiseUtils.flush(MainActivity.this, is_praised, is_collected, posts);
                            subscriber.onNext(postAdapter);
                        }
                    }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<PostAdapter>() {
                        @Override
                        public void call(PostAdapter postAdapter) {
                            postAdapter.notifyDataSetChanged();
                        }
                    });
                }
                Log.i("userId", myUser.getObjectId());
                refreshInstalllation(myUser.getObjectId());
                break;
            case Constants.LOGOUT:
                MyApplication.getInstance().clearCurrentUser();
                initHead();
                is_collected.clear();
                is_praised.clear();
                postAdapter.notifyDataSetChanged();
                refreshInstalllation("0");
                break;
            case Constants.SAVE_OK:
                initHead();
                for (Post post : posts) {
                    if (post.getAuthor().getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId()))
                        post.setAuthor(MyApplication.getInstance().getCurrentUser());
                }
                postAdapter.notifyDataSetChanged();
                break;
            case Constants.SUBMIT_OK:
                query = new BmobQuery<>();
                if (posts.size() > 0)
                    query.addWhereGreaterThan("id", posts.get(0).getId());
                postPresenter.loadPost(query);
                break;
            case Constants.RESEARCH:
                quickSearchAdapter.clear();
                String textColumn = QuickSearchDao.Properties.Id.columnName;
                String orderBy = textColumn + " DESC";
                Cursor cursor =  MyApplication.getInstance().getDb().query(quickSearchDao.getTablename(),quickSearchDao.getAllColumns(),null, null, null, null, orderBy);
                if(cursor!=null){

                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        QuickSearch quickSearch=new QuickSearch();
                        quickSearchDao.readEntity(cursor, quickSearch, 0);
                        quickSearchAdapter.addAll(quickSearch);
                    }}
                break;
            default:
                break;
        }

    }
    private void InitiateSearch(){
        contentList.setListener(new InputWindowListener() {
            @Override
            public void show() {

            }

            @Override
            public void hide() {
                Log.i("input","hide");
                if(cardSearch.getVisibility()==View.VISIBLE)
                initiateSearch.handleToolBar(MainActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
            }
        });
        quickSearchDao=  MyApplication.getInstance().getDaoSession().getQuickSearchDao();
        String textColumn = QuickSearchDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        Cursor cursor =  MyApplication.getInstance().getDb().query(quickSearchDao.getTablename(),quickSearchDao.getAllColumns(),null, null, null, null, orderBy);
        if(cursor!=null){
            quickSearchAdapter=new QuickSearchAdapter(this);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                QuickSearch quickSearch=new QuickSearch();
                quickSearchDao.readEntity(cursor, quickSearch, 0);
                quickSearchAdapter.addAll(quickSearch);
            }
            quickSearchAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if(position!=0)
                    { QuickSearch quickSearch=new QuickSearch();
                        quickSearch.setAdd_time(new Date(System.currentTimeMillis()));
                        quickSearch.setContent(quickSearchAdapter.getData().get(position).getContent());
                        quickSearchDao.insert(quickSearch);
                        quickSearchDao.delete(quickSearchAdapter.getData().get(position));
                        quickSearchAdapter.remove(position);
                        quickSearchAdapter.add(0,quickSearch);}
                    initiateSearch.handleToolBar(MainActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
                    Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                    intent.putExtra("key",quickSearchAdapter.getData().get(position).getContent());
                    startActivityForResult(intent,0);
                }
            });
            listView.setLayoutManager(new MyLayoutManager(this));
            listView.setAdapter(quickSearchAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<QuickSearch>(quickSearchDao,quickSearchAdapter));
            itemTouchHelper.attachToRecyclerView(listView);
        }
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextSearch.getText().toString().length() == 0) {
                    clearSearch.setImageResource(R.mipmap.ic_keyboard_voice);
                } else {
                    clearSearch.setImageResource(R.mipmap.ic_close);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextSearch.getText().toString().length() == 0) {

                } else {
                    editTextSearch.setText("");
                    listView.setVisibility(View.VISIBLE);
                    ((InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                }
            }
        });
    }
    private void HandleSearch() {
        imageSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("search", "back");
                initiateSearch.handleToolBar(MainActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
            }
        });
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (editTextSearch.getText().toString().trim().length() > 0) {
                        for (int i = 0; i < quickSearchAdapter.getData().size(); i++) {
                            if (quickSearchAdapter.getData().get(i).getContent().equals(editTextSearch.getText().toString())) {
                                if(i==0){
                                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                                    intent.putExtra("key", editTextSearch.getText().toString());
                                    startActivityForResult(intent,0);
                                    initiateSearch.handleToolBar(MainActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
                                    return true;
                                }
                                quickSearchDao.delete(quickSearchAdapter.getData().get(i));
                                quickSearchAdapter.remove(i);
                                break;
                            }
                        }
                        QuickSearch quickSearch = new QuickSearch();
                        quickSearch.setAdd_time(new Date(System.currentTimeMillis()));
                        quickSearch.setContent(editTextSearch.getText().toString());
                        quickSearchDao.insert(quickSearch);
                        quickSearchAdapter.add(0, quickSearch);
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        intent.putExtra("key", editTextSearch.getText().toString());
                        startActivityForResult(intent,0);
                        initiateSearch.handleToolBar(MainActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);

                    }
                    return true;
                }
                return false;
            }
        });
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                startRefreshIconAnimation(item);
                contentList.setHeaderRefreshing(true);
                query = new BmobQuery<>();
                if (posts.size() > 0)
                    //loadMoreQuery();
                    query.addWhereGreaterThan("id", posts.get(0).getId());
                postPresenter.loadPost(query);
                break;
            case R.id.action_search:
                initiateSearch.handleToolBar(MainActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(cardSearch.getVisibility()==View.GONE){
            if ((System.currentTimeMillis() - firstclick) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstclick = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }}
        if(cardSearch.getVisibility()==View.VISIBLE)
        {
            Log.i("cardView","hide");
            initiateSearch.handleToolBar(MainActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
            return true;
        }
        return true;
    }

    public void initRefreshLayout() {
        contentList.setRefreshListener(this);
        contentList.setFooterRefrehingColorResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        contentList.setHeaderRefreshingColorResources(android.R.color.holo_blue_bright,
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
        Log.i("objectId", BmobInstallation.getCurrentInstallation(MyApplication.getInstance()).getObjectId() + "");
        query.findObjects(this, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                // TODO Auto-generated method stub
                if (object.size() > 0) {
                    final MyBmobInstallation mbi = object.get(0);
                    Log.i("userId", userId);
                    mbi.setUid(userId);
                    Log.i("objectId", mbi.getObjectId());
                    SPUtils.put(MyApplication.getInstance(), "settings", "installation", mbi.getObjectId());
                    mbi.update(MainActivity.this, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i("objectId", mbi.getObjectId() + "");
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


}
