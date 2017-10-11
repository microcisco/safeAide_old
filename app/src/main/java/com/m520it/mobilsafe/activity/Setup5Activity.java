package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.utils.IntentUtils;
import com.m520it.mobilsafe.utils.SPUtils;

/**
 * @author 王维波
 * @time 2016/9/7  15:02
 * @desc ${TODD}
 */
public class Setup5Activity extends Activity {
    private TextView tv_setup5_phone;
    private ImageView iv_setup5_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }



    private void initView() {
        setContentView(R.layout.activity_setup5);
        tv_setup5_phone = (TextView)findViewById(R.id.tv_setup5_phone);
        iv_setup5_state = (ImageView)findViewById(R.id.iv_setup5_state);

    }

    private void initData() {
        tv_setup5_phone.setText(SPUtils.getString(getApplicationContext(), Constant.SAFENUMBER));
        iv_setup5_state.setImageResource(SPUtils.getBoolean(getApplicationContext(),
                Constant.PROTOCOTING)?R.drawable.lock:R.drawable.unlock);
    }

    public void reSetup(View view){
        IntentUtils.startIntentAndFinish(Setup5Activity.this,Setup1Activity.class);
    }
}
