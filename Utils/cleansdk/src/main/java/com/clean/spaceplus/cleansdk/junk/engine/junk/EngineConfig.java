package com.clean.spaceplus.cleansdk.junk.engine.junk;

import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;

import java.util.List;

/**
 * @author liangni
 * @Description:引擎配置接口
 * @date 2016/4/22 10:58
 * @copyright TCL-MIG
 */
public interface EngineConfig {

     int ENG_CFG_NAME_VALID_CACHE_DATA_TIME = 1;		///< 缓存数据的有效时长(单位ms)

     int ENG_CFG_ID_PIC_RECYCLE_SWTICH = 3;		    ///< 回收站引擎是否工作
     int ENG_CFG_ID_PIC_RECYCLE_SIZE = 4;		    ///< 云端控制的回收文件大小
     int ENG_CFG_ID_PIC_CLEAN_MODE = 5;		        ///< 清理类型
     int ENG_CFG_ID_PIC_CLEAN_FOLDER_EXT = 6;	    ///< 缓存文件的文件夹名称
     int ENG_CFG_ID_REST_CLEAN_ITEM_LIST = 7;	    ///< 获取未被清理条目
     int ENG_CFG_ID_SET_RECYCLE_ITEM_LIST = 8;	    ///< 获取待回收的对象

     void setCfgLong(int nCfgId, long lCfgValue);

     long getCfgLong(int nCfgId, long lDefValue);

     int getCfgInt(int nCfgId, int nDefault);

     String getCfgString(int nCfgId, String defaultStringValue);

     List<BaseJunkBean> getRestCleanList(int nCfgId);

    void setCfgList(int nCfgId, List<String> list);
}
