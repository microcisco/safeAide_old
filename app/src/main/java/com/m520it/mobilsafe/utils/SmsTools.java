package com.m520it.mobilsafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author 王维波
 * @time 2016/9/14  9:05
 * @desc 短信备份
 */
public class SmsTools {
      //接口里面的方法都是抽象
    public  interface  BackupSms{
          //备份之前需要获得总数
          void beforeBackup(int max);

          //备份中的参数,需要知道备份到哪个位置
          void backuping(int process);

    }


    public static boolean smsBckup(Context context, String filename,BackupSms backup){
        try {
        //查询别人数据库步骤

        //1 获得一个解析器
        ContentResolver resolver = context.getContentResolver();

        //2 获得对应查询的uri
        Uri uri=Uri.parse("content://sms/");

            //保存文件到本地需要现有一个文件夹,获得文件夹又有两个条件 一个路径,一个是文件名
        File file=new File(Environment.getExternalStorageDirectory(),filename);
            //文件写入流
        FileOutputStream fos=new FileOutputStream(file);
            //得到一个xml的序列化器
            XmlSerializer serializer = Xml.newSerializer();
            //设置输出流
            serializer.setOutput(fos,"utf-8");
            serializer.startDocument("utf-8",true);
            serializer.startTag(null,"info");

            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            backup.beforeBackup(cursor.getCount());
            int presss=0;

        while (cursor.moveToNext()){

            serializer.startTag(null,"sms");
            serializer.startTag(null,"address");
            String address = cursor.getString(0);
            serializer.text(address);
            serializer.endTag(null,"address");

            serializer.startTag(null,"date");
            String date = cursor.getString(1);
            serializer.text(date);
            serializer.endTag(null,"date");

            serializer.startTag(null,"type");
            String type = cursor.getString(2);
            serializer.text(type);
            serializer.endTag(null,"type");

            serializer.startTag(null,"body");
            String body = cursor.getString(3);
            serializer.text(body);
            serializer.endTag(null,"body");
            serializer.endTag(null,"sms");
            SystemClock.sleep(2000);
            presss++;
            //当前的备份进度
            backup.backuping(presss);
        }
            serializer.endTag(null,"info");
          serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
