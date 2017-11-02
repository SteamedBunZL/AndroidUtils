package com.clean.spaceplus.cleansdk.junk.engine.junk;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/22 11:09
 * @copyright TCL-MIG
 */
public interface JunkRequest {

    /**
     * 请求数据类别
     */
     enum EM_JUNK_DATA_TYPE {
        UNKNOWN,	///< 缺省无效值
        //standard
        SYSCACHE, SYSFIXEDCACHE, SDCACHE, PROCESS, ADVERTISEMENT, TEMPFOLDER,
        APPLEFTOVER, APKFILE, USELESSTHUMBNAIL, ROOTCACHE, SCRSHOTSCOMPRESS,
        //advanced
        SDCACHE_ADV, TEMPFOLDER_ADV, APPLEFTOVER_ADV, MYPHOTO,
        MYAUDIO, CALCFOLDER, BIGFILE, MYVIDEO,
        //offline have to be continue 连续
        VIDEO_OFF, SDCACHE_OFF;
    }

    /**
     * @return 请求类别
     */
     EM_JUNK_DATA_TYPE getRequestType();


    /**
     * @return 请求扫描回调对象
     */
     RequestCallback getScanCallback();

    /**
     * 配置管理
     */
     void setRequestConfig(RequestConfig cfg);

    /**
     * @return 获取配置
     */
     RequestConfig getRequestConfig();


    interface RequestCallback {
        /**
         * 开始扫描当前对象
         * @param request request对象自身
         */
         void onScanBegin(JunkRequest request);

        /**
         * 回传当下掃描项目
         * @param strItemName 掃描项目名
         */
         void onScanningItem(String strItemName);

        /**
         * 回传当下掃到的垃圾大小
         * @param nSize
         * @param bChecked
         */
         void onFoundItemSize(long nSize, boolean bChecked);

        /**
         * 扫描结束
         * @param request request对象自身
         * @param result 扫描结果
         */
         void onScanEnd(JunkRequest request, JunkResult result);
    }
}
