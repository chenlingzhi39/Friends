package com.cyan.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.cyan.bean.User;

/**
 * Created by Administrator on 2016/3/31.
 */
public class UserAdapter extends RecyclerArrayAdapter<User>{
    public UserAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(parent);
    }
}
