package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.process_list.JunkApkWhiteListTable;
import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessListProvider;
import com.hawkclean.framework.log.NLog;

/**
 * @author zengtao.kuang
 * @Description:抽象白名单DAO
 * @date 2016/5/11 18:57
 * @copyright TCL-MIG
 */
public abstract class WhiteListDAO {

    public abstract ProcessListProvider getProvider();
    public boolean queryExists(String strPackageName){
        boolean result = false;
        ProcessListProvider provider = getProvider();
        if (null == provider) {
            return result;
        }

        Cursor cursor = null;
        try {
            do {
                String sql = String.format("select %s from %s where %s = ?"
                        , JunkApkWhiteListTable.PROCESS_NAME
                        ,JunkApkWhiteListTable.TABLE_NAME
                        , JunkApkWhiteListTable.PROCESS_NAME);
                cursor = provider.rawQuery(sql,new String[]{String.valueOf(strPackageName)});

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
                } catch (Exception e) {
                    NLog.printStackTrace(e);
                } catch (Error e) {
                    NLog.printStackTrace(e);
                }
                cursor = null;
            }
        }

        return result;
    }
}
