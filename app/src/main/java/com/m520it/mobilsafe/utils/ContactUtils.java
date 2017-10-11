package com.m520it.mobilsafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/8  10:45
 * @desc ${TODD}
 */
public class ContactUtils {

    public static List<ContactInfo>  readContact(Context context){

        List<ContactInfo> lsts=new ArrayList<>();
          //读系统数据库的步骤
        //1 先获得解析器
        ContentResolver resolver = context.getContentResolver();

        //2 获得查询的uri,先获得cotact表的id,然后通过id查询对应的数据
        Uri dataUri=Uri.parse("content://com.android.contacts/data");
        Uri uri=Uri.parse("content://com.android.contacts/raw_contacts");
        //通过路径获得数据库的数据
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            if(!TextUtils.isEmpty(id)) {
                ContactInfo info=new ContactInfo();
                Cursor dataCursor = resolver.query(dataUri, new String[]{"mimetype", "data1"}, "raw_contact_id=?", new String[]{id}, null);
                while (dataCursor.moveToNext()){
                    String mimetype = dataCursor.getString(0);
                    if("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                        String data1 = dataCursor.getString(1);
                        info.phone=data1;
                    }else  if("vnd.android.cursor.item/name".equals(mimetype)) {
                        String data1 = dataCursor.getString(1);
                        info.name=data1;
                    }


                }
                lsts.add(info);
            }
        }
        return  lsts;

    }

  public   static class ContactInfo{
      public   String name;
      public  String phone;
    }
}
