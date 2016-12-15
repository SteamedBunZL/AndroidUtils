package com.tcl.zhanglong.utils.activity;

import android.view.View;

import com.tcl.zhanglong.utils.R;

/**
 * Created by Steve on 16/12/15.
 */

public class BaseOneButtonActivity extends BaseActivity{


    @Override
    protected int getContentViewId() {
        return R.layout.activity_one_button;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });
    }

    protected void click(){

    }


}
