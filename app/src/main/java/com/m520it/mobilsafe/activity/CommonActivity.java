package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.db.dao.SelectDataDao;

/**
 * @author 王维波
 * @time 2016/9/13  16:07
 * @desc ${TODD}
 */
public class  CommonActivity extends Activity {
    private ExpandableListView elv;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        db = SQLiteDatabase.openDatabase("/data/data/com.m520it.mobilsafe/files/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);

        elv = (ExpandableListView)findViewById(R.id.elv);
        elv.setAdapter(new CommmAdapter());
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Toast.makeText(CommonActivity.this, "被点击的父亲"+i+"孩子的位置"+i1, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    class CommmAdapter extends BaseExpandableListAdapter{
        //最外层listView的item的个数
        @Override
        public int getGroupCount() {
            return SelectDataDao.getGroupCount(db);
        }

        //最外层listview的每个item的item的个数,根据每个不同的item的位置放回不同的孩子个数
        @Override
        public int getChildrenCount(int groupPosition) {

            return SelectDataDao.getChildrenCount(groupPosition,db);
        }

        @Override
        public Object getGroup(int i) {
            return null;
        }

        @Override
        public Object getChild(int i, int i1) {
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }


        //最外层的item的view
        @Override
        public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
            TextView tv=new TextView(getApplicationContext());
            tv.setText("     "+SelectDataDao.getGroupView(groupPosition,db));
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(20);
            return tv;
        }
       //每一个外层item的孩子的item
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
            TextView tv=new TextView(getApplicationContext());
            tv.setText(SelectDataDao.getChildView(groupPosition,childPosition,db));
            tv.setTextColor(Color.RED);
            tv.setTextSize(20);
            return tv;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
