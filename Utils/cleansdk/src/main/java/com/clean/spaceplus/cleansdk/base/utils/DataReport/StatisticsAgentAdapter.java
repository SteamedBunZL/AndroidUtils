package com.clean.spaceplus.cleansdk.base.utils.DataReport;

import com.clean.spaceplus.cleansdk.base.utils.analytics.DataReport;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
import com.hawkclean.framework.log.NLog;

import java.util.List;

/**
 * @author zeming_liu
 * @Description: 鹰眼sdk适配类
 * @date 2016/9/14.
 * @copyright TCL-MIG
 */
public class StatisticsAgentAdapter implements IDataReportTarget{

    @Override
    public void putEvent(Event event) {
        //鹰眼api
        try{
            DataReport.getInstance().send(event);
        }catch (Exception e){
            NLog.printStackTrace(e);
        }catch (Error er){
        }
    }

    @Override
    public void putEvents(List<Event> events,boolean isAllReport) {
        //鹰眼api
        try{
            DataReport.getInstance().batchSend(events,isAllReport);
        }catch (Exception e){
        }catch (Error er){
        }
    }
}
