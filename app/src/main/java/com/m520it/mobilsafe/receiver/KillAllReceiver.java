package com.m520it.mobilsafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/19  10:14
 * @desc ${TODD}
 */
public class KillAllReceiver extends BroadcastReceiver {
    //小狗一叫,这个事件就被激活,激活zhihou,想干嘛
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo  info: processes) {
                am.killBackgroundProcesses(info.processName);
        }
        Toast.makeText(context, "全部被清理", Toast.LENGTH_SHORT).show();

    }
}
