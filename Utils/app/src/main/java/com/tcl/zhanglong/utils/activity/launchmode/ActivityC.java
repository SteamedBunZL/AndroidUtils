package com.tcl.zhanglong.utils.activity.launchmode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.utils.R;

/**
 * Created by Steve on 16/12/14.
 */

public class ActivityC extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singletask);
        DebugLog.w("Activity C singleTask taskAffinity -- com.zl.task1");
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityC.this,ActivityA.class));
            }
        });
    }
}
