package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.process_list.APKParserCacheTable;
import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessListProvider;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: APKParserCache的DAO
 * @date 2016/5/12 10:51
 * @copyright TCL-MIG
 */
public class APKParserCacheDAO {

    private ProcessListProvider mProvider;
    APKParserCacheDAO(){
        mProvider = ProcessListProvider.getInstance(SpaceApplication.getInstance().getContext());
    }

    private List<APKModel> parseCursor(Cursor cursor){
        if(cursor==null){
            return null;
        }
        List<APKModel> modelList = new ArrayList<APKModel>();
        while (cursor.moveToNext()){
            APKModel apkModel = null;
            String filePath = cursor.getString(cursor.getColumnIndex(APKParserCacheTable.FILE_PATH));
            if(TextUtils.isEmpty(filePath))
            {
                break;
            }
            apkModel = new APKModel();
            apkModel.setPackageName(cursor.getString(cursor.getColumnIndex(APKParserCacheTable.PACKAGENAME)));//包名
            apkModel.setVersion(cursor.getString(cursor.getColumnIndex(APKParserCacheTable.VERSIONNAME)));//版本名
            apkModel.setVersionCode(cursor.getInt(cursor.getColumnIndex(APKParserCacheTable.VERSIONCODE)));//版本号
            apkModel.setPath(filePath);//路径
            apkModel.setSize(cursor.getLong(cursor.getColumnIndex(APKParserCacheTable.SIZE)));//大小
            apkModel.setModifyTime(cursor.getLong(cursor.getColumnIndex(APKParserCacheTable.LAST_MODIFIED)));//修改时间
            apkModel.setTitle(cursor.getString(cursor.getColumnIndex(APKParserCacheTable.TITLE)));//标题
            modelList.add(apkModel);
        }
        cursor.close();
        return modelList;
    }

    protected final ArrayMap<String, APKModel> getAllCache() {
        String sql = String.format("select * from %s"
                , APKParserCacheTable.TABLE_NAME);
        ArrayMap<String, APKModel> arrayMap = null;
        Cursor cursor = null;
        try {
            cursor = mProvider.rawQuery(sql, null);
            List<APKModel> list = parseCursor(cursor);

            if (list != null && list.size() > 0) {
                arrayMap = new ArrayMap<String, APKModel>();

                for (APKModel apkModel : list) {
                    arrayMap.put(apkModel.getPath(), apkModel);
                }

            }
            return arrayMap;
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            IOUtils.closeSilently(cursor);
        }
        return null;
    }


    /**
     * 根据
     * @param filePath
     * @return
     */
    protected final APKModel getAPKModelByFilePath(String filePath) {

        if(TextUtils.isEmpty(filePath))
        {
            return null;
        }

        APKModel model = null;

        Cursor cursor = null;
        try {
            String sql = String.format("select * from %s where %s = '%s'"
                    , APKParserCacheTable.TABLE_NAME
                    ,APKParserCacheTable.FILE_PATH,filePath );
            cursor = mProvider.rawQuery(sql,null);

            if (cursor == null || cursor.getCount() <= 0) {
                return null;
            }
            if ( cursor.moveToNext()) {
                model = findByCursor(cursor);
            }
            return model;
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            if (null != cursor) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    NLog.printStackTrace(e);
                } catch (Error e) {
                    NLog.printStackTrace(e);
                }
                cursor = null;
            }
        }

        return model;
    }



    /***
     * 更新条目
     *
     * @param model
     * @return
     */
    protected boolean update(APKModel model) {
        if (model == null||TextUtils.isEmpty(model.getPath()))
            return false;
        int ret = mProvider.update(APKParserCacheTable.TABLE_NAME
                ,getContentValues(model)
                ,APKParserCacheTable.FILE_PATH+"=?"
                ,new String[] { model.getPath()  });
        return ret<0;

    }

    /***
     * 新增
     *
     * @param model
     * @return
     */
    protected boolean add(APKModel model) {
        if (model == null||TextUtils.isEmpty(model.getPath())) {
            return false;
        }

        long ret = mProvider.insert(APKParserCacheTable.TABLE_NAME
                ,null
                ,getContentValues(model));
        return ret<0;
    }

    public static ContentValues getContentValues(APKModel model) {
        if (model == null ) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(APKParserCacheTable.SIZE, model.getSize());
        values.put(APKParserCacheTable.LAST_MODIFIED, model.getModifyTime());
        values.put(APKParserCacheTable.FILE_PATH, model.getPath());
        values.put(APKParserCacheTable.PACKAGENAME, model.getPackageName());
        values.put(APKParserCacheTable.VERSIONNAME, model.getVersion());
        values.put(APKParserCacheTable.VERSIONCODE, model.getVersionCode());
        values.put(APKParserCacheTable.TITLE, model.getTitle());

        return values;
    }

    public static APKModel findByCursor(Cursor cursor) {
        cursor.moveToFirst();
        APKModel apkModel = null;
        {
            String filePath = cursor.getString(cursor.getColumnIndex(APKParserCacheTable.FILE_PATH));
            if(TextUtils.isEmpty(filePath))
            {
                return null;
            }
            apkModel = new APKModel();
            apkModel.setPackageName(cursor.getString(cursor.getColumnIndex(APKParserCacheTable.PACKAGENAME)));//包名
            apkModel.setVersion(cursor.getString(cursor.getColumnIndex(APKParserCacheTable.VERSIONNAME)));//版本名
            apkModel.setVersionCode(cursor.getInt(cursor.getColumnIndex(APKParserCacheTable.VERSIONCODE)));//版本号
            apkModel.setPath(filePath);//路径
            apkModel.setSize(cursor.getLong(cursor.getColumnIndex(APKParserCacheTable.SIZE)));//大小
            apkModel.setModifyTime(cursor.getLong(cursor.getColumnIndex(APKParserCacheTable.LAST_MODIFIED)));//修改时间
            apkModel.setTitle(cursor.getString(cursor.getColumnIndex(APKParserCacheTable.TITLE)));//标题
        }
        return apkModel;
    }

}
