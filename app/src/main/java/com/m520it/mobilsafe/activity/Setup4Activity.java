package com.m520it.mobilsafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.receiver.DeviceReceiver;
import com.m520it.mobilsafe.utils.IntentUtils;
import com.m520it.mobilsafe.utils.SPUtils;

/**
 * @author 王维波
 * @time 2016/9/7  15:02
 * @desc ${TODD}
 */
public class Setup4Activity extends BaseSetupActivity {
    private LinearLayout ll_check;
    private CheckBox cb_check;
    private TextView tv_state;


    @Override
    public void initData() {
        boolean protect = SPUtils.getBoolean(getApplicationContext(), Constant.PROTOCOTING);
        cb_check.setChecked(protect);
    }

    @Override
    public void initEvent() {
        ll_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_check.isChecked()) {
                    DevicePolicyManager apm= (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    ComponentName componentNa=new ComponentName(Setup4Activity.this,DeviceReceiver.class);
                    apm.removeActiveAdmin(componentNa);

                    cb_check.setChecked(false);
                    tv_state.setText("防盗保护 已经关闭");
                    SPUtils.putBoolean(getApplicationContext(), Constant.PROTOCOTING,false);
                }else {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    ComponentName componentNa=new ComponentName(Setup4Activity.this,DeviceReceiver.class);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentNa);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            "点我就激活,不然没法用");
                    startActivity(intent);
                    cb_check.setChecked(true);
                    tv_state.setText("防盗保护 已经打开");
                    SPUtils.putBoolean(getApplicationContext(), Constant.PROTOCOTING,true);
                }
            }
        });
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);
        ll_check = (LinearLayout)findViewById(R.id.ll_check);
        cb_check = (CheckBox)findViewById(R.id.cb_check);
        tv_state = (TextView)findViewById(R.id.tv_state);
    }

    @Override
    public void next() {
        SPUtils.putBoolean(getApplicationContext(),Constant.FINISH,true);
        IntentUtils.startIntentAndFinish(Setup4Activity.this,Setup5Activity.class);
    }

    @Override
    public void pre() {
        IntentUtils.startIntentAndFinish(Setup4Activity.this,Setup3Activity.class);
    }

}
