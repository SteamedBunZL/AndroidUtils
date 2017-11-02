package com.clean.spaceplus.cleansdk.junk.mgmt;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.hawkclean.framework.log.NLog;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.core.residual.KResidualCommonData;
import space.network.cleancloud.core.residual.pkg.KResidualNetWorkPkgQuery;
import space.network.util.compress.EnDeCodeUtils;
import space.network.util.hash.KQueryMd5Util;

/**
 * @author Jerry
 * @Description: 按包查询残留的业务处理类
 * @date 2016/5/11 20:29
 * @copyright TCL-MIG
 */
public class JunkCloudByPkgMgmt extends BaseCloudMgmt {
//    public static String TAG = AppMgrMgmt.class.getSimpleName();
    public static JunkCloudByPkgMgmt newInstance(){
        return new JunkCloudByPkgMgmt();
    }

    /**************************************按包查询残留开始****************************************************/
    public boolean getResidualByPkgName(Collection<KResidualCloudQuery.PkgQueryData> datas) throws Exception{
        KResidualNetWorkPkgQuery mResidualPkgNetWorkQuery = new KResidualNetWorkPkgQuery(SpaceApplication.getInstance().getContext(),getQueryUrls());
        mResidualPkgNetWorkQuery.setConfigData(getConfigData());
        boolean query = mResidualPkgNetWorkQuery.query(datas, null);
        return query;
    }




    /**
     * 根据包查询残留
     * @param pkgnames
     * @throws Exception
     */
    public Collection<KResidualCloudQuery.PkgQueryData> getResidualByPkgName(ArrayList<String> pkgnames) throws Exception{
        KResidualNetWorkPkgQuery mResidualNetWorkPkgQuery = new KResidualNetWorkPkgQuery(SpaceApplication.getInstance().getContext(), getQueryUrls());
        mResidualNetWorkPkgQuery.setConfigData(getConfigData());
        Collection<KResidualCloudQuery.PkgQueryData> querydatas = getResidualParamsByPkgQuery(pkgnames);
        NLog.i("queryData:",querydatas.toString());
        mResidualNetWorkPkgQuery.query(querydatas, null);
        return querydatas;
    }

    /**
     * 封装按包查询残留的输入参数
     * @param pkgnames
     * @return
     */
    private Collection<KResidualCloudQuery.PkgQueryData> getResidualParamsByPkgQuery(ArrayList<String> pkgnames) {
        MessageDigest md = KQueryMd5Util.getMd5Digest();
        if (null == md){
            return null;
        }
        ArrayList<KResidualCloudQuery.PkgQueryData> result = new ArrayList<>(pkgnames.size());
        for (String pkgname : pkgnames) {
            KResidualCommonData.PkgQueryInnerData innerData = getResidualPkgQueryInnerData(md, pkgname);
            KResidualCloudQuery.PkgQueryData data = new KResidualCloudQuery.PkgQueryData();
            data.mResult 	= new KResidualCloudQuery.PkgQueryResult();
            data.mLanguage 	= "en";
            data.mInnerData = innerData;
            data.mPkgName   = pkgname;
            result.add(data);
        }
        return result;
    }
    private KResidualCommonData.PkgQueryInnerData getResidualPkgQueryInnerData(MessageDigest md, String pkgname) {
        KResidualCommonData.PkgQueryInnerData result = new KResidualCommonData.PkgQueryInnerData();
        byte[] md5Bytes = KQueryMd5Util.getPkgQueryMd5Bytes(md, pkgname);
        result.mPkgNameMd5 = EnDeCodeUtils.byteToHexString(md5Bytes);
        result.mPkgNameMd5High64Bit = KQueryMd5Util.getMD5High64BitFromMD5(md5Bytes);
        return result;
    }





    @Override
    public String getAction() {
        return "packageResidual/query";
    }


    @Override
    public String[] getQueryUrls() {
     /*   if (useCleanMasterServer){
            return KResidualDef.DIR_QUERY_URLS;
        }*/
        /*if (true){
            return new String[]{"http://10.115.10.118:8080/cleanportal-server/packageResidual/query"};
        }*/
        return super.getQueryUrls();
    }
}
