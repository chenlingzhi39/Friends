
package com.example.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.example.bean.User;
import com.example.listener.OnItemClickListener;
import com.example.ui.CollectionActivity;
import com.example.ui.MainActivity;
import com.example.ui.MyApplication;
import com.example.util.StringUtils;
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
    private SparseArray<Boolean> is_praised;
    private SparseArray<Boolean> is_collected;
    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }



    @Override

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_FOOTER) return;
        final Post entity = posts.get(position);
/*判断输入何种类型，并与系统做连接*/
        Linkify.addLinks
                (
                       holder.content, Linkify.WEB_URLS |
                                Linkify.EMAIL_ADDRESSES |
                                Linkify.PHONE_NUMBERS
                );
        holder.listItem.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            /*  Intent intent = new Intent(context, ContentActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              intent.putExtra("post", entity);
              intent.putExtra("isPraised", is_praised.get(entity.getId()));
              intent.putExtra("isCollected", is_collected.get(entity.getId()));
              context.startActivity(intent);*/
              onItemClickListener.onClick(holder.itemView,position);
          }
      });
        if (entity.getAuthor().getHead() != null)
            imageLoader.displayImage(entity.getAuthor().getHead().getFileUrl(context), holder.userHead, MyApplication.getInstance().getOptions());
              else
           holder.userHead.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));
        if (entity.getImage() != null) {
            imageLoader.displayImage(entity.getImage().getFileUrl(context), holder.image, MyApplication.getInstance().getOptions());
            holder.image.setVisibility(View.VISIBLE);
        } else {
            holder.image.setVisibility(View.GONE);
        }
        holder.userName.setText(entity.getAuthor().getUsername());
        holder.content.setText(entity.getContent());
        holder.addtime.setText(StringUtils.friendly_time(entity.getCreatedAt()));
        holder.checkBox.setVisibility(View.GONE);
        holder.praise.setText(entity.getPraise_count() + "");
        holder.comment.setText(entity.getComment_count() + "");
        if (MyApplication.getInstance().getCurrentUser() != null) {
            if (is_praised.get(entity.getId(),false)) {
                holder.praise.setTextColor(context.getResources().getColor(R.color.material_blue_500));

            } else {
                holder.praise.setTextColor(context.getResources().getColor(android.R.color.black));
            }
            if(is_collected.get(entity.getId(),false)){
                holder.collection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_fav_selected));
            }else{
                holder.collection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_fav_normal));
            }
        }else{
            holder.praise.setTextColor(holder.praise.getCurrentHintTextColor());
            holder.collection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_fav_normal));
        }

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
                    if (is_praised.get(entity.getId(),false)) {
                        entity.increment("praise_count", -1);
                        post.removeAll("praise_user_id", Arrays.asList(MyApplication.getInstance().getCurrentUser().getObjectId()));
                        post.update(context, new UpdateListener() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                is_praised.put(entity.getId(),false);
                                entity.setPraise_count(entity.getPraise_count() - 1);
                                holder.praise.setTextColor(context.getResources().getColor(android.R.color.black));
                                //DatabaseUtil.getInstance(context).deletePraise(entity);
                                holder.praise.setClickable(true);
                                holder.praise.setText(entity.getPraise_count() + "");
                                Log.i("bmob", "删除点赞成功");
                                if(context instanceof CollectionActivity)
                                {Intent intent = new Intent();
                                intent.putExtra("post_id", entity.getId());
                                intent.putExtra("is_praised", is_praised.get(entity.getId(), false));
                                ((Activity)context).setResult(MainActivity.REFRESH_PRAISE, intent);}
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Log.i("bmob", "删除点赞失败：" + msg);

                            }

                            @Override
                            public void postOnFailure(int code, String msg) {
                                super.postOnFailure(code, msg);
                                holder.praise.setClickable(true);
                            }
                        });
                    } else {
                        entity.increment("praise_count",1);
                        post.addUnique("praise_user_id", MyApplication.getInstance().getCurrentUser().getObjectId());
                        post.update(context, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                is_praised.put(entity.getId(),true);
                                entity.setPraise_count(entity.getPraise_count()+1);
                                holder.praise.setTextColor(context.getResources().getColor(R.color.material_blue_500));
                                holder.praise.setClickable(true);
                                //DatabaseUtil.getInstance(context).insertPraise(entity);
                                holder.praise.setText(entity.getPraise_count() + "");
                                Log.i("bmob", "添加点赞成功");
                                if(context instanceof CollectionActivity)
                                {Intent intent = new Intent();
                                intent.putExtra("post_id", entity.getId());
                                intent.putExtra("is_praised", is_praised.get(entity.getId(), false));
                                ((Activity)context).setResult(MainActivity.REFRESH_PRAISE, intent);}
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Log.i("bmob", "添加点赞失败：" + msg);
                                holder.praise.setClickable(true);
                            }

                            @Override
                            public void postOnFailure(int code, String msg) {
                                super.postOnFailure(code, msg);
                                holder.praise.setClickable(true);
                            }
                        });
                    }


                }
            }
        });
        holder.collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getCurrentUser() != null) {
                    holder.collection.setClickable(false);

                   final User user=MyApplication.getInstance().getCurrentUser();
                    if (is_collected.get(entity.getId(), false)) {
                        user.removeAll("collect_post_id", Arrays.asList(entity.getObjectId()));
                        user.update(context, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                is_collected.put(entity.getId(), false);
                                holder.collection.setClickable(true);
                                holder.collection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_fav_normal));
                                user.getCollect_post_id().remove(entity.getObjectId());
                                Log.i("bmob", "删除收藏成功");
                                if(context instanceof CollectionActivity)
                                {Intent intent = new Intent();
                                intent.putExtra("post_id", entity.getId());
                                intent.putExtra("is_collected", is_collected.get(entity.getId(), false));
                                ((Activity)context).setResult(MainActivity.REFRESH_COLLECTION,intent);}
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.i("bmob", "删除收藏失败" + s);
                            }

                            @Override
                            public void postOnFailure(int code, String msg) {
                                super.postOnFailure(code, msg);
                                holder.collection.setClickable(true);
                            }

                        });
                    } else {
                        user.addUnique("collect_post_id", entity.getObjectId());
                        user.update(context, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                is_collected.put(entity.getId(), true);
                                holder.collection.setClickable(true);
                                holder.collection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_fav_selected));
                                user.getCollect_post_id().add(entity.getObjectId());
                                Log.i("bmob", "添加收藏成功");
                                if(context instanceof CollectionActivity)
                                {    Intent intent = new Intent();
                                intent.putExtra("post_id", entity.getId());
                                intent.putExtra("is_collected", is_collected.get(entity.getId(), false));
                                ((Activity)context).setResult(MainActivity.REFRESH_COLLECTION, intent);}
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.i("bmob", "添加收藏失败" + s);
                            }

                            @Override
                            public void postOnFailure(int code, String msg) {
                                super.postOnFailure(code, msg);
                                holder.collection.setClickable(true);
                            }
                        });
                    }

                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (footerView != null && viewType == TYPE_FOOTER) return new ViewHolder(footerView);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        if (hasNavigationBar)
            return posts.size() + 1;
        else
            return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasNavigationBar)
            if (position == posts.size())
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
        @InjectView(R.id.list_item)
        LinearLayout listItem;
        public ViewHolder(View itemView) {

            super(itemView);
            if (itemView == footerView) return;
            ButterKnife.inject(this, itemView);
        }
    }

    public PostAdapter(ArrayList<Post> posts,SparseArray<Boolean> is_praised,SparseArray<Boolean> is_collected, Context context, Boolean hasNavigationBar) {
        this.context = context;
        this.posts = posts;
        this.hasNavigationBar = hasNavigationBar;
        this.is_praised=is_praised;
        this.is_collected=is_collected;
    }

}
