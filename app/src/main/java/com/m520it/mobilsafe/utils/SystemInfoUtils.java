package com.m520it.mobilsafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author 王维波
 * @time 2016/9/5  11:15
 * @desc 用来获取和系统信息相关的数据
 */
public class SystemInfoUtils {

    /**
     * 获得对应包名的版本名称
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public  static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
         return info.versionName;
    }


    /**
     * 获得对应包名的版本号
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public  static int  getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
        return info.versionCode;
    }
}
