package de.greenrobot.daoexample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "QUICK_SEARCH".
*/
public class QuickSearchDao extends AbstractDao<QuickSearch, Long> {

    public static final String TABLENAME = "QUICK_SEARCH";

    /**
     * Properties of entity QuickSearch.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Add_time = new Property(1, java.util.Date.class, "add_time", false, "ADD_TIME");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
    };


    public QuickSearchDao(DaoConfig config) {
        super(config);
    }
    
    public QuickSearchDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"QUICK_SEARCH\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," + // 0: id
                "\"ADD_TIME\" INTEGER," + // 1: add_time
                "\"CONTENT\" TEXT NOT NULL );"); // 2: content
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"QUICK_SEARCH\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, QuickSearch entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        java.util.Date add_time = entity.getAdd_time();
        if (add_time != null) {
            stmt.bindLong(2, add_time.getTime());
        }
        stmt.bindString(3, entity.getContent());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public QuickSearch readEntity(Cursor cursor, int offset) {
        QuickSearch entity = new QuickSearch( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // add_time
            cursor.getString(offset + 2) // content
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, QuickSearch entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setAdd_time(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setContent(cursor.getString(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(QuickSearch entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(QuickSearch entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
