package com.example.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.adapter.RecordAdapter;
import com.example.adapter.RecyclerArrayAdapter;
import com.example.administrator.myapplication.R;
import com.example.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.daoexample.CommentToMeDao;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Record;
import de.greenrobot.daoexample.RecordDao;

/**
 * Created by Administrator on 2016/2/15.
 */
public class RecordActivity extends BaseActivity {
    @InjectView(R.id.list)
    EasyRecyclerView recordList;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    private ArrayList<Record> records;
    private RecordAdapter recordAdapter;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private RecordDao recordDao;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("记录");
        recordList.setRefreshEnabled(false);
        recordList.setLayoutManager(new LinearLayoutManager(this));
        recordList.showProgress();
        records=new ArrayList<>();
        getRecords();


        if ( records.size() > 0) {
            recordAdapter = new RecordAdapter(this);
            recordAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent=new Intent(RecordActivity.this,ContentActivity.class);
                    intent.putExtra("type",records.get(position).getType());
                    intent.putExtra("object_id",records.get(position).getObject_id());
                    intent.putExtra("parent_id",records.get(position).getParent_id());
                    startActivityForResult(intent,0);

                }
            });
            recordAdapter.addAll(records);
            recordList.setAdapter(recordAdapter);
            recordList.showRecycler();
        } else recordList.showEmpty();
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            /**
             * @param recyclerView
             * @param viewHolder 拖动的ViewHolder
             * @param target 目标位置的ViewHolder
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                 recordDao.delete(records.get(position));
                 records.remove(position);
                 recordAdapter.remove(position);
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float)viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(recordList.getRecyclerView());

    }


    private void getRecords(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "records-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        recordDao = daoSession.getRecordDao();

        String textColumn = CommentToMeDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        String where= RecordDao.Properties.User_id.columnName+" = '" + MyApplication.getInstance().getCurrentUser().getObjectId() + "'";
        cursor = db.query(recordDao.getTablename(),recordDao.getAllColumns(),where, null, null, null, orderBy);
        if (cursor == null) {
            return;
        }
       records = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Record record = new Record();
            recordDao.readEntity(cursor,record,0);
            records.add(record);
        }
        if (cursor != null) {
            cursor.close();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case MainActivity.REFRESH_PRAISE:

            case MainActivity.REFRESH_COLLECTION:

            case MainActivity.REFRESH_COMMENT:
                setResult(resultCode,data);
                break;}
    }
}
