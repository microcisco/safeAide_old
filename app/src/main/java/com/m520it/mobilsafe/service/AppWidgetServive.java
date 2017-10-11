package com.m520it.mobilsafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.receiver.AppWidgetReceiver;
import com.m520it.mobilsafe.utils.SystemTaskUitls;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 王维波
 * @time 2016/9/19  9:30
 * @desc ${TODD}
 */
public class AppWidgetServive extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private AppWidgetManager awm;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        awm=AppWidgetManager.getInstance(getApplicationContext());
        timer=new Timer();
        timerTask=new TimerTask() {
            @Override
            public void run() {
                //执行一些操作
                ComponentName provider=new ComponentName(getApplication(), AppWidgetReceiver.class);
                RemoteViews views=new RemoteViews(getPackageName(), R.layout.process_widget);
                int count = SystemTaskUitls.getRunningProcess(getApplicationContext());
                views.setTextViewText(R.id.process_count,"正在运行的软件:"+count+"个");
                long availMem = SystemTaskUitls.getAvailMem(getApplicationContext());
                String avail = Formatter.formatFileSize(getApplicationContext(), availMem);
                views.setTextViewText(R.id.process_memory,"可用内存:"+avail);
                Intent intent=new Intent();
                intent.setAction("com.m520it.mobilsafe.killAll");
                PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);
                awm.updateAppWidget(provider,views);
            }
        };
        timer.schedule(timerTask,0,5000);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
