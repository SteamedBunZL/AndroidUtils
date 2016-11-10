package com.tcl.security.virusengine;

import com.intel.security.vsm.ScanObserver;
import com.intel.security.vsm.ScanResult;
import com.intel.security.vsm.Threat;
import com.intel.security.vsm.UpdateObserver;

/**
 * Constant Values
 *
 * Created by Steve on 2016/6/29.
 */
public class Constants {

    /**ScanInfo 字段解释*/
    public static class ScanInfo{
        //ScanInfo.fileType
        /**全盘扫描文件*/
        public static final int FILE_TYPE_NORMAL = 0;
        /**快扫已安装APK文件*/
        public static final int FILE_TYPE_APK = 1;

        //ScanInfo.state
        public static final int CATEGORY_CLEAN = ScanResult.CATEGORY_CLEAN;
        public static final int CATEGORY_RISKY = ScanResult.CATEGORY_RISKY;
        public static final int CATEGORY_UNSPECIFIED = ScanResult.CATEGORY_UNSPECIFIED;

        //ScanInfo.riskLevel
        public static final int RISK_HIGH = Threat.RISK_HIGH;
        public static final int RISK_MEDIUM = Threat.RISK_MEDIUM;
        public static final int RISK_LOW = Threat.RISK_LOW;
        public static final int NO_RISK = -10001;

        //ScanInfo.riskType
        public static final int DEFAULT_VIRUS_TYPE =-10000;

    }

    /**更新相关 字段解释*/
    public static class Update{

        public static final int RESULT_SUCCEEDED = UpdateObserver.RESULT_SUCCEEDED;

        public static final int RESULT_CANCELED = UpdateObserver.RESULT_CANCELED;

        public static final int RESULT_UPDATE_FAILED_IN_CONNECTED = UpdateObserver.RESULT_UPDATE_FAILED_IN_CONNECTED;

        public static final int RESULT_UPDATE_FAILED_IN_DATDL = UpdateObserver.RESULT_UPDATE_FAILED_IN_DATDL;
    }

    public static class FileScan{

        public static final int RESULT_SUCCEEDED = ScanObserver.RESULT_SUCCEEDED;

        public static final int RESULT_CANCELED = ScanObserver.RESULT_CANCELED;

        public static final int RESULT_FAILED = ScanObserver.RESULT_FAILED;


    }


    public static final int QUERY_FROM_LOCAL_ENGINE = 0;

    public static final int QUERY_FROM_MCAFEE_CLOUD_ENGINE = 1;

    public static final int QUERY_FROM_TCL_CLOUD_ENGINE =2;


    /** tcl云，avengine云扫描结果*/

    public static final int CLOUD_RESULT_ERROR = -1;

    public static final int CLOUD_RESULT_CLEAN = 0;

    public static final int CLOUD_RESULT_RISK = 1;

    public static final int CLOUD_RESULT_UNSPECIFIED = 2;


    /** tcl云查策略，response.from 101 表示这个数据是McAfee临时表*/
    public static final int RESPONSE_TCL_FROM_AVENGINE = 101;

    public static final String DEFAULT_SCAN_RESULT_CACHE_TIME = "86400000";

    /**隐私扫描数据库中type*/
    public static final int PRIVACY_TYPE_HISTORY = 1;
    public static final int PRIVACY_TYPE_SEARCH = 2;
    public static final int PRIVACY_TYPE_CLIP = 3;

    /**扫描过滤的白名单*/
    public static final String PACKAGENAME_NOTIFYBOX = "com.hawk.notifybox";
    public static final String PACKAGENAME_APPLOCK = "com.hawk.applock.privacy";
    public static final String PACKGENAME_SECURITY = "com.hawk.security";


    /**apk数据上报时，ResultFrom表示的结果来自McAfee本地引擎扫描还是McAfee云查结果*/
    public static final int DATA_REPORT_RESULT_FROM_UNSPECIFY = -1;
    public static final int DATA_REPORT_RESULT_FROM_LOCAL = 0;
    public static final int DATA_REPORT_RESULT_FROM_CLOUD = 1;






}
