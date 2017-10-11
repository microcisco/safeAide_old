package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.bean.ProcessInfo;
import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.utils.IntentUtils;
import com.m520it.mobilsafe.utils.SPUtils;
import com.m520it.mobilsafe.utils.SystemTaskUitls;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/18  9:18
 * @desc ${TODD}
 */
public class TaskManagerActivity extends Activity {
    private TextView tv_taskcount;//运行中的进程个数
    private TextView tv_mem;//剩余内存和总类存相关
    private LinearLayout ll_loading;//正在加载数据
    private List<ProcessInfo> infos;
    private List<ProcessInfo> userInfos;//用户进程集合
    private List<ProcessInfo> systemInfos;//系统进程集合
    private List<ProcessInfo> killInfos;
    private TextView tv_name_count;//显示当前的进程个数
    private ListView lv_task;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.GONE);
            adapter = new TaskManagerAdapter();
            lv_task.setAdapter(adapter);
        }
    };
    private ProcessInfo processInfo;
    private TaskManagerAdapter adapter;
    private int runningProcess;
    private long availMem_Long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        setContentView(R.layout.activity_task);
        userInfos = new ArrayList<>();
        killInfos = new ArrayList<>();
        tv_name_count = (TextView) findViewById(R.id.tv_name_count);
        systemInfos = new ArrayList<>();
        tv_taskcount = (TextView) findViewById(R.id.tv_taskcount);
        tv_mem = (TextView) findViewById(R.id.tv_mem);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_task = (ListView) findViewById(R.id.lv_task);
    }

    private void initData() {
        //设置进程数
        runningProcess = SystemTaskUitls.getRunningProcess(this);
        tv_taskcount.setText("运行中的进程:" + runningProcess + "个");
        availMem_Long = SystemTaskUitls.getAvailMem(this);
        String avaliMem_Str = Formatter.formatFileSize(this, availMem_Long);

        String totalMem = Formatter.formatFileSize(this, SystemTaskUitls.getTotalMem(this));
        //设置可用内存
        tv_mem.setText("剩余/总内存:" + avaliMem_Str + "/" + totalMem);
        fillData();
    }

    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                infos = SystemTaskUitls.getAllPeocess(getApplicationContext());
                for (ProcessInfo info : infos) {
                    if (info.isUserProcess()) {
                        userInfos.add(info);
                    } else {
                        systemInfos.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initEvent() {
        lv_task.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int position, int firstpositon, int i2) {
                if (userInfos != null && systemInfos != null) {
                    if (position > userInfos.size()) {
                        tv_name_count.setText("系统进程:" + systemInfos.size() + "个");
                    } else {
                        tv_name_count.setText("用户进程:" + userInfos.size() + "个");
                    }
                }
            }
        });

        lv_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {

                    return;
                } else if (position == userInfos.size() + 1) {

                    return;
                } else if (position <= userInfos.size()) {
                    position = position - 1;
                    processInfo = userInfos.get(position);
                } else {
                    position = position - userInfos.size() - 2;
                    processInfo = systemInfos.get(position);
                }
                //去掉自己包名
                if (processInfo.getPackName().equals(getPackageName())) {
                    return;
                }
                //走到下面,判断是否被选中

                processInfo.setCheck(!processInfo.isCheck());
                //只要是界面修改,都需要通知适配器更新
                adapter.notifyDataSetChanged();
            }
        });
    }

    class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            boolean result = SPUtils.getBoolean(getApplicationContext(), Constant.SHOWSYS);

            if(result) {
                //显示系统进程
                return userInfos.size()+systemInfos.size()+2;
            }else {
                //不显示系统进程
                return userInfos.size() +1;
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ProcessInfo processInfo;
            if (position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("用户进程:" + userInfos.size() + "个");
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(18);
                return tv;
            } else if (position == userInfos.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("系统进程:" + systemInfos.size() + "个");
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(18);
                return tv;
            } else if (position <= userInfos.size()) {
                position = position - 1;
                processInfo = userInfos.get(position);
            } else {
                position = position - userInfos.size() - 2;
                processInfo = systemInfos.get(position);
            }

            View view = null;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.item_task_viiew, null);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_size = (TextView) view.findViewById(R.id.tv_size);
                holder.cb_check = (CheckBox) view.findViewById(R.id.cb_check);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(processInfo.getIcon());
            holder.tv_size.setText(Formatter.formatFileSize(getApplicationContext(), processInfo.getSize()));
            holder.tv_name.setText(processInfo.getName());
            holder.cb_check.setChecked(processInfo.isCheck());
            if (processInfo.getPackName().equals(getPackageName())) {
                holder.cb_check.setVisibility(View.GONE);
            } else {
                holder.cb_check.setVisibility(View.VISIBLE);
            }
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
        CheckBox cb_check;
    }

    //全选
    public void selectAll(View view) {


        for (ProcessInfo info : userInfos) {
            if (info.getPackName().equals(getPackageName())) {
                continue;
            }
            info.setCheck(true);
        }
        for (ProcessInfo info : systemInfos) {
            info.setCheck(true);
        }
        adapter.notifyDataSetChanged();
    }

    //反选
    public void selectRever(View view) {
        for (ProcessInfo info : userInfos) {
            if (info.getPackName().equals(getPackageName())) {
                continue;
            }
            info.setCheck(!info.isCheck());
        }

        for (ProcessInfo info : systemInfos) {
            info.setCheck(!info.isCheck());
        }
        adapter.notifyDataSetChanged();
    }

    //清除所有被选中的项目
    public void killAll(View view) {

        int count = 0;
        long mem = 0;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ProcessInfo info : userInfos) {
            if (info.isCheck()) {
                count++;
                mem += info.getSize();
                am.killBackgroundProcesses(info.getPackName());
                killInfos.add(info);
            }

        }

        for (ProcessInfo info : systemInfos) {
            if (info.isCheck()) {
                count++;
                mem += info.getSize();
                am.killBackgroundProcesses(info.getPackName());
                killInfos.add(info);
            }

        }

        for (ProcessInfo info : killInfos) {
            if (info.isUserProcess()) {
                userInfos.remove(info);
            } else {
                systemInfos.remove(info);
            }
        }

        runningProcess = runningProcess - count;
        tv_taskcount.setText("运行中的进程:" + runningProcess + "个");


        String totalMem = Formatter.formatFileSize(this, SystemTaskUitls.getTotalMem(this));
        //设置可用内存
        availMem_Long += mem;
        String aval = Formatter.formatFileSize(this, availMem_Long);
        tv_mem.setText("剩余/总内存:" + aval + "/" + totalMem);
        Toast.makeText(TaskManagerActivity.this, "清理进程" + count + "释放内存" + Formatter.formatFileSize(this, mem), Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    public  void setting(View view){
        IntentUtils.startIntent(TaskManagerActivity.this,TaskMangerSettingActivity.class);

    }

    @Override
    protected void onStart() {
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
        super.onStart();
    }
}
