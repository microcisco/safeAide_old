package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.utils.IntentUtils;
import com.m520it.mobilsafe.utils.SmsTools;

/**
 * @author 王维波
 * @time 2016/9/11  16:06
 * @desc ${TODD}
 */
public class AtoolActivity extends Activity {
     private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_atool);
        pb = (ProgressBar)findViewById(R.id.pb);

    }

    public  void queryAddress(View v){
        IntentUtils.startIntent(AtoolActivity.this,ShowAddressActivity.class);
    }

    public  void SelectCommonContact(View v){
        IntentUtils.startIntent(AtoolActivity.this,CommonActivity.class);
    }


    public  void lockApp(View view){
        IntentUtils.startIntent(AtoolActivity.this,AppLockActivity.class);
    }

    public void smsBackup(View view){

        new Thread(){
            public void run(){
                boolean resutl = SmsTools.smsBckup(AtoolActivity.this, "smsbackup.xml", new SmsTools.BackupSms() {
                    @Override
                    public void beforeBackup(int max) {
                        pb.setMax(max);
                    }

                    @Override
                    public void backuping(int process) {
                       pb.setProgress(process);
                    }
                });
            }
        }.start();
    }
}
