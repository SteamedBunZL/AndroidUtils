package com.tcl.zhanglong.utils.activity.ui;

import com.tcl.zhanglong.utils.activity.BaseListActivity;
import com.tcl.zhanglong.utils.activity.customview.ECGViewActivity;
import com.tcl.zhanglong.utils.activity.customview.LightingColorFilterActivity;
import com.tcl.zhanglong.utils.activity.customview.PorterDuffColorFilterActivity;

/**
 * Created by Steve on 16/10/11.
 */

public class CustViewListActivity extends BaseListActivity {


    @Override
    protected String[] getFuncStrArray() {
        return new String[]{
                ECGViewActivity.class.getSimpleName(),
                LightingColorFilterActivity.class.getSimpleName(),
                PorterDuffColorFilterActivity.class.getSimpleName()
        };
    }
}
