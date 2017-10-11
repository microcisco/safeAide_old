package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilsafe.R;

/**
 * @author 王维波
 * @time 2016/9/21  10:25
 * @desc ${TODD}
 */
public class EnterPasserworldActivity extends Activity {

    private String packname;
    private ImageView iv_icon;
    private TextView tv_name;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }


    private void initView() {
        setContentView(R.layout.activity_enter_password);
        Intent intent = getIntent();
        packname = intent.getStringExtra("packname");
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_name = (TextView) findViewById(R.id.tv_name);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    private void initData() {
        try {
            PackageManager pm = getPackageManager();

            PackageInfo packageInfo = pm.getPackageInfo(packname, 0);

            Drawable drawable = packageInfo.applicationInfo.loadIcon(pm);
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            iv_icon.setImageDrawable(drawable);
            tv_name.setText(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void tijiao(View v){
        String password = et_password.getText().toString().trim();
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(EnterPasserworldActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
        }else {
            if("123".equals(password)) {
                //通过自定义的广播临时停止保护
                Intent intent=new Intent();
                intent.setAction("com.m520it.mobilsafe.stopwatchdog");
                intent.putExtra("packname",packname);
                //发出广播
                sendBroadcast(intent);
                finish();
            }
        }

    }

    //屏蔽回退

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
      /*  <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.HOME" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.MONKEY"/>
        </intent-filter>*/
        Intent intent=new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
