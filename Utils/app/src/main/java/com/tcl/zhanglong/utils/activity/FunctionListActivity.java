package com.tcl.zhanglong.utils.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcl.zhanglong.utils.R;

/**
 * Created by Steve on 16/10/11.
 */

public class FunctionListActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private String[] functionStr = {"CustomView","Notification"};

    private FunctionAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_list);
        mAdapter = new FunctionAdapter();
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String function = functionStr[position];
        if (function.equals("CustomView")){
            Intent customViewIntent = new Intent(this,CustViewListActivity.class);
            startActivity(customViewIntent);
        }else if(function.equals("Notification")){

        }
    }


    private class FunctionAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return functionStr.length;
        }

        @Override
        public Object getItem(int position) {
            return functionStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(FunctionListActivity.this).inflate(R.layout.function_item,null);
                holder.textView = (TextView) convertView.findViewById(R.id.textview);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            String function = functionStr[position];
            holder.textView.setText(function);
            return convertView;
        }
    }

    class ViewHolder{
        TextView textView;
    }
}
