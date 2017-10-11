package com.m520it.mobilsafe.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * @author 王维波
 * @time 2016/9/7  17:29
 * @desc ${TODD}
 */
public class IntentUtils {

    public static  void startIntentAndFinish(Activity activity,Class clazz){
        Intent intent=new Intent(activity,clazz);
        activity.startActivity(intent);
        activity.finish();

    }
   //开启一个新的activity,并且获得返回值
    public static  void startIntentAndFinishAndReturnData(Activity activity,Class clazz){
        Intent intent=new Intent(activity,clazz);
        activity.startActivityForResult(intent,0);


    }

    public static  void startIntent(Activity activity,Class clazz){
        Intent intent=new Intent(activity,clazz);
        activity.startActivity(intent);

    }
}
