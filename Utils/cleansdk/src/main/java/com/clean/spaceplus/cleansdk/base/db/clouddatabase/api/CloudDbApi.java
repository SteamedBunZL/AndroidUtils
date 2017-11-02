package com.clean.spaceplus.cleansdk.base.db.clouddatabase.api;

import com.clean.spaceplus.cleansdk.base.db.clouddatabase.bean.UpdateDbBean;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author shunyou.huang
 * @Description: 云端数据库API
 * @date 2016/5/20 14:37
 * @copyright TCL-MIG
 */

public interface CloudDbApi {

    /**
     * 获取数据库升级信息
     * @return
     */
    @GET("libraryUpdate/query")
    Call<UpdateDbBean> getDbUpgradeRecord();
}
