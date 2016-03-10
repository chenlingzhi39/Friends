package com.example.module.file.view;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface SendFileView {
    void showHorizonalDialog();
    void updateHorizonalDialog(Integer i);
    void dismissHorizonalDialog();
    void toastUploadFailure(int i,String s);
    void toastUploadSuccess();
    void getFile(BmobFile bmobFile);
}
