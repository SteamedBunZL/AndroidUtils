
package space.network.cleancloud;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
 * 残留云端查询接口
 */
public interface KResidualCloudQuery {
    /*
     * 目录查询结果类型
     */
    class DirResultType {
        public static final int UNKNOWN   = 0;///< 无效参数
        public static final int NOT_FOUND = 1;///< 类型不存在，
        public static final int PKG_LIST  = 2;///< 包列表
        public static final int DIR_LIST  = 3;///< 目录列表
        public static final int SIGN_IGNORE= 4;///< 该目录忽略
        public static final int DIR_QUERY_LIST= 5;///< 要进行遍历查询的目录列表
    }

    /*
     * 目录查询结果类型
     */
    class PkgResultType {
        public static final int UNKNOWN   = 0;///< 无效参数
        public static final int NOT_FOUND = 1;///< 类型不存在，
        public static final int DIR_LIST  = 3;///< 目录列表
    }

    class DirScanType {
        public static final int DIR_INVAILD_SCAN   = 0;///< 无效类型
        public static final int DIR_STANDARD_SCAN  = 1;///< 建议扫描
        public static final int DIR_ADVANVCED_SCAN = 2;///< 深度扫描
        public static final int DIR_ALL_SCAN 	   = 3;///< 所描所有(空间不足的扫描入口)
    }

    /*
     * 目录清理类型
     */
    class DirCleanType {
        public static final int INVAILD   = 0;///< 无效参数
        public static final int SUGGESTED = 1;///< 建议清理
        public static final int CAREFUL   = 2;///< 谨慎清理
        public static final int SUGGESTED_WITH_FILTER = 3;///< 带过滤列表的建议清理,过滤列表在DirQueryResult中的mFilter
        public static final int CAREFUL_WITH_FILTER = 4;///< 带过滤列表的慎重清理,过滤列表在DirQueryResult中的mFilter
        public static final int PURE_WHITE_FILTER = 5;///< 纯白名单
    }



    /*
     * 结果查询来源类型,查询结果可以来自云端，本地高频库或者是云端结果缓存
     */
    class ResultSourceType {
        public static final int INVAILD   = 0;///< 无效参数
        public static final int CLOUD     = 1;///< 云端结果
        public static final int HFREQ     = 2;///< 本地高频库结果
        public static final int CACHE     = 3;///< 本地缓存结果
        public static final int LOCAL_RULE = 4;//< 本地规则检出结果
    }

    /*
    * 内容分类
    */
    class ContentType {
        /**未知/其他 */
        public static final int CONTENT_TYPE_UNKNOWN = 0;

        /**图片 */
        public static final int CONTENT_TYPE_PICTURE = 1;

        /**音频 */
        public static final int CONTENT_TYPE_AUDIO = 2;

        /**视频 */
        public static final int CONTENT_TYPE_VIDEO  = 3;

        /**备份 */
        public static final int CONTENT_TYPE_BACKUP = 4;

        /**加密 */
        public static final int CONTENT_TYPE_ENCRYPTION = 5;

        /**文档 */
        public static final int CONTENT_TYPE_DOC = 6;

        /**下载 */
        public static final int ONTENT_TYPE_DOWNLOAD = 7;

        /**图片+回收站 */
        public static final int CONTENT_TYPE_PICTURES_AND_TRASH = 8;

        /**下载管理 */
        public static final int CONTENT_TYPE_DOWNLOAD_MANAGE = 9;
    }

    /**
     * 本地规则分类
     * PS : 本地规则只能从-10向下递减;
     * */
    class LocalRuleType {
        public static final int ANDROID_DATA = -10;
        public static final int ANDROID_OBB = -11;
    }


    class FilterDirData {
        public int mSingId;
        public String mPath;
        public int mCleanType;         ///< 清除类型,见DirCleanType

        @Override
        public String toString() {
            return "FilterDirData{" +
                    "mSingId=" + mSingId +
                    ", mPath='" + mPath + '\'' +
                    ", mCleanType=" + mCleanType +
                    '}';
        }
    }

    /**
     * 文件检测数据
     */
    class FileCheckerData {
        public int[] globalSuffixCatIds;        // 全局过滤后缀名类别id数组
        public Set<String> whiteSuffixFilter;   // 过滤后缀名白名单（不可删除）
        public Set<String> blackSuffixFilter;   // 过滤后缀名黑名单（可删除）

