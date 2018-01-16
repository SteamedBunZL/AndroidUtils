package com.tcl.zhanglong.utils.View.wechat_emoji_rank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.tcl.zhanglong.utils.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tcl.zhanglong.utils.R.id.item;

/**
 * Created by Steve on 2017/12/5.
 */

public class WeChatEmojiActivity extends AppCompatActivity{


    RankItemTouchHelper itemTouchHelper;
    List<RankBean> list = new ArrayList<>();
    RankAdapter adapter;
    RecyclerView mRv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        initData();
        mRv = (RecyclerView) findViewById(R.id.rv_rank);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter = new RankAdapter(this,list);
        itemTouchHelper = new RankItemTouchHelper(new DefaultCallbackImpl());
        adapter.setListener(new ItemTouchListener() {
            @Override
            public void onTouchDrag(RecyclerView.ViewHolder holder) {
                itemTouchHelper.startDrag(holder);
            }
        });
        mRv.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(mRv);
    }

    private void initData() {
        list.add(new RankBean("1"));
        list.add(new RankBean("2"));
        list.add(new RankBean("3"));
        list.add(new RankBean("4"));
        list.add(new RankBean("5"));
        list.add(new RankBean("6"));
        list.add(new RankBean("7"));
        list.add(new RankBean("8"));
        list.add(new RankBean("9"));
        list.add(new RankBean("10"));
        list.add(new RankBean("11"));
        list.add(new RankBean("12"));
        list.add(new RankBean("13"));
        list.add(new RankBean("14"));
        list.add(new RankBean("15"));
        list.add(new RankBean("16"));
        list.add(new RankBean("17"));
        list.add(new RankBean("18"));
        list.add(new RankBean("19"));

    }


    public class DefaultCallbackImpl extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlags,0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.e("zl","holder.position = " + viewHolder.getAdapterPosition() + ",target.position = " + target.getAdapterPosition());
            Collections.swap(list,viewHolder.getAdapterPosition(),target.getAdapterPosition());
            adapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }
    }


    public interface ItemTouchListener{
        void onTouchDrag(RecyclerView.ViewHolder holder);
    }
}
