package com.m520it.mobilsafe.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.m520it.mobilsafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/14  11:00
 * @desc 获得所有的安装软件的信息
 */
public class SystemAppInfo {

    public static List<AppInfo>  getAllAppInfo(Context context){
        List<AppInfo> infos=new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        //获得当前所有安装在该设备上面的所有数据集合
        List<PackageInfo> infoList = pm.getInstalledPackages(0);
        for (PackageInfo  info: infoList) {
            AppInfo appinfo=new AppInfo();
            String packageName = info.packageName;
            appinfo.setPackname(packageName);
            Drawable icon = info.applicationInfo.loadIcon(pm);
            appinfo.setIcon(icon);
            String name = info.applicationInfo.loadLabel(pm).toString();
            appinfo.setName(name);
            String path = info.applicationInfo.sourceDir;
            File file=new File(path);
            long size = file.length();
            appinfo.setSize(size);
            int flags = info.applicationInfo.flags;
            if((flags& ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0) {
                //说明不是安装在SD卡上面
                appinfo.setRom(true);
            }else {
                appinfo.setRom(false);
            }

            if((flags & ApplicationInfo.FLAG_SYSTEM)==1) {
                 appinfo.setUser(false);
            }else {
                appinfo.setUser(true);
            }
            infos.add(appinfo);
        }

        return infos;
    }
}
