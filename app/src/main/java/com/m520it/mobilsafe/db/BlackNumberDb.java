package com.m520it.mobilsafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 王维波
 * @time 2016/9/10  11:40
 * @desc ${TODD}
 */
public class BlackNumberDb extends SQLiteOpenHelper {

    /**
     * context 上下文
     * it5204.db 数据库名称
     * null 游标工程,null代表使用默认
     * 1 当前版本,从 1 开始
     */
    public BlackNumberDb(Context context) {
        super(context, "it5204.db", null, 1);
    }


    /**mode 拦截模式 0全部拦截 1 电话拦截 2 短信拦截
     * phone 拦截的黑名单号码
     * _id 主键(integer primary key autoincrement)
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //执行sql语句,创建数据表,和表的字段,主要三个字段 phone 拦截模式
     db.execSQL("create table blacknumberinfo(_id integer primary key autoincrement,phone varchar(20),mode varchar(2) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
