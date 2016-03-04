package com.example.module.file.model;

import android.content.Context;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface FileModel<T> {
    void sendFile(Context context,T t,FileModelImpl.SendFileListener sendFileListener);
}
