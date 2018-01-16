package com.tcl.zhanglong.utils.View.wechat_address_book;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.zhanglong.utils.R;

import java.util.List;

/**
 * Created by Steve on 2017/12/4.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{

    private Context mContext;
    private List<MusicBean> mDatas;
    private LayoutInflater mInflater;

    public MusicAdapter(Context context,List<MusicBean> datas){
        mContext = context;
        mDatas = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    public List<MusicBean> getDatas(){
        return mDatas;
    }

    public MusicAdapter setDatas(List<MusicBean> datas){
        mDatas = datas;
        return this;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_music,parent,false));
    }

    @Override
    public void onBindViewHolder(MusicAdapter.ViewHolder holder, final int position) {
        final MusicBean musicBean = mDatas.get(position);
        holder.tvMusic.setText(musicBean.getMusic());
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"pos:" + position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size():0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvMusic;
        View content;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMusic = (TextView) itemView.findViewById(R.id.tvMusic);
            content = itemView.findViewById(R.id.content);
        }
    }
}
