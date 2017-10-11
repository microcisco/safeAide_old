package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.service.AutoKillProcessServer;
import com.m520it.mobilsafe.utils.SPUtils;
import com.m520it.mobilsafe.utils.ServicStateUtils;
import com.m520it.mobilsafe.views.SettingItemView;

/**
 * @author 王维波
 * @time 2016/9/18  15:50
 * @desc ${TODD}
 */
public class TaskMangerSettingActivity  extends Activity{
    private SettingItemView stv_show_sys;
    private SettingItemView stv_autl_kill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        boolean result = SPUtils.getBoolean(getApplicationContext(), Constant.SHOWSYS);
        stv_show_sys.setToggle(result);

    }


    private void initView() {
        setContentView(R.layout.activity_task_manager_setting);
        stv_show_sys = (SettingItemView)findViewById(R.id.stv_show_sys);
        stv_autl_kill = (SettingItemView)findViewById(R.id.stv_autl_kill);
    }

    private void initEvent() {
        stv_show_sys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = SPUtils.getBoolean(getApplicationContext(), Constant.SHOWSYS);
                SPUtils.putBoolean(getApplicationContext(),Constant.SHOWSYS,!result);
                stv_show_sys.setToggle(!result);
            }
        });

        stv_autl_kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = ServicStateUtils.isServiceRunning(getApplicationContext(), "com.m520it.mobilsafe.service.AutoKillProcessServer");
                if(result) {
                    //关闭服务
                    Intent Server=new Intent(TaskMangerSettingActivity.this, AutoKillProcessServer.class);
                    stopService(Server);
                }else {
                    //开启服务
                    Intent Server=new Intent(TaskMangerSettingActivity.this, AutoKillProcessServer.class);
                    startService(Server);
                }
                stv_autl_kill.setToggle(!result);
            }
        });
    }

    @Override
    protected void onStart() {
        boolean result = ServicStateUtils.isServiceRunning(getApplicationContext(), "com.m520it.mobilsafe.service.AutoKillProcessServer");
        stv_autl_kill.setToggle(result);
        super.onStart();
    }
}
