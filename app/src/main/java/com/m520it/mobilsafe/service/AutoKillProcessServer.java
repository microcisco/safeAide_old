package com.m520it.mobilsafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 王维波
 * @time 2016/9/18  16:09
 * @desc ${TODD}
 */
public class AutoKillProcessServer extends Service {

    private AutoScreenReceiver receiver;
    private Timer timer;
    private TimerTask timerTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


        receiver = new AutoScreenReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver,filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {

        unregisterReceiver(receiver);
        receiver=null;
        super.onDestroy();
    }


    class AutoScreenReceiver extends BroadcastReceiver{
        //一锁屏这个方法就会被回调,需要清除所有的进程
        @Override
        public void onReceive(Context context, Intent intent) {
             ActivityManager am= (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo  info: runningAppProcesses) {
                //清除后台进程,但是不能干掉自己
                am.killBackgroundProcesses(info.processName);
            }
        }
    }
}
