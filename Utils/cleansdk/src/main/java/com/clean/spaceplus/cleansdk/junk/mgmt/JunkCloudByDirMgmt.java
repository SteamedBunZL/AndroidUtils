package com.clean.spaceplus.cleansdk.junk.mgmt;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.cleancloud.residual.cloud.KResidualCloudQueryHelper;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.core.residual.dir.KResidualNetWorkDirQuery;
import space.network.util.hash.KQueryMd5Util;

/**
 * @author Jerry
 * @Description:按路径查询残留的业务处理类
 * @date 2016/4/19 16:55
 * @copyright TCL-MIG
 */
public class JunkCloudByDirMgmt extends BaseCloudMgmt {
//    public static String TAG = AppMgrMgmt.class.getSimpleName();
    public static JunkCloudByDirMgmt newInstance(){
        return new JunkCloudByDirMgmt();
    }


    /**************************************按目录查询残留开始****************************************************/


    /**
     * 为了不改变猎豹的代码结构 这里传入的参数为做了处理后的参数 不是最原始的List<String>dirs
     * @param datas
     * @return
     * @throws Exception
     */
    public boolean getResidualByDirName(Collection<KResidualCloudQuery.DirQueryData> datas, KResidualCloudQuery.DirQueryCallback callback) throws Exception{
        KResidualNetWorkDirQuery mResidualNetWorkQuery = new KResidualNetWorkDirQuery(SpaceApplication.getInstance().getContext(),getQueryUrls());
        mResidualNetWorkQuery.setConfigData(getConfigData());
        boolean query = mResidualNetWorkQuery.query(datas, callback);
        return query;
    }




    /**
     * 根据路径查询残留信息
     * @param dirnames
     * @return
     * @throws Exception
     */
    public Collection<KResidualCloudQuery.DirQueryData> getResidualByDirName(Collection<String> dirnames) throws Exception{
        KResidualNetWorkDirQuery mResidualNetWorkQuery = new KResidualNetWorkDirQuery(SpaceApplication.getInstance().getContext(),getQueryUrls());
        mResidualNetWorkQuery.setConfigData(getConfigData());
        Collection<KResidualCloudQuery.DirQueryData> querydatas = getResidualParamsByDirQuery(dirnames);
        mResidualNetWorkQuery.query(querydatas, null);
        return querydatas;
    }

    /**
     * 封装按目录查询残留的输入参数
     * @param dirnames
     * @return
     */
    private Collection<KResidualCloudQuery.DirQueryData> getResidualParamsByDirQuery(Collection<String> dirnames) {
        MessageDigest md = KQueryMd5Util.getMd5Digest();
        if (null == md){
            return null;
        }
        ArrayList<KResidualCloudQuery.DirQueryData> result = new ArrayList<>(dirnames.size());
        for (String dirname : dirnames) {
            KResidualCloudQuery.DirQueryData data = KResidualCloudQueryHelper.getDirQueryDatas(md, dirname, "en");
            result.add(data);
        }
        return result;
    }
    /**************************************按目录查询残留结束****************************************************/


    @Override
    public String getAction() {
        return "directoryResidual/query";
    }


    @Override
    public String[] getQueryUrls() {
     /*   if (useCleanMasterServer){
            return KResidualDef.DIR_QUERY_URLS;
        }*/
        /*if (true){
            return new String[]{"http://10.115.10.118:8080/cleanportal-server/directoryResidual/query"};
        }*/
        return super.getQueryUrls();
    }
}
