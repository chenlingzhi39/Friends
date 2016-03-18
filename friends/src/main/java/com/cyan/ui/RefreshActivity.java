package com.cyan.ui;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.cyan.App.AppManager;
import com.cyan.adapter.PostAdapter;
import com.cyan.bean.Post;
import com.cyan.bean.RefreshData;
import com.cyan.util.RxBus;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/3/14.
 */
public abstract class RefreshActivity extends BaseActivity {
    private Observable<RefreshData> mRefreshObservable;
    ArrayList<Post> posts;
    PostAdapter postAdapter;
    SparseArray<Boolean> is_praised;
    SparseArray<Boolean> is_collected;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        is_praised = new SparseArray<>();
        is_collected = new SparseArray<>();
        mRefreshObservable = RxBus.get().register("refresh", RefreshData.class);
        mRefreshObservable.subscribe(new Action1<RefreshData>() {
            @Override
            public void call(RefreshData data) {
                try {
                    Log.i("appname",AppManager.getAppManager().getCurrentActivity().getName());
                    if(!AppManager.getAppManager().getCurrentActivity().getName().equals(RefreshActivity.this.getClass().getName()))
                    refresh(data);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void refresh(RefreshData data){
        switch(data.getType()){
            case "praise":
                Log.i("refresh","praise");
                boolean praised = (boolean)data.getValue();
                for (Post post : posts) {
                    if (post.getObjectId().equals(data.getId())) {
                        if (praised)
                            post.setPraise_count(post.getPraise_count() + 1);
                        else
                            post.setPraise_count(post.getPraise_count() - 1);
                        is_praised.put(post.getId(), praised);
                        postAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
            case "collection":
                Log.i("refresh","collection");
                boolean collected = (boolean)data.getValue();
                for (Post post : posts) {
                    if (post.getObjectId().equals(data.getId())) {
                        is_collected.put(post.getId(), collected);
                        postAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
            case "comment":
                Log.i("refresh","comment");
                for (Post post : posts) {
                    if (post.getObjectId().equals(data.getId())) {
                        post.setComment_count(post.getComment_count() + 1);
                        postAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRefreshObservable != null) {
            RxBus.get().unregister("refresh", mRefreshObservable);
        }
    }
}
