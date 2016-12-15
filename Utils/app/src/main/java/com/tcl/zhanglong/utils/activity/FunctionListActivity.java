package com.tcl.zhanglong.utils.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.activity.intentfilter.IntentfilterActivity;
import com.tcl.zhanglong.utils.activity.launchmode.LaunchModeActivity;

import java.util.Arrays;

/**
 * Created by Steve on 16/10/11.
 */

public class FunctionListActivity extends BaseListActivity {

    private String[] functionStr = {
            CustViewListActivity.class.getName(),
            LaunchModeActivity.class.getName(),
            IntentfilterActivity.class.getName()};




    @Override
    protected void initData() {
        super.initData();
        functionArray.addAll(Arrays.asList(functionStr));
    }




}
