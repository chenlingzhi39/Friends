package com.example.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by Administrator on 2016/1/9.
 */
public  class BasicActivity extends BaseActivity implements Communicate {
    public final int START=0;
    public final int SUCCEED=1;
    public final int FAIL=2;
    public ProgressDialog pd;
    public String dialog_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Handler handler= new Handler(){
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what){
               case START:

                   start();
                   break;
               case SUCCEED:
                   if(pd!=null)
                   pd.dismiss();
                   succeed();
                   break;
               case FAIL:
                   if(pd!=null)
                   pd.dismiss();
                   fail();
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
    public  void loadMoreQuery() {

    }

    @Override
    public void refreshQuery() {

    }
    public void setDialogContent(String content){
        dialog_content=content;
    }
}
