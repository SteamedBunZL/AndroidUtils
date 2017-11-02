package com.clean.spaceplus.cleansdk.junk.cleancloud;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.setting.history.bean.HistoryAddInfoBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/22 15:57
 * @copyright TCL-MIG
 */
public class CleanCloudResultReporter {
    /**
     * 保存清理完成记录，用于记录上报
     */
    private static Map<String, HistoryAddInfoBean> mAppCleanRecord = new LinkedHashMap<>();
    private static Object mAppCleanRecordLock = new Object();

    /*
	 * 功能Id类型
	 */
    public static class FunctionType {
        public static final byte RESIDUAL_SCAN = 1;///< 残留
        public static final byte CACHE_SCAN    = 2;///< 缓存
        public static final byte ROOT_CACHE_SCAN = 3;///< Root缓存
        /**
         * 新残留上报
         */
        public static final byte RESIDUAL_SCAN_2 = 11;///< 残留2
        public static final byte RESIDUAL_SCAN_FOREGROUND = 21;///< 残留前台扫描检出上报
        public static final byte RESIDUAL_SCAN_TEST_SIGN = 31;///< 残留测试特征检出上报
        /**
         * 新缓存扫描上报
         */
        public static final byte CACHE_SCAN_2 = 12;
        public static final byte CACHE_SCAN_FOREGROUND = 22;///< 缓存前台扫描检出上报
        public static final byte CACHE_SCAN_TEST_SIGN  = 32;///< 缓存前台扫描检出上报
    }

    public static class IS_FIRST_TYPE {
        public static final byte ALL_SCAN = 3;
        public static final byte PRIVACY_SCAN = 4;
        public static final byte STD_FIRST_CLEANED = 11;
        public static final byte STD_NOT_FIRST_CLEANED = 10;
        public static final byte ADV_FIRST_CLEANED = 21;
        public static final byte ADV_NOT_FIRST_CLEANED = 20;
        public static final byte OTHER_SCAN = 127;
    }

    /*
     * 上报数据
     */
    public static class ResultData implements Cloneable {
        public byte mFunctionId;  ///< 功能Id(如：云残留、云缓存等)
        public int  mSignId;      ///< 特征Id
        public byte mCleanType;   ///< 清理类型
        public boolean mIsCleaned;///< 是否已清理
        public int mFileCount;    ///< 清理文件个数
        public int mFileSize;     ///< 清理文件总大小
        public byte mSignSource;  ///< 特征来源,(1:云端, 2:高频库, 3:本地缓存库)
        public byte mHaveNotCleaned;///< 是否是首扫
        public byte mIsTest;      ///< 是否是测试特征



        @Override
        public Object clone() {
            ResultData obj = null;
            try {
                obj = (ResultData)super.clone();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        }

        public static ResultData getHeadGuardObject(byte functionId,
                                                    byte cleanType,
                                                    int itemCount,
                                                    long time,
                                                    byte haveNotCleaned) {
            ResultData resultData = new ResultData();
            resultData.mSignId = -1;//头部填充对象
            resultData.mFunctionId = functionId;
            resultData.mCleanType = cleanType;
            resultData.mFileSize = itemCount;///< 记录元素总个数
            resultData.mFileCount = (int)(time/1000);///< 记录时间戳
            resultData.mHaveNotCleaned = haveNotCleaned;
            return resultData;
        }

        public static ResultData getTailGuardObject(ResultData headGuard) {
            ResultData resultData = (ResultData)headGuard.clone();
            resultData.mSignId = -2;//尾部填充对象
            return resultData;
        }

        public static ResultData getTotalSizeHeadGuardObject(byte functionId,
                                                             byte cleanType,
                                                             int totalSize,
                                                             int totalCount,
                                                             byte haveNotCleaned,
                                                             boolean isForegroundScan) {
            ResultData resultData = new ResultData();
            resultData.mSignId = -3;//头部填充对象
            resultData.mFunctionId = functionId;
            resultData.mCleanType = cleanType;///< 扫描类型
            resultData.mFileSize = totalSize;///< 检出的总大小,以KB为单位
            resultData.mFileCount = totalCount;///< 检出的总文件个数
            resultData.mHaveNotCleaned = haveNotCleaned;
            resultData.mSignSource = (byte)(isForegroundScan ? 1 : 0);///< 是否是前台扫描
            return resultData;
        }

    }

    /*
     * 设置查询的语言,如果不设置默认使用英文:"en"
     * @param language 语言字符串,如cn ,en, zh-cn,zh-tw等
     */
    public boolean setLanguage(String language){return true;};

    /*
     * 上报数据,现在就是简单的同步上报,由外部进行异步处理
     * @param datas 上报数据列表
     */
    public boolean report(Collection<ResultData> datas){return true;};

    /**
     * 保存应用清理完成记录
     */
    public static void saveAppCleanRecord(String appName, String pkgName, long size){
        if(TextUtils.isEmpty(pkgName)){
            return;
        }

        HistoryAddInfoBean historyInf = new HistoryAddInfoBean();

        synchronized (mAppCleanRecordLock){
            if(mAppCleanRecord == null){
                mAppCleanRecord = new LinkedHashMap<>();
            }

            if(mAppCleanRecord.containsKey(pkgName)){
                historyInf = mAppCleanRecord.get(pkgName);
            }

            if(historyInf == null){
                historyInf = new HistoryAddInfoBean();
            }
            
            historyInf.cleanName = appName;
            historyInf.packageName = pkgName;
            historyInf.mCleanByteSize = historyInf.mCleanByteSize + size;
            historyInf.cleanSize = ((double) historyInf.mCleanByteSize) / 1024 / 1024;//单位：m
        }

        mAppCleanRecord.put(pkgName, historyInf);
    }

    /**
     * 获取应用清理记录
     */
    public static List<HistoryAddInfoBean> getAppCleanRecord(){
        List<HistoryAddInfoBean> infos = new ArrayList<>();

        if(mAppCleanRecord != null){
            infos.addAll(mAppCleanRecord.values());
        }

        return infos;
    }

    public static void cleanRecord(){
        if(mAppCleanRecord != null){
            mAppCleanRecord.clear();
        }
    }
}
