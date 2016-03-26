package com.cyan.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.cyan.app.MyApplication;
import com.cyan.adapter.RecordAdapter;
import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.community.R;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Record;
import de.greenrobot.daoexample.RecordDao;

/**
 * Created by Administrator on 2016/2/15.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_list,
        toolbarTitle = R.string.record
)
public class RecordActivity extends BaseActivity {
    @InjectView(R.id.list)
    EasyRecyclerView recordList;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    private RecordAdapter recordAdapter;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private RecordDao recordDao;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getRecords();
    }


    private void getRecords(){
        recordList.setRefreshEnabled(false);
        recordList.showProgress();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "records-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        recordDao = daoSession.getRecordDao();
        String textColumn = RecordDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        String where= RecordDao.Properties.User_id.columnName+" = '" + MyApplication.getInstance().getCurrentUser().getObjectId() + "'";
        cursor = db.query(recordDao.getTablename(),recordDao.getAllColumns(),where, null, null, null, orderBy);
        if (cursor == null) {
            recordList.showEmpty();
            return;
        }
        recordList.setLayoutManager(new LinearLayoutManager(this));
        recordAdapter = new RecordAdapter(this);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Record record = new Record();
            recordDao.readEntity(cursor, record, 0);
            recordAdapter.addAll(record);
        }
        recordAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i("record", "click");
                Intent intent = new Intent(RecordActivity.this, ContentActivity.class);
                intent.putExtra("type", recordAdapter.getData().get(position).getType());
                intent.putExtra("object_id", recordAdapter.getData().get(position).getObject_id());
                intent.putExtra("parent_id", recordAdapter.getData().get(position).getParent_id());
                startActivityForResult(intent, 0);

            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<Record>(recordDao,recordAdapter));
        itemTouchHelper.attachToRecyclerView(recordList.getRecyclerView());
        if (cursor != null) {
            recordList.showRecycler();
            recordList.setAdapter(recordAdapter);
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
