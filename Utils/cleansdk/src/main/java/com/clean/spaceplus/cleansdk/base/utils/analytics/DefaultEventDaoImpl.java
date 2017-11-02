package com.clean.spaceplus.cleansdk.base.utils.analytics;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.utils.analytics.db.AnalyticsDBProvider;
import com.clean.spaceplus.cleansdk.base.utils.analytics.db.AnalyticsTable;
import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.hawkclean.framework.log.NLog;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/6 11:31
 * @copyright TCL-MIG
 */
public class DefaultEventDaoImpl implements EventDao{

    private static final String TAG = DefaultEventDaoImpl.class.getSimpleName();
    private AnalyticsDBProvider provider;
    private static final Object object = new Object();

    public DefaultEventDaoImpl(){
        provider = AnalyticsDBProvider.getInstance();
    }

    @Override
    public List<JSONObject> getEvents() {
        synchronized (object){
            Cursor cursor = null;
            List<JSONObject> content = new LinkedList<>();
            try{
                String sql = "select * from " + AnalyticsTable.TABLE_NAME;
                cursor = provider.rawQuery(sql, null);
                while (cursor.moveToNext()){
                    JSONObject event = null;
                    try {
                        event = new JSONObject(cursor.getString(cursor.getColumnIndex(AnalyticsTable.EVENT_INFO)));
                    } catch (Exception e){
                        NLog.d(TAG, "error from Exception");
                    }
                    content.add(event);
                }
                cursor.close();

                provider.delete(AnalyticsTable.TABLE_NAME, null, null);
                // 2016/7/7 删除掉数据库
            }catch (Exception e){
                NLog.printStackTrace(e);
            }
            finally {
                IOUtils.closeSilently(cursor);
            }
            return content;
        }
    }

    @Override
    public void putEvent(JSONObject t) {
        synchronized (object) {
            try{
                long ret = provider.insert(AnalyticsTable.TABLE_NAME, null, getContentValue(t));

            }catch (Exception e){
                NLog.printStackTrace(e);
            }
        }
    }

    @Override
    public void putEvents(List<JSONObject> t) {
        synchronized (object) {
            try{
                ContentValues[] values = getContentValues(t);
                if (values != null) {
                    long ret = provider.insert(AnalyticsTable.TABLE_NAME, null, values);
                    NLog.e(TAG, "Analytics insert return : %d", ret);
                }
            }catch (Exception e){
                NLog.printStackTrace(e);
            }

        }
    }

    private ContentValues getContentValue(JSONObject event){
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnalyticsTable.EVENT_INFO, event.toString());
        return contentValues;
    }

    private ContentValues[] getContentValues(List<JSONObject> events){
        if(events==null){
            return null;
        }
        ContentValues [] contentValues = new ContentValues[events.size()];
        JSONObject event;
        for (int i = 0; i < events.size(); i++) {

            event =events.get(i);
            contentValues[i] = new ContentValues();
            contentValues[i].put(AnalyticsTable.EVENT_INFO, event.toString());
        }
        return contentValues;
    }
}
