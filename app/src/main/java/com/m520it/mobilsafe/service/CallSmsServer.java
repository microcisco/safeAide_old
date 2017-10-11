package com.m520it.mobilsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.m520it.mobilsafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

/**
 * @author 王维波
 * @time 2016/9/10  17:27
 * @desc 用来监听短信到来和电话拨打
 */
public class CallSmsServer extends Service {

    private InnerSmsReceiver receiver;
    private BlackNumberDao dao;
    private TelephonyManager tm;
    private MyListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dao = new BlackNumberDao(this);
        tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        //监听电话打进来的状态
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);


        /**
         * 在代码中初始化广播的步骤
         */
        // 1初始化一个广播
        receiver = new InnerSmsReceiver();
        //2 初始化关闭的事件过滤器
        IntentFilter filter = new IntentFilter();
        // 3 添加关心的事件
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        //设置广播优先级
        filter.setPriority(Integer.MAX_VALUE);
        // 4 注该广播
        registerReceiver(receiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //反注册广播,也就是服务存在的时候,广播存在,服务不存在的时候,广播不存在
        unregisterReceiver(receiver);
        receiver=null;
        //服务不存在的时候应该取消电话打进来的 监听
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        tm=null;
        listener=null;
        super.onDestroy();
    }

    //在服务内部声明了一个广播
    class InnerSmsReceiver extends BroadcastReceiver {
        //一旦有短息到来,这个方法就会被调用
        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * 获得短息内存步骤如下
             */
            // 1 先通过一个pdus的规范会的短息的对象数组
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            // 2 遍历pdus集合
            for (Object obj : objs) {
                // 把pdu转换为一个短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                // 获得发现短信的号码
                String phone = sms.getOriginatingAddress();
                //获得短信内容
                String body = sms.getMessageBody();
                // 根据短信号码查询拦截模式
                String mode = dao.find(phone);
                if ("0".equals(mode) || "2".equals(mode)) {
                    //说明是一个短信拦截把
                    //拦截短信
                    abortBroadcast();
                }
                if (body.contains("fapiao")) {
                    System.out.println("发票被拦截了");
                    abortBroadcast();
                }
            }

        }
    }

    class MyListener extends PhoneStateListener {
        //电话的三种状态
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲状态

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接通转态
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃转态
                    //响铃的时候获得电话号码,获得电话号码后,查询是否是需要挂断的号码
                    String mode = dao.find(incomingNumber);
                    if("0".equals(mode)||"1".equals(mode)) {
                      //  tm.endCall();//在1.5版本的时候这个方法是可以被调用
                        endCall();
                      /*  new Thread(){
                            public void run(){
                                SystemClock.sleep(200);

                            }
                        }.start();*/
                        Uri uri=Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
                            //当我观察的数据库发生改变了我就大喊一声
                            @Override
                            public void onChange(boolean selfChange) {
                                deleteCallLog(incomingNumber);
                                super.onChange(selfChange);
                            }
                        });


                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
    //删除通话记录
    private void deleteCallLog(String incomingNumber) {
         //操作别人的数据库之前,先获得内容解析器
        ContentResolver resolver = getContentResolver();
        //获得操作路径
        Uri uri=Uri.parse("content://call_log/calls");

        resolver.delete(uri,"number=?",new String[]{incomingNumber});

    }

    //挂断电话方法
    private void endCall() {
        //IBinder b = ServiceManager.getService(TELEPHONY_SERVICE);
        try {
            // 1获得字节码
            Class<?> clazz = CallSmsServer.class.getClassLoader().loadClass("android.os.ServiceManager");
            // 2获得方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            // 3 调用方法
            IBinder b = (IBinder) method.invoke(null,TELEPHONY_SERVICE);
           // IAlarmManager service = ITe.Stub.asInterface(b);
            ITelephony iTelephony = ITelephony.Stub.asInterface(b);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
