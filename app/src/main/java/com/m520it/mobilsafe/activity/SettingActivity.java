package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.service.CallSmsServer;
import com.m520it.mobilsafe.service.ShowAddresssServer;
import com.m520it.mobilsafe.service.WatchDogServer;
import com.m520it.mobilsafe.utils.SPUtils;
import com.m520it.mobilsafe.utils.ServicStateUtils;
import com.m520it.mobilsafe.views.SelectBackgroundView;
import com.m520it.mobilsafe.views.SettingItemView;

/**
 * @author 王维波
 * @time 2016/9/7  9:11
 * @desc ${TODD}
 */
public class SettingActivity extends Activity {
    private SettingItemView siv_update;
    private SettingItemView siv_black_interface;
    private SettingItemView siv_show_address;
    private SettingItemView siv_watch_dog;
    private SelectBackgroundView sbv_background;
    private AlertDialog dialog;
    private String name;
     String [] items=new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        setContentView(R.layout.activity_setting);
        siv_update = (SettingItemView)findViewById(R.id.siv_update);
        siv_black_interface = (SettingItemView)findViewById(R.id.siv_black_interface);
        siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
        sbv_background = (SelectBackgroundView)findViewById(R.id.sbv_background);
        siv_watch_dog = (SettingItemView)findViewById(R.id.siv_watch_dog);

    }

    private void initData() {

        boolean update = SPUtils.getBoolean(getApplicationContext(), Constant.UPDATE);
        siv_update.setToggle(update);


        int which = SPUtils.getInt(getApplicationContext(), Constant.WHICHBACKGROUND);
        sbv_background.setDesc(items[which]);

       /* boolean black = SPUtils.getBoolean(getApplicationContext(), Constant.BLACKINTERFACE);
        siv_black_interface.setToggle(black);*/

    }

    private void initEvent() {
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean update = SPUtils.getBoolean(getApplicationContext(), Constant.UPDATE);
                update=!update;
                siv_update.setToggle(update);
                SPUtils.putBoolean(getApplicationContext(),Constant.UPDATE,update);
            }
        });

        siv_black_interface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean blackintreface = ServicStateUtils.isServiceRunning(SettingActivity.this, "com.m520it.mobilsafe.service.CallSmsServer");
                if(blackintreface) {
                       //设置关闭
                       Intent intnetserver=new Intent(SettingActivity.this, CallSmsServer.class);
                       stopService(intnetserver);

                   }else {
                       //设置为开启
                       Intent intnetserver=new Intent(SettingActivity.this, CallSmsServer.class);
                       startService(intnetserver);
                   }
                siv_black_interface.setToggle(!blackintreface);

            }
        });


        siv_show_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean blackintreface = ServicStateUtils.isServiceRunning(SettingActivity.this, "com.m520it.mobilsafe.service.ShowAddresssServer");
                if(blackintreface) {
                    //设置关闭
                    Intent intnetserver=new Intent(SettingActivity.this, ShowAddresssServer.class);
                    stopService(intnetserver);

                }else {
                    //设置为开启
                    Intent intnetserver=new Intent(SettingActivity.this, ShowAddresssServer.class);
                    startService(intnetserver);
                }
                siv_show_address.setToggle(!blackintreface);

            }
        });

        /**
         * 弹出一个选择框
         */
        sbv_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("选择背景");
                name.length();

                builder.setSingleChoiceItems(items, SPUtils.getInt(getApplicationContext(),Constant.WHICHBACKGROUND), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                          SPUtils.putInt(getApplicationContext(),Constant.WHICHBACKGROUND,which);
                        sbv_background.setDesc(items[which]);
                        dialog.dismiss();
                    }
                });

                dialog = builder.show();

            }
        });

        //看门狗的实现
        siv_watch_dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean watchDog = ServicStateUtils.isServiceRunning(SettingActivity.this, "com.m520it.mobilsafe.service.WatchDogServer");
                if(watchDog) {
                    //关闭看门狗服务
                    Intent server=new Intent(SettingActivity.this, WatchDogServer.class);
                    stopService(server);
                }else {
                    //开启看门狗的服务
                    Intent server=new Intent(SettingActivity.this, WatchDogServer.class);
                    startService(server);
                }
                //设置开关的界面变化
                siv_watch_dog.setToggle(!watchDog);
            }
        });


    }
   //在屏幕可见的时候我就初始化这个开关的图片状态
    @Override
    protected void onStart() {
        boolean blackintreface = ServicStateUtils.isServiceRunning(this, "com.m520it.mobilsafe.service.CallSmsServer");
        siv_black_interface.setToggle(blackintreface);

        boolean showAddress = ServicStateUtils.isServiceRunning(this, "com.m520it.mobilsafe.service.ShowAddresssServer");
        siv_show_address.setToggle(showAddress);
        //看门狗的逻辑回显
        boolean watchDog = ServicStateUtils.isServiceRunning(SettingActivity.this, "com.m520it.mobilsafe.service.WatchDogServer");
        siv_watch_dog.setToggle(watchDog);
        super.onStart();
    }
}
