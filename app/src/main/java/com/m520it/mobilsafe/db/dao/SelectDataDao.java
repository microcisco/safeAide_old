package com.m520it.mobilsafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author 王维波
 * @time 2016/9/13  16:55
 * @desc ${TODD}
 */
public class SelectDataDao {


    //查询最外层的item的个数
    public static int getGroupCount(SQLiteDatabase db){
        int count=0;
        Cursor cursor = db.rawQuery("select count(*) from classlist", null);
        while (cursor.moveToNext()){
             count = cursor.getInt(0);
        }

        return count;
    }

     //查询每个item的个数要根据不同item的positon
    public  static  int getChildrenCount(int groupPosition,SQLiteDatabase db){
        int count=0;
        int newPosition=groupPosition+1;
        Cursor cursor = db.rawQuery("select count(*) from table" + newPosition, null);
        while (cursor.moveToNext()){
          count=  cursor.getInt(0);
        }

        return count;
    }

    // 根据传过来的不同的位置返回不同的名称
    public  static String  getGroupView(int groupPosition,SQLiteDatabase db){
        int newPosition=groupPosition+1;
        String name=null;
        Cursor cursor = db.query("classlist", new String[]{"name"}, "idx=?", new String[]{String.valueOf(newPosition)}, null, null, null);
        while (cursor.moveToNext()){
             name = cursor.getString(0);
        }

        return name;
    }


    public static String getChildView(int groupPosition, int childPosition,SQLiteDatabase db){
        String data=null;
        int newChildPositon=childPosition+1;
        int newPosition=groupPosition+1;
        String table="table"+newPosition;

        Cursor cursor = db.query(table, new String[]{"number", "name"}, "_id=?", new String[]{String.valueOf(newChildPositon)}, null, null, null);
       while (cursor.moveToNext()){
           String number = cursor.getString(0);
           String name = cursor.getString(1);
           data=number+"\n"+name;
       }

        return data;
    }
}
