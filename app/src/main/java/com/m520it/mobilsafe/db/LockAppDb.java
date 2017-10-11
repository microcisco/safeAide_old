package com.m520it.mobilsafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.m520it.mobilsafe.conf.Constant;

/**
 * @author 王维波
 * @time 2016/9/19  11:23
 * @desc ${TODD}
 */
public class LockAppDb extends SQLiteOpenHelper {

    public LockAppDb(Context context) {
        super(context, "AppLock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ Constant.TABLENAME_LOCK+"(_id Integer primary key autoincrement,packname varchar(40))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
