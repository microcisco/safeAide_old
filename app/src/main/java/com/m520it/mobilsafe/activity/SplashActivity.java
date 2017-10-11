package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.conf.Constant;
import com.m520it.mobilsafe.db.dao.Md5Dao;
import com.m520it.mobilsafe.utils.SPUtils;
import com.m520it.mobilsafe.utils.Stream2JsonString;
import com.m520it.mobilsafe.utils.SystemInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
    private static final int LOADINGMAIN = 0;//代表进入主界面
    private static final int SHOWDIALOG = 1;

    private RelativeLayout rl_splash_root;//根布局
    private TextView tv_splash_version_name;//版本名称
    private TextView tv_splash_version_code;//版本号
    private AnimationSet animationSet;
    private VersionInfo versionInfo;
    private long startTime;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADINGMAIN:
                    statHome();
                    break;
                case SHOWDIALOG:
                    showISDialog();
                    break;
                default:
                    switch (msg.what) {
                        case 1001:
                            Toast.makeText(getApplicationContext(), "服务器错误", Toast.LENGTH_LONG).show();
                            break;
                        case 1002:
                            Toast.makeText(getApplicationContext(), "IO异常", Toast.LENGTH_LONG).show();
                            break;
                        case 1003:
                            Toast.makeText(getApplicationContext(), "JSON解析粗我", Toast.LENGTH_LONG).show();
                            break;
                        case 1004:
                            Toast.makeText(getApplicationContext(), "包名没有找到", Toast.LENGTH_LONG).show();
                            break;
                    }
                    statHome();
            }
        }
    };

    //弹出一个升级提醒对话框
    private void showISDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("升级提醒");
        builder.setMessage(versionInfo.desc);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                statHome();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //断点下载
                downLoadNewAPK();
            }
        });
        builder.show();

    }

    //断点下载最新的apk
    private void downLoadNewAPK() {
        HttpUtils httpUtils = new HttpUtils();
        final File file = new File(Environment.getExternalStorageDirectory(), "xxx.apk");
        httpUtils.download(versionInfo.downloadurl, file.getAbsolutePath(), false, new RequestCallBack<File>() {
            //下载成功的回调
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //下载成功开始自动安装apk
             /*   <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="content" />
                <data android:scheme="file" />
                <data android:mimeType="application/vnd.android.package-archive" />*/
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivityForResult(intent, 0);
            }

            //下载失败的回调
            @Override
            public void onFailure(HttpException error, String msg) {
                statHome();
            }
        });
    }

    private Message msg;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        statHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//必须setContentView(R.layout.activity_main);方法前面
        /* new Thread(){
             public void run(){
                 SystemClock.sleep(10000);
                 PackageManager pm = getPackageManager();
                 pm.setComponentEnabledSetting(getComponentName(),PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
             }
         }.start();*/

        //界面初始化
        initView();
        //初始化动画
        initAnimation();
        //初始化数据
        initData();
        //初始化事件
        initEvent();
        //复制数据库
        copyDb("address.db");
        copyDb("commonnum.db");
        copyDb("antivirus.db");
        upDateDb();
        creatShowcut();
        createNotif();
    }

    private void createNotif() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.a)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, HomeActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void creatShowcut() {
        //需要桌面生成的快捷图标

            Intent intent=new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

            // 告诉桌面 快捷的图片
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.drawable.a));
            //告诉桌面 快捷的名称
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"小码哥手机卫士");
            intent.putExtra("duplicate", false);
            // 告诉桌面 快捷图标点击后的事件
            Intent intentStart=new Intent();
            intentStart.setAction("com.m520it.mobilsafe.starthome");
            intentStart.addCategory(Intent.CATEGORY_DEFAULT);

            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intentStart);
            sendBroadcast(intent);


    }

    //更新数据库
    private void upDateDb() {
        new Thread(){
            public void run(){
                // 1 需要连接网络获得服务端最新的版本号
                try {
                    URL url = new URL(Constant.URL.UPDATEANTIR);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    int code = conn.getResponseCode();
                    if(code==200) {
                        //说明连接是成功
                        InputStream in = conn.getInputStream();
                        //需要把这样的一个字节流,转换成json字符串
                        String jsonStr = Stream2JsonString.readStream(in);
                        //开始解析这样的一个json字符串
                        JSONObject jsonObject=new JSONObject(jsonStr);
                        String version = jsonObject.getString("version");
                        String md5 = jsonObject.getString("md5");
                        String type = jsonObject.getString("type");
                        String name = jsonObject.getString("name");
                        String desc = jsonObject.getString("desc");
                        //2 需要获得用户手机病毒版本号
                        int oldVersion = Md5Dao.getVersion();
                        // 3比对版本号,看是否需要升级
                        if(Integer.parseInt(version)>oldVersion) {
                            //更新数据库
                            Md5Dao.upDateVersion(Integer.parseInt(version));
                            //把最新的病毒信息插入到数据库
                            Md5Dao.addAntir(md5,type,name,desc);

                        }

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();



        // 4 如果需要升级,插入最新的病毒信息,并且病毒数据库的版本需要更新

    }

    /**
     * 1 放在子线程中复制
     * 2 不是每次都需要复制
     */
    private void copyDb(final String name) {
        new Thread() {
            public void run() {

                File file = new File(getFilesDir(), name);
                if (file.exists() && file.length() > 0) {

                } else {
                    InputStream in = null;
                    try {
                        in = getAssets().open(name);

                        FileOutputStream fos = new FileOutputStream(file);
                        int len = 0;
                        byte[] buffer = new byte[1024];
                        while ((len = in.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();


    }


    //界面初始化
    private void initView() {
        setContentView(R.layout.activity_main);
        //初始化根布局
        rl_splash_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
        //初始化版本名称
        tv_splash_version_name = (TextView) findViewById(R.id.tv_splash_version_name);
        //版本号
        tv_splash_version_code = (TextView) findViewById(R.id.tv_splash_version_code);
    }

    //初始化动画
    private void initAnimation() {
        /**0 开始旋转角度
         * 360 结束角度
         * Animation.RELATIVE_TO_SELF 锚点,相对于自己的某个位置固定
         * 0.5f 在X轴方向说明锚点相对于自己的宽度的一半去旋转
         */
        // 旋转动画
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        //设置动画作用时间
        ra.setDuration(2000);
        //补间动画,又称影子动画
        //固定动画做完的位置
        ra.setFillAfter(true);
        /**
         * 0.0f 在X轴方向从0放大到1
         */
        //比例动画
        ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        //设置动画作用时间
        sa.setDuration(2000);
        //补间动画,又称影子动画
        //固定动画做完的位置
        sa.setFillAfter(true);

        /**0.0f 完全透明
         * 1.0f 全不透
         */
        //透明度渐变动画
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        //设置动画作用时间
        aa.setDuration(2000);
        //补间动画,又称影子动画
        //固定动画做完的位置
        aa.setFillAfter(true);
        /**
         * false 不共用动画插入器
         */

        //动画容器,用来转载所有的动画,然后统一运行
        animationSet = new AnimationSet(false);
        animationSet.addAnimation(ra);
        animationSet.addAnimation(sa);
        animationSet.addAnimation(aa);
        rl_splash_root.startAnimation(animationSet);


    }

    //初始化数据
    private void initData() {
        try {
            tv_splash_version_code.setText(SystemInfoUtils.getVersionCode(getApplicationContext()) + "");
            tv_splash_version_name.setText(SystemInfoUtils.getVersionName(getApplicationContext()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //初始化事件
    private void initEvent() {
        //动画执行的监听
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            //动画一启动就被回调的方法
            @Override
            public void onAnimationStart(Animation animation) {
                //是否用户打开了检查自动更新的操作
                if (SPUtils.getBoolean(getApplicationContext(), Constant.UPDATE)) {
                    //说明需要升级
                    //在子线中联网检查
                    startTime = System.currentTimeMillis();
                    checkVersion();

                    System.out.println("需要检查更新");

                } else {
                    //不需要升级
                    //啥事都不做
                }
            }

            //动画做完之后的回调
            @Override
            public void onAnimationEnd(Animation animation) {
                //是否用户打开了检查自动更新的操作
                if (SPUtils.getBoolean(getApplicationContext(), Constant.UPDATE)) {
                    //不需要升级
                    //啥事都不做
                } else {

                    //进入主界面
                    System.out.println("进入主界面");
                    statHome();


                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //检查版本更新的方法

    /**
     * @called 当需要版本更新的时候在调用
     */
    private void checkVersion() {
        //在子线程中检查是否需要更新
        new Thread() {
            public void run() {
                readURLData();

            }
        }.start();

    }

    private void readURLData() {
        msg = Message.obtain();
        try {
            //1 获得url路径
            URL url = new URL(Constant.URL.UPDATEURL);
            // 2得到一个链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 3 设置一些属性
            conn.setRequestMethod("GET");
            // 设置超时时间
            conn.setConnectTimeout(5000);
            //得到请求码
            int code = conn.getResponseCode();
            if (code == 200) {
                //说明请求成功,获得服务端返回的字节流
                InputStream inputStream = conn.getInputStream();
                //调用工具方法把字节流转换为字符串
                String jsonString = Stream2JsonString.readStream(inputStream);
                //解析字符流
                versionInfo = parseJson(jsonString);
                if (Integer.parseInt(versionInfo.version) == SystemInfoUtils.getVersionCode(getApplicationContext())) {
                    //版本号相等,直接进入主界面
                    msg.what = LOADINGMAIN;
                } else {
                    msg.what = SHOWDIALOG;
                }


            } else {
                //说明请求失败
                msg.what = code;
            }

        } catch (MalformedURLException e) {
            msg.what = 1001;
            e.printStackTrace();
        } catch (IOException e) {
            msg.what = 1002;
            e.printStackTrace();
        } catch (JSONException e) {
            msg.what = 1003;
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            msg.what = 1004;
            e.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            if (endTime - startTime < 2000) {
                SystemClock.sleep(2000 - (endTime - startTime));
            }
            handler.sendMessage(msg);
        }


    }

    //解析jsonString
    private VersionInfo parseJson(String jsonString) throws JSONException {
        VersionInfo info = new VersionInfo();
        JSONObject jsonObject = new JSONObject(jsonString);
        info.version = jsonObject.getString("version");
        info.downloadurl = jsonObject.getString("downloadurl");
        info.desc = jsonObject.getString("desc");
        return info;
    }

    //进入主界面
    private void statHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    class VersionInfo {
        String version;
        String downloadurl;
        String desc;
    }
}
