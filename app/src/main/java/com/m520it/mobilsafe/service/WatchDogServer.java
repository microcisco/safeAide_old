package com.m520it.mobilsafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.m520it.mobilsafe.activity.EnterPasserworldActivity;
import com.m520it.mobilsafe.db.dao.AppLockDao;

import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/21  9:17
 * @desc ${TODD}
 */
public class WatchDogServer extends Service {
    private boolean flag;
    private AppLockDao dao;
    private InnerStopWatcherReceiver receiver;
    private String stopPackName;
    private ActivityManager am;
    private List<ActivityManager.RunningTaskInfo> tasks;
    private Intent intent;
    private List<String> all;
    private InnerContent content;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        receiver = new InnerStopWatcherReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.m520it.mobilsafe.stopwatchdog");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver,filter);
      //注册观察者
        content = new InnerContent(new Handler());
        Uri uri=Uri.parse("content://com.m520it.mobilsafe.applock");
        getContentResolver().registerContentObserver(uri,true, content);

        flag=true;
        dao=new AppLockDao(this);
        all = dao.findAll();
        //1 在子线程中开启一个永真循环
        fillData();
        super.onCreate();
    }

    private void fillData() {
        intent = new Intent(WatchDogServer.this, EnterPasserworldActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Thread(){
            public void run(){
                while (flag){
                    //2 开始不停的监视应用的开启,检查是否是已经枷锁的软件打开
                    long startTime = System.currentTimeMillis();
                    try {
                        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        tasks = am.getRunningTasks(1);
                        String packageName = tasks.get(0).topActivity.getPackageName();
                        //3 拿到包名查询数据库
                        if(all.contains(packageName)) {
                            if(packageName.equals(stopPackName)) {
                                //停止保护
                            }else {
                                //弹出一个输入密码框
                                intent.putExtra("packname",packageName);
                                startActivity(intent);
                            }
                        }
                        long endTimer = System.currentTimeMillis();
                        SystemClock.sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        flag=false;
        unregisterReceiver(receiver);
        getContentResolver().unregisterContentObserver(content);
        content=null;
        super.onDestroy();
    }

    class InnerStopWatcherReceiver extends BroadcastReceiver{
       //关系的事件是一个自定义的事件"com.m520it.mobilsafe.stopwatchdog"
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("com.m520it.mobilsafe.stopwatchdog".equals(action)) {
                //如果没有发送广播,这里获得数据是null
                stopPackName = intent.getStringExtra("packname");
            }else if(Intent.ACTION_SCREEN_OFF.equals(action)) {
                stopPackName=null;
                flag=false;
            }else if(Intent.ACTION_SCREEN_ON.equals(action)) {
                flag=true;
                fillData();
            }


        }
    }

    //内容观察者
  class InnerContent extends ContentObserver{
       //构造方法
        public InnerContent(Handler handler) {
            super(handler);
        }
      //听到有吼一声的时候并且是我所想听到的内容的时候,回调
        @Override
        public void onChange(boolean selfChange) {
            all=dao.findAll();
            super.onChange(selfChange);
        }
    }

}
