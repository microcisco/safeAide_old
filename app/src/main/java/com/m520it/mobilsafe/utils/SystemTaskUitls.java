package com.m520it.mobilsafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.bean.ProcessInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/18  9:29
 * @desc ${TODD}
 */
public class SystemTaskUitls {

    /**
     * 获取当前正在运行的进程数
     * @param context
     * @return
     */
    public static int getRunningProcess(Context context){
     ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        return infos.size();
    }

    //获得当前的可用内存信息
    public static long getAvailMem(Context context){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outeinfo=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outeinfo);
        return outeinfo.availMem;
    }

    //获得当前的总内存信息
    public static long getTotalMem(Context context){
        /*ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outeinfo=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outeinfo);
        return outeinfo.totalMem;*/
        StringBuffer buffer=null;
        try {
        File file=new File("/proc/meminfo/");
        FileInputStream fis=new FileInputStream(file);
             buffer=new StringBuffer();
            BufferedReader Reader=new BufferedReader(new InputStreamReader(fis));
            //读了一行
            String line = Reader.readLine();//读取了第一行的总类内:MemTotal:         516568 kB
            for (char   c  : line.toCharArray()) {
                if(c>='0'&&c<='9') {
                    buffer.append(c);
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.parseLong(buffer.toString())*1024;
    }

    /**
     * 获取设备上面的所有的进程信息
     * @param context
     * @return
     */
    public static List<ProcessInfo> getAllPeocess(Context context){
        List<ProcessInfo> infos=new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo  info: runningAppProcesses) {
            ProcessInfo processInfo=new ProcessInfo();
            String processName = info.processName;//进程名称,就是app的包名
            processInfo.setPackName(processName);
            long  size = am.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty() * 1024;
            processInfo.setSize(size);
            try {
                PackageInfo packageInfo = pm.getPackageInfo(processName, 0);
                String name = packageInfo.applicationInfo.loadLabel(pm).toString();//软件名称
                processInfo.setName(name);
                Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
                processInfo.setIcon(icon);


                int flags = packageInfo.applicationInfo.flags;
                if((flags & ApplicationInfo.FLAG_SYSTEM)==0) {
                    //说明是一个用户进程
                    processInfo.setUserProcess(true);
                }else {
                    //说明是一个系统进程
                    processInfo.setUserProcess(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                processInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                processInfo.setName(processName);
            }
            infos.add(processInfo);
        }
        return infos;
    }
}
