package com.tcl.zhanglong.utils.activity;

import com.tcl.zhanglong.utils.IPC.IPCActivityA;
import com.tcl.zhanglong.utils.IPC_Messenger.MessengerActivity;
import com.tcl.zhanglong.utils.activity.intentfilter.IntentfilterActivity;
import com.tcl.zhanglong.utils.activity.launchmode.LaunchModeActivity;
import com.tcl.zhanglong.utils.activity.ui.UIListActivity;
import com.tcl.zhanglong.utils.project_pattern.ProjectPatternActivity;

/**
 * Created by Steve on 16/10/11.
 */

public class FunctionListActivity extends BaseListActivity {



    @Override
    protected String[] getFuncStrArray() {
        return new String[]{
                UIListActivity.class.getName(),
                LaunchModeActivity.class.getName(),
                IntentfilterActivity.class.getName(),
                IPCActivityA.class.getName(),
                MessengerActivity.class.getName(),
                ProjectPatternActivity.class.getName(),
                LiyingAcitivty.class.getName()
                };
    }


}
