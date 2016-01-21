package com.example.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.bean.CommentToMe;
import com.example.util.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/1/20.
 */
public class CommentToMeViewHolder extends BaseViewHolder<CommentToMe> {
    @InjectView(R.id.user_head)
    ImageView userHead;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.addtime)
    TextView addtime;
    @InjectView(R.id.reply)
    TextView reply;
    @InjectView(R.id.comment)
    TextView comment;
    @InjectView(R.id.content)
    TextView content;

    public CommentToMeViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_commenttome);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void setData(CommentToMe data) {
     if(data.getHead()!=null)
         imageLoader.displayImage(data.getHead(),userHead);
         userName.setText(data.getUser_name());
         comment.setText(data.getComment_content());
         addtime.setText(StringUtils.friendly_time(data.getCreate_time()));
         content.setText(data.getPost_content());


    }
}
