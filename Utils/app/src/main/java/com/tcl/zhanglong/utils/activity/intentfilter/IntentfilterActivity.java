package com.tcl.zhanglong.utils.activity.intentfilter;


import com.tcl.zhanglong.utils.activity.BaseActivity;
import com.tcl.zhanglong.utils.activity.BaseListActivity;

import java.util.Arrays;

/**
 * Created by Steve on 16/12/15.
 */

public class IntentfilterActivity extends BaseListActivity{


    String[] funcStr = {
            ActionActivity.class.getName()
    };

    @Override
    protected void initData() {
        super.initData();
        functionArray.addAll(Arrays.asList(funcStr));
    }



}
