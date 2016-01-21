package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.bean.CommentToMe;
import com.example.db.DBHelper.ComToMeTable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/14.
 */
public class DatabaseUtil {
    private static final String TAG = "DatabaseUtil";

    private static DatabaseUtil instance;

    /**
     * 数据库帮助类
     **/
    private DBHelper dbHelper;

    public synchronized static DatabaseUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseUtil(context);
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private DatabaseUtil(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * 销毁
     */
    public static void destory() {
        if (instance != null) {
            instance.onDestory();
        }
    }

    /**
     * 销毁
     */
    public void onDestory() {
        instance = null;
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }

    public long insertCommentToMe(CommentToMe commentToMe) {
        long uri = 0;
        ContentValues cv = new ContentValues();
        cv.put(ComToMeTable.USER_ID, commentToMe.getUser_id());
        cv.put(ComToMeTable.POST_ID, commentToMe.getPost_id());
        cv.put(ComToMeTable.USER_NAME, commentToMe.getUser_name());
        cv.put(ComToMeTable.COMMENT_CONTENT, commentToMe.getComment_content());
        cv.put(ComToMeTable.POST_CONTENT, commentToMe.getPost_content());
        cv.put(ComToMeTable.COMMENT_ID,commentToMe.getComment_id());
        cv.put(ComToMeTable.USER_HEAD,commentToMe.getHead());
        cv.put(ComToMeTable.CREATE_TIME,commentToMe.getCreate_time());
        uri = dbHelper.insert(DBHelper.TABLE_NAME, null, cv);
        dbHelper.close();
        return uri;

    }
public void deleteCommentToMe(CommentToMe commentToMe){
    Cursor cursor = null;
    String where = ComToMeTable._ID + " = '" +commentToMe.getId()
            + "'";
    cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
    if (cursor != null && cursor.getCount() > 0)
    dbHelper.delete(DBHelper.TABLE_NAME, where, null);
}
    public ArrayList<CommentToMe> queryCommentToMe(){
        ArrayList<CommentToMe> commentToMes = null;
        // ContentResolver resolver = context.getContentResolver();
        Cursor cursor = dbHelper.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor == null) {
            return null;
        }
        commentToMes = new ArrayList<CommentToMe>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            CommentToMe commentToMe = new CommentToMe();
            // content.setIs_praised(cursor.getInt(cursor.getColumnIndex(FavTable.IS_PRAISED)) == 1);
            // content.setIs_collected(cursor.getInt(cursor.getColumnIndex(FavTable.IS_COLLECTED)) == 1);
            commentToMe.setId(cursor.getInt(cursor.getColumnIndex(ComToMeTable._ID)));
            commentToMe.setPost_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.POST_ID)));
            commentToMe.setUser_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.USER_ID)));
            commentToMe.setComment_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.COMMENT_ID)));
            commentToMe.setUser_name(cursor.getString(cursor.getColumnIndex(ComToMeTable.USER_NAME)));
            commentToMe.setHead(cursor.getString(cursor.getColumnIndex(ComToMeTable.USER_HEAD)));
            commentToMe.setPost_content(cursor.getString(cursor.getColumnIndex(ComToMeTable.POST_CONTENT)));
            commentToMe.setComment_content(cursor.getString(cursor.getColumnIndex(ComToMeTable.COMMENT_CONTENT)));
            commentToMes.add(commentToMe);
        }
        if (cursor != null) {
            cursor.close();
        }
        // if (contents.size() > 0) {
        // return contents;
        // }
        return commentToMes;
    }

}
