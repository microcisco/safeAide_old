package com.m520it.mobilsafe.activity;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.utils.IntentUtils;

/**
 * @author 王维波
 * @time 2016/9/7  15:02
 * @desc ${TODD}
 */
public class Setup1Activity extends BaseSetupActivity {


    @Override
    public void initView() {
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void next() {
        IntentUtils.startIntentAndFinish(Setup1Activity.this,Setup2Activity.class);

    }

    @Override
    public void pre() {

    }




}
