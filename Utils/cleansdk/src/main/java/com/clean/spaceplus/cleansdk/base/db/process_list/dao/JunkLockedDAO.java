package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

import android.database.Cursor;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.process_list.JunkLockedModel;
import com.clean.spaceplus.cleansdk.base.db.process_list.JunkLockedTable;
import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessListProvider;
import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengtao.kuang
 * @Description: 锁定垃圾选项DAO
 * @date 2016/5/11 19:33
 * @copyright TCL-MIG
 */
public class JunkLockedDAO {

    public static final String TAG = JunkLockedDAO.class.getSimpleName();
    private volatile boolean mLoaded = false;
    private volatile ConcurrentHashMap<String, JunkLockedModel> mCheckedListByPath = new ConcurrentHashMap<String, JunkLockedModel>();
    private volatile ConcurrentHashMap<Integer, JunkLockedModel> mCheckedListById = new ConcurrentHashMap<Integer, JunkLockedModel>();

    private ProcessListProvider mProvider;
    JunkLockedDAO(){
        mProvider = ProcessListProvider.getInstance(SpaceApplication.getInstance().getContext());
    }

    private boolean checkLocked(JunkLockedModel model,boolean checked) {
        //不存在于数据库中 则以勾选状态为准     默认勾选  ->不锁定    默认不勾选->锁定
        if (model == null) {
            return !checked;
        }

        if (JunkLockedModel.STATUS_LOCKED == model.getStatus()) {
            return true;
        }
        return false;
    }

    private List<JunkLockedModel> parseCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        List<JunkLockedModel> modelList = new ArrayList<JunkLockedModel>();
        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndex(JunkLockedTable.ID));
                int type = cursor.getInt(cursor.getColumnIndex(JunkLockedTable.TYPE));
                String filePath = cursor.getString(cursor.getColumnIndex(JunkLockedTable.FILE_PATH));
                int status = cursor.getInt(cursor.getColumnIndex(JunkLockedTable.STATUS));
                JunkLockedModel model = new JunkLockedModel(type);
                model.setId(id);
                model.setFilePath(filePath);
                model.setStatus(status);
                modelList.add(model);
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            IOUtils.closeSilently(cursor);
        }
        return modelList;
    }

    private void loadAllRecords() {
        String sql = String.format("select * from %s"
                , JunkLockedTable.TABLE_NAME);
        Cursor cursor = null;
        try {
            cursor = mProvider.rawQuery(sql, null);
            List<JunkLockedModel> tmpCheckedList = parseCursor(cursor);
            if (null != tmpCheckedList) {
                for (JunkLockedModel tmpModel : tmpCheckedList) {
                    String filePath = tmpModel.getFilePath();
                    if (null != filePath) {
                        mCheckedListByPath.put(filePath, tmpModel);
                    }
                    mCheckedListById.put(tmpModel.getId(), tmpModel);
                }
            } else {
                NLog.w(TAG, "findAll() return null!! Check if database or provider error.");
            }

            mLoaded = true;
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            IOUtils.closeSilently(cursor);
        }
    }

    private JunkLockedModel queryByCache(String filePath) {
        if (null == filePath || TextUtils.isEmpty(filePath)) {
            return null;
        }
        return mCheckedListByPath.get(filePath);
    }

    private JunkLockedModel queryByCache(int id) {
        if (id > 0 || id == JunkLockedModel.ID_ALL_SYS_CACHE) {
            return mCheckedListById.get(Integer.valueOf(id));
        }
        return null;
    }

    public boolean checkLocked(String filePath,boolean checked) {
        synchronized (this) {
            if (!mLoaded) {
                loadAllRecords();
            }
        }
        JunkLockedModel model = queryByCache(filePath);
        return checkLocked(model,checked);
    }

    public boolean checkLocked(int id,boolean checked) {
        synchronized (this) {
            if (!mLoaded) {
                loadAllRecords();
            }
        }
        JunkLockedModel model = queryByCache(id);
        return checkLocked(model,checked);
    }
}
