package com.clean.spaceplus.cleansdk.junk.engine.junk;

/**
 * @author liangni
 * @Description:请求扫描配置
 * @date 2016/4/22 11:12
 * @copyright TCL-MIG
 */
public interface RequestConfig {
     int REQ_CFG_ID_SD_CACHE_PKG_NAME = 1;		///< 要请求扫描SD卡缓存的包名
     int REQ_CFG_ID_VIDEO_OFF_NEED_SCAN = 2;		///< 要请求视频扫描
     void setCfgString(int nCfgId, String strCfgValue);
     String getCfgString(int nCfgId, String strDefValue);
     void setCfgBoolean(int nCfgId, boolean bCfgValue);
     boolean getCfgBoolean(int nCfgId, boolean bDefValue);
}
