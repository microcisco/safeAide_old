package com.m520it.mobilsafe.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m520it.mobilsafe.R;

/**
 * @author 王维波
 * @time 2016/9/7  10:17
 * @desc ${TODD}
 */
public class SettingItemView extends RelativeLayout {
    private ImageView iv_setting_toggle;
    private TextView tv_name;
    private RelativeLayout rv_setting_toggle;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData(attrs);
    }

    //调用当前的这个方法去设置不同的背景
    public  void setToggle(boolean isOpen){
         if(isOpen) {
             iv_setting_toggle.setImageResource(R.drawable.on);
         }else {
             iv_setting_toggle.setImageResource(R.drawable.off);
         }
       // iv_setting_toggle.setImageResource(isOpen? R.drawable.on:R.drawable.off);
    }


    /**
     * @param attrs 当前控件的所有属性集合得类
     */
    private void initData(AttributeSet attrs) {

        String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc");
        String position = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "position");
        tv_name.setText(desc);
        switch (Integer.parseInt(position)) {

            case 0:
                rv_setting_toggle.setBackgroundResource(R.drawable.iv_first_selector);
                break;
            case 1:
                rv_setting_toggle.setBackgroundResource(R.drawable.iv_middle_selector);
                break;
            case 2:
                rv_setting_toggle.setBackgroundResource(R.drawable.iv_last_selector);
                break;
        }
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.view_setting_item, this);
        iv_setting_toggle = (ImageView) findViewById(R.id.iv_setting_toggle);
        tv_name = (TextView) findViewById(R.id.tv_name);
        rv_setting_toggle = (RelativeLayout) findViewById(R.id.rv_setting_toggle);
    }
}
