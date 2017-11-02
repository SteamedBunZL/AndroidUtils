package com.clean.spaceplus.cleansdk.boost.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessListProvider;
import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessWhiteList;
import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessWhiteListTable;
import com.clean.spaceplus.cleansdk.boost.util.ProcessWhiteListMarkHelper;
import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengtao.kuang
 * @Description: 进程白名单DAO
 * @date 2016/4/19 17:11
 * @copyright TCL-MIG
 */
public class ProcessWhiteListDAO {

    private ProcessListProvider mProvider;

    /**
     * 内存缓存
     */
    private Map<String, ProcessWhiteList> mCache = new HashMap<String, ProcessWhiteList>();
    public ProcessWhiteListDAO(){
        mProvider = ProcessListProvider.getInstance(SpaceApplication.getInstance().getContext());
    }

    public int getProcessWhiteListCount() {
        String sql = String.format("select %s from %s",ProcessWhiteListTable.PKG_NAME,ProcessWhiteListTable.TABLE_NAME);

        int count = 0;
        Cursor cursor = null;
        try{
        cursor = mProvider.rawQuery(sql,null);
        if(cursor == null){
            return 0;
        }
        count = cursor.getCount();
        }catch (Exception e){}

        return count;
    }

    /**
     * 包括原始白名单和用户加入白名单
     */
    public List<ProcessWhiteList> getProcessWhiteList() {
        String sql = "select * from " + ProcessWhiteListTable.TABLE_NAME;
        Cursor cursor = null;
        List<ProcessWhiteList> result = new ArrayList<ProcessWhiteList>();
        try {
            cursor = mProvider.rawQuery(sql, null);
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }
            List<ProcessWhiteList> all = new ArrayList<ProcessWhiteList>();
            while (cursor.moveToNext()) {
                ProcessWhiteList info = new ProcessWhiteList();
                info.mMark = cursor.getInt(cursor.getColumnIndex(ProcessWhiteListTable.MARK));
                info.mPkgname = cursor.getString(cursor.getColumnIndex(ProcessWhiteListTable.PKG_NAME));
                info.mTitle = cursor.getString(cursor.getColumnIndex(ProcessWhiteListTable.TITLE));
                all.add(info);
            }
            for (ProcessWhiteList item : all) {
                if (ProcessWhiteListMarkHelper.isInWhiteList(item.mMark)) {
                    result.add(item);
                }
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            IOUtils.closeSilently(cursor);
        }

        return result;
    }

    public int queryMarkByName(String packageName){
        ProcessWhiteList processWhiteList = mCache.get(packageName);
        if(null!=processWhiteList){
            return processWhiteList.mMark;
        }
        return 0;
    }


    /**
     * 测试过  , 删除
     */
    public void deleteProcessWhiteListItem(String pkgName) {
        mCache.remove(pkgName);
        mProvider.delete(ProcessWhiteListTable.TABLE_NAME,ProcessWhiteListTable.PKG_NAME+" = ?",new String[]{pkgName});
    }

    public void insertOrUpdate(String pkgName, String title, int mark) {
        ProcessWhiteList processWhiteList = new ProcessWhiteList(mark, pkgName, title);
        mCache.put(pkgName, processWhiteList);
        String sql = String.format("select %s from %s where %s = ?", ProcessWhiteListTable.PKG_NAME, ProcessWhiteListTable.TABLE_NAME, ProcessWhiteListTable.PKG_NAME);
        Cursor cursor = null;
        try {
            cursor = mProvider.rawQuery(sql, new String[]{pkgName});
            ContentValues cv = new ContentValues();
            cv.put(ProcessWhiteListTable.MARK, processWhiteList.mMark);
            cv.put(ProcessWhiteListTable.PKG_NAME, processWhiteList.mPkgname);
            cv.put(ProcessWhiteListTable.TITLE, processWhiteList.mTitle);
            if (cursor != null && cursor.getCount() != 0) {
                mProvider.update(ProcessWhiteListTable.TABLE_NAME, cv, ProcessWhiteListTable.PKG_NAME + " = ?", new String[]{pkgName});
            } else {
                mProvider.insert(ProcessWhiteListTable.TABLE_NAME, null, cv);
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            IOUtils.closeSilently(cursor);
        }
    }

    public void loadAllProcessWhiteList() {
        String sql = "select * from " + ProcessWhiteListTable.TABLE_NAME;
        Cursor cursor = null;
        List<ProcessWhiteList> all = new ArrayList<ProcessWhiteList>();
        try {
            cursor = mProvider.rawQuery(sql, null);
            if (cursor == null || cursor.getCount() == 0) {
                return;
            }

            while (cursor.moveToNext()) {
                ProcessWhiteList info = new ProcessWhiteList();
                info.mMark = cursor.getInt(cursor.getColumnIndex(ProcessWhiteListTable.MARK));
                info.mPkgname = cursor.getString(cursor.getColumnIndex(ProcessWhiteListTable.PKG_NAME));
                info.mTitle = cursor.getString(cursor.getColumnIndex(ProcessWhiteListTable.TITLE));
                all.add(info);
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            IOUtils.closeSilently(cursor);
        }

        if(all!=null){
            for(ProcessWhiteList processWhiteList:all){
                mCache.put(processWhiteList.getPkgname(),processWhiteList);
            }
        }

    }

}
