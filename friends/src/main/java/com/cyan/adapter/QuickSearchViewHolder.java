package com.cyan.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.cyan.community.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.daoexample.QuickSearch;

/**
 * Created by Administrator on 2016/3/30.
 */
public class QuickSearchViewHolder extends BaseViewHolder<QuickSearch> {
    @InjectView(R.id.textView)
    TextView textView;
    public QuickSearchViewHolder(ViewGroup parent) {
        super(parent, R.layout.list_quick_search_row);
        ButterKnife.inject(this,itemView);
    }
    @Override
    public void setData(QuickSearch data) {
      textView.setText(data.getContent());
    }
}
