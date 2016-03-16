package com.cyan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cyan.bean.CommentToMe;
import com.cyan.bean.ReplyToMe;
import com.cyan.db.DBHelper.ComToMeTable;
import com.cyan.db.DBHelper.ReplyToMeTable;
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
        cv.put(ComToMeTable.YOUR_ID,commentToMe.getYour_id());
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
    public ArrayList<CommentToMe> queryCommentToMe(String id){
        ArrayList<CommentToMe> commentToMes = null;
        // ContentResolver resolver = context.getContentResolver();
        String where = ComToMeTable.YOUR_ID + " = '" +id
                + "'order by "+ComToMeTable._ID+" desc";
        Cursor cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);

        if (cursor == null) {
            return null;
        }
        commentToMes = new ArrayList<CommentToMe>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            CommentToMe commentToMe = new CommentToMe();
            // content.setIs_praised(cursor.getInt(cursor.getColumnIndex(FavTable.IS_PRAISED)) == 1);
            // content.setIs_collected(cursor.getInt(cursor.getColumnIndex(FavTable.IS_COLLECTED)) == 1);
            commentToMe.setId(cursor.getInt(cursor.getColumnIndex(ComToMeTable._ID)));
            commentToMe.setYour_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.YOUR_ID)));
            commentToMe.setPost_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.POST_ID)));
            commentToMe.setUser_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.USER_ID)));
            commentToMe.setComment_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.COMMENT_ID)));
            commentToMe.setUser_name(cursor.getString(cursor.getColumnIndex(ComToMeTable.USER_NAME)));
            commentToMe.setHead(cursor.getString(cursor.getColumnIndex(ComToMeTable.USER_HEAD)));
            commentToMe.setPost_content(cursor.getString(cursor.getColumnIndex(ComToMeTable.POST_CONTENT)));
            commentToMe.setComment_content(cursor.getString(cursor.getColumnIndex(ComToMeTable.COMMENT_CONTENT)));
            commentToMe.setCreate_time(cursor.getString(cursor.getColumnIndex(ComToMeTable.CREATE_TIME)));
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
   /* public long insertReplyToMe(ReplyToMe replyToMe){
        long uri = 0;
        ContentValues cv = new ContentValues();
        cv.put(ReplyToMeTable.YOUR_ID,replyToMe.getYour_id());
        cv.put(ReplyToMeTable.USER_ID,replyToMe.getUser_id());
        cv.put(ReplyToMeTable.POST_ID,replyToMe.getPost_id());
        cv.put(ReplyToMeTable.USER_NAME,replyToMe.getUser_name());
        cv.put(ReplyToMeTable.COMMENT_CONTENT,replyToMe.getComment_content());
        cv.put(ReplyToMeTable.POST_CONTENT,replyToMe.getPost_content());
        cv.put(ReplyToMeTable.COMMENT_ID,replyToMe.getComment_id());
        cv.put(ReplyToMeTable.USER_HEAD,replyToMe.getHead());
        cv.put(ReplyToMeTable.CREATE_TIME,replyToMe.getCreate_time());
        cv.put(ReplyToMeTable.POST_AUTHOR_ID,replyToMe.getPost_author_id());
        cv.put(ReplyToMeTable.POST_AUTHOR_NAME,replyToMe.getPost_author_name());
        cv.put(ReplyToMeTable.REPLY_CONTENT,replyToMe.getReply_content());
        uri = dbHelper.insert(DBHelper.TABLE_NAME, null, cv);
        dbHelper.close();
        return uri;
    }*/
    public ArrayList<ReplyToMe> queryReplyToMe(String id){
        ArrayList<ReplyToMe> replyToMes = null;
        // ContentResolver resolver = context.getContentResolver();
        String where = ComToMeTable.YOUR_ID + " = '" +id
                + "'order by "+ComToMeTable._ID+" desc";
        Cursor cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);

        if (cursor == null) {
            return null;
        }
        replyToMes = new ArrayList<ReplyToMe>();
     /*   for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ReplyToMe  replyToMe = new ReplyToMe();
            // content.setIs_praised(cursor.getInt(cursor.getColumnIndex(FavTable.IS_PRAISED)) == 1);
            // content.setIs_collected(cursor.getInt(cursor.getColumnIndex(FavTable.IS_COLLECTED)) == 1);
            replyToMe.setId(cursor.getInt(cursor.getColumnIndex(ComToMeTable._ID)));
            replyToMe.setYour_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.YOUR_ID)));
            replyToMe.setPost_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.POST_ID)));
            replyToMe.setUser_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.USER_ID)));
            replyToMe.setComment_id(cursor.getString(cursor.getColumnIndex(ComToMeTable.COMMENT_ID)));
            replyToMe.setUser_name(cursor.getString(cursor.getColumnIndex(ComToMeTable.USER_NAME)));
            replyToMe.setHead(cursor.getString(cursor.getColumnIndex(ComToMeTable.USER_HEAD)));
            replyToMe.setPost_content(cursor.getString(cursor.getColumnIndex(ComToMeTable.POST_CONTENT)));
            replyToMe.setComment_content(cursor.getString(cursor.getColumnIndex(ComToMeTable.COMMENT_CONTENT)));
            replyToMe.setCreate_time(cursor.getString(cursor.getColumnIndex(ComToMeTable.CREATE_TIME)));
            replyToMes.add(replyToMe);
        }*/
        if (cursor != null) {
            cursor.close();
        }
        // if (contents.size() > 0) {
        // return contents;
        // }
        return replyToMes;
    }
}
