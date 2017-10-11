package com.m520it.mobilsafe;

import android.test.AndroidTestCase;

import com.m520it.mobilsafe.db.dao.ShowAddressDao;

/**
 * @author 王维波
 * @time 2016/9/11  16:01
 * @desc ${TODD}
 */
public class TestAddress extends AndroidTestCase {

    public  void testAddress(){
        String address = ShowAddressDao.getAddress("18627752172");
        assertEquals("湖北武汉联通",address);
    }
}
