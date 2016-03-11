package com.cyan.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cyan.community.R;
import com.cyan.bean.Focus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/2/1.
 */
public class FansViewHolder extends BaseViewHolder<Focus> {
    @InjectView(R.id.user_head)
    CircleImageView userHead;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.intro)
    TextView intro;

    public FansViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_focus);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void setData(Focus data) {
        if (data.getUser().getHead() != null) {
            Glide.with(getContext()).load(data.getUser().getHead().getFileUrl(getContext())).into(userHead);
        }
        userName.setText(data.getUser().getUsername());
        if (data.getUser().getIntro() != null)
        intro.setHint(data.getUser().getIntro());
    }
}
