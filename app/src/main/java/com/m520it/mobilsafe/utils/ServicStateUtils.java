package com.m520it.mobilsafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/11  9:40
 * @desc 动态判断服务是否开启
 */
public class ServicStateUtils {

    public  static boolean isServiceRunning(Context context,String clazz){
        //获取到设备的相关服务
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的前100个服务
        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo   info: serviceInfos) {
            if(clazz.equals(info.service.getClassName()) ) {
                //说明服务开启啦
                return  true;

            }
        }
        return false;
    }
}
