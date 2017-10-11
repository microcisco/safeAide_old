package com.m520it.mobilsafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.db.LockAppDb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/19  11:28
 * @desc ${TODD}
 */
public class AppLockDao {
    private LockAppDb dbHelper;
    private Context context;


    public AppLockDao(Context context) {
        dbHelper = new LockAppDb(context);
        this.context=context;
    }


    /**
     * 查询全部
     * @return
     */
    public List<String> findAll(){
        List<String> lists=new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Constant.TABLENAME_LOCK, new String[]{"packname"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            String packName = cursor.getString(0);
            lists.add(packName);

        }
        cursor.close();
        db.close();
        return lists;
    }

    //添加软件枷锁
    public boolean add(String packname) {
        //获得一个可写的数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packname);
        long id = db.insert(Constant.TABLENAME_LOCK, null, values);
        if (id != -1) {
            //只要发生数据变化就大吼一声
            Uri uri=Uri.parse("content://com.m520it.mobilsafe.applock");
            context.getContentResolver().notifyChange(uri,null);
            return true;
        } else {
            return false;
        }
    }

    //删除
    public boolean deleter(String packname) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = db.delete(Constant.TABLENAME_LOCK, "packname=?", new String[]{packname});
        if (id != 0) {
            //只要发生数据变化就大吼一声
            Uri uri=Uri.parse("content://com.m520it.mobilsafe.applock");
            context.getContentResolver().notifyChange(uri,null);
            return true;
        } else {
            return false;
        }
    }

    //查找
    public boolean find(String packname) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Constant.TABLENAME_LOCK, new String[]{"packname"}, "packname=?", new String[]{packname}, null, null, null);
        return cursor.moveToNext();
    }
}
