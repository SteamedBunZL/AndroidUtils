package com.clean.spaceplus.cleansdk.base.utils.DataReport;

import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;

import java.util.List;

/**
 * @author zeming_liu
 * @Description: 数据上报接口
 * @date 2016/9/14.
 * @copyright TCL-MIG
 */
public interface IDataReportTarget {

    void putEvent(Event event);

    void putEvents(List<Event> events, boolean isAllReport);
}
