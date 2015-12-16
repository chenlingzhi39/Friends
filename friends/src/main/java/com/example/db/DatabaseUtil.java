package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.bean.Post;
import com.example.ui.MyApplication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.example.db.DBHelper.FavTable;
/**
 * Created by Administrator on 2015/12/14.
 */
public class DatabaseUtil {
    private static final String TAG="DatabaseUtil";

    private static DatabaseUtil instance;

    /** 数据库帮助类 **/
    private DBHelper dbHelper;

    public synchronized static DatabaseUtil getInstance(Context context) {
        if(instance == null) {
            instance=new DatabaseUtil(context);
        }
        return instance;
    }

    /**
     * 初始化
     * @param context
     */
    private DatabaseUtil(Context context) {
        dbHelper=new DBHelper(context);
    }

    /**
     * 销毁
     */
    public static void destory() {
        if(instance != null) {
            instance.onDestory();
        }
    }

    /**
     * 销毁
     */
    public void onDestory() {
        instance=null;
        if(dbHelper != null) {
            dbHelper.close();
            dbHelper=null;
        }
    }
    public void deletePraise(Post post){
        Cursor cursor=null;
        String where = FavTable.USER_ID+" = '"+ MyApplication.getInstance().getCurrentUser().getObjectId()
                +"' AND "+FavTable.OBJECT_ID+" = '"+post.getObjectId()+"'";
        cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int isCollected = cursor.getInt(cursor.getColumnIndex(FavTable.IS_COLLECTED));
            if(isCollected==0){
                dbHelper.delete(DBHelper.TABLE_NAME, where, null);
            }else{
                ContentValues cv = new ContentValues();
                cv.put(FavTable.IS_PRAISED, 0);
                dbHelper.update(DBHelper.TABLE_NAME, cv, where, null);
            }
        }
        if(cursor != null) {
            cursor.close();
            dbHelper.close();
        }
    }


    public boolean isCollected(Post post){
        Cursor cursor = null;
        String where = FavTable.USER_ID+" = '"+MyApplication.getInstance().getCurrentUser().getObjectId()
                +"' AND "+FavTable.OBJECT_ID+" = '"+post.getObjectId()+"'";
        cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_COLLECTED))==1){
                return true;
            }
        }
        return false;
    }
    public boolean isPraised(Post post){
        Cursor cursor = null;
        String where = FavTable.USER_ID+" = '"+MyApplication.getInstance().getCurrentUser().getObjectId()
                +"' AND "+FavTable.OBJECT_ID+" = '"+post.getObjectId()+"'";
        cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_PRAISED))==1){
                return true;
            }
        }
        return false;
    }
    public long insertPraise(Post post){
        long uri = 0;
        Cursor cursor=null;
        String where = FavTable.USER_ID+" = '"+MyApplication.getInstance().getCurrentUser().getObjectId()
                +"' AND "+FavTable.OBJECT_ID+" = '"+post.getObjectId()+"'";
        cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues conv = new ContentValues();
            conv.put(FavTable.IS_PRAISED, 1);

            dbHelper.update(DBHelper.TABLE_NAME, conv, where, null);
        }else{
            ContentValues cv = new ContentValues();
            cv.put(FavTable.USER_ID, MyApplication.getInstance().getCurrentUser().getObjectId());
            cv.put(FavTable.OBJECT_ID, post.getObjectId());
          /*  cv.put(FavTable.IS_PRAISED, post.getIs_praised()?1:0);
            cv.put(FavTable.IS_COLLECTED,post.getIs_collected()?1:0)*/;
            uri = dbHelper.insert(DBHelper.TABLE_NAME, null, cv);
        }
        if(cursor != null) {
            cursor.close();
            dbHelper.close();
        }
        return uri;
    }

//	    public int deleteFav(QiangYu qy){
//	    	int row = 0;
//	    	String where = FavTable.USER_ID+" = "+qy.getAuthor().getObjectId()
//	    			+" AND "+FavTable.OBJECT_ID+" = "+qy.getObjectId();
//	    	row = dbHelper.delete(DBHelper.TABLE_NAME, where, null);
//	    	return row;
//	    }


    /**
     * 设置内容的点赞状态
     * @param context
     * @param lists
     */
    public List<Post> setPraise(List<Post> lists) {
        Cursor cursor=null;
        if(lists != null && lists.size() > 0) {
            for(Iterator iterator=lists.iterator(); iterator.hasNext();) {
               Post content=(Post)iterator.next();
                String where = FavTable.USER_ID+" = '"+MyApplication.getInstance().getCurrentUser().getObjectId()//content.getAuthor().getObjectId()
                        +"' AND "+FavTable.OBJECT_ID+" = '"+content.getObjectId()+"'";
                cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
                if(cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_PRAISED))==1){

                       // content.setIs_praised(true);
                    }else{
                       // content.setIs_praised(false);
                    }
                    if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_COLLECTED))==1){
                        //content.setIs_collected(true);
                    }else{
                       // content.setIs_collected(false);
                    }
                }

            }
        }
        if(cursor != null) {
            cursor.close();
            dbHelper.close();
        }
        return lists;
    }

    /**
     * 设置内容的收藏状态
     * @param context
     * @param lists
     */
    public List<Post> setCollect(List<Post> lists) {
        Cursor cursor=null;
        if(lists != null && lists.size() > 0) {
            for(Iterator iterator=lists.iterator(); iterator.hasNext();) {
                Post content=(Post)iterator.next();
               // content.setIs_collected(true);
                String where = FavTable.USER_ID+" = '"+MyApplication.getInstance().getCurrentUser().getObjectId()
                        +"' AND "+FavTable.OBJECT_ID+" = '"+content.getObjectId()+"'";
                cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
                if(cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_COLLECTED))==1){
                       // content.setIs_collected(true);
                    }else{
                       // content.setIs_collected(false);
                    }
                }

            }
        }
        if(cursor != null) {
            cursor.close();
            dbHelper.close();
        }
        return lists;
    }


    public ArrayList<Post> queryFav() {
        ArrayList<Post> contents=null;
        // ContentResolver resolver = context.getContentResolver();
        Cursor cursor=dbHelper.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);

        if(cursor == null) {
            return null;
        }
        contents=new ArrayList<Post>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
           Post content=new Post();
           // content.setIs_praised(cursor.getInt(cursor.getColumnIndex(FavTable.IS_PRAISED)) == 1);
           // content.setIs_collected(cursor.getInt(cursor.getColumnIndex(FavTable.IS_COLLECTED)) == 1);

            contents.add(content);
        }
        if(cursor != null) {
            cursor.close();
        }
        // if (contents.size() > 0) {
        // return contents;
        // }
        return contents;
    }

}
