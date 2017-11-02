package com.clean.spaceplus.cleansdk.boost.engine.clean;


import android.support.v4.util.ArrayMap;

import java.util.Map;

/**
 * @author zengtao.kuang
 * @Description: boost清理设置
 * @date 2016/4/5 20:38
 * @copyright TCL-MIG
 */
public class BoostCleanSetting {
    // BOOST_TASK_MEM = 0x00000001;
    // BOOST_TASK_CPUTEMP = 0x00000002;
    // BOOST_TASK_AUTOSTART = 0x00000004;
    // BOOST_TASK_ABNORMAL = 0x00000008;
    public int mTaskType = 0;    // bitwise above task types

    public Map<Integer, Object> mSettings = new ArrayMap<Integer, Object>();
}
