package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilsafe.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/22  11:18
 * @desc ${TODD}
 */
public class CleanCacherActivity extends Activity {
    private static final int FINISH = 1;//代表扫描完成
    private  final static int SCANNING=0;//代表正在扫描
    private FrameLayout fl_clean;
    private ProgressBar pb_clean;
    private TextView tv_clean_name;
    private ListView lv_clean;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case SCANNING://代表正在扫描
                    String name  = (String) msg.obj;
                    tv_clean_name.setText(name);

                    break;
                case FINISH://扫描完成
                    // 隐藏帧布局
                    fl_clean.setVisibility(View.GONE);
                    //给listView设置adapter
                    InnerAdapter adapter=new InnerAdapter();
                    lv_clean.setAdapter(adapter);
                    break;
            }
        }
    };
    private List<CacheInfo> lists;
    private PackageManager pm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        scanfCacher();
    }

    private void scanfCacher() {
        fl_clean.setVisibility(View.VISIBLE);
        lists = new ArrayList<>();
        new Thread(){
            private String packageName;
            public void run(){

                List<PackageInfo> infos = pm.getInstalledPackages(0);
                pb_clean.setMax(infos.size());
                int process=0;
                for (PackageInfo  info: infos) {
                    process++;
                    pb_clean.setProgress(process);
                    packageName = info.packageName;
                    try {
                        Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                        method.invoke(pm,packageName,new InnerClean());
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    SystemClock.sleep(50);
                }
                //发布以已经扫描完毕的消息
                Message msg=Message.obtain();
                msg.what=FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_clean);
        pm = getPackageManager();
        fl_clean = (FrameLayout)findViewById(R.id.fl_clean);
        pb_clean = (ProgressBar)findViewById(R.id.pb_clean);
        tv_clean_name = (TextView)findViewById(R.id.tv_clean_name);
        lv_clean = (ListView)findViewById(R.id.lv_clean);

    }

    class InnerClean extends IPackageStatsObserver.Stub{

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
          //发布一个正在扫描哪一个软件的消息
            PackageManager pm = getPackageManager();
            try {
            Message msg=Message.obtain();
            msg.what=SCANNING;
            msg.obj=pStats.packageName;
            handler.sendMessage(msg);
            if(pStats.cacheSize>0) {
                CacheInfo info=new CacheInfo();
                PackageInfo packageInfo = pm.getPackageInfo(pStats.packageName, 0);
                Drawable drawable = packageInfo.applicationInfo.loadIcon(pm);
                info.icon=drawable;
                String name = packageInfo.applicationInfo.loadLabel(pm).toString();
                info.name=name;
                info.packName=pStats.packageName;
                lists.add(info);
            }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class InnerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int i) {

            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int postion, View converView, ViewGroup viewGroup) {
            View  view=null;
            ViewHolder holder=null;
            if(converView!=null) {
                view=converView;
                holder= (ViewHolder) view.getTag();
            }else {
                holder=new ViewHolder();
                view=View.inflate(getApplicationContext(),R.layout.item_clean_view,null);
                holder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                holder.iv_clean= (ImageView) view.findViewById(R.id.iv_clean);
                view.setTag(holder);
            }
            final CacheInfo cacheInfo = lists.get(postion);
            holder.iv_icon.setImageDrawable(cacheInfo.icon);
            holder.tv_name.setText(cacheInfo.name);

            holder.iv_clean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Method method = PackageManager.class.getDeclaredMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                        method.invoke(pm,cacheInfo.packName,new DeleterObser());
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
            return view;
        }
    }
    class CacheInfo{
        Drawable icon;
        String name;
        long size;
        String packName;
    }

    class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_clean;
    }

    class DeleterObser extends IPackageDataObserver.Stub{

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            if(succeeded) {
                Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "失败", Toast.LENGTH_SHORT).show();

            }


        }
    }

    public  void cleanAll(View view){
        Method[] methods = PackageManager.class.getMethods();
        for (Method  method: methods) {
            if(method.getName().equals("freeStorageAndNotify")) {
                try {
                    method.invoke(pm,Long.MAX_VALUE,new DeleterObser());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                scanfCacher();
            }
        }

    }
}
