package com.tcl.zhanglong.utils.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.steve.commonlib.DebugLog;

/**
 * Created by Steve on 16/12/15.
 */

public abstract class BaseActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //日志输出每个activity的名字
        DebugLog.i("%s",getClass().getSimpleName());
        setContentView(getContentViewId());
        initData();
        initView();
    }


    protected abstract int getContentViewId();

    protected abstract void initData();

    protected abstract void initView();
}
