package com.tcl.zhanglong.utils.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.zhanglong.utils.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve on 16/12/15.
 */

public class BaseListActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    protected List<String> functionArray = new ArrayList<>();

    private FunctionAdapter mAdapter;
    private ListView mListView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_function_list;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mAdapter = new FunctionAdapter();
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String function = functionArray.get(position);
        Class<?> clazz = null;
        Intent intent = null;
        try {
            clazz = Class.forName(functionArray.get(position));
            intent = new Intent(this,clazz);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Class Not Found",Toast.LENGTH_SHORT).show();
        }
    }

    protected class FunctionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return functionArray.size();
        }

        @Override
        public Object getItem(int position) {
            return functionArray.get(position);
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
                convertView = LayoutInflater.from(BaseListActivity.this).inflate(R.layout.function_item,null);
                holder.textView = (TextView) convertView.findViewById(R.id.textview);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            String function = functionArray.get(position);
            try {
                String[] split = function.split("\\.");
                if (split.length>0)
                    function = split[split.length-1];
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.textView.setText(function);
            return convertView;
        }
    }

    static class ViewHolder{
        TextView textView;
    }
}
