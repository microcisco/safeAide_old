package com.m520it.mobilsafe;

import android.test.AndroidTestCase;

import com.m520it.mobilsafe.db.dao.BlackNumberDao;

import java.util.Random;

/**
 * @author 王维波
 * @time 2016/9/10  14:48
 * @desc ${TODD}
 */
public class TestCRUD extends AndroidTestCase {
 private BlackNumberDao  dao;

    //初始化使用在getContext之后
    @Override
    protected void setUp() throws Exception {
        dao=new BlackNumberDao(getContext());
        super.setUp();
    }

    //擦屁股
    @Override
    protected void tearDown() throws Exception {
        dao=null;
        super.tearDown();
    }

    public void testAdd(){
        for(int i = 0; i < 200; i++) {
            Random random=new Random();
            boolean add = dao.add("18627752"+i, random.nextInt(3)+"");

        }

    }

    public void testDelete(){
        boolean delete = dao.delete("1862775212");
        assertEquals(true,delete);
    }

    public  void testUpdate(){

        boolean update = dao.update("1862775212", "2");
        assertEquals(true,update);
    }

    public void testFind(){
        BlackNumberDao dao=new BlackNumberDao(getContext());
        String mode = dao.find("1862775212");
        assertEquals("2",mode);
    }
}
