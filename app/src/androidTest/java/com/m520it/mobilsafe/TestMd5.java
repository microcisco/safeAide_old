package com.m520it.mobilsafe;

import android.test.AndroidTestCase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 王维波
 * @time 2016/9/8  15:45
 * @desc ${TODD}
 */
public class TestMd5 extends AndroidTestCase {

    public void testMd5() throws NoSuchAlgorithmException {
        // 1 需要有数字摘要器
        MessageDigest digest=MessageDigest.getInstance("MD5");
        String text="123456";
        //代表将明文加密,加密后的放回类型是一个byte[] 数组
        StringBuffer buffer=new StringBuffer();
        byte[] bytes = digest.digest(text.getBytes());
        for (byte  b: bytes) {
            String str = Integer.toHexString(b & 0xff);
            if(str.length()==1) {
                buffer.append("0");
            }
            buffer.append(str);
        }

    }
}
