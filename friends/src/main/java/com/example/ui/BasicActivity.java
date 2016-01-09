package com.example.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.dao.Communicate;

/**
 * Created by Administrator on 2016/1/9.
 */
public  class BasicActivity extends BaseActivity implements Communicate {
    private final int START=0;
    private final int SUCCEED=1;
    private final int FAIL=2;
    private ProgressDialog pd;
    private String dialog_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Handler handler= new Handler(){
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what){
               case START:
                   pd=ProgressDialog.show(getApplicationContext(),null,dialog_content);
                   start();
                   break;
               case SUCCEED:
                   pd.dismiss();
                   succeed();
                   break;
               case FAIL:
                   fail();
                   pd.dismiss();
                   break;
           }
       }
   };

    @Override
    public  void fail(){};

    @Override
    public  void start(){};

    @Override
    public  void succeed(){};

    @Override
    public void LoadMoreQuery() {

    }

    @Override
    public void refreshQuery() {

    }
    public void setDialogContent(String content){
        dialog_content=content;
    }
}
