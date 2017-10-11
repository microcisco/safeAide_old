package com.m520it.mobilsafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.utils.SPUtils;

/**
 * @author 王维波
 * @time 2016/9/8  14:48
 * @desc 监听手机重启的广播,只要手机重启,这个广播就会被激活
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean baohu = SPUtils.getBoolean(context, Constant.PROTOCOTING);
        if(baohu) {
            // 1 检查SIM卡的序列号是否有变化
            String oldSim = SPUtils.getString(context, Constant.SIMNUMBER);

            TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String newSim = tm.getSimSerialNumber()+"wrww";

            //2 如果有变化,就给安全号码发送一条短信
            if(oldSim.equals(newSim)) {
                //说明没有改变
                System.out.println("没有该变");
            }else {
                String phone = SPUtils.getString(context, Constant.SAFENUMBER);
                SmsManager.getDefault().sendTextMessage(phone,null,"sim change",null,null);
            }
        }



    }
}
