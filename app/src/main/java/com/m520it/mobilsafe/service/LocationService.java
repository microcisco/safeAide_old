package com.m520it.mobilsafe.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.utils.SPUtils;

/**
 * @author 王维波
 * @time 2016/9/10  11:10
 * @desc ${TODD}
 */
public class LocationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获得定位的管理者
     LocationManager lm= (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria=new Criteria();
        //设置精度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置功率
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        //得到经纬度
        String bestProvider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(bestProvider, 0, 0, new LocationListener() {
            //位置改变的时候回调

            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String text="w="+latitude+"  "+"j="+longitude;
                String phone = SPUtils.getString(getApplicationContext(), Constant.SAFENUMBER);
                SmsManager.getDefault().sendTextMessage(phone,null,text,null,null);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }
}
