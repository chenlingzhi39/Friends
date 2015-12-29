package com.example.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.administrator.myapplication.R;
import com.example.widget.TuyaView;
import com.rarepebble.colorpicker.ColorPickerView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DoodleActivity extends BaseActivity {


    Bitmap background = null;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.palette)
    ImageButton palette;
    @InjectView(R.id.broom)
    ImageButton broom;
    @InjectView(R.id.undo)
    ImageButton undo;
    @InjectView(R.id.image)
    ImageButton image;
    @InjectView(R.id.brush)
    ImageButton brush;

    @InjectView(R.id.settings)
    LinearLayout settings;
    @InjectView(R.id.tuyaView)
    TuyaView tuyaView;
    Handler handler=new Handler();
    private int curMode=1;
    private int color= Color.BLACK;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_doodle);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("涂鸦板");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setContentView(R.layout.activity_doodle);
        int width = this.getWindowManager().getDefaultDisplay().getWidth();
        int height = this.getWindowManager().getDefaultDisplay().getHeight();
        //background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //Canvas canvas = new Canvas(background);
        //Paint paint = new Paint();
        //paint.setColor(Color.WHITE);
        //canvas.drawColor(Color.WHITE);
        //canvas.drawBitmap(background, 0, 0, paint);

      //  background = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);


    }

    @OnClick(R.id.brush)
    public void brush() {
switch (curMode){
    case 1:brush.setImageDrawable(getResources().getDrawable(R.drawable.ic_eraser_dark_x24));
        curMode=4;
        tuyaView.setMode(curMode);
        break;
    case 4:
        brush.setImageDrawable(getResources().getDrawable(R.drawable.ic_brush_dark_x24));
        curMode=1;
        tuyaView.setMode(curMode);
        break;
}

    }

    @OnClick(R.id.palette)
    public void palette() {
        showColorPickerDialog();
    }

    @OnClick(R.id.broom)
    public void broom() {

    }

    @OnClick(R.id.undo)
    public void undo() {
    tuyaView.undo();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doodle, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.finish:

                break;
            case R.id.delete:
            tuyaView.clear();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    class ColorPickerDialogHelper  implements
            DialogInterface.OnDismissListener{
        private Dialog mDialog;
        private View mView;
        private ColorPickerView colorPickerView;
        private ColorPickerDialogHelper() {
            mView=DoodleActivity.this.getLayoutInflater().inflate(R.layout.dialog_colorpicker,null);
             colorPickerView=(ColorPickerView)mView.findViewById(R.id.colorPicker);
             colorPickerView.setColor(color);
        }
        public View getView(){return mView; }
        public ColorPickerView getColorPickerView(){return colorPickerView;}
        @Override
        public void onDismiss(DialogInterface dialog) {
            mDialog=null;
        }
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }


    }
public void showColorPickerDialog(){
   final ColorPickerDialogHelper helper=new ColorPickerDialogHelper();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {

            Dialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(DoodleActivity.this, R.style.myDialog))
                    .setView(helper.getView())
                    .setOnDismissListener(helper)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            color=helper.getColorPickerView().getColor();
                            tuyaView.setColor(color);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();

            helper.setDialog(dialog);
            dialog.show();
        }

    };
   handler.post(runnable);
}
}
