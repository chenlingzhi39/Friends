package com.example.administrator.myapplication;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.app.Activity;

import java.util.logging.LogRecord;


public class MainActivity extends Activity {
    private FrameLayout fl;
    private SplashView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl = new FrameLayout(this);
        splash = new SplashView(this);
        fl.addView(splash);
        setContentView(fl);
        startLoad();
    }
  Handler handler=new Handler() ;

    public void startLoad() {
handler.postDelayed(new Runnable() {
    @Override
    public void run() {
        splash.splashAndDisappear();
        
    }
},3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
