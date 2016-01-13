package com.example.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.myapplication.R;
import com.example.fragment.PageFragment;

/**
 * Created by Administrator on 2016/1/13.
 */
public class MessageActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

    }
    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[]{"评论我的","回复我的","tab3"};
        private Context context;

        public SimpleFragmentPagerAdapter(FragmentManager fm,Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
}}
