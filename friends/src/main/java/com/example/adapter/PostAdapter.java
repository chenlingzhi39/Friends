package com.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2015/11/6.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    ArrayList<Post> posts;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Context context;


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
      final Post entity=posts.get(position);
        if(entity.getAuthor().getHead()!=null)
        imageLoader.displayImage(entity.getAuthor().getHead().getFileUrl(context), holder.userHead);
        if(entity.getImage()!=null){
            imageLoader.displayImage(entity.getImage().getFileUrl(context), holder.image);
        }else{
            holder.image.setVisibility(View.GONE);
        }
        holder.userName.setText(entity.getAuthor().getUsername());
        holder.content.setText(entity.getContent());
        holder.addtime.setText(entity.getCreatedAt());
        holder.checkBox.setVisibility(View.GONE);
        holder.praise.setText(entity.getPraise_count() + "");
        holder.comment.setText(entity.getComment_count() + "");
        BmobRelation relation = new BmobRelation();
        relation.getObjects();
        holder.praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               entity.setPraise_count(entity.getPraise_count() + 1);

                entity.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        holder.praise.setText(entity.getPraise_count() + "");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.user_head)
        ImageView userHead;
        @InjectView(R.id.user_name)
        TextView userName;
        @InjectView(R.id.addtime)
        TextView addtime;
        @InjectView(R.id.checkBox)
        CheckBox checkBox;
        @InjectView(R.id.content)
        TextView content;
        @InjectView(R.id.image)
       ImageView image;
        @InjectView(R.id.location)
        TextView location;
        @InjectView(R.id.praise)
        TextView praise;
        @InjectView(R.id.comment)
        TextView comment;
        @InjectView(R.id.collection)
        ImageButton collection;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public PostAdapter(ArrayList<Post> posts,Context context) {
        this.context=context;
        this.posts = posts;
    }
}
