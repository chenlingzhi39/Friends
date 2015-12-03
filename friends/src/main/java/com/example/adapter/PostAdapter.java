
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
import com.example.bean.User;
import com.example.ui.BaseApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
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
        final Post entity = posts.get(position);
        if (entity.getAuthor().getHead() != null)
            imageLoader.displayImage(entity.getAuthor().getHead().getFileUrl(context), holder.userHead,BaseApplication.getInstance().getOptions());
        if (entity.getImage() != null) {
            imageLoader.displayImage(entity.getImage().getFileUrl(context), holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }
        holder.userName.setText(entity.getAuthor().getUsername());
        holder.content.setText(entity.getContent());
        holder.addtime.setText(entity.getCreatedAt());
        holder.checkBox.setVisibility(View.GONE);
        holder.praise.setText(entity.getPraise_count() + "");
        holder.comment.setText(entity.getComment_count() + "");
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("objectId", BaseApplication.getInstance().getCurrentUser().getObjectId());
        query.addWhereRelatedTo("praises", new BmobPointer(entity));
        query.findObjects(context, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list.size() > 0)
                {   entity.setIs_praised(true);
                    holder.praise.setTextColor(context.getResources().getColor(R.color.material_blue_500));

               }
                else
                {    entity.setIs_praised(false);
                    holder.praise.setTextColor(context.getResources().getColor(android.R.color.black));
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        holder.praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.praise.setClickable(false);
                BmobRelation relation = new BmobRelation();
             if(entity.getIs_praised()) {
                 entity.setPraise_count(entity.getPraise_count() - 1);
                 relation.remove(BaseApplication.getInstance().getCurrentUser());
             }else{  entity.setPraise_count(entity.getPraise_count() +1);
                 relation.add(BaseApplication.getInstance().getCurrentUser());}
                entity.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        holder.praise.setText(entity.getPraise_count() + "");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
                 entity.setPraises(relation);
                 entity.update(context, new UpdateListener() {
                     @Override
                     public void onSuccess() {
                         if(entity.getIs_praised()) {
                             entity.setIs_praised(false);
                             holder.praise.setTextColor(context.getResources().getColor(android.R.color.black));
                             ;}
                         else{
                             entity.setIs_praised(true);
                             holder.praise.setTextColor(context.getResources().getColor(R.color.material_blue_500));

                         }
                         holder.praise.setClickable(true);
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
