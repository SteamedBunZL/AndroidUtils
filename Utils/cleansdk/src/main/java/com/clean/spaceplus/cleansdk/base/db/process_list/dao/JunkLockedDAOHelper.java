package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/5/11 20:33
 * @copyright TCL-MIG
 */
public class JunkLockedDAOHelper {
    private JunkLockedDAO mJunkLockedDAO = null;
    private Object mJunkLockedLock = new Object();

    public interface JunkLockDAOHelperHolder{
        JunkLockedDAOHelper sInstance = new JunkLockedDAOHelper();
    }

    public static final JunkLockedDAOHelper getInstance(){
        return JunkLockDAOHelperHolder.sInstance;
    }

    private JunkLockedDAOHelper(){

    }

    private JunkLockedDAO getJunkLockedDAO(){
        if(mJunkLockedDAO ==null){
            mJunkLockedDAO = new JunkLockedDAO();
        }
        return mJunkLockedDAO;
    }

    public boolean checkLocked(String filePath,boolean checked) {
        synchronized(mJunkLockedLock){
            return getJunkLockedDAO().checkLocked(filePath,checked);
        }
    }

    public boolean checkLocked(int id, boolean checked){
        synchronized (mJunkLockedLock){
            return getJunkLockedDAO().checkLocked(id, checked);
        }
    }
}
