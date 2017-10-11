package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.utils.IntentUtils;
import com.m520it.mobilsafe.utils.MD5Utils;
import com.m520it.mobilsafe.utils.SPUtils;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * @author 王维波
 * @time 2016/9/5  11:45
 * @desc ${TODD}
 */
public class HomeActivity extends Activity {
    private ImageView iv_home_logo;
    private ImageView iv_home_setting;
    private GridView gv_home;
    private static final String names[] = {"手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "病毒查杀", "缓存清理", "高级工具"};
    private static final String desc[] = {"手机丢失好找", "防骚扰监听", "方便管理软件", "保存手机畅通", "注意流量超标", "手机安全管家", "手机快步如飞", "特性处理更好"};
    private static final int icons[] = {R.drawable.a, R.drawable.b,
            R.drawable.c, R.drawable.d,
            R.drawable.e, R.drawable.f,
            R.drawable.h, R.drawable.j};
    private AlertDialog dialog;
    private EditText et_password;
    private EditText et_password_confirm;
    private Button bt_cancel;
    private Button bt_ok;
    private AlertDialog.Builder builder;
    private TextView tv_title_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAnimation();
        initData();
        initEvent();

    }

    //初始化视图和控件
    private void initView() {
        setContentView(R.layout.activity_home);
        //需要设置动画的图片
        iv_home_logo = (ImageView) findViewById(R.id.iv_home_logo);
        //seeting图片
        iv_home_setting = (ImageView) findViewById(R.id.iv_home_setting);
        //GridView
        gv_home = (GridView) findViewById(R.id.gv_home);

    }

    //初始化动画
    private void initAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv_home_logo, "rotationY", 0, 60, 120, 180, 240, 300, 360);

        //每循环一次时间
        objectAnimator.setDuration(2000);
        //无线循环
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        //动画开始
        objectAnimator.start();
    }

    //初始化数据
    private void initData() {
        HomeAdapter adapter = new HomeAdapter();
        gv_home.setAdapter(adapter);
    }

    //初始化事件
    private void initEvent() {
        iv_home_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //手机防盗
                        String password = SPUtils.getString(getApplicationContext(), Constant.PASSWORD);
                        if (TextUtils.isEmpty(password)) {
                            //说明没有设置过密码,应该出一个设置密码的对话框
                            showSetPasswordDialog();
                        } else {
                            //说明设置过密码,应该弹出一个输入密码的对话框
                            showEnterPasswordDialog();
                        }

                        break;
                    case 1://黑名单列表
                        IntentUtils.startIntent(HomeActivity.this, BlackNumberActivity.class);

                        break;
                    case 2://软件管理
                        IntentUtils.startIntent(HomeActivity.this, AppManagerActivity.class);

                        break;
                    case 3://进程管理
                        IntentUtils.startIntent(HomeActivity.this, TaskManagerActivity.class);

                        break;
                    case 4://流量统计
                        IntentUtils.startIntent(HomeActivity.this, TrafficActivity.class);

                        break;
                    case 5://病毒查杀
                        IntentUtils.startIntent(HomeActivity.this, KillAntivirusActivity.class);

                        break;
                    case 6://缓存清理
                        IntentUtils.startIntent(HomeActivity.this,CleanCacherActivity.class);

                        break;
                    case 7://高级工具
                        IntentUtils.startIntent(HomeActivity.this, AtoolActivity.class);

                        break;
                }
            }
        });
    }
    // 输入密码对话框

    private void showEnterPasswordDialog() {
        builder = new AlertDialog.Builder(HomeActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.alert_set_password, null);
        et_password = (EditText) view.findViewById(R.id.et_password);
        et_password_confirm = (EditText) view.findViewById(R.id.et_password_confirm);
        bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        tv_title_name = (TextView) view.findViewById(R.id.tv_title_name);
        bt_ok = (Button) view.findViewById(R.id.bt_ok);
        tv_title_name.setText("输入密码");
        et_password_confirm.setVisibility(View.GONE);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消的操作,应该关闭当前的对话框
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认操作,应该获取当前输入框的信息
                String password = et_password.getText().toString().trim();
                String password_confirm = et_password_confirm.getText().toString().trim();
                if (!TextUtils.isEmpty(password)) {
                    String oldPassword = SPUtils.getString(getApplicationContext(), Constant.PASSWORD);
                    if (MD5Utils.string2Md5(password).equals(oldPassword)) {
                        //密码相等.进入设置向导页面
                        boolean finish = SPUtils.getBoolean(getApplicationContext(), Constant.FINISH);
                        if (finish) {
                            //说明已经设置完了这个向导
                            IntentUtils.startIntent(HomeActivity.this, Setup5Activity.class);


                        } else {
                            //说明没有设置过向导,如果没有设置过向导,应该从第一个设置向导进入
                            System.out.println("没有设置过向导,进入第一页");
                            Intent intent = new Intent(HomeActivity.this, Setup1Activity.class);
                            startActivity(intent);
                        }
                        dialog.dismiss();
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //说明其中有一个或者两个为null
                    Toast.makeText(HomeActivity.this, "请按要求输入", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setView(view);
        dialog = builder.show();
    }

    //设置密码对话框的方法
    private void showSetPasswordDialog() {
        builder = new AlertDialog.Builder(HomeActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.alert_set_password, null);
        et_password = (EditText) view.findViewById(R.id.et_password);
        et_password_confirm = (EditText) view.findViewById(R.id.et_password_confirm);
        bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_ok = (Button) view.findViewById(R.id.bt_ok);
        tv_title_name = (TextView) view.findViewById(R.id.tv_title_name);
        tv_title_name.setText("设置密码");

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消的操作,应该关闭当前的对话框
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认操作,应该获取当前输入框的信息
                String password = et_password.getText().toString().trim();
                String password_confirm = et_password_confirm.getText().toString().trim();
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(password_confirm)) {
                    if (password.equals(password_confirm)) {
                        //说明两次输入相等,应该保存一份密码
                        SPUtils.putString(getApplicationContext(), Constant.PASSWORD, MD5Utils.string2Md5(password));
                        dialog.dismiss();

                    } else {
                        Toast.makeText(HomeActivity.this, "两次输入密码不同", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //说明其中有一个或者两个为null
                    Toast.makeText(HomeActivity.this, "请按要求输入", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setView(view);
        dialog = builder.show();
    }





    class HomeAdapter extends BaseAdapter {
        //显示item的个数
        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.item_hone, null);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_item_icon);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_item_name);
            TextView tv_desc = (TextView) view.findViewById(R.id.tv_item_desc);
            iv_icon.setImageResource(icons[position]);
            tv_name.setText(names[position]);
            tv_desc.setText(desc[position]);

            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }
}
