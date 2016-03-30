package com.cyan.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.cyan.adapter.DraftAdapter;
import com.cyan.adapter.RecyclerArrayAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.app.MyApplication;
import com.cyan.community.R;
import com.cyan.widget.recyclerview.DividerItemDecoration;
import com.cyan.widget.recyclerview.EasyRecyclerView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Draft;
import de.greenrobot.daoexample.DraftDao;

/**
 * Created by Administrator on 2016/3/11.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_list,
        toolbarTitle = R.string.draft
)
public class DraftActivity extends BaseActivity {
    @InjectView(R.id.list)
    EasyRecyclerView draftList;
    private DraftAdapter draftAdapter;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private DraftDao draftDao;
    private Cursor cursor;
    private ClipboardManager myClipboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        getDrafts();
    }

    public void getDrafts() {
        draftList.setRefreshEnabled(false);
        draftList.showProgress();
        draftDao= MyApplication.getInstance().getDaoSession().getDraftDao();
        String textColumn = DraftDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        cursor =  MyApplication.getInstance().getDb().query(draftDao.getTablename(), draftDao.getAllColumns(), null, null, null, null, orderBy);
        if (cursor == null) {
            draftList.showEmpty();
            return;
        }
        draftAdapter=new DraftAdapter(this);
        draftList.setLayoutManager(new LinearLayoutManager(this));
        draftList.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL_LIST));
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Draft draft = new Draft();
            draftDao.readEntity(cursor,draft,0);
            draftAdapter.addAll(draft);
        }
        draftAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
           myClipboard.setPrimaryClip( ClipData.newPlainText("text", draftAdapter.getData().get(position).getContent()));
                toast("已复制到剪贴板");
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<Draft>(draftDao,draftAdapter));
        itemTouchHelper.attachToRecyclerView(draftList.getRecyclerView());
        if (cursor != null) {
            draftList.setAdapter(draftAdapter);
            draftList.showRecycler();
            cursor.close();
        }

    }

}
