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
import com.tcl.zhanglong.utils.activity.customview.AvoidXfermodeActivity;
import com.tcl.zhanglong.utils.activity.customview.ECGViewActivity;
import com.tcl.zhanglong.utils.activity.customview.LightingColorFilterActivity;
import com.tcl.zhanglong.utils.activity.customview.PorterDuffColorFilterActivity;

import java.util.Arrays;

/**
 * Created by Steve on 16/10/11.
 */

public class CustViewListActivity extends BaseListActivity{

    private String[] functionStr = {AvoidXfermodeActivity.class.getSimpleName(),
           ECGViewActivity.class.getSimpleName(),
           LightingColorFilterActivity.class.getSimpleName(),
            PorterDuffColorFilterActivity.class.getSimpleName()};



    @Override
    protected void initData() {
        super.initData();
        functionArray.addAll(Arrays.asList(functionStr));
    }


}
