package com.example.module.file.model;

import android.content.Context;
import android.util.Log;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2016/3/4.
 */
public class FileModelImpl implements FileModel<File>{
    @Override
    public void sendFile(Context context, File file,final FileModelImpl.SendFileListener sendFileListener) {
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.upload(context, new UploadFileListener() {
            @Override
            public void onStart() {
                sendFileListener.onStart();
            }

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                sendFileListener.onSuccess(bmobFile);
            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub
                Log.i("life", "uploadMovoieFile-->onProgress:" + arg0);
                sendFileListener.onProgress(arg0);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                sendFileListener.onFailure(arg0,arg1);
            }

            @Override
            public void onFinish() {
                sendFileListener.onFinish();
            }
        });
    }

    public interface SendFileListener {
        void onStart();
        void onSuccess(BmobFile imageFile);
        void onFailure(int i,String s);
        void onProgress(Integer progress);
        void onFinish();
    }
}
