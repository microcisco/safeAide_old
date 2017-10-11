package com.m520it.mobilsafe.bean;

import android.graphics.drawable.Drawable;

/**
 * @author 王维波
 * @time 2016/9/14  11:04
 * @desc ${TODD}
 */
public class AppInfo {
    private String name; //软件名称
    private String packname;//软件的包名
    private long size;
    private boolean isRom;//是否安全在手机内存里面
    private Drawable icon; //软件的图片
    private boolean isUser;//是否是用户软件

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setRom(boolean rom) {
        isRom = rom;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }
}
