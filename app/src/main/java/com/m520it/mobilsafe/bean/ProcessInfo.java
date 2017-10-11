package com.m520it.mobilsafe.bean;

import android.graphics.drawable.Drawable;

/**
 * @author 王维波
 * @time 2016/9/18  10:41
 * @desc ${TODD}
 */
public class ProcessInfo {
    private boolean isCheck;
    private String packName;//包名也就是进程名
    private String name;//进程对应的软件名称
    private Drawable icon;//软件图标
    private long size;
    private boolean isUserProcess;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isUserProcess() {
        return isUserProcess;
    }

    public void setUserProcess(boolean userProcess) {
        isUserProcess = userProcess;
    }
}
