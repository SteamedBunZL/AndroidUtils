package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.local.regular;

import java.util.ArrayList;
import java.util.Collection;

import space.network.cleancloud.CleanCloudDef;
import space.network.cleancloud.KResidualCloudQuery;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/21 13:42
 * @copyright TCL-MIG
 */
public interface KResidualRegularCloudQuery {

    class RegularDirQueryInnerData {
        public String mDirName;       // 明文路径
        public String mRegularGroup;  // mDirName内容当中，正则匹配的部分
        public boolean misDetect;     ///< 是否检出,由于这个版本有部分特征不开放给上层,只用于测试特征,所以内部也加个检出标记

        public ArrayList<String> mOriFilterSubDirs;
        public ArrayList<KResidualCloudQuery.FilterDirData> mFilterSubDirDatas;
    }

    /*
     * 初始化,目前没有判断是否重复初始化
     */
    boolean initialize();

    /*
     * 反初始化,调用反初始化让内部维护的线程退出，如果有没有完成的查询也会放弃
     */
    void unInitialize();

    /*
 * 设置查询的语言,如果不设置默认使用英文:"en"
 * @param language 语言字符串,如cn ,en, zh-cn,zh-tw等
 */
    boolean setLanguage(String language);

    /*
     * 获取设置查询的语言
     * @return 返回语言字符串
     */
    String getLanguage();

    /*
     * 设置包信息获取接口
     */
    boolean setPackageChecker(KResidualCloudQuery.PackageChecker packageChecker);

    /*
     * 设置sd卡根路径
     * @param path sd卡根路径
     */
    boolean setSdCardRootPath(String path);


    /*
     * 获取设置进去的sd卡根路径
     * @return 返回sd卡根路径
     */
    String getSdCardRootPath();

    /*
     * 清除枚举目录的缓存
     */
    void cleanPathEnumCache();

    /*
     * 通过目录查询安装包信息,
     * 如果本地查询(高频库和缓存)有结果但是语言信息不符合,那么还是不进行网络查询，
     * 这种情况下如果最后通过安装包名特征查询后有检出，最后再用强制网络查询一次来获得正确的语言描诉(这样是为了最大限度的减少网络查询)
     * @param scanType 扫描类型,见DirCleanType
     * @param dirnames 目录查询数据列表，详细说明见DirQueryData
     * @param callback 回调接口，详细说明见IDirQueryCallback
     * @param pureAsync 是否纯异步，如果为false，查询本地数据库和缓存库部分同步，查询网络异步，所有的回调都异步
     *                  如果为true,全部都是异步
     * @param forceNetQuery 是否强制用网络扫描,如果为true，不利用本地缓存扫描
     */
    boolean queryByDirName(
            int scanType,
            Collection<String> dirnames,
            KResidualCloudQuery.DirQueryCallback callback,
            boolean pureAsync,
            boolean asyncCallback);

    /*
     * 如果有未完成的查询，那么丢弃
     */
    void discardAllQuery();

    /*
     * 等待扫描结束，注意不要在回调线程中调用注意不要在回调线程中调用
     * @param timeout 等待的超时时间
     * @param discardQueryIfTimeout 等待如果超时是否丢弃所有未完成的查询
     * @return 等待结果，结果含义见CleanCloudDef.WaitResultType中的详细说明
     */
    int waitForComplete(long timeout, boolean discardQueryIfTimeout, CleanCloudDef.ScanTaskCtrl ctrl);
}
