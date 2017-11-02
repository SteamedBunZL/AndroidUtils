package com.clean.spaceplus.cleansdk.base.db.clouddatabase;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.biz.ABizLogic;
import com.clean.spaceplus.cleansdk.base.db.clouddatabase.api.CloudDbApi;
import com.clean.spaceplus.cleansdk.base.db.clouddatabase.bean.UpdateDbBean;
import com.hawkclean.mig.commonframework.network.BaseHttpUtils;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author shunyou.huang
 * @Description: 云端数据库升级
 * @date 2016/5/20 14:23
 * @copyright TCL-MIG
 */

public class DbUpgradeMgmt extends ABizLogic {

    public static final String TAG = DbUpgradeMgmt.class.getSimpleName();
    public static DbUpgradeMgmt newInstance(){
        return new DbUpgradeMgmt();
    }

    /**
     * 获取数据库升级信息
     * @return
     * @throws Exception
     */
    public UpdateDbBean getUpgradeRecord() throws Exception{
        BaseHttpUtils baseHttpUtils = new BaseHttpUtils(SpaceApplication.getInstance().getContext(), getCleanBaseUrl()[0]);
        baseHttpUtils.setLogLevel(true, HttpLoggingInterceptor.Level.BODY);
        CloudDbApi dbGetApi = baseHttpUtils.getRetrofit().create(CloudDbApi.class);
        Call<UpdateDbBean> call = dbGetApi.getDbUpgradeRecord();
        Response<UpdateDbBean> res = call.execute();
        if (res != null && res.body() != null){
            return checkResult(res.body());
        }
        return null;
    }

//    http://cleanportal-test.tclclouds.com/libraryUpdate/query
//    返回数据为：
/*
  {
  "code": "0",
  "msg": "success",
  "data": [
    {
      "versionName": "junkpath_cache",
      "versionId": "1.0.0",
      "versionType": 1,
      "url": "http://osg-test.tclclouds.com/swift/v1/gl3/spaceplus/db/junkpath_cache_1.0.0.db"
    },
    {
      "versionName": "junkprocess_en",
      "versionId": "1.0.0",
      "versionType": 2,
      "url": "http://osg-test.tclclouds.com/swift/v1/gl3/spaceplus/db/junkprocess_en_1.0.0.filter"
    },
    {
      "versionName": "cache_hf_en",
      "versionId": "1.0.0",
      "versionType": 3,
      "url": "http://osg-test.tclclouds.com/swift/v1/gl3/spaceplus/db/cache_hf_en.1.0.0.db"
    },
    {
      "versionName": "cache",
      "versionId": "1.0.0",
      "versionType": 4,
      "url": "http://osg-test.tclclouds.com/swift/v1/gl3/spaceplus/db/cache.db"
    },
    {
      "versionName": "query_hf_en",
      "versionId": "1.0.0",
      "versionType": 5,
      "url": "http://osg-test.tclclouds.com/swift/v1/gl3/spaceplus/db/query_hf_en_1.0.0.db"
    },
    {
      "versionName": "leftover_cache",
      "versionId": "1.0.0",
      "versionType": 6,
      "url": "http://osg-test.tclclouds.com/swift/v1/gl3/spaceplus/db/leftover_cache.db"
    },
    {
      "versionName": "advdesc_cache",
      "versionId": "1.0.0",
      "versionType": 7,
      "url": "http://osg-test.tclclouds.com/swift/v1/gl3/spaceplus/db/advdesc_cache.db"
    }
  ]
}
*/

}
