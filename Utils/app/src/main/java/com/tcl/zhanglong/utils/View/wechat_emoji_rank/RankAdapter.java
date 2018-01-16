package com.tcl.zhanglong.utils.View.wechat_emoji_rank;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcl.zhanglong.utils.R;

import java.util.List;

/**
 * Created by Steve on 2017/12/5.
 */

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder>{

    private Context mContext;
    private List<RankBean> mDatas;
    private LayoutInflater mInflater;
    //private ItemTouchHelper mHelper;
    private WeChatEmojiActivity.ItemTouchListener listener;

    public RankAdapter(Context mContext, List<RankBean> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setListener(WeChatEmojiActivity.ItemTouchListener listener) {
        this.listener = listener;
    }

    @Override
    public RankAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RankAdapter.ViewHolder(mInflater.inflate(R.layout.item_rank,parent,false));
    }

    @Override
    public void onBindViewHolder(final RankAdapter.ViewHolder holder, int position) {
        final RankBean bean = mDatas.get(position);
        holder.tvEmoji.setText(bean.emojiName);
        holder.tvRank.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    if (listener!=null)
                        listener.onTouchDrag(holder);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size():0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvEmoji;
        TextView tvRank;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEmoji = (TextView) itemView.findViewById(R.id.tv_emoji_name);
            tvRank = (TextView) itemView.findViewById(R.id.tv_emoji_rank);
        }
    }

    private void check(View view, final RecyclerView.ViewHolder holder){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    if (listener!=null)
                        listener.onTouchDrag(holder);
                }
                return false;
            }
        });
    }
}
