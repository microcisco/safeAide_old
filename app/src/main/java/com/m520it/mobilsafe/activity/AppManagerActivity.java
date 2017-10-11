package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.bean.AppInfo;
import com.m520it.mobilsafe.utils.SystemAppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/14  10:46
 * @desc ${TODD}
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    private TextView tv_rom;
    private TextView tv_sd;
    private ListView lv_appmanager;
    private List<AppInfo> infos;
    private TextView tv_name_count;

    private List<AppInfo> userInfos;//用户软件集合
    private List<AppInfo> systeminfos;//系统软件集合
    private LinearLayout ll_loading;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.GONE);
            adapter = new AppManagerAdapter();
            lv_appmanager.setAdapter(adapter);
        }
    };
    private AppInfo appInfo;
    private PopupWindow popupWindow;
    private InnerReceiver receiver;
    private AppManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        receiver = new InnerReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        //卸载app特有
        intentFilter.addDataScheme("package");
        registerReceiver(receiver,intentFilter);
        setContentView(R.layout.activity_appmanager);
        tv_rom = (TextView) findViewById(R.id.tv_rom);
        tv_sd = (TextView) findViewById(R.id.tv_sd);
        lv_appmanager = (ListView) findViewById(R.id.lv_appmanager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_name_count = (TextView) findViewById(R.id.tv_name_count);
    }

    private void initData() {
        //拿到一个内存的文件
        File romFile = Environment.getDataDirectory();
        long romFree = romFile.getFreeSpace();
        tv_rom.setText("内存可用:" + Formatter.formatFileSize(this, romFree));

        File SDFile = Environment.getExternalStorageDirectory();
        long SDFree = SDFile.getFreeSpace();
        tv_sd.setText("SD卡用:" + Formatter.formatFileSize(this, SDFree));
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                userInfos = new ArrayList<AppInfo>();
                systeminfos = new ArrayList<AppInfo>();
                infos = SystemAppInfo.getAllAppInfo(AppManagerActivity.this);
                for (AppInfo info : infos) {
                    if (info.isUser()) {
                        userInfos.add(info);
                    } else {
                        systeminfos.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initEvent() {
        lv_appmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            //状态改变监听
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            //滚动监听
            @Override
            public void onScroll(AbsListView absListView, int firstVisable, int i1, int i2) {
                dissmissPP();
                if (userInfos != null && systeminfos != null) {
                    if (firstVisable > userInfos.size()) {
                        tv_name_count.setText("系统软件" + systeminfos.size() + "个");
                    } else {
                        tv_name_count.setText("用户软件软件" + userInfos.size() + "个");
                    }
                }

            }
        });

        lv_appmanager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    return;
                } else if (position == userInfos.size() + 1) {
                    return;
                } else if (position <= userInfos.size()) {
                    appInfo = userInfos.get(position - 1);
                } else {
                    appInfo = systeminfos.get(position - userInfos.size() - 2);
                }
                dissmissPP();
                View ppview = View.inflate(getApplicationContext(), R.layout.view_pp, null);
                popupWindow = new PopupWindow(ppview, -2, -2);
                //如果pp要显示动画,必须设置背景
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                int[] location = new int[2];
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(adapterView, Gravity.LEFT + Gravity.TOP, 60, location[1]);
                //透明度渐变动画
                AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
                aa.setDuration(300);
                //缩放动画
                ScaleAnimation sa = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(300);
                //动画容器
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(aa);
                set.addAnimation(sa);
                ppview.startAnimation(set);
                ppview.findViewById(R.id.ll_uninstall).setOnClickListener(AppManagerActivity.this);
                ppview.findViewById(R.id.ll_share).setOnClickListener(AppManagerActivity.this);
                ppview.findViewById(R.id.ll_start).setOnClickListener(AppManagerActivity.this);
                ppview.findViewById(R.id.ll_message).setOnClickListener(AppManagerActivity.this);

            }
        });
    }

    private void dissmissPP() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }

    }

    // 点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_uninstall:
                  uninstallApp();
                break;
            case R.id.ll_start:
                 startApp();
                break;
            case R.id.ll_share:
                shareApp();

                break;
            case R.id.ll_message:
                 messageApp();
                break;
        }
        dissmissPP();

    }

    private void startApp() {
        Intent launchIntentForPackage = this.getPackageManager().getLaunchIntentForPackage(appInfo.getPackname());
        startActivity(launchIntentForPackage);
    }

    private void messageApp() {
       /* <action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="package" />*/
        Intent intent=new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+appInfo.getPackname()));
        startActivity(intent);
    }

    private void shareApp() {
        /*<intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
        </intent-filter>*/

        Intent intent=new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"这个是我很好用的一款手机卫士,如果你喜欢,拿U盘来考"+appInfo.getPackname());
        startActivity(intent);
    }

    //卸载app
    private void uninstallApp() {
      /*  <action android:name="android.intent.action.VIEW" />
        <action android:name="android.intent.action.DELETE" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="package" />*/
        if(appInfo.isUser()) {
            Intent intent=new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setAction("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:"+appInfo.getPackname()));
            startActivity(intent);
        }else {
            Toast.makeText(AppManagerActivity.this, "系统软件不能被卸载", Toast.LENGTH_SHORT).show();
        }


        
    }

    class AppManagerAdapter extends BaseAdapter {

        private AppInfo appInfo;

        @Override
        public int getCount() {
            return userInfos.size() + 1 + systeminfos.size() + 1;
        }

        @Override
        public View getView(int position, View convetView, ViewGroup viewGroup) {
            if (position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("用户软件:" + userInfos.size() + "个");
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(18);
                return tv;
            } else if (position == userInfos.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("系统软件软件:" + systeminfos.size() + "个");
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(18);
                return tv;
            } else if (position <= userInfos.size()) {
                position = position - 1;
                appInfo = userInfos.get(position);
            } else {
                position = position - userInfos.size() - 2;
                appInfo = systeminfos.get(position);
            }

            View view = null;
            ViewHolder holder;
            if (convetView != null && convetView instanceof RelativeLayout) {
                view = convetView;
                holder = (ViewHolder) view.getTag();
            } else {

                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.item_appmanager_view, null);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_position = (TextView) view.findViewById(R.id.tv_position);
                holder.tv_size = (TextView) view.findViewById(R.id.tv_size);
                view.setTag(holder);
            }

            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_size.setText(Formatter.formatFileSize(getApplicationContext(), appInfo.getSize()) + "");
            holder.tv_name.setText(appInfo.getName());
            holder.tv_position.setText(appInfo.isRom() ? "手机内存" : "SD卡");
            return view;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_size;
        TextView tv_position;
    }

    @Override
    protected void onDestroy() {
        dissmissPP();
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    
    
    class InnerReceiver extends BroadcastReceiver{
        //一但有软件被卸载,这个方法就会被调用
        @Override
        public void onReceive(Context context, Intent intent) {
              if(appInfo.isUser()) {
                  userInfos.remove(appInfo);
                  adapter.notifyDataSetChanged();
              }
        }
    }
}
