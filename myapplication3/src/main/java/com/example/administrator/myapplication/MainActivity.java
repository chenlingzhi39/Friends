package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.RecyclerListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.administrator.myapplication.CustomAdapter.OnItemPressedListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerlayout;
    @InjectView(R.id.drawer_view)
    LinearLayout drawerView;
    @InjectView(R.id.mToolbarContainer)
    LinearLayout mToolbarContainer;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER, LINEAR_LAYOUT_MANAGER
    }

    private CustomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LayoutManagerType mCurrentLayoutManagerType;

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    //建立的模拟数据
    private ArrayList<String> mDataList;
    //网格布局中的设置列数
    private static int SpanCount = 3;
    //模拟数据的个数
    private static int DataSize = 100;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mToolbarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        drawerlayout.setDrawerListener(mDrawerToggle);

        initData();
        initView(savedInstanceState);
    }

    public void initData() {
        mDataList = new ArrayList<String>();
        for (int i = 0; i < DataSize; i++) {
            mDataList.add(String.format(getString(R.string._default) + i, i));
        }
    }

    public void initView(Bundle savedInstanceState) {

       /* linear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewManagerType(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
            }
        });

        grid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewManagerType(LayoutManagerType.GRID_LAYOUT_MANAGER);
            }
        });*/
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
            Log.i(TAG, "mCurrentLayoutManagerType=" + mCurrentLayoutManagerType);
        }
 /*       int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);


        linear.measure(w, h);
        int height =linear.getMeasuredHeight();
        int width =linear.getMeasuredWidth();*/

        int paddingTop = Utils.getToolbarHeight(MainActivity.this) ;

        setRecyclerViewManagerType(mCurrentLayoutManagerType);
        mAdapter = new CustomAdapter(mDataList);



        recyclerView.setPadding(recyclerView.getPaddingLeft(), paddingTop, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        recyclerView.setAdapter(mAdapter);
        //设置recycler拥有固定的大小，提高展示效率
        recyclerView.setHasFixedSize(true);
        //设置默认的动画，在移除和添加的效果下展现，现在github上可以找到许多拓展，有兴趣的可以找找
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //实现我们给Adapter监听的方法，因为recyclerview没有Listview的OnClick和OnlongClick相似的方法
        mAdapter.setOnItemPressedListener(new OnItemPressedListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "你点击了 item-" + mDataList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean OnItemLongClick(int position) {
                //这里模拟了删除的功能
                removeItemByPosition(position);
//              insertItemByPosition(position);
                Toast.makeText(MainActivity.this, "你长按了 item-" + mDataList.get(position), Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onGridClick() {
                setRecyclerViewManagerType(LayoutManagerType.GRID_LAYOUT_MANAGER);
            }

            @Override
            public void onLinearClick() {
                setRecyclerViewManagerType(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
            }
        });

        recyclerView.setRecyclerListener(new RecyclerListener() {


            //资源回收后被调用
            @Override
            public void onViewRecycled(ViewHolder viewHolder) {
              /*  CustomAdapter.ViewHolder vh = (CustomAdapter.ViewHolder) viewHolder;
                Log.d(TAG, "onViewRecycled->" + vh.getButton().getText());*/
            }
        });
        mToolbarHeight = Utils.getToolbarHeight(this);
        recyclerView.addOnScrollListener(new HidingScrollListener(this) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setRecyclerViewManagerType(LayoutManagerType type) {
        int scrollPosition = 0;
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView
                    .getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        switch (type) {
            //网状
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(this, SpanCount);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            //线性，如list
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(this);
                mLayoutManager.canScrollHorizontally();
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
        }
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    protected void removeItemByPosition(int position) {
        if (mAdapter != null && position >= 0) {
            mAdapter.notifyItemRemoved(position);
            mDataList.remove(position);
            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            //你如果用了这个 ，就没有动画效果了。
//          mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
