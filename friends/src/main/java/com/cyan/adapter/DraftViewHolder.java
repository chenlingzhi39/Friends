package com.cyan.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.cyan.community.R;
import com.cyan.util.StringUtils;

import butterknife.InjectView;
import de.greenrobot.daoexample.Draft;

/**
 * Created by Administrator on 2016/3/11.
 */
public class DraftViewHolder extends BaseViewHolder<Draft> {
    @InjectView(R.id.add_time)
    TextView addTime;
    @InjectView(R.id.content)
    TextView content;

    public DraftViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_draft);

    }

    @Override
    public void setData(Draft data) {
     content.setText(data.getContent());
        addTime.setText(StringUtils.friendly_time(data.getCreate_time()));
    }
}