        /**
         * 从json字符串中解析 FileCheckerData 对象
         * @param jsonStr   json字符串
         * @return          成功时返回 FileCheckerData对象，失败时返回null
         */
        public static FileCheckerData parseFromJsonString(String jsonStr) {
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    /*if (!jsonObject.has("f") && !jsonObject.has("w") &&
                            !jsonObject.has("b")) {
                        return null;
                    }*/
                    if (!jsonObject.has("y") && !jsonObject.has("t") &&
                            !jsonObject.has("n")) {
                        return null;
                    }

                    FileCheckerData fcData = new FileCheckerData();
                    //if (jsonObject.has("f")) {
                    if (jsonObject.has("t")) {
                        //String strTemp = jsonObject.getString("f");
                        String strTemp = jsonObject.getString("t");
                        if (!TextUtils.isEmpty(strTemp)) {
                            String[] sArr = strTemp.split("\\|");
                            if (sArr.length > 0) {
                                fcData.globalSuffixCatIds = new int[sArr.length];
                                for (int j = 0; j < sArr.length; j++) {
                                    fcData.globalSuffixCatIds[j] = Integer.parseInt(sArr[j]);
                                }
                            }
                        }
                    }
                    //if (jsonObject.has("w")) {
                    if (jsonObject.has("y")) {
                        //String strTemp = jsonObject.getString("w");
                        String strTemp = jsonObject.getString("y");
                        if (!TextUtils.isEmpty(strTemp)) {
                            String[] sArr = strTemp.split("\\|");
                            if (sArr.length > 0) {
                                fcData.whiteSuffixFilter = new HashSet<>();
                                for (String s : sArr) {
                                    fcData.whiteSuffixFilter.add(s);
                                }
                            }
                        }
                    }
                   // if (jsonObject.has("b")) {
                    if (jsonObject.has("n")) {
                        //String strTemp = jsonObject.getString("b");
                        String strTemp = jsonObject.getString("n");
                        if (!TextUtils.isEmpty(strTemp)) {
                            String[] sArr = strTemp.split("\\|");
                            if (sArr.length > 0) {
                                fcData.blackSuffixFilter = new HashSet<>();
                                for (String s : sArr) {
                                    fcData.blackSuffixFilter.add(s);
                                }
                            }
                        }
                    }

                    return fcData;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    /**
     * 文件检测接口
     */
    interface FileChecker {
        /**
         * 文件是否为可删除的文件
         * @param fileName  文件名
         * @param data      过滤数据
         * @return          如果文件可被删除则返回true，否则返回false
         */
        boolean removable(String fileName, FileCheckerData data);
    }

    /*
     * 目录查询结果
     */
    class DirQueryResult{
        public int mQueryResult;       ///< 查询结果,见DirResultType
        public int mCleanType;         ///< 清除类型,见DirCleanType
        public int mSignId;            ///< 特征id
        public int mCleanMediaFlag;    ///< 是否清除媒体文件的标记,具体含义见CleanMediaFlagUtil工具类
        public int mContentType;       ///< 文件类型,值含义见ContentType
        public String mNameAlert;      ///< name与alert描述信息

        /**清理多久前的*/
        public int mCleanTime;

        public FileCheckerData mFileCheckerData;

        /** 测试特征标记*/
        public int mTestFlag;

        public Collection<String> mDirs;          ///< 目录列表
        public Collection<FilterDirData> mFilterSubDirs; ///< 子目录列表
        public Collection<String> mPkgsMD5HexString;      ///< 包列表
        public Collection<Long> mPkgsMD5High64;///< 包Md5的高64位列表
        public Collection<String> mPackageRegexs; ///< 包正则式列表
        public ShowInfo mShowInfo;	              ///< 展示信息

        @Override
        public String toString() {
            return "DirQueryResult{" +
                    "mQueryResult=" + mQueryResult +
                    ", mCleanType=" + mCleanType +
                    ", mSignId=" + mSignId +
                    ", mCleanMediaFlag=" + mCleanMediaFlag +
                    ", mContentType=" + mContentType +
                    ", mNameAlert='" + mNameAlert + '\'' +
                    ", mCleanTime=" + mCleanTime +
                    ", mFileCheckerData=" + mFileCheckerData +
                    ", mTestFlag=" + mTestFlag +
                    ", mPkgsMD5HexString=" + mPkgsMD5HexString +
                    ", mPkgsMD5High64=" + mPkgsMD5High64 +
                    ", mPackageRegexs=" + mPackageRegexs +
                    ", mShowInfo=" + mShowInfo +
                    '}';
        }
    }

    class DirQueryResultUtil {

        public static boolean isHavePackageList(DirQueryResult queryResult) {
            boolean result = false;
            if (
                    ((queryResult.mPkgsMD5HexString != null && !queryResult.mPkgsMD5HexString.isEmpty())
                            || (queryResult.mPkgsMD5High64 != null && !queryResult.mPkgsMD5High64.isEmpty())
                            || (queryResult.mPackageRegexs != null && !queryResult.mPackageRegexs.isEmpty()))
                            &&
                            (queryResult.mQueryResult == DirResultType.PKG_LIST
                                    || queryResult.mQueryResult == DirResultType.DIR_LIST
                                    || queryResult.mQueryResult == DirResultType.DIR_QUERY_LIST)) {
                result = true;
            }
            return result;
        }
    }

    class ShowInfo {
        public String mName;           ///< 名称
        public String mAlertInfo;       ///< 提示信息
        public String mDescription;    ///< 描述信息(可能没有)
        public boolean mResultLangMissmatch;///<结果的语言信息是否不匹配(当本地其他语言有缓存,可能获取这个值)

        @Override
        public String toString() {
            return "ShowInfo{" +
                    "mName='" + mName + '\'' +
                    ", mAlertInfo='" + mAlertInfo + '\'' +
                    ", mDescription='" + mDescription + '\'' +
                    ", mResultLangMissmatch=" + mResultLangMissmatch +
                    '}';
        }
    }

    class CleanMediaFlagUtil {
        public static final int CLEAN_VIDEO_MASK = 0x1;
        public static final int CLEAN_AUDIO_MASK = 0x2;
        public static final int CLEAN_IMAGE_MASK = 0x4;

		/*
		 * 是否清除视频文件
		 */
///<DEAD CODE>/// 		public static boolean IsCleanVideo(int cleanMediaFlag) {
//			return ((cleanMediaFlag & CLEAN_VIDEO_MASK) != 0);
//		}

		/*
		 * 是否清除音频文件
		 */
///<DEAD CODE>/// 		public static boolean IsCleanAudio(int cleanMediaFlag) {
//			return ((cleanMediaFlag & CLEAN_AUDIO_MASK) != 0);
//		}

		/*
		 * 是否清除图片文件
		 */
///<DEAD CODE>/// 		public static boolean IsCleanImage(int cleanMediaFlag) {
//			return ((cleanMediaFlag & CLEAN_IMAGE_MASK) != 0);
//		}
    }

    /*
     * 测试特征标记工具
     */
    public static class TestFlagUtil {

        /** 是否是测试特征 1为测试 0为非测试 */
        public static boolean isTestSign(int testFlag) {
            return ((testFlag & 0x1) != 0);
        }

        /** 测试特征详细信息上报率*/
///<DEAD CODE>/// 		public  static int getTestReportingRate(int testFlag) {
//			return ((testFlag & 0xFE) >> 1);
//		}
    }

    /*
     * 目录查询数据
     */
    class DirQueryData {
        ///////////////////////////////////////
        //input params
        public String mDirName;        ///< 目录名
        public String mLanguage;	   ///< 语言
        ///////////////////////////////////////
        //output Result
        public int mErrorCode;         ///< 0成功， 其他失败
        //public String mErrorMsg;     ///< 服务端返回的错误码，现在没用先不开放
        public boolean mIsDetected;    ///< 是否检出
        public DirQueryResult mResult; ///< 查询结果
        ////////////////////////////////////////
        //output other Result
        public int mResultSource;      ///< 结果来源,见ResultSourceType
        public boolean mResultExpired; ///< 是否过期,只有本地缓存的结果有可能过期,如果本地有缓存但是过期，联网查询又失败,外部就有可能获取到这个结果
        public Object  mInnerData;	   ///< 保存内部一些数据，内部使用，不公开


        @Override
        public String toString() {
            return "DirQueryData{" +
                    "mDirName='" + mDirName + '\'' +
                    ", mLanguage='" + mLanguage + '\'' +
                    ", mErrorCode=" + mErrorCode +
                    ", mIsDetected=" + mIsDetected +
                    ", mResult=" + mResult +
                    ", mResultSource=" + mResultSource +
                    ", mResultExpired=" + mResultExpired +
                    ", mInnerData=" + mInnerData +
                    '}';
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //包查询相关数据

    /*
     * 包查询结果项
     */
    class PkgQueryDirItem {
        public int mRegexSignId;	   ///< 正则包id,大于0是有效的正则id，默认为0
        public String mDirString;	   ///< 目录串,带有md5
        public boolean mIsDirStringExist;///< 目录是否存在
        public String mDir;	   			///< 目录
        public DirQueryData mDirQueryData;///< 目录查询结果

        @Override
        public String toString() {
            return "PkgQueryDirItem{" +
                    "mRegexSignId=" + mRegexSignId +
                    ", mDirString='" + mDirString + '\'' +
                    ", mIsDirStringExist=" + mIsDirStringExist +
                    ", mDir='" + mDir + '\'' +
                    ", mDirQueryData=" + mDirQueryData +
                    '}';
        }
    }

    /*
     * 包查询结果
     */
    class PkgQueryResult{
        public int mQueryResult = DirResultType.UNKNOWN;       ///< 查询结果,见DirResultType
        public int mSignId = -1;            ///< 包特征id
        public Collection<PkgQueryDirItem> mPkgQueryDirItems;

        @Override
        public String toString() {
            return "PkgQueryResult{" +
                    "mQueryResult=" + mQueryResult +
                    ", mSignId=" + mSignId +
                    ", mPkgQueryDirItems=" + mPkgQueryDirItems +
                    '}';
        }
    }

    /*
     * 包查询数据
     */
    class PkgQueryData {
        ///////////////////////////////////////
        //input params
        public String mPkgName;        ///< 包名
        public String mLanguage;	   ///< 语言
        ///////////////////////////////////////
        //output Result
        public int mErrorCode = -1;         ///< 0成功， 其他失败
        //public String mErrorMsg;     ///< 服务端返回的错误码，现在没用先不开放
        public PkgQueryResult mResult; ///< 查询结果
        ////////////////////////////////////////
        //output other Result
        public int mResultSource = ResultSourceType.INVAILD;      ///< 结果来源,见ResultSourceType
        public boolean mResultExpired; ///< 是否过期,只有本地缓存的结果有可能过期,如果本地有缓存但是过期，联网查询又失败,外部就有可能获取到这个结果
        public boolean mResultMatchRegex;///< 是否是正则匹配结果
        public Object  mInnerData;	   ///< 保存内部一些数据，内部使用，不公开


        @Override
        public String toString() {
            return "PkgQueryData{" +
                    "mPkgName='" + mPkgName + '\'' +
                    ", mLanguage='" + mLanguage + '\'' +
                    ", mErrorCode=" + mErrorCode +
                    ", mResult=" + mResult +
                    ", mResultSource=" + mResultSource +
                    ", mResultExpired=" + mResultExpired +
                    ", mResultMatchRegex=" + mResultMatchRegex +
                    ", mInnerData=" + mInnerData +
                    '}';
        }
    }

    //包查询相关数据
    /////////////////////////////////////////////////////////////////////////////////////

	/*
	 * 一些内部状态，主要用于上报
	 */
//	public static class QueryInnerStatistics {
//		public long mLocalQueryUseTime;     ///< 本地查询总耗时
//		public long mNetQueryUseTime;       ///< 网络查询总耗时
//		public long mLastQueryCompleteTime; ///< 最后一次查询的完成时间
//		public int  mNetQueryCount;         ///< 网络查询次数
//		public int  mNetQueryFailedCount;   ///< 网络查询失败次数
//		public int  mTotalDirQueryCount;	///< 目录查询总数
//		public int  mDirNetQueryCount;		///< 需要联网查询的目录总数
//		public int  mTotalPostSize;			///< 总计的post数据大小
//		public int  mTotalResponseSize;		///< 总计response数据大小
//		public boolean mIsUserBreakQuery;   ///< 查询是否被中断
//	}

    interface PackageChecker {
        Collection<String> getAllPackageNames();
    }
    /*
     * 目录查询回调接口
     */
    interface DirQueryCallback {

        /*
         * 查询的时候可能会需要进行更深目录的二次扫描，通知二次扫描的目录
         */
        void onGetQueryDirs(int queryId, final Collection<String> dirs);

        /*
         * 开始查询，内部会生成一个唯一id并通知给调用方
         */
        void onGetQueryId(int queryId);

        /*
         * 查询结果回调
         * @param queryId 查询id
         * @param results 结果列表
         * @param queryComplete 是否查询结束,如果queryComplete为true，对相同查询id的查询，后面不会再有结果回调
         */
        void onGetQueryResult(int queryId, Collection<DirQueryData> results, boolean queryComplete);


        /*
         * 查询是会不断调用这个回调来判断是否需要中止查询,返回true会中止查询，返回false继续查询
         */
        boolean checkStop();
    }

    /*
     * 包名查询回调接口
     */
    interface PkgQueryCallback {

        /*
         * 开始查询，内部会生成一个唯一key并通知给调用方
         */
        void onGetQueryId(int queryId);

        /*
         * 查询结果回调
         * @param queryId 查询id
         * @param results 结果列表
         * @param queryComplete 是否查询结束,如果queryComplete为true，对相同查询id的查询，后面不会再有结果回调
         */
        void onGetQueryResult(int queryId, Collection<PkgQueryData> results, boolean queryComplete);

        /*
         * 查询是会不断调用这个回调来判断是否需要中止查询,返回true会中止查询，返回false继续查询
         */
        boolean checkStop();
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
     * 设置sd卡根路径
     * @param path sd卡根路径
     */
    boolean setSdCardRootPath(String path);

    /*
     * 设置包信息获取接口
     */
    boolean setPackageChecker(PackageChecker packageChecker);

    /*
     * 获取设置进去的sd卡根路径
     * @return 返回sd卡根路径
     */
    String getSdCardRootPath();

    /*
     * 清除枚举目录的缓存
     */
    void cleanPathEnumCache();

    /**
     * 获取文件检测接口
     * @return 文件检测接口
     */
    FileChecker getFileChecker();

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
            DirQueryCallback callback,
            boolean pureAsync,
            boolean forceNetQuery);

    /*
     * 通过包名查询路径信息,
     * 如果本地查询(高频库和缓存)有结果但是语言信息不符合,那么还是不进行网络查询，
     * @param pkgnames 包查询数据列表
     * @param callback 回调接口，详细说明见IPkgQueryCallback
     * @param pureAsync 是否纯异步，如果为false，查询本地数据库和缓存库部分同步，查询网络异步，所有的回调都异步
     *                  如果为true,全部都是异步
     * @param forceNetQuery 是否强制用网络扫描,如果为true，不利用本地缓存扫描
     */
    boolean queryByPkgName(
            Collection<String> pkgnames,
            PkgQueryCallback callback,
            boolean pureAsync,
            boolean forceNetQuery);

    Collection<PkgQueryData> syncQueryByPkgName(Collection<String> pkgnames, boolean forceNetQuery, long timeout);

    PkgQueryData syncQueryByPkgName(String pkgname, boolean forceNetQuery, long timeout);

    /**
     * 设置目录网络查询的最长持续时间控制器,网络查询超出时间限制后将不再进行网络查询
     * @param timeCalculator 控制器接口
     */
    void setDirNetQueryTimeController(MultiTaskTimeCalculator timeCalculator);

    /*
     * 如果有未完成的查询，那么丢弃
     */
    void discardAllQuery();


	/*
	 * 获取一些内部状态，主要用于上报
	 * @return 返回内部状态对象，详细信息见QueryInnerStatistics的说明
	 */
    //public QueryInnerStatistics getInnerStatistics();

	/*
	 * 清空内部状态数据
	 */
    //public void clearInnerStatistics();

    /*
     * 通过路径获取残留库的信息(本地库)
     * @param dirname 目录名
     * @queryParentDir 是否查询父目录，如queryParentDir为真,传入a\b\c 目录，就会查询 a,a\b,a\b\c 三个目录的信息
     * @isGetShowInfo 是否获取描述信息
     * @language 设置获取描述信息的语言设置，可以传空，传空为当前语言
     * @return 结果数组
     */
    DirQueryData[] localQueryDirInfo(String dirname, boolean queryParentDir, boolean isGetShowInfo, String language);

    /*
     * 通过路径获取残留库的信息(本地库),返回对应目录和子目录的特征信息
     * @param dirname 目录名
     * @isGetShowInfo 是否获取描述信息
     * @language 设置获取描述信息的语言设置，可以传空，传空为当前语言
     * @return 结果数组
     */
    DirQueryData[] localQueryDirAndSubDirInfo(String dirname, boolean isGetShowInfo, String language);

    /*
     * 通过目录id获取描述信息(本地库)
     * @param signId 目录id
     * @language 语言信息,如果传null使用统一的语言设置
     * @return 描述信息
     */
    ShowInfo localQueryDirShowInfo(int signId, String language);

    /**
     * @return
     */
    String getDefaultLanguage();



    /*
	 * 等待扫描结束，注意不要在回调线程中调用
	 * @param timeout 等待的超时时间
	 * @param discardQueryIfTimeout 等待如果超时是否丢弃所有未完成的查询
	 * @return 等待结果，结果含义见CleanCloudDef.WaitResultType中的详细说明
	 */
    int waitForComplete(long timeout, boolean discardQueryIfTimeout, CleanCloudDef.ScanTaskCtrl ctrl);

    interface IPkgDirFilter {
        boolean isInFilter(String strPkg);
    }

}
