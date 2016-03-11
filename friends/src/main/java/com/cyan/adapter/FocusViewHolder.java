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
public class FocusViewHolder extends BaseViewHolder<Focus> {


    @InjectView(R.id.user_head)
    CircleImageView userHead;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.intro)
    TextView intro;

    public FocusViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_focus);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void setData(Focus data) {
 if(data.getFocusUser().getHead()!=null){
     Glide.with(getContext()).load(data.getFocusUser().getHead().getFileUrl(getContext())).into(userHead);
 }
        userName.setText(data.getFocusUser().getUsername());
        if(data.getFocusUser().getIntro()!=null)
        intro.setHint(data.getFocusUser().getIntro());
}
}
