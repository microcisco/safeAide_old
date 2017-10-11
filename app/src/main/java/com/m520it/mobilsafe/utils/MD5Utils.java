package com.m520it.mobilsafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 王维波
 * @time 2016/9/8  15:55
 * @desc ${TODD}
 */
public class MD5Utils {
    public static String string2Md5(String text) {
        // 1 需要有数字摘要器
        MessageDigest digest = null;
        StringBuffer buffer=null;
        try {
            digest = MessageDigest.getInstance("SHA-1");


            //代表将明文加密,加密后的放回类型是一个byte[] 数组
            buffer= new StringBuffer();
            byte[] bytes = digest.digest(text.getBytes());
            for (byte b : bytes) {
                String str = Integer.toHexString(b & 0xff);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return buffer.toString();
    }

}
