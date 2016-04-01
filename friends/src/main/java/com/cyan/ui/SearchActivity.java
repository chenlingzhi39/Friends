package com.cyan.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyan.adapter.QuickSearchAdapter;
import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.app.MyApplication;
import com.cyan.community.R;
import com.cyan.fragment.PostFragment;
import com.cyan.fragment.UserFragment;
import com.cyan.listener.InputWindowListener;
import com.cyan.manager.MyLayoutManager;
import com.cyan.util.InitiateSearch;
import com.cyan.util.RxBus;
import com.cyan.widget.IMMListenerRelativeLayout;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.daoexample.QuickSearch;
import de.greenrobot.daoexample.QuickSearchDao;

/**
 * Created by Administrator on 2016/3/30.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_search
)
public class SearchActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.layout_tab)
    TabLayout layoutTab;
    @InjectView(R.id.mToolbarContainer)
    AppBarLayout mToolbarContainer;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    @InjectView(R.id.view_search)
    IMMListenerRelativeLayout viewSearch;
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

    private QuickSearchAdapter quickSearchAdapter;
    private QuickSearchDao quickSearchDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getSupportActionBar().setTitle(getIntent().getStringExtra("key"));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitiateSearch.handleToolBar1(SearchActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
            }
        });
        viewpager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this));
        layoutTab.setupWithViewPager(viewpager);
        InitiateSearch();
        HandleSearch();
    }
    private void InitiateSearch(){
        editTextSearch.setText(getIntent().getStringExtra("key"));
        editTextSearch.setSelection(getIntent().getStringExtra("key").length());
  viewSearch.setListener(new InputWindowListener() {
    @Override
    public void show() {

    }

    @Override
    public void hide() {
        Log.i("input","hide");
        if(cardSearch.getVisibility()==View.VISIBLE)
            InitiateSearch.handleToolBar1(SearchActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
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
                    InitiateSearch.handleToolBar(SearchActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                    getSupportActionBar().setTitle(quickSearchAdapter.getData().get(position).getContent());
                    RxBus.get().post("reSearch", quickSearchAdapter.getData().get(position).getContent());
                    editTextSearch.setText(quickSearchAdapter.getData().get(position).getContent());
                }
            });
            listView.setLayoutManager(new MyLayoutManager(this));
            listView.setAdapter(quickSearchAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<>(quickSearchDao,quickSearchAdapter));
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
                    ((InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                }
            }
        });
    }
    private void HandleSearch() {
        imageSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("search", "back");
                InitiateSearch.handleToolBar1(SearchActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
            }
        });
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (editTextSearch.getText().toString().trim().length() > 0) {
                        QuickSearch quickSearch = new QuickSearch();
                        quickSearch.setAdd_time(new Date(System.currentTimeMillis()));
                        quickSearch.setContent(editTextSearch.getText().toString());
                        quickSearchDao.insert(quickSearch);
                        quickSearchAdapter.add(0, quickSearch);
                        InitiateSearch.handleToolBar1(SearchActivity.this, cardSearch, viewSearch, listView, editTextSearch, lineDivider);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                        RxBus.get().post("reSearch", editTextSearch.getText().toString());
                        getSupportActionBar().setTitle(editTextSearch.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"作品", "用户"};
        private Context context;

        public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;

        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PostFragment.newInstance(getIntent().getStringExtra("key"));

                case 1:
                    return UserFragment.newInstance(getIntent().getStringExtra("key"));

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
}
