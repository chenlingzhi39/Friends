package com.cyan.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.cyan.community.R;
import com.cyan.fragment.CommentFragment;
import com.cyan.fragment.ReplyFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/1/13.
 */
public class MessageActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.layout_tab)
    TabLayout layoutTab;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    CommentFragment commentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("消息");
        viewpager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this));
        layoutTab.setupWithViewPager(viewpager);
        if(getIntent().getStringExtra("mode")!=null)
        viewpager.setCurrentItem(getIntent().getStringExtra("mode").equals("comment")?0:1);
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
            switch(position){
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case MainActivity.REFRESH_PRAISE:

            case MainActivity.REFRESH_COLLECTION:

            case MainActivity.REFRESH_COMMENT:
                setResult(resultCode,data);
                break;}
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("tag", "onNewINtent执行了");
        setIntent(intent);
        viewpager.setCurrentItem(getIntent().getStringExtra("mode").equals("comment")?0:1);
       getIntent().putExtras(intent);
    }
}
