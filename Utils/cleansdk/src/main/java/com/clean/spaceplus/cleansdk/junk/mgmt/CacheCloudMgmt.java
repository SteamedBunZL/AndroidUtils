package com.clean.spaceplus.cleansdk.junk.mgmt;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;

import space.network.cleancloud.KCacheCloudQuery;
import space.network.cleancloud.core.cache.KCacheCommonData;
import space.network.cleancloud.core.cache.KCacheNetWorkPkgQuery;
import space.network.util.compress.EnDeCodeUtils;
import space.network.util.hash.KQueryMd5Util;

/**
 * @author Jerry
 * @Description:处理缓存的接口类
 * @date 2016/4/29 19:00
 * @copyright TCL-MIG
 */
public class CacheCloudMgmt extends BaseCloudMgmt {

    public static String TAG = CacheCloudMgmt.class.getSimpleName();
    public static CacheCloudMgmt newInstance(){
        return new CacheCloudMgmt();
    }

    /**************************************按包查询缓存开始****************************************************/
    /**
     * 根据包查询缓存 为了不改变猎豹的代码结构 这里传入的参数为做了处理后的参数 不是最原始的List<String>pkgs
     * @return
     * @throws Exception
     */
    public boolean getCacheByPkgName(Collection<KCacheCloudQuery.PkgQueryData> datas, KCacheCloudQuery.PkgQueryCallback callback)throws Exception{
        KCacheNetWorkPkgQuery mCacheNetQuery = new KCacheNetWorkPkgQuery(SpaceApplication.getInstance().getContext(), getQueryUrls());
        mCacheNetQuery.setConfigData(getConfigData());
        boolean query = mCacheNetQuery.query(datas, callback);
        if(!query){
            for (KCacheCloudQuery.PkgQueryData pkgQueryData : datas) {
                if(pkgQueryData.mErrorCode == 0){
                    pkgQueryData.mErrorCode = -1;
                }
            }
        }
        return query;
    }

    /**
     * 根据包查询缓存 为了不改变之前代码结构 不采用这种方式
     * @param pkgnames
     * @return
     * @throws Exception
     */
    public Collection<KCacheCloudQuery.PkgQueryData> getCacheByPkgName(ArrayList<String> pkgnames)throws Exception{
        KCacheNetWorkPkgQuery mCacheNetQuery = new KCacheNetWorkPkgQuery(SpaceApplication.getInstance().getContext(), getQueryUrls());
        mCacheNetQuery.setConfigData(getConfigData());
        Collection<KCacheCloudQuery.PkgQueryData> querydatas = getCacheParamsByPkgQuery(pkgnames);
        mCacheNetQuery.query(querydatas, null);
        return querydatas;
    }
    /**************************************按包查询缓存结束****************************************************/



    /**
     * 封装按包查询缓存的输入参数
     * @param pkgnames
     * @return
     */
    protected Collection<KCacheCloudQuery.PkgQueryData> getCacheParamsByPkgQuery(ArrayList<String> pkgnames) {
        ArrayList<KCacheCloudQuery.PkgQueryParam> pkgQueryParams = new ArrayList<>();
        for (String pkgName: pkgnames){
            KCacheCloudQuery.PkgQueryParam pkg1 = new KCacheCloudQuery.PkgQueryParam();
            pkg1.mPkgName = pkgName;
            pkg1.mCleanType = 0;  //传0就是扫描所有,也可以传CleanType里面的对应值
            pkgQueryParams.add(pkg1);
        }
        Collection<KCacheCloudQuery.PkgQueryData> querydatas = new ArrayList<>(pkgnames.size());
        MessageDigest md5 = KQueryMd5Util.getMd5Digest();
        for (KCacheCloudQuery.PkgQueryParam param : pkgQueryParams) {
            KCacheCloudQuery.PkgQueryData data = new KCacheCloudQuery.PkgQueryData();
            data.mQueryParam = param;
            data.mLanguage = "en";
            data.mResult = new KCacheCloudQuery.PkgQueryResult();

            KCacheCommonData.CachePkgQueryInnerData pkgQueryInnerData = new KCacheCommonData.CachePkgQueryInnerData();
            byte[] md5Bytes = KQueryMd5Util.getPkgQueryMd5Bytes(md5, param.mPkgName);
            pkgQueryInnerData.mPkgNameMd5 = EnDeCodeUtils.byteToHexString(md5Bytes);
            pkgQueryInnerData.mPkgNameMd5High64Bit = KQueryMd5Util.getMD5High64BitFromMD5(md5Bytes);
            data.mInnerData = pkgQueryInnerData;

            querydatas.add(data);
        }
        return querydatas;
    }


    @Override
    public String getAction() {
        return "packageRefer/cache";
    }

    @Override
    public String[] getQueryUrls() {
       /* if (useCleanMasterServer){
            return KCacheDef.CACHE_QUERY_URLS;
        }*/
       /* if (true){
            return new String[]{"http://10.115.10.224:80/cleanportal-server/packageRefer/cache"};
        }*/
        return super.getQueryUrls();
    }
}
