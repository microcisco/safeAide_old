package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.db.dao.Md5Dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/21  16:38
 * @desc ${TODD}
 */
public class KillAntivirusActivity extends Activity {
    private ImageView iv_scanf;
    private ProgressBar pb_antir;
    private LinearLayout ll_antir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAnimation();
        initAntir();
    }


    private void initView() {
        setContentView(R.layout.activity_antivirus);
        iv_scanf = (ImageView) findViewById(R.id.iv_scanf);
        pb_antir = (ProgressBar) findViewById(R.id.pb_antir);
        ll_antir = (LinearLayout) findViewById(R.id.ll_antir);
    }

    private void initAnimation() {
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //无限循环
        ra.setRepeatCount(Animation.INFINITE);
        ra.setDuration(2000);
        iv_scanf.startAnimation(ra);

    }
    private void initAntir() {
        new Thread() {
            public void run() {
                PackageManager pm = getPackageManager();
                List<PackageInfo> infos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES + PackageManager.GET_SIGNATURES);
                pb_antir.setMax(infos.size());
                try {
                    MessageDigest digest = MessageDigest.getInstance("md5");
                    int process=0;
                    for (PackageInfo info : infos) {
                        process++;
                        pb_antir.setProgress(process);
                        StringBuilder builder=new StringBuilder();
                        final String name = info.applicationInfo.loadLabel(pm).toString();
                        String path = info.applicationInfo.sourceDir;
                        File file = new File(path);

                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = fis.read(buffer)) != -1) {
                          digest.update(buffer,0,len);
                        }
                        byte[] bytes = digest.digest();
                        for (byte  b: bytes) {
                            String str = Integer.toHexString(b & 0xff);
                            if(str.length()==1) {
                                builder.append("0");
                            }
                            builder.append(str);
                        }
                        String md5String = builder.toString();
                        final String Strname = Md5Dao.getMd5(md5String);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tv;
                                if(TextUtils.isEmpty(Strname)) {
                                    //说明不是病毒
                                    tv=new TextView(getApplicationContext());
                                    tv.setText(name+"不是病毒");
                                    tv.setTextColor(Color.BLUE);
                                }else {
                                    //说明是一个病毒
                                    tv=new TextView(getApplicationContext());
                                    tv.setText(name+"是病毒");
                                    tv.setTextColor(Color.RED);
                                }

                                ll_antir.addView(tv,0);
                            }
                        });

                        SystemClock.sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
