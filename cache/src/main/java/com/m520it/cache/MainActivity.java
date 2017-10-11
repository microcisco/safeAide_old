package com.m520it.cache;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFilesDir();//获得手机里面的缓存信息,但是这个只能手动的清理
        getCacheDir();//获得缓存的信息,但是这个是可以被系统回收,是通过响应的算法

       /* File file=new File(getCacheDir(),"aa.txt");
        try {
            FileOutputStream fos=new FileOutputStream(file);
            fos.write("ffefawfwsfasfsafsaf".getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        PackageManager pm = getPackageManager();
        //pm.getPackageSizeInfo

        try {
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(pm,getPackageName(),new InnerIP());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    class InnerIP extends IPackageStatsObserver.Stub{

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize;
            long dataSize = pStats.dataSize;
            long codeSize = pStats.codeSize;
            System.out.println("cacheSize="+ Formatter.formatFileSize(getApplicationContext(),cacheSize));
            System.out.println("dataSize="+ Formatter.formatFileSize(getApplicationContext(),dataSize));
            System.out.println("codeSize="+ Formatter.formatFileSize(getApplicationContext(),codeSize));
        }
    }
}
