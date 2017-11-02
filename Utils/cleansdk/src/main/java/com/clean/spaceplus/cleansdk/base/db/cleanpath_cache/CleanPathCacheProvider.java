package com.clean.spaceplus.cleansdk.base.db.cleanpath_cache;

import android.content.Context;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;
import com.clean.spaceplus.cleansdk.main.bean.cleanpath_cache.AdvFolder;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:CleanPathCache数据提供
 * @date 2016/4/23 15:08
 * @copyright TCL-MIG
 */

public class CleanPathCacheProvider extends BaseDatabaseProvider {
    private final static String TAG = CleanPathCacheProvider.class.getSimpleName();
    private static CleanPathCacheProvider sCleanPathCacheProvider = null;

    public static synchronized CleanPathCacheProvider getInstance() {
        if (sCleanPathCacheProvider == null) {
            sCleanPathCacheProvider = new CleanPathCacheProvider(SpaceApplication.getInstance().getContext());
        }
        return sCleanPathCacheProvider;
    }

    private CleanPathCacheProvider(Context context){
        //onCreate(context,CleanPathCacheFactory.createFactory(context));
        NLog.d(TAG, "CleanPathCacheProvider onCreate");
        //onCreate(context, CleanPathCacheFactory.createFactory(context, new CleanPathCacheImpl(context), new CleanPathCacheDBTableGenerator()));
        onCreate(context, BaseDBFactory.getTableFactory(context, BaseDBFactory.TYPE_CLEANPATH));

    }




    /**
     * 接口测试
     *
     * 一次性获取AdvFoler所有数据
     */
    public List<AdvFolder> findAllAdvCleanPath() {
        List<AdvFolder> dataList = null;
        String allDataSQL = "select * from " + AdvFolderTable.TABLE_NAME ;
        NLog.d(TAG, "findAllAdvCleanPath sql = "+ allDataSQL);
        Cursor cursor = null;
        try {
            cursor = rawQuery(allDataSQL, null);
            if (cursor != null && cursor.getCount() > 0) {
                dataList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    AdvFolder info = new AdvFolder();
                    info._id = cursor.getInt(cursor.getColumnIndex(AdvFolderTable._ID));
                    info.path = cursor.getString(cursor.getColumnIndex(AdvFolderTable.PATH));
                    info.describeinfo = cursor.getString(cursor.getColumnIndex(AdvFolderTable.DESCRIBEINFO));
                    info.srsid = cursor.getInt(cursor.getColumnIndex(AdvFolderTable.SRSID));
                    dataList.add(info);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
                cursor = null;
            }
        }

        return dataList;
    }

}
