package com.example.module.file.presenter;

import android.content.Context;

import com.example.module.file.model.FileModel;
import com.example.module.file.model.FileModelImpl;
import com.example.module.file.view.SendFileView;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/3/4.
 */
public class FilePresenterImpl implements FilePresenter<File>,FileModelImpl.SendFileListener{
    private Context context;
    private SendFileView sendFileView;
   private FileModel fileModel;
    public FilePresenterImpl(Context context, SendFileView sendFileView) {
        fileModel=new FileModelImpl();
        this.context = context;
        this.sendFileView = sendFileView;
    }

    @Override
    public void sendFile( File file) {
    fileModel.sendFile(context,file,this);
    }

    @Override
    public void onStart() {
        sendFileView.showHorizonalDialog();
    }

    @Override
    public void onSuccess(BmobFile imageFile) {
        sendFileView.toastUploadSuccess();
        sendFileView.getFile(imageFile);
    }

    @Override
    public void onFailure(int i, String s) {
        sendFileView.toastUploadFailure();
    }

    @Override
    public void onProgress(Integer progress) {
        sendFileView.updateHorizonalDialog(progress);
    }

    @Override
    public void onFinish() {
        sendFileView.dismissHorizonalDialog();
    }
}
