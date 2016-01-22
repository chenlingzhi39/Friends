package com.example.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapter.CommentToMeAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.CommentToMe;
import com.example.db.DatabaseUtil;
import com.example.ui.MyApplication;
import com.example.widget.recyclerview.DividerItemDecoration;
import com.example.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/14.
 */
public class CommentFragment extends Fragment {
    private ArrayList<CommentToMe> commentToMes;
    private CommentToMeAdapter commentToMeAdapter;

public static CommentFragment newInstance(){
     CommentFragment commentFragment=new CommentFragment();
    return commentFragment;
}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        EasyRecyclerView commentList=(EasyRecyclerView)view.findViewById(R.id.comment_list);
        commentToMes=new ArrayList<>();
         commentToMes = DatabaseUtil.getInstance(getActivity()).queryCommentToMe(MyApplication.getInstance().getCurrentUser().getObjectId());
        commentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentList.setRefreshEnabled(false);
        commentList.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        commentList.showProgress();
      if (commentToMes.size() > 0) {
            commentToMeAdapter = new CommentToMeAdapter(getActivity());
            commentToMeAdapter.addAll(commentToMes);
            commentList.setAdapter(commentToMeAdapter);
            commentList.showRecycler();
        } else commentList.showEmpty();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
