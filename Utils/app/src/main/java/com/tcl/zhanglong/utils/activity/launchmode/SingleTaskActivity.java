package com.tcl.zhanglong.utils.activity.launchmode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.utils.R;

/**
 * Created by Steve on 16/12/13.
 */

public class SingleTaskActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singletask);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleTaskActivity.this,SingleTaskActivity.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        DebugLog.w("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugLog.w("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        DebugLog.w("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        DebugLog.w("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLog.w("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        DebugLog.w("");
    }
}
