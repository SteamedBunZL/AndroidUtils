package com.clean.spaceplus.cleansdk.base.scan;

import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.junk.RequestConfig;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/27 21:25
 * @copyright TCL-MIG
 */
public class ScanRequest implements JunkRequest {
    private JunkRequest.EM_JUNK_DATA_TYPE mJunkRequestType=EM_JUNK_DATA_TYPE.UNKNOWN;
    private ScanRequestCallback mReqCb = null;
    private RequestConfig mReqCfg = null;

    @Override
    public EM_JUNK_DATA_TYPE getRequestType() {
        return mJunkRequestType;
    }

    @Override
    public RequestCallback getScanCallback() {
        return mReqCb;
    }

    public void setRequestType(EM_JUNK_DATA_TYPE type) {
        mJunkRequestType = type;
    }

    public void setScanCallback(ScanRequestCallback cb) {
        mReqCb = cb;
    }

    @Override
    public void setRequestConfig(RequestConfig cfg) {
        mReqCfg = cfg;
    }

    @Override
    public RequestConfig getRequestConfig() {
        return mReqCfg;
    }
}