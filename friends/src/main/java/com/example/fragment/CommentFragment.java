package com.example.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.myapplication.R;
import com.example.bean.CommentToMe;
import com.example.db.DatabaseUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/14.
 */
public class CommentFragment extends Fragment{
    private ArrayList<CommentToMe> commentToMes;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_comment,container);
        commentToMes=DatabaseUtil.getInstance(getActivity()).queryCommentToMe();


        return view;
    }


}
