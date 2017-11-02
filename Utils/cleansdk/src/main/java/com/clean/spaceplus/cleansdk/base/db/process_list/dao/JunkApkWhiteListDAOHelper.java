package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

/**
 * @author zengtao.kuang
 * @Description: JunkApk白名单DAO辅助类
 * @date 2016/5/11 18:41
 * @copyright TCL-MIG
 */
public class JunkApkWhiteListDAOHelper {
    private JunkApkWhiteListDAO mJunkApkWhiteListDao = null;
    private Object mJunkApkWhiteListLock = new Object();

    public interface JunkApkWhiteListDAOHelperHolder{
        JunkApkWhiteListDAOHelper sInstance = new JunkApkWhiteListDAOHelper();
    }

    public static final JunkApkWhiteListDAOHelper getInstance(){
        return JunkApkWhiteListDAOHelperHolder.sInstance;
    }

    private JunkApkWhiteListDAOHelper(){

    }

    private JunkApkWhiteListDAO getJunkApkWhiteListDAO(){
        if(mJunkApkWhiteListDao==null){
            mJunkApkWhiteListDao = new JunkApkWhiteListDAO();
        }
        return mJunkApkWhiteListDao;
    }

    public boolean queryExists(String strPackageName){
        synchronized (mJunkApkWhiteListLock){
            return getJunkApkWhiteListDAO().queryExists(strPackageName);
        }
    }

}
