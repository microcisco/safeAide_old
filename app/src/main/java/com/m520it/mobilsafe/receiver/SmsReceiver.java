package com.m520it.mobilsafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.service.LocationService;

/**
 * @author 王维波
 * @time 2016/9/8  15:05
 * @desc ${TODD}
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DevicePolicyManager apm= (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentNa=new ComponentName(context,DeviceReceiver.class);
        Object[] objs = (Object[]) intent.getExtras().get("pdus");//短信的国标
        //在java语言中,获得一个数据,或者一个集合下一步的操作是遍历
        for (Object  object: objs) {
            //将一个pdu转换为一个短信
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
            //获取到短信的文本
            String body = sms.getDisplayMessageBody();
            if("#*location*#".equals(body)) {
                //获取当前的经纬度,发送给安全号码
                Intent intentService=new Intent(context, LocationService.class);
                context.startService(intentService);
                //屏蔽短信,不让小偷看到
                abortBroadcast();
            }else if("#*alarm*#".equals(body)) {
                //需要发警报
                MediaPlayer player=MediaPlayer.create(context, R.raw.ylzs);
                //左声道,又声道,最大声音
                player.setVolume(1.0f,1.0f);
                //循环
                player.setLooping(true);
                //开启音乐
                player.start();
                abortBroadcast();
            }else if("#*wipedate*#".equals(body)) {
                if(apm.isAdminActive(componentNa)) {
                    apm.wipeData(DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);
                }
                abortBroadcast();
            }else if("#*lockscreen*#".equals(body)) {
                if(apm.isAdminActive(componentNa)) {
                    apm.lockNow();
                    apm.resetPassword("123",0);
                }

                abortBroadcast();
            }

        }
    }
}
