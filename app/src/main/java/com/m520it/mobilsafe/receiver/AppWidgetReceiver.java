package com.m520it.mobilsafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.m520it.mobilsafe.service.AppWidgetServive;

/**
 * @author 王维波
 * @time 2016/9/19  9:05
 * @desc ${TODD}
 */
public class AppWidgetReceiver extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("onReceive被调用");
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //初始化
        Intent intent=new Intent(context, AppWidgetServive.class);
        context.startService(intent);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        System.out.println("onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        System.out.println("onEnabled");
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        //销毁
        //初始化
        Intent intent=new Intent(context, AppWidgetServive.class);
        context.stopService(intent);
        super.onDisabled(context);
    }
}
