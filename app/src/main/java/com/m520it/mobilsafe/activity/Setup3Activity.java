package com.m520it.mobilsafe.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
public class Setup3Activity extends BaseSetupActivity {
    private EditText et_phone;


    @Override
    public void initData() {
        String phone = SPUtils.getString(getApplicationContext(), Constant.SAFENUMBER);
        if(!TextUtils.isEmpty(phone)) {
            et_phone.setText(phone);
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup3);
        et_phone = (EditText)findViewById(R.id.et_phone);
    }

    public  void selectContact(View view){
          IntentUtils.startIntentAndFinishAndReturnData(Setup3Activity.this,ContactActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null) {
            String phone = data.getStringExtra("phone").replace("-","").replace(" ", "");

            et_phone.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void next() {
        String phone = et_phone.getText().toString().replace("-", "");
        if(TextUtils.isEmpty(phone)) {
           Toast.makeText(Setup3Activity.this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
        }else {
            SPUtils.putString(getApplicationContext(), Constant.SAFENUMBER,phone);
            IntentUtils.startIntentAndFinish(Setup3Activity.this,Setup4Activity.class);
        }

    }

    @Override
    public void pre() {
        IntentUtils.startIntentAndFinish(Setup3Activity.this,Setup2Activity.class);
    }
}
