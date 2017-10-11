package com.m520it.mobilsafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.m520it.mobilsafe.conf.Constant;

/**
 * @author 王维波
 * @time 2016/9/5  11:29
 * @desc 专门用来管理sp
 */
public class SPUtils {
      // 得到一个sp
    public  static SharedPreferences getSp(Context context){
        SharedPreferences sp = context.getSharedPreferences(Constant.SPNAME, Context.MODE_PRIVATE);
        return  sp;
    }

     //得到sp里面保存的boolean
    public  static boolean getBoolean(Context context,String key){
        SharedPreferences sp = getSp(context);
        return sp.getBoolean(key,false);
    }

    //往sp保存一个Boolean
    public  static void  putBoolean(Context context,String key ,boolean  state){
        SharedPreferences sp = getSp(context);
        sp.edit().putBoolean(key,state).commit();
    }

    //得到sp里面保存的boolean
    public  static String getString(Context context,String key){
        SharedPreferences sp = getSp(context);
        return sp.getString(key,null);
    }

    //往sp保存一个Boolean
    public  static void  putString(Context context,String key ,String  text){
        SharedPreferences sp = getSp(context);
        sp.edit().putString(key,text).commit();
    }

    //得到sp里面保存的boolean
    public  static int getInt(Context context,String key){
        SharedPreferences sp = getSp(context);
        return sp.getInt(key,0);
    }

    //往sp保存一个Boolean
    public  static void  putInt(Context context,String key ,int  state){
        SharedPreferences sp = getSp(context);
        sp.edit().putInt(key,state).commit();
    }
}
