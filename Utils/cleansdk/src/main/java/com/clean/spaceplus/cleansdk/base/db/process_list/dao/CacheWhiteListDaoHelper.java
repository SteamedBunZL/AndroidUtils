package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.process_list.CacheProcessModel;

import java.util.List;


/**
 * @author zeming_liu
 * @Description:垃圾扫描缓存白名单数据表Helper
 * @date 2016/7/23 17:00
 * @copyright TCL-MIG
 */
public class CacheWhiteListDaoHelper {

    private CacheWhiteListDao mCacheWhiteListDao = null;
    private Object mCacheWhiteListLock = new Object();

    public interface CacheWhiteListDaoHelperHolder{
        CacheWhiteListDaoHelper sInstance = new CacheWhiteListDaoHelper();
    }

    public static CacheWhiteListDaoHelper getInstance(){
        return CacheWhiteListDaoHelperHolder.sInstance;
    }
    private CacheWhiteListDaoHelper(){

    }

    private CacheWhiteListDao getCacheWhiteListDAO(){
        if(mCacheWhiteListDao ==null){
            mCacheWhiteListDao = new CacheWhiteListDao();
        }
        return mCacheWhiteListDao;
    }

    /**
     * 垃圾清理缓存加入白名单
     * @param model
     * @return
     */
    public boolean saveCacheWhiteList(CacheProcessModel model)
    {
        synchronized (mCacheWhiteListLock){
            if(model==null|| TextUtils.isEmpty(model.getPkgName()))
            {
                return false;
            }
            CacheWhiteListDao dao = getCacheWhiteListDAO();
            //判断当前数据库表是否存在该白名单
            if(dao.queryExists(model.getPkgName())){
                return true;
            }
            //新增
            return	dao.add(model);
        }

    }

    /**
     * 根据包名+路径删除白名单
     * @param pkgname
     * @return
     */
    public boolean deleteCacheWhiteList(String pkgname){
        synchronized (mCacheWhiteListLock){
            CacheWhiteListDao dao = getCacheWhiteListDAO();
            //新增
            return	dao.delete(pkgname);
        }
    }

    /**
     * 查询白名单
     * @return
     */
    public List<CacheProcessModel> queryAll(){
        synchronized (mCacheWhiteListLock){
            CacheWhiteListDao dao = getCacheWhiteListDAO();
            return dao.queryAll();
        }
    }

}
