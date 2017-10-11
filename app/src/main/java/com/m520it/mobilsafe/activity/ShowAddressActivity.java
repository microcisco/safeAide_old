package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.db.dao.ShowAddressDao;

/**
 * @author 王维波
 * @time 2016/9/11  16:05
 * @desc ${TODD}
 */
public class ShowAddressActivity  extends Activity{
    private EditText et_phone;
    private TextView tv_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_showaddress);
        tv_address = (TextView)findViewById(R.id.tv_address);
        et_phone = (EditText)findViewById(R.id.et_phone);
    }

    public  void query(View v){
        String phone = et_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_phone.startAnimation(shake);
            Toast.makeText(ShowAddressActivity.this, "请输入电话号码", Toast.LENGTH_SHORT).show();
          return;
        }


        String address = ShowAddressDao.getAddress(phone);
        tv_address.setText("查询的地址为:" +address);
    }
}
