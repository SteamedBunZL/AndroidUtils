package com.tcl.zhanglong.utils.View.wechat_address_book;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tcl.zhanglong.utils.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve on 2017/12/4.
 */

public class WeChatActivity extends AppCompatActivity{

    private RecyclerView mRv;

    private TitleItemDecoration mDecoration;

    private List<MusicBean> mDatas = new ArrayList<>();

    private MusicAdapter mAdapter;

    private IndexBar mIndexBar;

    private LinearLayoutManager mManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat);
        initData();
        mManager = new LinearLayoutManager(this);
        mIndexBar = (IndexBar) findViewById(R.id.indexBar);
        mIndexBar.setLayoutManager(mManager);
        mIndexBar.setSourceDatas(mDatas).invalidate();

        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(mManager);
        mDecoration = new TitleItemDecoration(this,mDatas);
        mRv.addItemDecoration(mDecoration);
        mRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));



        mAdapter = new MusicAdapter(this,mDatas);
        mAdapter.setDatas(mDatas);
        mRv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();



    }


    private void initData(){
        mDatas.add(new MusicBean("asljfls"));
        mDatas.add(new MusicBean("bsdsds"));
        mDatas.add(new MusicBean("bssssdsds"));
        mDatas.add(new MusicBean("bsd333sds"));
        mDatas.add(new MusicBean("bsdsds"));
        mDatas.add(new MusicBean("cbsdsds"));
        mDatas.add(new MusicBean("ccccbsdsds"));
        mDatas.add(new MusicBean("c112bsdsds"));
        mDatas.add(new MusicBean("DDDDDDbsdsds"));
        mDatas.add(new MusicBean("DSFSFSFbsdsds"));
        mDatas.add(new MusicBean("D3566bsdsds"));


        mDatas.add(new MusicBean("EDDDDDDbsdsds"));
        mDatas.add(new MusicBean("eDSFSFSFbsdsds"));
        mDatas.add(new MusicBean("ED3566bsdsds"));


        mDatas.add(new MusicBean("FDDDDDDbsdsds"));
        mDatas.add(new MusicBean("FDSFSFSFbsdsds"));
        mDatas.add(new MusicBean("FD3566bsdsds"));
        mDatas.add(new MusicBean("你是我的眼"));
        mDatas.add(new MusicBean("啦 啦 啦"));

    }
}
