package com.tcl.zhanglong.utils.View.wechat_emoji_rank;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Steve on 2017/12/5.
 */

public class WrapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private RecyclerView.Adapter adapter;

    public WrapAdapter(RecyclerView.Adapter adapter){
        this.adapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return adapter.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (adapter!=null){
            adapter.onBindViewHolder(holder,position);
        }
    }

    @Override
    public int getItemCount() {
        if (adapter!=null)
            return adapter.getItemCount();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (adapter!=null)
            return adapter.getItemViewType(position);
        return 0;
    }


    @Override
    public long getItemId(int position) {
        if (adapter!=null)
            return adapter.getItemId(position);
        return -1;
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(observer);
        }
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder{

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }



}
