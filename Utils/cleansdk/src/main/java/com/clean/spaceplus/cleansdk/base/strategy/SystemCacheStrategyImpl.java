package com.clean.spaceplus.cleansdk.base.strategy;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.service.SystemCacheService;

/**
 * @author liangni
 * @Description:系统缓存策略加载
 * @date 2016/6/28 13:44
 * @copyright TCL-MIG
 */

public class SystemCacheStrategyImpl extends BaseStrategy{

    @Override
    public void netquery(BaseStrategy bs) {
        SystemCacheService.startPreloadSysCache(SpaceApplication.getInstance().getContext(), bs);
    }

    @Override
    public void success() {
    }

    @Override
    public void fail() {
    }
}
