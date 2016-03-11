package com.cyan.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.cyan.adapter.DraftAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.community.R;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getDrafts();
    }

    public void getDrafts() {
        draftList.showProgress();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "drafts-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        draftDao=daoSession.getDraftDao();
        String textColumn = DraftDao.Properties.Id.columnName;
        String orderBy = textColumn + " DESC";
        cursor = db.query(draftDao.getTablename(),draftDao.getAllColumns(),null, null, null, null, orderBy);
        if (cursor == null) {
            draftList.showEmpty();
            return;
        }
        draftAdapter=new DraftAdapter(this);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Draft draft = new Draft();
            draftDao.readEntity(cursor,draft,0);
            draftAdapter.addAll(draft);
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelper<Draft>(draftDao,draftAdapter));
        itemTouchHelper.attachToRecyclerView(draftList.getRecyclerView());
        if (cursor != null) {
            draftList.setAdapter(draftAdapter);
            draftList.showRecycler();
            cursor.close();
        }

    }

}
