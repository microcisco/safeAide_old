package com.m520it.mobilsafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author 王维波
 * @time 2016/9/11  15:53
 * @desc ${TODD}
 */
public class ShowAddressDao {

    public  static  String getAddress(String phone){
        String address=null;
        if(phone.matches("^1[34578]\\d{9}$")) {
            //说明是一个电话号码
            // 打开本地的数据库
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.m520it.mobilsafe/files/address.db", null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{phone.substring(0, 7)});
            while (cursor.moveToNext()){
                address = cursor.getString(0);
            }

        }else {
         switch (phone.length()){
             case 3:
                 if(phone.equals("110")) {
                     return "匪警";
                 }else if("119".equals(phone)) {
                     return "火警";
                 }else  if("120".equals(phone)) {
                     return "急救";
                 }
                 break;
             case 4:
                 return "模拟器";
             case 5:
                 return "客服";

             case 7:
                 if(!phone.startsWith("0")|| !phone.startsWith("1")) {
                     return "本地号码";
                 }

                 break;
             case 8:
                 if(!phone.startsWith("0")|| !phone.startsWith("1")) {
                     return "本地号码";
                 }

                 break;
             default:
                 if(phone.length()>=10&&phone.startsWith("0")) {
                     //说明是一个外地座机
                     SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.m520it.mobilsafe/files/address.db", null, SQLiteDatabase.OPEN_READONLY);
                     Cursor cursor = db.rawQuery("select  location from data2 where  area=?", new String[]{phone.substring(1, 3)});
                     while (cursor.moveToNext()){
                         return   address = cursor.getString(0).substring(0,cursor.getString(0).length()-2);
                     }

                      cursor = db.rawQuery("select  location from data2 where  area=?", new String[]{phone.substring(1, 4)});
                     while (cursor.moveToNext()){
                         return   address = cursor.getString(0).substring(0,cursor.getString(0).length()-2);
                     }
                 }
                 break;
         }
        }
        return address;
    }
}
