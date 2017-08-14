package com.tcl.zhanglong.utils.activity.launchmode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.utils.R;

/**
 * Created by Steve on 16/12/13.
 */

public class LaunchModeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private String[] functionStr = {"SingleTaskActivity","StandardActivity","ActivityA","SingleInstanceActivity"};

    private FunctionAdapter mAdapter;
    private ListView mListView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launchmode);
        DebugLog.w("Activity LaunchMode");
        mAdapter = new FunctionAdapter();
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String function = functionStr[position];
        if (function.equals(functionStr[0])){
            //adb shell dumpsys activity
            startActivity(new Intent(this,SingleTaskActivity.class));
        }else if (function.equals(functionStr[1])){
            startActivity(new Intent(this,StandardActivity.class));
        }else if (function.equals(functionStr[2])){
            startActivity(new Intent(this,ActivityA.class));
        }else if(function.equals(functionStr[3])){
            startActivity(new Intent(this,SingleInstanceActivity.class));
        }
    }




    private class FunctionAdapter extends BaseAdapter {

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
                convertView = LayoutInflater.from(LaunchModeActivity.this).inflate(R.layout.function_item,null);
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

    static class ViewHolder{
        TextView textView;
    }
}
