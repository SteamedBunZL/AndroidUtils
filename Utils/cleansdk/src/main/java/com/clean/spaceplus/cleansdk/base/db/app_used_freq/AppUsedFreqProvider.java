package com.clean.spaceplus.cleansdk.base.db.app_used_freq;

import android.content.ContentValues;
import android.content.Context;

import com.clean.spaceplus.cleansdk.appmgr.service.AppUsedInfoRecord;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;

/**
 * @author zengtao.kuang
 * @Description:ProcessList数据提供
 * @date 2016/5/3 11:04
 * @copyright TCL-MIG
 */
public class AppUsedFreqProvider extends BaseDatabaseProvider {

    private static final String TAG = AppUsedFreqProvider.class.getSimpleName();

    private static AppUsedFreqProvider sListProvider;

    public static synchronized AppUsedFreqProvider getInstance(Context context) {
        if (sListProvider == null) {
            sListProvider = new AppUsedFreqProvider(context);
        }
        return sListProvider;
    }

    private AppUsedFreqProvider(Context context){
        onCreate(context, BaseDBFactory.getTableFactory(context, BaseDBFactory.TYPE_APP_USED_FREQ));
    }




    private synchronized  void saveOrUpdateRecord(AppUsedInfoRecord record){
        ContentValues cv = new ContentValues();
        cv.put(AppUsedInfoTable.PKG_NAME,record.mAppUsedFreqInfo.getPackageName());
        cv.put(AppUsedInfoTable.LAST_OPEN_TIME,record.mAppUsedFreqInfo.getLastOpenTime());
        cv.put(AppUsedInfoTable.RECORD_DATE,record.mRecordDate);
        cv.put(AppUsedInfoTable.TOTAL_OPEN_COUNT,record.mAppUsedFreqInfo.getTotalOpenCount());
        cv.put(AppUsedInfoTable.TOTAL_OPEN_TIME,record.mAppUsedFreqInfo.getTotalOpenTime());
        if(record.mId>0){
            //更新
//            String sql=String.format("update %s set %s=?,%s=?,%s=?", AppUsedInfoTable.TABLE_NAME,AppUsedInfoTable.TOTAL_OPEN_COUNT,AppUsedInfoTable.TOTAL_OPEN_TIME);
//            rawQuery(sql,null);
            String where=AppUsedInfoTable._ID+"=?";
            int result=update(AppUsedInfoTable.TABLE_NAME,cv,where,new String[]{record.mId+""});

        }else{
            //插入
            long result=insert(AppUsedInfoTable.TABLE_NAME,null,cv);
            record.mId=result;
//            NLog.e(TAG,"插入结果:"+result);
        }
//        String sql = String.format("INSERT  INTO %s (%s,%s,%s,%s,%s) VALUES (?,?,?,?,?)",AppUsedInfoTable.TABLE_NAME,AppUsedInfoTable.PKG_NAME,AppUsedInfoTable.LAST_OPEN_TIME,AppUsedInfoTable.RECORD_DATE,AppUsedInfoTable.TOTAL_OPEN_COUNT,AppUsedInfoTable.TOTAL_OPEN_TIME);
//        String[] args=new String[]{record.mAppUsedFreqInfo.getPackageName(),record.mAppUsedFreqInfo.getLastOpenTime()+"",record.mRecordDate,record.mAppUsedFreqInfo.getTotalOpenCount()+"",record.mAppUsedFreqInfo.getTotalOpenTime()+""};
//        Cursor cursor = rawQuery(sql,args);



//        if(cursor!=null&&cursor.getCount()!=0){
//            mProvider.update(ProcessWhiteListTable.TABLE_NAME,cv,ProcessWhiteListTable.PKG_NAME+" = ?",new String[]{pkgName});
//        }else{
//            mProvider.insert(ProcessWhiteListTable.TABLE_NAME,null,cv);
//        }
    }
}
