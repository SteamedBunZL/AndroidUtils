package com.clean.spaceplus.cleansdk.base.clean;

import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest.EM_JUNK_DATA_TYPE;
import com.clean.spaceplus.cleansdk.junk.engine.junk.RequestConfig;

import java.util.List;
import java.util.Map;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 16:21
 * @copyright TCL-MIG
 */
public class CleanRequestImpl implements CleanRequest{
    private static final String TAG = CleanRequest.class.getSimpleName();

    private Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mCleanJunkMap = null;
    private ICleanCallback mCleanCallBack;
    private RequestConfig mRequestConfig;
    private int mCleanType = CLEAN_TYPE_DETAIL;

    public CleanRequestImpl(Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapList, ICleanCallback callBack, int nType) {
        mCleanJunkMap = mapList;
        mCleanCallBack = callBack;
        mCleanType = nType;
    }

    @Override
    public Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> getCleanJunkInfoList() {
        return mCleanJunkMap;
    }

    @Override
    public ICleanCallback getCleanCallback() {
        return mCleanCallBack;
    }

    @Override
    public int getCleanType() {
        return mCleanType;
    }

    @Override
    public void setRequestConfig(RequestConfig cfg) {
        mRequestConfig = cfg;
    }

    @Override
    public RequestConfig getRequestConfig() {
        return mRequestConfig;
    }
}
