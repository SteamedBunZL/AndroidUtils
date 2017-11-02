package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.process_list.CacheProcessModel;
import com.clean.spaceplus.cleansdk.base.db.process_list.CacheWhiteListTable;
import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessListProvider;
import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zeming_liu
 * @Description:垃圾扫描缓存白名单DAO
 * @date 2016/7/23 17:00
 * @copyright TCL-MIG
 */
public class CacheWhiteListDao{

    private ProcessListProvider mProvider;
    CacheWhiteListDao(){
        mProvider = ProcessListProvider.getInstance(SpaceApplication.getInstance().getContext());
    }

    /***
     * 新增
     *
     * @param model
     * @return
     */
    protected boolean add(CacheProcessModel model) {
        if (model == null || TextUtils.isEmpty(model.getPkgName())) {
            return false;
        }
        long ret = mProvider.insert(CacheWhiteListTable.TABLE_NAME
                , null
                , getContentValues(model));
        return ret>0;
    }

    /**
     * 根据包名删除
     * @param pkgname
     * @return
     */
    protected  boolean delete(String pkgname){
        if(TextUtils.isEmpty(pkgname)){
            return false;
        }
        int result=mProvider.delete(CacheWhiteListTable.TABLE_NAME,CacheWhiteListTable.PROCESS_NAME + "=?", new String[]{pkgname});
        return result>0;
    }

    private ContentValues getContentValues(CacheProcessModel processModel){
        int check=0;
        if(processModel.isChecked()){
            check=1;
        }
        ContentValues values = new ContentValues();
        values.put(CacheWhiteListTable.PROCESS_NAME, processModel.getPkgName());
        values.put(CacheWhiteListTable.TITLE, processModel.getTitle());
        values.put(CacheWhiteListTable.CHECKED, check);
        return values;
    }

    public boolean queryExists(String strPackageName){
        boolean result = false;
        if (null == mProvider) {
            return result;
        }

        Cursor cursor = null;
        try {
            do {
                String sql = String.format("select %s from %s where %s = ?"
                        , CacheWhiteListTable.PROCESS_NAME
                        ,CacheWhiteListTable.TABLE_NAME
                        , CacheWhiteListTable.PROCESS_NAME);
                cursor = mProvider.rawQuery(sql,new String[]{String.valueOf(strPackageName)});

                if(cursor == null || cursor.getCount()==0)
                {
                    if(cursor!=null && !cursor.isClosed())
                    {
                        cursor.close();
                        cursor = null;
                    }
                    result = false;
                    break;
                }
                cursor.close();
                cursor = null;
                result = true;
            } while (false);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception | Error e) {
                    NLog.printStackTrace(e);
                }
                cursor = null;
            }
        }

        return result;
    }

    public List<CacheProcessModel> queryAll(){
        String sql = String.format("select * from %s"
                , CacheWhiteListTable.TABLE_NAME);
        Cursor cursor = null;
        try {
            cursor = mProvider.rawQuery(sql, null);
            return parseCursor(cursor);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            IOUtils.closeSilently(cursor);
        }
        return null;
    }

    private List<CacheProcessModel> parseCursor(Cursor cursor){
        if(cursor==null){
            return null;
        }
        List<CacheProcessModel> modelList = new ArrayList<CacheProcessModel>();
        while (cursor.moveToNext()){
            CacheProcessModel model = new CacheProcessModel();
            int idx = cursor.getColumnIndex(CacheWhiteListTable.ID);
            if(idx > -1){
                model.setId(cursor.getInt(idx));
            }
            idx = cursor.getColumnIndex(CacheWhiteListTable.PROCESS_NAME);
            if(idx > -1){
                model.setPkgName(cursor.getString(idx));
            }
            idx = cursor.getColumnIndex(CacheWhiteListTable.TITLE);
            if(idx > -1){
                model.setTitle(cursor.getString(idx));
            }
            idx = cursor.getColumnIndex(CacheWhiteListTable.CHECKED);
            if(idx>-1){
                model.setId(cursor.getLong(idx));
            }
            modelList.add(model);
        }
        cursor.close();
        return modelList;
    }
}
