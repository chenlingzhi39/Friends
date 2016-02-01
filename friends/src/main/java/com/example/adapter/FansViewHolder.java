package com.example.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.bean.Focus;

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
    }

    @Override
    public void setData(Focus data) {
        if (data.getUser().getHead() != null) {
            imageLoader.displayImage(data.getUser().getHead().getFileUrl(getContext()), userHead);
        }
        userName.setText(data.getUser().getUsername());
        if (data.getUser().getIntro() != null)
        intro.setHint(data.getUser().getIntro());
    }
}
