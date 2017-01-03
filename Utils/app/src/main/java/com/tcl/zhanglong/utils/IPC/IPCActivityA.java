package com.tcl.zhanglong.utils.IPC;

import android.content.Intent;

import com.tcl.zhanglong.utils.activity.BaseOneButtonActivity;

/**
 * Created by Steve on 16/12/17.
 */

public class IPCActivityA extends BaseOneButtonActivity{

    @Override
    protected void click() {
        super.click();
        startActivity(new Intent(this,IPCActivityB.class));
    }
}
