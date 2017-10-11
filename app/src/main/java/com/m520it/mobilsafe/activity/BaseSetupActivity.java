package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.m520it.mobilsafe.R;

/**
 * @author 王维波
 * @time 2016/9/7  17:20
 * @desc ${TODD}
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector gestureDetector;//手指触目屏幕的管理事件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        initEvent();
        //初始化手指触摸屏幕的事件
        initGestureDetector();
    }

    public void initEvent() {

    }

    public void initData() {

    }

    public  void initGestureDetector(){
        gestureDetector=new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            /**
             *
             * @param e1 手指放下去的事件
             * @param e2  手指放下去后的移动事件
             * @param velocityX 水平方向滑动的加速度
             * @param velocityY 垂直方向的加速度
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                      if(e1.getRawX()-e2.getRawX()>50) {
                          System.out.println("拉出下一页");
                          //想拉出下一页
                          next();
                          overridePendingTransition(R.anim.next_in_translate,R.anim.next_out_translate);
                        return  true;//如果放回的是true代表当前事件被消费,如果是false代表事件一直没有处理完毕
                      }

                      if(e2.getRawX()-e1.getRawX()>50) {
                          //想拉出上一页
                          System.out.println("拉出上一页");
                          pre();
                          overridePendingTransition(R.anim.pre_in_translate,R.anim.pre_out_translate);
                          return  true;//代表当前事件被消费
                      }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    //手指一触碰到屏幕就会被激活

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //该方法不是抽象,是一个public子类选择实现
    public void initView() {

    }

    public abstract  void next();

    public  abstract  void pre();

    //下一步
    public void showNext(View view){
        next();
        overridePendingTransition(R.anim.next_in_translate,R.anim.next_out_translate);

    }
   //上一步都是在xml里面定义的点击事件
    public  void showPre(View view){
        pre();
        overridePendingTransition(R.anim.pre_in_translate,R.anim.pre_out_translate);

    }


}
