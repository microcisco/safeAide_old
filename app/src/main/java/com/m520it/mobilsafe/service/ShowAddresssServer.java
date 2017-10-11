package com.m520it.mobilsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.db.dao.ShowAddressDao;
import com.m520it.mobilsafe.utils.SPUtils;

/**
 * @author 王维波
 * @time 2016/9/11  17:10
 * @desc ${TODD}
 */
public class ShowAddresssServer extends Service {

    private TelephonyManager tm;
    private MyListener listener;
    private InnerCallOutReceiver receiver;
    private WindowManager mWM;
    private View view;
    private int startX;
    private int startY;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //电话打进来的监听
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);


        receiver = new InnerCallOutReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        tm = null;
        listener = null;

        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }


    class MyListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    String address = ShowAddressDao.getAddress(incomingNumber);
                    showToast(address);

                    break;
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                    if (mWM != null) {
                        mWM.removeView(view);
                    }

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接通状态

                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }


    }

    class InnerCallOutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获得打出去的电话
            String phone = getResultData();
            String address = ShowAddressDao.getAddress(phone);
            showToast(address);
        }
    }

    //显示自定义的吐司
    private void showToast(String address) {
        int[] icons=new int[]{R.drawable.call_locate_white,R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;

        //指明当前的WindowManager显示的控件是什么
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

        // 必须去掉不能触摸的属性
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        view = View.inflate(getApplicationContext(), R.layout.view_toast, null);
      //  params.x+=200;//代表toast在原来现实的位置X 轴加200
      //  params.y+=200;//代表toast在原来现实的位置Y 轴加200
        params.gravity= Gravity.LEFT+Gravity.TOP;//设置吐司默认的位置改变到左上角
        params.x=SPUtils.getInt(getApplicationContext(),Constant.X);
        params.y=SPUtils.getInt(getApplicationContext(),Constant.Y);
       LinearLayout ll_background= (LinearLayout) view.findViewById(R.id.ll_show);
        int which = SPUtils.getInt(getApplicationContext(), Constant.WHICHBACKGROUND);
        ll_background.setBackgroundResource(icons[which]);
        TextView tv = (TextView) view.findViewById(R.id.tv_location);
        tv.setText(address);
        mWM.addView(view, mParams);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN://手指放下去
                        // 1 先获得手指放下去坐标
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://手指移动
                        // 2 获得移动后的坐标
                        int newX= (int) motionEvent.getRawX();
                        int newY= (int) motionEvent.getRawY();
                        // 3 获得两个坐标的差值
                        int dx=newX-startX;
                        int dy=newY-startY;

                        // 4 移动空间
                      //  view.layout();//只能在activity中使用


                         params.x+=dx;
                         params.y+=dy;
                        if(params.x<0) params.x=0;
                        if(params.y<0) params.y=0;
                        if(params.x>mWM.getDefaultDisplay().getWidth()-view.getWidth()) {
                            params.x=mWM.getDefaultDisplay().getWidth()-view.getWidth();
                        }
                        if(params.y>mWM.getDefaultDisplay().getHeight()-view.getHeight()) {
                            params.y=mWM.getDefaultDisplay().getHeight()-view.getHeight();
                        }

                        //通知mWM 移动
                        mWM.updateViewLayout(view,params);
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();

                        break;
                    case MotionEvent.ACTION_UP://手指抬起
                           SPUtils.putInt(getApplicationContext(),Constant.X,params.x);
                           SPUtils.putInt(getApplicationContext(),Constant.Y,params.y);
                        break;

                }
                return false;
            }
        });
    }
}
