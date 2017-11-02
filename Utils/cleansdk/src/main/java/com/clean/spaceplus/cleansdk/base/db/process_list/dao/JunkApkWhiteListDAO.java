package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessListProvider;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

/**
 * @author zengtao.kuang
 * @Description: JunkApk白名单DAO
 * @date 2016/5/11 17:28
 * @copyright TCL-MIG
 */
public class JunkApkWhiteListDAO extends WhiteListDAO{
    private ProcessListProvider mProvider;
    JunkApkWhiteListDAO(){
        mProvider = ProcessListProvider.getInstance(SpaceApplication.getInstance().getContext());
    }

    @Override
    public ProcessListProvider getProvider() {
        return mProvider;
    }
}
