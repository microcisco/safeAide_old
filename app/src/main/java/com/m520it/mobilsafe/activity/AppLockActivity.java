package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.bean.AppInfo;
import com.m520it.mobilsafe.db.dao.AppLockDao;
import com.m520it.mobilsafe.utils.LogUtils;
import com.m520it.mobilsafe.utils.SystemAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/19  10:45
 * @desc ${TODD}
 */
public class AppLockActivity extends Activity implements View.OnClickListener {
    private TextView tv_unlock;//未枷锁的title
    private TextView tv_lock;//已枷锁的title

    private LinearLayout ll_unlock;//未枷锁的线性布局
    private LinearLayout ll_lock;//已枷锁的线性布局

    private TextView tv_unlock_name;//未枷锁的name
    private TextView tv_lock_name;//已枷锁的name

    private ListView lv_lock;//已枷锁的listview
    private ListView lv_unlock;//未枷锁的listivew

    private List<AppInfo> infos;//所有的数据
    private List<AppInfo> lockInfos;//枷锁的集合
    private List<AppInfo> unLockInfos;//未枷锁的集合
    private AppLockDao dao;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //枷锁的adapter
            lockAdapter = new LockAndUnLockAdapter(false);

            //未枷锁adapter
            unLockAdapter = new LockAndUnLockAdapter(true);

            lv_unlock.setAdapter(unLockAdapter);
            lv_lock.setAdapter(lockAdapter);

        }
    };
    private LockAndUnLockAdapter lockAdapter;
    private LockAndUnLockAdapter unLockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        dao = new AppLockDao(this);
        new Thread() {
            public void run() {
                infos = SystemAppInfo.getAllAppInfo(getApplicationContext());
                for (AppInfo info : infos) {
                    if (dao.find(info.getPackname())) {
                        //查询到说明是一个枷锁
                        lockInfos.add(info);
                    } else {
                        //说明是一个未枷锁
                        unLockInfos.add(info);
                    }

                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }


    private void initView() {
        setContentView(R.layout.activity_lock);
        LogUtils.d("TAG","打印");
        lockInfos = new ArrayList<>();
        unLockInfos = new ArrayList<>();
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);

        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

        tv_unlock_name = (TextView) findViewById(R.id.tv_unlock_name);
        tv_lock_name = (TextView) findViewById(R.id.tv_lock_name);

        lv_lock = (ListView) findViewById(R.id.lv_lock);
        lv_unlock = (ListView) findViewById(R.id.lv_unlock);


    }

    private void initEvent() {
        tv_unlock.setOnClickListener(this);
        tv_lock.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_unlock:
                ll_unlock.setVisibility(View.VISIBLE);
                ll_lock.setVisibility(View.GONE);
                tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tv_lock.setBackgroundResource(R.drawable.tab_right_default);
                break;

            case R.id.tv_lock:
                ll_unlock.setVisibility(View.GONE);
                ll_lock.setVisibility(View.VISIBLE);
                tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
                tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                break;

        }
    }

    class LockAndUnLockAdapter extends BaseAdapter {

        private boolean isUnlock;//是否是一个未枷锁,规定true代表未枷锁,false代表已枷锁


        public LockAndUnLockAdapter(boolean isUnlock) {
            this.isUnlock = isUnlock;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (isUnlock) {
                //未枷锁的数据
                count = unLockInfos.size();
                tv_unlock_name.setText("未枷锁的软件:" + count + "个");
            } else {
                //已枷锁的数据
                count = lockInfos.size();
                tv_lock_name.setText("已枷锁的软件:" + count + "个");
            }
            return count;
        }


        @Override
        public View getView(int position, View convetView, ViewGroup viewGroup) {
            final View view ;
            ViewHolder holder = null;

            if (convetView != null) {
                view = convetView;
                holder = (ViewHolder) view.getTag();
            } else {
                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.item_applock_view, null);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_unlock = (ImageView) view.findViewById(R.id.iv_unlock);
                view.setTag(holder);
            }
            final AppInfo appInfo;
            if (isUnlock) {
                appInfo = unLockInfos.get(position);
            } else {
                appInfo = lockInfos.get(position);
            }

            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getName());
            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUnlock) {
                        TranslateAnimation ta=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                        ta.setDuration(1000);
                        view.startAnimation(ta);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                boolean result = dao.add(appInfo.getPackname());
                                unLockInfos.remove(appInfo);
                                lockInfos.add(appInfo);
                                unLockAdapter.notifyDataSetChanged();
                                lockAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    } else {

                        TranslateAnimation ta=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,-1.0f,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                        ta.setDuration(1000);
                        view.startAnimation(ta);

                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //已枷锁
                                dao.deleter(appInfo.getPackname());
                                unLockInfos.add(appInfo);
                                lockInfos.remove(appInfo);
                                unLockAdapter.notifyDataSetChanged();
                                lockAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }

                }
            });

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
        ImageView iv_unlock;
    }
}
