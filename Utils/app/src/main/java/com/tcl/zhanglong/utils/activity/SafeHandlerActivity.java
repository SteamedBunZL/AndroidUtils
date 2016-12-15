package com.tcl.zhanglong.utils.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.tcl.zhanglong.utils.R;

import java.lang.ref.WeakReference;

/**
 * 线程安全的Handler
 * Created by Steve on 16/9/7.
 */
public class SafeHandlerActivity extends BaseActivity{

    private SafeHandler mHandler;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mHandler = new SafeHandler(this);
    }


    /**
     * 解决方案
     */
    private static class SafeHandler extends Handler{
        WeakReference<SafeHandlerActivity> mActivity;

        public SafeHandler(SafeHandlerActivity activity){
            this.mActivity = new WeakReference<SafeHandlerActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            final SafeHandlerActivity activity = mActivity.get();
        }
    }


}
