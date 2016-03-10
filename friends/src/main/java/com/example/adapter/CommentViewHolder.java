package com.example.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.myapplication.R;
import com.example.bean.Comment;
import com.example.ui.UserInfoActivity;
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
    @InjectView(R.id.replyTo)
    TextView replyTo;


    public CommentViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_comment);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void setData(final Comment data) {
/*判断输入何种类型，并与系统做连接*/
        Linkify.addLinks
                (
                        contentText, Linkify.WEB_URLS |
                                Linkify.EMAIL_ADDRESSES |
                                Linkify.PHONE_NUMBERS
                );
        if (data.getAuthor().getHead() != null) {
            Glide.with(getContext()).load(data.getAuthor().getHead().getFileUrl(getContext())).into(userHead);
        }
        userName.setText(data.getAuthor().getUsername());
        time.setText(StringUtils.friendly_time(data.getCreatedAt()));
        contentText.setText(data.getContent());
        if (data.getComment() != null) {
            replyTo.setVisibility(View.VISIBLE);
            replyTo.setText(data.getComment().getAuthor().getUsername() + ":" + data.getComment().getContent());
            SpannableString spannableString1 = new SpannableString(data.getComment().getAuthor().getUsername() + ":" + data.getComment().getContent());


            spannableString1.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                   Intent intent=new Intent(getContext(),UserInfoActivity.class);
                    intent.putExtra("user",data.getComment().getAuthor());
                    ((Activity)getContext()).startActivityForResult(intent,0);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);
                    ds.setUnderlineText(false); //去掉下划线
                }
            }, 0, data.getComment().getAuthor().getUsername().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString1.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.material_blue_500)), 0, data.getComment().getAuthor().getUsername().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            replyTo.setText(spannableString1);
            replyTo.setMovementMethod(LinkMovementMethod.getInstance());
            replyTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else replyTo.setVisibility(View.GONE);
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserInfoActivity.class);
                intent.putExtra("user", data.getAuthor());
                ((Activity)getContext()).startActivityForResult(intent,0);
            }
        });

    }

}
