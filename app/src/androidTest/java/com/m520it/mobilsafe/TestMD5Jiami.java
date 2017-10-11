package com.m520it.mobilsafe;

import android.test.AndroidTestCase;

import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @author 王维波
 * @time 2016/9/21  16:07
 * @desc ${TODD}
 */
public class TestMD5Jiami extends AndroidTestCase {
    public  void test() throws Exception {
        MessageDigest digest=MessageDigest.getInstance("sha-1");
        InputStream in = getContext().getAssets().open("sfsffsfsfsfsffff.doc");
        byte[] buffer=new byte[1024];
        int len=0;
        while ((len=in.read(buffer))!=-1){
            digest.update(buffer,0,len);
        }
        byte[] bytes = digest.digest();
        StringBuilder builder=new StringBuilder();
        for (byte  b: bytes) {
          //  (b& 0xff)
            String str = Integer.toHexString(b & 0xff);
            if(str.length()==1) {
                builder.append("0");
            }
            builder.append(str);
        }

        System.out.println("获得的特征码:"+builder.toString());

    }
}
