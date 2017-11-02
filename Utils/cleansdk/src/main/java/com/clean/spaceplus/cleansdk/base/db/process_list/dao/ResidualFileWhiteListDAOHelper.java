package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

/**
 * @author zengtao.kuang
 * @Description: ResidualFile白名单DAO辅助类
 * @date 2016/5/11 19:06
 * @copyright TCL-MIG
 */
public class ResidualFileWhiteListDAOHelper {

    private ResidualFileWhiteListDAO mRFWhiteListDao;
    private Object mRFWhiteListLock = new Object();

    public interface ResidualFileWhiteListDAOHelperHolder{
        ResidualFileWhiteListDAOHelper sInstance = new ResidualFileWhiteListDAOHelper();
    }

    public static ResidualFileWhiteListDAOHelper getInstance(){
        return ResidualFileWhiteListDAOHelperHolder.sInstance;
    }

    private ResidualFileWhiteListDAOHelper(){

    }

    private synchronized ResidualFileWhiteListDAO getRFWhiteListDAO(){
        if(mRFWhiteListDao==null){
            mRFWhiteListDao = new ResidualFileWhiteListDAO();
        }
        return mRFWhiteListDao;
    }

    public boolean isRFWhiteListItem(String path) {
        synchronized (mRFWhiteListLock) {
            return getRFWhiteListDAO().queryExists(path);
        }
    }
}
