package com.m520it.mobilsafe.conf;

/**
 * @author 王维波
 * @time 2016/9/5  11:32
 * @desc 常量类,用来保存所有的一些常量信息
 */
public class Constant {
    public  static final String UPDATE="update";
    public  static final String PASSWORD="password";
    public  static final String SPNAME="config";
    public static final String FINISH = "finish";
    public static final String SIMNUMBER = "simnumber";
    public static final String SAFENUMBER = "safenumber";
    public static final String PROTOCOTING = "protocoting";
    public static final String BLACKINTERFACE = "blackinterface";
    public static final String WHICHBACKGROUND = "whichbackground";
    public static final String X = "x";
    public static final String Y = "Y";
    public static final String SHOWSYS = "showsys";
    public static final String TABLENAME_LOCK="applock";

    public static class URL{
        public static final String BASEURL="http://192.168.35.10:8080/";
        public  static  final String UPDATEURL=BASEURL+"info.json";
        public static final String UPDATEANTIR=BASEURL+"update.json";

    }
}
