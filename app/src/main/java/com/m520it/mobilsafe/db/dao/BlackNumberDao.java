package com.m520it.mobilsafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.m520it.mobilsafe.bean.BlackNumberInfo;
import com.m520it.mobilsafe.db.BlackNumberDb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/10  11:52
 * @desc 黑名单数据库的CRUD
 */
public class BlackNumberDao {

    private BlackNumberDb dbHelper;
    private final static  String TABNAME="blacknumberinfo";


    /**
     * 写在构造器中,只要该对象一new出来,构造函数中的对象就会自动创建
     * @param context
     */
    public BlackNumberDao(Context context){
        dbHelper =new BlackNumberDb(context);
    }

    /**
     * 添加黑名单操作
     * @param phone 要添加的黑名单号码
     * @param mode  黑名单的拦截模式
     */
    public  boolean add(String phone,String mode){
        // 拿到一个可写的数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        long id = database.insert(TABNAME, null, values);
        database.close();
        if(id!=-1) {
            return  true;
        }else {
            return  false;
        }
    }

    /**
     * 删除黑名单号码
     * @param phone
     * @return
     */
    public boolean delete(String phone){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int delete = database.delete(TABNAME, "phone=?", new String[]{phone});
        database.close();
        if(delete==0) {
            return false;
        }else {
            return true;
        }
    }

    /**
     * 修改黑名单列表
     * @param phone
     * @param mode
     * @return
     */
    public  boolean update(String phone,String mode){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("mode",mode);
        int update = database.update(TABNAME, value, "phone=?", new String[]{phone});
        database.close();
        if(update==0) {
            return  false;
        }else {
            return true;
        }
    }

    /**
     * 查找的操作
     * @param phone
     * @return
     */
    public String find(String phone){
        String mode=null;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(TABNAME, new String[]{"mode"}, "phone=?", new String[]{phone}, null, null, null);
        while (cursor.moveToNext()){
             mode = cursor.getString(0);
        }
        cursor.close();
        database.close();
        return mode;
    }

     //查找全部
    public List<BlackNumberInfo> findAll(){
        List<BlackNumberInfo> infos=new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(TABNAME, new String[]{"phone", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            String phone = cursor.getString(0);
            info.setPhone(phone);
            SystemClock.sleep(20);
            String mode = cursor.getString(1);
            info.setMode(mode);
            infos.add(info);
        }
        cursor.close();
        database.close();
        return  infos;
    }
    //规定每次从查询20条数据重0开始查
    public List<BlackNumberInfo> findPart(int limit,int offset){
        List<BlackNumberInfo> infos=new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select _id, phone,mode from blacknumberinfo order by _id desc limit ? offset ?", new String[]{String.valueOf(limit), String.valueOf(offset)});
        while (cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            info.setPhone(phone);
            SystemClock.sleep(20);
            info.setMode(mode);
            infos.add(info);
        }
        return infos;
    }

    public int getCount(){
        int count=0;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select count(*) from blacknumberinfo", null);

        while (cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        return count;
    }

}
