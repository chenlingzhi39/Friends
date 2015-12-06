
package com.example.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.example.ui.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2015/11/6.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    ArrayList<Post> posts;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Context context;
    public static final int TYPE_FOOTER = 0;
    public static final int TYPE_NORMAL = 1;
    private View footerView;
    private Boolean hasNavigationBar;

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(getItemViewType(position) == TYPE_FOOTER) return;



       if(posts.get(position).getAuthor().getHead()!=null)
           imageLoader.displayImage(posts.get(position).getAuthor().getHead().getFileUrl(context), holder.userHead, MyApplication.getInstance().getOptions());

        if(posts.get(position).getImage()!=null)
        { imageLoader.displayImage(posts.get(position).getImage().getFileUrl(context), holder.image, MyApplication.getInstance().getOptions());
        holder.image.setVisibility(View.VISIBLE);}else{holder.image.setVisibility(View.GONE);}
        holder.userName.setText(posts.get(position).getAuthor().getUsername());
        holder.content.setText(posts.get(position).getContent());
        holder.addtime.setText(posts.get(position).getCreatedAt());
        holder.checkBox.setVisibility(View.GONE);
        holder.praise.setText(posts.get(position).getPraise_count() + "");
        holder.comment.setText(posts.get(position).getComment_count() + "");


        if( MyApplication.getInstance().getCurrentUser()!=null)
        {
            BmobQuery<Post> query = new BmobQuery<Post>();


            String[] praise_user_id={MyApplication.getInstance().getCurrentUser().getObjectId()};
            query.addWhereEqualTo("objectId", posts.get(position).getObjectId());
            query.addWhereContainsAll("praise_user_id", Arrays.asList(praise_user_id));
        query.findObjects(context, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list.size() > 0) {
                    posts.get(position).setIs_praised(true);
                    holder.praise.setTextColor(context.getResources().getColor(R.color.material_blue_500));
                    Log.i(position + "", "查询个数：" + list.size());
                } else {
                    posts.get(position).setIs_praised(false);
                    holder.praise.setTextColor(context.getResources().getColor(android.R.color.black));
                    holder.praise.setTag(false);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        }
        holder.praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getCurrentUser() != null) {
                    holder.praise.setClickable(false);
                    Post post = new Post();
                    post.setObjectId(posts.get(position).getObjectId());
                    if (posts.get(position).getIs_praised()) {
                        posts.get(position).setPraise_count(posts.get(position).getPraise_count() - 1);
                        post.removeAll("praise_user_id", Arrays.asList(MyApplication.getInstance().getCurrentUser().getObjectId()));
                        post.update(context, new UpdateListener() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                posts.get(position).setIs_praised(false);
                                holder.praise.setTextColor(context.getResources().getColor(android.R.color.black));
                                holder.praise.setClickable(true);
                                Log.i("bmob", "从hobby字段中移除阅读、唱歌、游泳成功");
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Log.i("bmob", "从hobby字段中移除阅读、唱歌、游泳失败：" + msg);
                            }
                        });
                    } else {
                        posts.get(position).setPraise_count(posts.get(position).getPraise_count() + 1);
                        post.addUnique("praise_user_id", MyApplication.getInstance().getCurrentUser().getObjectId());
                        post.update(context, posts.get(position).getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                posts.get(position).setIs_praised(true);
                                holder.praise.setTextColor(context.getResources().getColor(R.color.material_blue_500));
                                holder.praise.setClickable(true);
                                Log.i("bmob", "添加爱好成功");
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Log.i("bmob", "添加爱好失败：" + msg);
                            }
                        });
                    }

                    posts.get(position).update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            holder.praise.setText(posts.get(position).getPraise_count() + "");
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });

                }
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(footerView!=null&&viewType==TYPE_FOOTER)return new ViewHolder(footerView);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        if(hasNavigationBar)
        return posts.size()+1;
        else
            return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
       if(hasNavigationBar)
        if(position==posts.size())
               return TYPE_FOOTER;
        else return TYPE_NORMAL;
        return TYPE_NORMAL;
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
            if(itemView==footerView)return;
            ButterKnife.inject(this, itemView);
        }
    }

    public PostAdapter(ArrayList<Post> posts, Context context,Boolean hasNavigationBar) {
        this.context = context;
        this.posts = posts;
        this.hasNavigationBar=hasNavigationBar;

    }

}
