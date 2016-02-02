package com.example.adapter;

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
import android.widget.Toast;

import com.example.administrator.myapplication.R;
import com.example.ui.MyApplication;
import com.example.util.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.daoexample.CommentToMe;

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
        /*判断输入何种类型，并与系统做连接*/
        Linkify.addLinks
                (
                        content, Linkify.WEB_URLS |
                                Linkify.EMAIL_ADDRESSES |
                                Linkify.PHONE_NUMBERS
                );
        Linkify.addLinks
                (
                        comment, Linkify.WEB_URLS |
                                Linkify.EMAIL_ADDRESSES |
                                Linkify.PHONE_NUMBERS
                );
     if(data.getHead()!=null)
         imageLoader.displayImage(data.getHead(),userHead);
        else userHead.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_launcher));
         userName.setText(data.getUser_name());
         comment.setText(data.getComment_content());
         addtime.setText(StringUtils.friendly_time(data.getCreate_time()));


        SpannableString spannableString1 = new SpannableString(MyApplication.getInstance().getCurrentUser().getUsername() + ":" + data.getPost_content());


        spannableString1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(getContext(), "who hit me", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ds.linkColor);
                ds.setUnderlineText(false); //去掉下划线
            }
        }, 0, MyApplication.getInstance().getCurrentUser().getUsername().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString1.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.material_blue_500)), 0, MyApplication.getInstance().getCurrentUser().getUsername().length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(spannableString1);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
