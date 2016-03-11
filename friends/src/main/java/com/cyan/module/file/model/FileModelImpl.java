package com.cyan.module.file.model;

import android.content.Context;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2016/3/4.
 */
public class FileModelImpl implements FileModel<BmobFile>{
    @Override
    public void sendFile(Context context, BmobFile bmobFile,UploadFileListener uploadFileListener) {
        bmobFile.upload(context, uploadFileListener);
    }


}
