package com.m520it.mobilsafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author 王维波
 * @time 2016/9/21  17:19
 * @desc ${TODD}
 */
public class Md5Dao {
    //获得MD5值
    public static  String  getMd5(String md5){
        String str=null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.m520it.mobilsafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"name"}, "md5=?", new String[]{md5}, null, null, null);
        while (cursor.moveToNext()){
           str= cursor.getString(0);
        }
        cursor.close();
        db.close();
        return str;
    }

    //获得当前的版本号
    public  static int getVersion(){
        int version=0;
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.m520it.mobilsafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("version", new String[]{"subcnt"}, null, null, null, null, null);
        while (cursor.moveToNext()){
          version=  cursor.getInt(0);
        }
        return version;
    }
    //更新数据库的版本号
    public  static void upDateVersion(int version){
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.m520it.mobilsafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values=new ContentValues();
        values.put("subcnt",version);
        db.update("version",values,null,null);
    }

    public static void addAntir(String md5,String type,String name, String desc){
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.m520it.mobilsafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values=new ContentValues();
        values.put("md5",md5);
        values.put("type",type);
        values.put("name",name);
        values.put("desc",desc);
        db.insert("datable",null,values);

    }
}
