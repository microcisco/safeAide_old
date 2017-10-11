package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.utils.ContactUtils;

import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/8  10:39
 * @desc ${TODD}
 */
public class ContactActivity  extends Activity{
    private ListView lv_contact;
    private List<ContactUtils.ContactInfo> lists;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            lv_contact.setAdapter(new ConstactAdapter());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = lists.get(position).phone;
                Intent intent=new Intent();
                intent.putExtra("phone",phone);
                setResult(0,intent);
                finish();
            }
        });
    }

    private void initData() {
        new Thread(){
            public void run(){
                lists = ContactUtils.readContact(getApplicationContext());
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_contact);
        lv_contact = (ListView)findViewById(R.id.lv_contact);
    }

    class ConstactAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=null;
            ViewHolder holder;
            if(convertView==null) {

                holder=new ViewHolder();
               view=View.inflate(getApplicationContext(),R.layout.item_contact_view,null);
                holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                holder.tv_phone= (TextView) view.findViewById(R.id.tv_phone);
                view.setTag(holder);
            }else {
                view=convertView;
                holder= (ViewHolder) view.getTag();
            }
            holder.tv_name.setText(lists.get(position).name);
            holder.tv_phone.setText(lists.get(position).phone);

            return view;
        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
    class ViewHolder{
        TextView tv_name;
        TextView tv_phone;
    }
}
