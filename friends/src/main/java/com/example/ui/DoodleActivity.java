package com.example.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.administrator.myapplication.R;
import com.example.widget.DoodleView;

import butterknife.InjectView;
import butterknife.OnClick;

public class DoodleActivity extends Activity {


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
    @InjectView(R.id.doodleView)
    DoodleView doodleView;
    @InjectView(R.id.settings)
    LinearLayout settings;
    private String[] modes = {"自由曲线", "橡皮擦"};
    private int curMode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_doodle);

        //setContentView(R.layout.activity_doodle);
        int width = this.getWindowManager().getDefaultDisplay().getWidth();
        int height = this.getWindowManager().getDefaultDisplay().getHeight();
        //background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //Canvas canvas = new Canvas(background);
        //Paint paint = new Paint();
        //paint.setColor(Color.WHITE);
        //canvas.drawColor(Color.WHITE);
        //canvas.drawBitmap(background, 0, 0, paint);

        //background = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        doodleView = new DoodleView(this);
        doodleView.setColor(Color.BLACK);
        doodleView.setBrushSize(10f);


    }

    @OnClick(R.id.brush)
    public void brush() {
        Dialog dialog = new AlertDialog.Builder(this).setSingleChoiceItems(modes, curMode, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                curMode = which;
                switch (which) {
                    case 0:
                        doodleView.setMode(DoodleView.Mode.LINE_MODE);
                        brush.setImageDrawable(getResources().getDrawable(R.drawable.ic_brush_dark_x24));
                        curMode = 0;
                        break;
                    case 1:
                        doodleView.setMode(DoodleView.Mode.ERASER_MODE);
                        brush.setImageDrawable(getResources().getDrawable(R.drawable.ic_eraser_dark_x24));
                        curMode = 1;
                        break;
                }
            }
        }).create();
    }

    @OnClick(R.id.palette)
    public void palette() {

    }

    @OnClick(R.id.broom)
    public void broom() {

    }

    @OnClick(R.id.undo)
    public void undo() {
        doodleView.setEnableDragAndZoom(false);
        doodleView.rollBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_doodle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.menu_line_mode:
                doodleView.setEnableDragAndZoom(false);
                doodleView.setMode(DoodleView.Mode.LINE_MODE);
                break;
            case R.id.menu_circle_mode:
                doodleView.setEnableDragAndZoom(false);
                doodleView.setMode(DoodleView.Mode.CIRCLE_MODE);
                break;
            case R.id.menu_eraser_mode:
                doodleView.setEnableDragAndZoom(false);
                doodleView.setMode(DoodleView.Mode.ERASER_MODE);
                break;
            case R.id.menu_rect_mode:
                doodleView.setEnableDragAndZoom(false);
                doodleView.setMode(DoodleView.Mode.RECT_MODE);
                break;
            case R.id.menu_roll_back:
                doodleView.setEnableDragAndZoom(false);
                doodleView.rollBack();
                break;
            case R.id.menu_clear_screen:
                //doodleView.setEnableDragAndZoom(false);
                doodleView.clear();
                break;
            case R.id.menu_enable_drag_and_zoom:
                doodleView.setEnableDragAndZoom(true);
                doodleView.setMode(DoodleView.Mode.NONE_MODE);
                break;
            case R.id.menu_deep_eraser_mode:
                doodleView.setEnableDragAndZoom(false);
                doodleView.setMode(DoodleView.Mode.DEEP_ERASER_MODE);
                break;
            case R.id.menu_text:
                showInputTextDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInputTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入文字");
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                doodleView.setMode(DoodleView.Mode.TEXT_MODE);
                doodleView.addTextAction(text);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
