package com.cyan.module.file.presenter;

import android.content.Context;

import com.cyan.module.file.model.FileModel;
import com.cyan.module.file.model.FileModelImpl;
import com.cyan.module.file.view.SendFileView;
import com.cyan.util.SimpleHandler;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2016/3/4.
 */
public class FilePresenterImpl implements FilePresenter<File>{
    private Context context;
    private SendFileView sendFileView;
   private FileModel fileModel;
    private BmobFile bmobFile;
    public FilePresenterImpl(Context context, SendFileView sendFileView) {
        fileModel=new FileModelImpl();
        this.context = context;
        this.sendFileView = sendFileView;
    }

    @Override
    public void sendFile( File file) {

        bmobFile=new BmobFile(file);
    fileModel.sendFile(context,bmobFile , new UploadFileListener() {
        @Override
        public void onStart() {
            SimpleHandler.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    sendFileView.showHorizonalDialog();}});
                }

                @Override
                public void onSuccess() {
                    sendFileView.toastUploadSuccess();
                    sendFileView.getFile(bmobFile);
                }

                @Override
                public void onFailure(int i, String s) {
                    sendFileView.toastUploadFailure(i,s);
                }

                @Override
                public void onProgress(Integer progress) {
                    sendFileView.updateHorizonalDialog(progress);
                }

                @Override
                public void onFinish() {
                    sendFileView.dismissHorizonalDialog();
                }
            });
    }


}
