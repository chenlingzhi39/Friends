package com.example.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adapter.FocusAdapter;
import com.example.adapter.RecordAdapter;
import com.example.adapter.RecyclerArrayAdapter;
import com.example.adapter.ReplyToMeAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Focus;
import com.example.bean.Post;
import com.example.bean.User;
import com.example.widget.recyclerview.DividerItemDecoration;
import com.example.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import de.greenrobot.daoexample.CommentToMeDao;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Record;
import de.greenrobot.daoexample.RecordDao;
import de.greenrobot.daoexample.ReplyToMe;

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
                Intent intent = new Intent();
                intent.putExtra("post_id", data.getStringExtra("post_id"));
                intent.putExtra("is_praised", data.getBooleanExtra("is_praised", false));
                setResult(MainActivity.REFRESH_PRAISE, intent);
                break;
            case MainActivity.REFRESH_COLLECTION:
                intent = new Intent();
                intent.putExtra("post_id", data.getStringExtra("post_id"));
                intent.putExtra("is_collected", data.getBooleanExtra("is_collected", false));
                setResult(MainActivity.REFRESH_COLLECTION, intent);
                break;
            case MainActivity.REFRESH_COMMENT:
                intent = new Intent();
                intent.putExtra("post", (Post) data.getExtras().get("post"));
                setResult(MainActivity.REFRESH_COMMENT, intent);
                break;}
    }
}
