package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;

import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: APKParserCacheDAO辅助类
 * @date 2016/5/12 10:53
 * @copyright TCL-MIG
 */
public class APKParserCacheDAOHelper {

    private APKParserCacheDAO mAPKParserCacheDAO = null;
    private Object mAPKParserCacheLock = new Object();

    public interface APKParseCacheDAOHelperHolder{
        APKParserCacheDAOHelper sInstance = new APKParserCacheDAOHelper();
    }

    public static APKParserCacheDAOHelper getInstance(){
        return APKParseCacheDAOHelperHolder.sInstance;
    }

    private APKParserCacheDAOHelper(){

    }

    private APKParserCacheDAO getAPKParserCacheDAO(){
        if(mAPKParserCacheDAO ==null){
            mAPKParserCacheDAO = new APKParserCacheDAO();
        }
        return mAPKParserCacheDAO;
    }

    public boolean saveCacheApkModel(APKModel apkModel)
    {
        synchronized (mAPKParserCacheLock){
            if(apkModel==null|| TextUtils.isEmpty(apkModel.getPath()))
            {
                return false;
            }
            APKParserCacheDAO dao = getAPKParserCacheDAO();
            APKModel cacheModel =  dao.getAPKModelByFilePath(apkModel.getPath());
            if(cacheModel!=null )
            {
                return	dao.update(apkModel);

            }

            return	dao.add(apkModel);
        }

    }

    public ArrayMap<String, APKModel> getAllApkCache()
    {
        synchronized (mAPKParserCacheLock){
            APKParserCacheDAO dao = getAPKParserCacheDAO();
            return dao.getAllCache();
        }
    }

    //	pubilc boolean
    public boolean isUpdateBlock =false;
    public void updateCahce(ArrayMap<String , APKModel> mCacheArrayMap , List<String> mUpdateList )
    {
        synchronized (mAPKParserCacheLock){
            isUpdateBlock = true;
            try {
                if(mUpdateList == null)
                {
                    return;
                }

                for (String filePath : mUpdateList) {
                    APKModel apkModel =  mCacheArrayMap.get(filePath);
                    if(apkModel!=null)
                    {
                        saveCacheApkModel(apkModel);
                    }
                }
            } catch (Exception e) {

            }finally{
                isUpdateBlock = false;
            }
        }


    }

}
