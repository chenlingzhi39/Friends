package com.cyan.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cyan.bean.User;
import com.cyan.community.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/3/31.
 */
public class UserViewHolder extends BaseViewHolder<User> {
    @InjectView(R.id.user_head)
    CircleImageView userHead;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.intro)
    TextView intro;

    public UserViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_user);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void setData(User data) {
        if (data.getHead() != null) {
            Glide.with(getContext()).load(data.getHead().getFileUrl(getContext())).into(userHead);
        }
        userName.setText(data.getUsername());
        if (data.getIntro() != null)
            intro.setHint(data.getIntro());
    }
}
