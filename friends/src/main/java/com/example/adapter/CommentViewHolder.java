package com.example.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.bean.Comment;
import com.example.util.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/1/8.
 */
public class CommentViewHolder extends BaseViewHolder<Comment> {


    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.time)
    TextView time;
    @InjectView(R.id.content_text)
    TextView contentText;
    @InjectView(R.id.user_head)
    ImageView userHead;


    public CommentViewHolder(ViewGroup parent) {
        super(parent, R.layout.comment_item);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void setData(Comment data) {

        if (data.getAuthor().getHead() != null) {
            imageLoader.displayImage(data.getAuthor().getHead().getFileUrl(getContext()), userHead);
        }
        userName.setText(data.getAuthor().getUsername());
time.setText(StringUtils.friendly_time(data.getCreatedAt()));
        contentText.setText(data.getContent());


    }
}
