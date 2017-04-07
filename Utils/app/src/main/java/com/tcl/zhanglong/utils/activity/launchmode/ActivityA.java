package com.tcl.zhanglong.utils.activity.launchmode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.Utils.DebugLog;

/**
 * Created by Steve on 16/12/14.
 */

public class ActivityA extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singletask);
        DebugLog.w("Activity A standard模式 ");
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityA.this,ActivityB.class));
            }
        });
    }
}