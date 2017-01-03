package com.tcl.zhanglong.utils.activity.ui;

import com.tcl.zhanglong.utils.activity.BaseListActivity;
import com.tcl.zhanglong.utils.activity.ui.material_design.MaterailDesignActivity;
import com.tcl.zhanglong.utils.opengl.OpenGlListActivity;

/**
 * Created by Steve on 17/1/1.
 */

public class UIListActivity extends BaseListActivity{


    @Override
    protected String[] getFuncStrArray() {
        return new String[]{
                CustViewListActivity.class.getName(),
                OpenGlListActivity.class.getName(),
                MaterailDesignActivity.class.getName()
        };
    }
}
