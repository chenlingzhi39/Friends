package com.cyan.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cyan.App.MyApplication;
import com.cyan.bean.Post;
import com.cyan.bean.RefreshData;
import com.cyan.bean.User;
import com.cyan.community.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/3/2.
 */
public class PraiseUtils {

    public static void flush(Context context, final SparseArray<Boolean> is_praised, final SparseArray<Boolean> is_collected, final List<Post> posts) {
        if (MyApplication.getInstance().getCurrentUser() != null) {
            for (final Post post : posts) {

                BmobQuery<Post> query = new BmobQuery<Post>();


                String[] praise_user_id = {MyApplication.getInstance().getCurrentUser().getObjectId()};

                query.addWhereEqualTo("objectId", post.getObjectId());
                query.addWhereContainsAll("praise_user_id", Arrays.asList(praise_user_id));

                query.findObjects(context, new FindListener<Post>() {
                    @Override
                    public void onSuccess(List<Post> list) {
                        if (list.size() > 0) {
                            is_praised.append(post.getId(), true);
                            Log.i("objectid", post.getId() + "");
                        } else {

                            is_praised.append(post.getId(), false);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });


            }
            //DatabaseUtil.getInstance(getApplicationContext()).insertPraise(post);
            List<String> collect_post_id = new ArrayList<String>();
            collect_post_id = MyApplication.getInstance().getCurrentUser().getCollect_post_id();
            if (collect_post_id != null) {
                for (final Post post : posts) {
                    if (collect_post_id.contains(post.getObjectId()))
                        is_collected.append(post.getId(), true);
                    else
                        is_collected.append(post.getId(), false);


                }
            }

        }
    }

    public static void setPraise(final Context context, final TextView praise, final Post entity, final SparseArray<Boolean> is_praised) {
        final Drawable drawable=context.getResources().getDrawable(R.drawable.ic_action_love_selected);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        final Drawable drawable1=context.getResources().getDrawable(R.drawable.ic_action_love);
        drawable1.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        if (MyApplication.getInstance().getCurrentUser() != null) {
            praise.setClickable(false);
            Post post = new Post();
            post.setObjectId(entity.getObjectId());

                if (is_praised.get(entity.getId(), false)) {
                    entity.increment("praise_count", -1);
                    post.removeAll("praise_user_id", Arrays.asList(MyApplication.getInstance().getCurrentUser().getObjectId()));
                } else {
                    entity.increment("praise_count", 1);
                    post.addUnique("praise_user_id", MyApplication.getInstance().getCurrentUser().getObjectId());
                }
                post.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        if (is_praised.get(entity.getId(), false)) {
                            is_praised.put(entity.getId(), false);
                            entity.setPraise_count(entity.getPraise_count() - 1);
                            praise.setCompoundDrawables(drawable1,null,null,null);
                        } else {
                            is_praised.put(entity.getId(), true);
                            entity.setPraise_count(entity.getPraise_count() + 1);
                            praise.setCompoundDrawables(drawable, null, null, null);
                        }
                        //DatabaseUtil.getInstance(context).deletePraise(entity);
                        praise.setClickable(true);
                        praise.setText(entity.getPraise_count() + "");
                        /*if (context instanceof CollectionActivity || context instanceof PostListActivity || context instanceof UserInfoActivity) {
                            Intent intent = new Intent();
                            intent.putExtra("post_id", entity.getObjectId());
                            intent.putExtra("is_praised", is_praised.get(entity.getId(), false));
                            ((Activity) context).setResult(MainActivity.REFRESH_PRAISE, intent);
                        }*/
                        RefreshData data=new RefreshData(entity.getObjectId(),"praise",is_praised.get(entity.getId(), false));
                        RxBus.get().post("refresh", data);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        // TODO Auto-generated method stub

                        praise.setClickable(true);
                    }

                    @Override
                    public void postOnFailure(int code, String msg) {
                        super.postOnFailure(code, msg);
                        praise.setClickable(true);
                    }
                });

        }
    }
    public static void setCollection(final Context context, final ImageButton collection, final Post entity, final SparseArray<Boolean> is_collected) {
        if (MyApplication.getInstance().getCurrentUser() != null) {
            final Gson gson =new Gson();
            collection.setClickable(false);
            final User user = new User();
            if (is_collected.get(entity.getId(), false))
                user.removeAll("collect_post_id", Arrays.asList(entity.getObjectId()));
            else user.addUnique("collect_post_id", entity.getObjectId());
            user.update(context, MyApplication.getInstance().getCurrentUser().getObjectId(), new UpdateListener() {
                @Override
                public void onSuccess() {
                    collection.setClickable(true);
                    if (is_collected.get(entity.getId(), false)) {
                        is_collected.put(entity.getId(), false);
                        collection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_fav_normal));
                        MyApplication.getInstance().getCurrentUser().getCollect_post_id().remove(entity.getObjectId());
                    } else {
                        is_collected.put(entity.getId(), true);
                        collection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_fav_selected));
                        if (MyApplication.getInstance().getCurrentUser().getCollect_post_id() != null)
                            MyApplication.getInstance().getCurrentUser().getCollect_post_id().add(entity.getObjectId());
                        else {
                            List<String> collect_post_id = new ArrayList<String>();
                            collect_post_id.add(entity.getObjectId());
                            MyApplication.getInstance().getCurrentUser().setCollect_post_id(collect_post_id);
                        }
                    }
                    SPUtils.put(context,"bmob_sp","user",gson.toJson(MyApplication.getInstance().getCurrentUser()));
                   /* if (context instanceof CollectionActivity || context instanceof PostListActivity || context instanceof UserInfoActivity) {
                        Intent intent = new Intent();
                        intent.putExtra("post_id", entity.getObjectId());
                        intent.putExtra("is_collected", is_collected.get(entity.getId(), false));
                        ((Activity) context).setResult(MainActivity.REFRESH_COLLECTION, intent);
                    }*/
                    RefreshData data=new RefreshData(entity.getObjectId(),"collection",is_collected.get(entity.getId(), false));
                    RxBus.get().post("refresh",data);
                }

                @Override
                public void onFailure(int i, String s) {
                }

                @Override
                public void postOnFailure(int code, String msg) {
                    super.postOnFailure(code, msg);
                    collection.setClickable(true);
                }

            });
        }
    }
}

