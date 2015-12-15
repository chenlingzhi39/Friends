
package com.example.adapter;

import android.content.Context;
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
import com.example.db.DatabaseUtil;
import com.example.ui.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
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
         final Post entity=posts.get(position);


       if(entity.getAuthor().getHead()!=null)
           imageLoader.displayImage(entity.getAuthor().getHead().getFileUrl(context), holder.userHead, MyApplication.getInstance().getOptions());

        if(entity.getImage()!=null)
        { imageLoader.displayImage(entity.getImage().getFileUrl(context), holder.image, MyApplication.getInstance().getOptions());
        holder.image.setVisibility(View.VISIBLE);}else{holder.image.setVisibility(View.GONE);}
        holder.userName.setText(entity.getAuthor().getUsername());
        holder.content.setText(entity.getContent());
        holder.addtime.setText(entity.getCreatedAt());
        holder.checkBox.setVisibility(View.GONE);
        holder.praise.setText(entity.getPraise_count() + "");
        holder.comment.setText(entity.getComment_count() + "");
if(entity.getIs_praised())
    holder.praise.setTextColor(context.getResources().getColor(R.color.material_blue_500));
    else
    holder.praise.setTextColor(context.getResources().getColor(android.R.color.black));
        /*if( MyApplication.getInstance().getCurrentUser()!=null)
        {
            BmobQuery<Post> query = new BmobQuery<Post>();


            String[] praise_user_id={MyApplication.getInstance().getCurrentUser().getObjectId()};
            query.addWhereEqualTo("objectId", entity.getObjectId());
            query.addWhereContainsAll("praise_user_id", Arrays.asList(praise_user_id));

        query.findObjects(context, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list.size() > 0) {
                    entity.setIs_praised(true);
                    holder.praise.setTextColor(context.getResources().getColor(R.color.material_blue_500));
                    Log.i(position + "", "查询个数：" + list.size());
                } else {
                   entity.setIs_praised(false);
                    holder.praise.setTextColor(context.getResources().getColor(android.R.color.black));
                    holder.praise.setTag(false);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        }*/
        holder.praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getCurrentUser() != null) {
                    holder.praise.setClickable(false);
                    Post post = new Post();
                    post.setObjectId(entity.getObjectId());
                    if (entity.getIs_praised()) {
                        entity.setPraise_count(entity.getPraise_count() - 1);
                        post.removeAll("praise_user_id", Arrays.asList(MyApplication.getInstance().getCurrentUser().getObjectId()));
                        post.update(context, new UpdateListener() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                entity.setIs_praised(false);
                                holder.praise.setTextColor(context.getResources().getColor(android.R.color.black));
                                DatabaseUtil.getInstance(context).deletePraise(entity);
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
                        entity.setPraise_count(entity.getPraise_count() + 1);
                        post.addUnique("praise_user_id", MyApplication.getInstance().getCurrentUser().getObjectId());
                        post.update(context, entity.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                entity.setIs_praised(true);
                                holder.praise.setTextColor(context.getResources().getColor(R.color.material_blue_500));
                                holder.praise.setClickable(true);
                                DatabaseUtil.getInstance(context).insertPraise(entity);
                                Log.i("bmob", "添加爱好成功");
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Log.i("bmob", "添加爱好失败：" + msg);
                            }
                        });
                    }

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
