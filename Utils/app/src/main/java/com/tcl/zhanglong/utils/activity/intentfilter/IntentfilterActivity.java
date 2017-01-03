package com.tcl.zhanglong.utils.activity.intentfilter;


import com.tcl.zhanglong.utils.activity.BaseListActivity;

/**
 * Created by Steve on 16/12/15.
 */

public class IntentfilterActivity extends BaseListActivity{


    @Override
    protected String[] getFuncStrArray() {
        return new String[]{
                ActionActivity.class.getName()
        };
    }
}
