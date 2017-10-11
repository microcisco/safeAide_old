package com.m520it.mobilsafe.activity;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.utils.IntentUtils;
import com.m520it.mobilsafe.utils.SPUtils;

/**
 * @author 王维波
 * @time 2016/9/7  15:02
 * @desc ${TODD}
 */
public class Setup2Activity extends BaseSetupActivity {

    private String simSerialNumber;
    private ImageView iv_bind;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup2);
        iv_bind = (ImageView)findViewById(R.id.iv_bind);
    }

    @Override
    public void initData() {
        String sim = SPUtils.getString(getApplicationContext(), Constant.SIMNUMBER);
        if(TextUtils.isEmpty(sim)) {
            iv_bind.setImageResource(R.drawable.unlock);
        }else {
            iv_bind.setImageResource(R.drawable.lock);
        }
        //手机管理者
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        simSerialNumber = tm.getSimSerialNumber();

    }

    public  void bindSim(View view){
        //去切换这个枷锁的图片,并且判断是否需要绑定sim
        String sim = SPUtils.getString(getApplicationContext(), Constant.SIMNUMBER);
        if(!TextUtils.isEmpty(sim)) {
            //取消绑定

            iv_bind.setImageResource(R.drawable.unlock);
            SPUtils.putString(getApplicationContext(), Constant.SIMNUMBER,null);
        }else {
            //绑定
            SPUtils.putString(getApplicationContext(), Constant.SIMNUMBER,simSerialNumber);
            iv_bind.setImageResource(R.drawable.lock);
        }

    }

    @Override
    public void next() {
        String sim = SPUtils.getString(getApplicationContext(), Constant.SIMNUMBER);

        if(TextUtils.isEmpty(sim)) {
            Toast.makeText(Setup2Activity.this, "必须绑定SIM卡", Toast.LENGTH_SHORT).show();
        }else {
            IntentUtils.startIntentAndFinish(Setup2Activity.this,Setup3Activity.class);

        }
    }

    @Override
    public void pre() {
     IntentUtils.startIntentAndFinish(Setup2Activity.this,Setup1Activity.class);
    }
}
