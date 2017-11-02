package com.clean.spaceplus.cleansdk.base.db.clouddatabase.api;

import com.clean.spaceplus.cleansdk.setting.authorization.bean.AuthorizationResponseBean;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author chaohao.zhou
 * @Description: 功能授权Api
 * @date 2017/7/11 15:47
 * @copyright TCL-MIG
 */
public interface AuthorizationApi {

    /**
     * 获取功能授权信息
     * @return
     */
    @GET("api/v1/verifySdk")
    Call<AuthorizationResponseBean> getAuthorization();
}
