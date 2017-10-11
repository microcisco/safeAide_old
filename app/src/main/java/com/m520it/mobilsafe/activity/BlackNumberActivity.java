package com.m520it.mobilsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilsafe.R;
import com.m520it.mobilsafe.bean.BlackNumberInfo;
import com.m520it.mobilsafe.db.dao.BlackNumberDao;

import java.util.List;

/**
 * @author 王维波
 * @time 2016/9/10  15:03
 * @desc ${TODD}
 */
public class BlackNumberActivity extends Activity {
    private ListView lv_black;
    private BlackNumberDao dao;
    private List<BlackNumberInfo> all;
    private BlackNumberAdapter adapter;
    private LinearLayout ll_loading;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.GONE);
            lv_black.setVisibility(View.VISIBLE);
            if(adapter==null) {
                adapter = new BlackNumberAdapter();
                lv_black.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }
        }
    };
    private EditText et_phone;
    private AlertDialog dialog;
    private AlertDialog alertDialog;
    private int limit=20;
    private int offset=0;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData(limit,offset);
        initEvent();
    }



    private void initData(final int limit, final int offset) {
        ll_loading.setVisibility(View.VISIBLE);
        lv_black.setVisibility(View.GONE);
        new Thread() {
            public void run() {
                if(all==null) {
                    all = dao.findPart(limit,offset);
                }else {
                    all.addAll(dao.findPart(limit,offset));
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initView() {
        dao = new BlackNumberDao(getApplicationContext());
        count = dao.getCount();
        setContentView(R.layout.activity_blacknumber);
        lv_black = (ListView) findViewById(R.id.lv_black);
        ll_loading = (LinearLayout)findViewById(R.id.ll_loading);
    }

    private void initEvent() {

        lv_black.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                 switch (scrollState){
                     case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://滑动停止转态
                         // 1 线获得最后一个listview的item可见的条目位置吧
                         int position = lv_black.getLastVisiblePosition();
                         //当前集合中的数据个数,也就是item的个数
                         int size = all.size();
                         if(position==(size-1)) {
                             //说明滚动到最后了
                             offset+=limit;
                             if(offset>=count) {
                               Toast.makeText(BlackNumberActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                             }else {
                                 initData(limit,offset);
                             }

                         }
                         break;
                     case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滑翔状态

                         break;
                     case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸滚动

                         break;
                 }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    public void add(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.item_black_add_view, null);

        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        Button btn_cancel = (Button) view.findViewById(R.id.bt_cancel);
        Button btn_ok = (Button) view.findViewById(R.id.bt_ok);
        final RadioGroup radiog = (RadioGroup) view.findViewById(R.id.rg);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = et_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(BlackNumberActivity.this, "请输入黑名单号码", Toast.LENGTH_SHORT).show();
                } else {
                    String mode=0+"";
                    int id = radiog.getCheckedRadioButtonId();
                    switch (id) {

                        case R.id.rb_all:
                            mode="0";
                            break;
                        case R.id.rb_phone:
                            mode="1";
                            break;
                        case R.id.rb_sms:
                            mode="2";
                            break;
                    }
                    boolean result = dao.add(phone, mode);
                    if(result) {
                        BlackNumberInfo  info=new BlackNumberInfo();
                        info.setMode(mode);
                        info.setPhone(phone);
                        all.add(0,info);
                        adapter.notifyDataSetChanged();

                    }else {
                        Toast.makeText(BlackNumberActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog= builder.create();
        dialog.setView(view,0,0,0,0);
        dialog.show();


    }

    class BlackNumberAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return all.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            View view = null;
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.item_blacknumber_view, null);
                holder.tv_phone = (TextView) view.findViewById(R.id.tv_phone);
                holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
                holder.iv_delete= (ImageView) view.findViewById(R.id.iv_item_delete);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            final BlackNumberInfo info = all.get(position);
            String mode = info.getMode();
            holder.tv_phone.setText(info.getPhone());
            switch (Integer.parseInt(mode)) {
                case 0:
                    holder.tv_mode.setText("全部拦截");
                    break;
                case 1:
                    holder.tv_mode.setText("电话拦截");
                    break;
                case 2:
                    holder.tv_mode.setText("短信拦截");
                    break;
            }

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(BlackNumberActivity.this);
                    builder.setTitle("删除");
                    builder.setMessage("您确认删除吗?");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            boolean result = dao.delete(info.getPhone());
                            if(result) {
                                all.remove(position);
                                adapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(BlackNumberActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                            alertDialog.dismiss();
                        }
                    });


                    alertDialog = builder.show();
                }
            });

            return view;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        class ViewHolder {
            TextView tv_phone;
            TextView tv_mode;
            ImageView iv_delete;
        }
    }
}
