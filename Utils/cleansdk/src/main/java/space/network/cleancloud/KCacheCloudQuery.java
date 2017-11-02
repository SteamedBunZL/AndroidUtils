

package space.network.cleancloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import space.network.cleancloud.CleanCloudDef.ScanTaskCtrl;

/**
 * 残留云端查询接口
 */
public interface KCacheCloudQuery {

    /**
     * 目录查询结果类型
     */
     class PkgResultType {
        public static final int UNKNOWN = 0;

        public static final int NOT_FOUND = 1;

        public static final int DIR_LIST = 3;
    }
    /*
     * showinfo查询结果类型
     */
     class ShowInfoResultType {
        /**无效参数*/
        public static final int UNKNOWN = 0;

        /**类型不存在*/
        public static final int NOT_FOUND = 1;

        /**目录列表*/
        public static final int SUCCESS = 3;
    }

    /*
     * 目录清理类型
     */
    class ScanType {
        /**无效参数*/
        public static final int INVALID = -1;

        /**默认参数   深度建议一起扫*/
        public static final int DEFAULT = 0;

        /**建议清理*/
        public static final int SUGGESTED = 1;

        /**慎重清理*/
        public static final int CAREFUL = 2;

        /**隐私扫描*/
        public static final int PRIVACY = 3;

        /*建议清理+cleantime不为65535的慎重清理*/
        public static final int SUGGESTED_WITH_CLEANTIME = 4;
    }

    /**
     * 清除类型
     */
     class CacheCleanOperation {
        /**只清除文件*/
        public static final int FILE_ONLY = 2;

        /**清除文件或目录*/
        public static final int FILE_AND_DIR = 1;
    }

    /**
     * cahce路径类型
     */
     class CachePathType {
        /**目录路径*/
        public static final int DIR = 1;

        /**正则表达式目录路径*/
        public static final int DIR_REG = 2;

        /**文件路径*/
        public static final int FILE = 3;

        /**文件正则表达式文件路径*/
        public static final int FILE_REG = 4;
        /**root dirs*/
        public static final int ROOT_DIR = 5;

        public static final int ROOT_DIR_REG = 6;
        /**文件路径2*/

        public static final int FILE_2 = 7;

        /**文件正则表达式文件路径2*/
        public static final int FILE_REG_2 = 8;

    }

    /*
	 * 内容分类
	 */
     class ContentType {

        /**未知 */
        public static final int CONTENT_TYPE_UNKNOWN = 0;

        /**封面 */
        public static final int CONTENT_TYPE_COVER = 1;

        /**图标 */
        public static final int CONTENT_TYPE_ICON = 2;

        /**头像 */
        public static final int CONTENT_TYPE_HEAD_PORTRAIT = 3;

        /**缩略图 */
        public static final int CONTENT_TYPE_THUMBNAILS = 4;

        /**列表 */
        public static final int CONTENT_TYPE_LIST = 5;

        /**新闻 */
        public static final int CONTENT_TYPE_NEWS = 6;

        /**游戏缓存 */
        public static final int CONTENT_TYPE_GAME_CACHE = 7;

        /**消息 */
        public static final int CONTENT_TYPE_MESSAGE = 8;

        /**表情 */
        public static final int CONTENT_TYPE_STICKER = 9;

        /**下载/备份 */
        public static final int CONTENT_TYPE_DOWNLOAD_OR_BACKUP = 10;

        /**音频 */
        public static final int CONTENT_TYPE_AUDIO = 11;

        /**视频 */
        public static final int CONTENT_TYPE_VIDEO = 12;

        /**图片/照片 */
        public static final int CONTENT_TYPE_PICTURE = 13;

        /**地图 */
        public static final int CONTENT_TYPE_MAP = 14;

        /**壁纸 */
        public static final int CONTENT_TYPE_WALLPAPER = 15;

        /**铃声 */
        public static final int CONTENT_TYPE_RINGTONE = 16;

        /**其他 */
        public static final int CONTENT_TYPE_OTHER = 17;

        /**录音 */
        public static final int CONTENT_TYPE_RECORDING = 18;

        /**APK */
        public static final int CONTENT_TYPE_APK = 19;

        /**图片+回收站 */
        public static final int CONTENT_TYPE_PICTURES_AND_TRASH = 20;

        /**下载管理 */
        public static final int CONTENT_TYPE_DOWNLOAD_MANAGE = 21;
    }

    /*
     * 结果查询来源类型,查询结果可以来自云端，本地高频库或者是云端结果缓存
     */
     class ResultSourceType {
        public static final int INVAILD = 0;///< 无效参数
        public static final int CLOUD = 1;///< 云端结果
        public static final int HFREQ = 2;///< 本地高频库结果
        public static final int CACHE = 3;///< 本地缓存结果
    }

     class ShowInfo implements Cloneable{
        /**名称*/
        public String mName = "";

        /**描述信息(可能没有)*/
        public String mDescription = "";

        /**结果的语言信息是否不匹配(当本地其他语言有缓存,可能获取这个值)*/
        public boolean mResultLangMissmatch = false;

        /** 是否过期,只有本地缓存的结果有可能过期,如果本地有缓存但是过期，联网查询又失败,外部就有可能获取到这个结果*/
        public boolean mResultExpired;
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // 包查询相关数据

     class PkgQueryParam {
        /**包名*/
        public String mPkgName;

        /**清除类型,传0就是扫描所有,也可以传CleanType里面的对应值 */
        public int mCleanType;


        @Override
        public String toString() {
            return "PkgQueryParam{" +
                    "mPkgName='" + mPkgName + '\'' +
                    ", mCleanType=" + mCleanType +
                    '}';
        }
    }

    class CleanMediaFlagUtil {
        public static final int CLEAN_VIDEO_MASK = 0x1;
        public static final int CLEAN_AUDIO_MASK = 0x2;
        public static final int CLEAN_IMAGE_MASK = 0x4;
        public static final int BIG_FILE_MASK    = 0x8;
        public static final int SCAN_BY_MEDIA_STORE_MASK = 0x10;
        public static final int TO_RECYCLE_BIN_MASK = 0x20;

        /*
         * 是否清除视频文件
         */
        public static boolean IsCleanVideo(int cleanMediaFlag) {
            return ((cleanMediaFlag & CLEAN_VIDEO_MASK) != 0);
        }

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

        /*
		 * 是否是大文件
		 */
///<DEAD CODE>///         public static boolean IsBigFile(int cleanMediaFlag) {
//            return ((cleanMediaFlag & BIG_FILE_MASK) != 0);
//        }

        /*
		 * 是否用mediastore扫描
		 */
        public static boolean IsScanByMediaStore(int cleanMediaFlag) {
            return ((cleanMediaFlag & SCAN_BY_MEDIA_STORE_MASK) != 0);
        }

        /*
         * 删除的相片文件是否进入回收站
         */
        public static boolean isToRecycleBin(int cleanMediaFlag) {
            return ((cleanMediaFlag & TO_RECYCLE_BIN_MASK) != 0);
        }
    }

    /**
     * 测试特征标记工具
     */
     class TestFlagUtil {

        /** 是否是测试特征*/
        public static boolean isTestSign(int testFlag) {
            return ((testFlag & 0x1) != 0);
        }

        /** 测试特征详细信息上报率*/
///<DEAD CODE>/// 		public  static int getTestReportingRate(int testFlag) {
//			return ((testFlag & 0xFE) >> 1);
//		}
    }

    /**
     * 获取用户主动勾选的路径记录,按包进行查询
     */
     interface CustomCleanCarefulPathGetter {
         ArrayList<String> getCustomCleanPath(String pkgName);
    }


    /**
     * 包查询结果项
     * <pre>
     *2.path_type：1-目录，2-目录正则，3-文件；
     *3.clean_type：1-建议清理，2-谨慎清理；
     *4.clean_operation：1-清理目录，2-不清理目录，只清理目录下文件；
     *5.clean_time：清理缓存文件的周期；
     *6.file_type：待定（int型，服务端不关心具体类别）
     *7.desc_dict：内容形如{"language":"path introduction"}，采用json格式；
     *8.name_dict：内容形如{"language":"package name"}，采用json格式；
     *</pre>
     */
     class PkgQueryPathItem implements Cloneable{
        /** 目录/文件串,带有md5,如果带有正则，正则与前面的父目录用‘//’分割 */
        public String mPathString;

        /** 目录/文件是否存在 */
        public boolean mIsPathStringExist;

        /** 是否是从用户主动勾选的路径记录中筛选出的路径,通过ICustomCleanPathGetter查询得出*/
        public boolean isCustomCleanPath;

        /** 目录*/
        public String mPath;

        /** 文件列表*/
        public String mFiles[];

        /**路径类型:CachePathType*/
        public int mPathType;

        /* 清理类型: 1,2 建议，深度*/
        public int mCleanType = 0;

        /**清除操作类型 见CacheCleanOperation*/
        public int mCleanOperation;

        /**清理多久前的*/
        public int mCleanTime;

        /**文件类型*/
        public int mContentType = 0;

        /**是否清除媒体文件的标记,具体含义见CleanMediaFlagUtil工具类*/
        public int mCleanMediaFlag = 0;

        /**路径id*/
        public String mSignId;

        /**展示信息*/
        public ShowInfo mShowInfo;

        /**
         * ShowInfo结果类型，见{@link ShowInfoResultType}}
         */
        public int mShowInfoResultType = ShowInfoResultType.UNKNOWN;

        /**
         * ShowInfo结果来源，见{@link ResultSourceType}
         */
        public int mShowInfoResultSource = ResultSourceType.INVAILD;

        /** 隐私类型,0代表非隐私类型文件，其他值代表隐私类型*/
        public int mPrivacyType = -1;

        public int mNeedCheck = 0;

        /** 测试特征标记*/
        public int mTestFlag;

        @Override
        public Object clone() {
            PkgQueryPathItem obj = null;
            try {
                obj = (PkgQueryPathItem)super.clone();
                if (mFiles != null) {
                    if (mFiles.length > 0) {
                        String[] files = new String[mFiles.length];
                        System.arraycopy(mFiles, 0, files, 0, mFiles.length);
                        obj.mFiles = files;
                    } else {
                        obj.mFiles = new String[0];
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        }

        @Override
        public String toString() {
            return "PkgQueryPathItem{" +
                    "mPathString='" + mPathString + '\'' +
                    ", mIsPathStringExist=" + mIsPathStringExist +
                    ", isCustomCleanPath=" + isCustomCleanPath +
                    ", mPath='" + mPath + '\'' +
                    ", mFiles=" + Arrays.toString(mFiles) +
                    ", mPathType=" + mPathType +
                    ", mCleanType=" + mCleanType +
                    ", mCleanOperation=" + mCleanOperation +
                    ", mCleanTime=" + mCleanTime +
                    ", mContentType=" + mContentType +
                    ", mCleanMediaFlag=" + mCleanMediaFlag +
                    ", mSignId='" + mSignId + '\'' +
                    ", mShowInfo=" + mShowInfo +
                    ", mShowInfoResultType=" + mShowInfoResultType +
                    ", mShowInfoResultSource=" + mShowInfoResultSource +
                    ", mPrivacyType=" + mPrivacyType +
                    ", mNeedCheck=" + mNeedCheck +
                    ", mTestFlag=" + mTestFlag +
                    '}';
        }
    }


     class SysFlagUtil {
        public static final int CACHE_IN_ANDROID_DATA_CAN_CLEAN = 0x1;
        public static final int CACHE_IN_DATA_DATA_CAN_BE_CLEAN = 0x2;
        public static final int CACHE_IN_ANDROID_DATA_CAN_NOT_CLEAN = 0x4;
        public static final int CACHE_IN_DATA_DATA_CAN_NOT_CLEAN = 0x8;
        public static final int COMPETITIVE_PRODUCT_MASK = 0x10;
    }

    /**
     * 包查询结果
     */
     class PkgQueryResult {
        /**查询结果,见PkgResultType*/
        public int mQueryResult = PkgResultType.UNKNOWN;

        /** 包特征id 为0代表查找失败 */
        public int mPkgId = -1;

        /**
         * <pre>
         * 系统缓存标记位定义
         * 对低五位做以下定义，置位为1就是开启
         * 0 android/data 下面的缓存可以清除,mask 0x1;
         * 1 data/data 下面的缓存可以清除,mask 0x2;
         * 2 android/data 下面的缓存不可以清除,mask 0x4;
         * 3 data/data 下面的缓存不可以清除,在root情况下是否去勾选，置为1时表示去勾选,mask 0x8;
         * 4 竞品标记位 android/data 下面的缓存标为不可以清除的情况下,如果竞品存在,那还是可以清除,mask 0x10;
         * 把组合也标上
         * android/data 可清  data/data 可清    二进制 0011  十进制 3
         * android/data 可清  data/data 不可清  二进制 1001  十进制 9
         * android/data 不可清 data/data 可清   二进制 0110  十进制 6
         * android/data 不可清 data/data 不可清  二进制 1100 十进制 12
         * 把可清和不可以清按位分开，主要是为了把未知和不可清进行区分
         * </br>
         * 系统缓存标记 简单的支持 0:unknown; 3:black; 12:white
         * 说明第2,3,4位供垃圾清理系统缓存扫描使用
         * </pre>
         */
        public int mSysFlag = 0;

        /**最终结果集*/
        public Collection<PkgQueryPathItem> mPkgQueryPathItems;

        /**root后可以清理的在系统分区下的缓存数据目录列表,在"/data/data/$packagename/"下面的目录*/
        public Collection<PkgQueryPathItem> mSystemDataCleanItems;

        @Override
        public String toString() {
            return "PkgQueryResult{" +
                    "mQueryResult=" + mQueryResult +
                    ", mPkgId=" + mPkgId +
                    ", mSysFlag=" + mSysFlag +
                    ", mPkgQueryPathItems=" + mPkgQueryPathItems +
                    ", mSystemDataCleanItems=" + mSystemDataCleanItems +
                    '}';
        }
    }

    /*
     * 包查询数据
     */
     class PkgQueryData {
        // /////////////////////////////////////
        // input params
        /**查询参数*/
        public PkgQueryParam mQueryParam;

        /**语言*/
        public String mLanguage;

        // /////////////////////////////////////
        // output Result
        /**查询结果错误码，0成功， 其他失败*/
        public int mErrorCode = -1;

        /**服务端返回的错误码，现在没用先不开放*/
        // public String mErrorMsg;

        /**查询结果*/
        public PkgQueryResult mResult;
        ////////////////////////////////////////
        //output other Result

        /** 结果来源,见ResultSourceType*/
        public int mResultSource = ResultSourceType.INVAILD;

        /**
         * 是否过期,只有本地缓存的结果有可能过期,如果本地有缓存但是过期,
         * 联网查询又失败,外部就有可能获取到这个结果
         */
        public boolean mResultExpired = false;

        /**
         * 结果是否是完整的
         */
        public boolean mResultIntegrity = true;
        /**
         * 不完整数据是否要联网查询
         */
        public boolean mResultIntegrityNeedNetQuery = true;

        /**是否是正则匹配结果*/
        public boolean mResultMatchRegex = false;

        /** 保存内部一些数据，内部使用，不公开*/
        public Object mInnerData = null;


        @Override
        public String toString() {
            return "PkgQueryData{" +
                    "mQueryParam=" + mQueryParam +
                    ", mLanguage='" + mLanguage + '\'' +
                    ", mErrorCode=" + mErrorCode +
                    ", mResult=" + mResult +
                    ", mResultSource=" + mResultSource +
                    ", mResultExpired=" + mResultExpired +
                    ", mResultIntegrity=" + mResultIntegrity +
                    ", mResultIntegrityNeedNetQuery=" + mResultIntegrityNeedNetQuery +
                    ", mResultMatchRegex=" + mResultMatchRegex +
                    ", mInnerData=" + mInnerData +
                    '}';
        }
    }

    // 包查询相关数据
    // ///////////////////////////////////////////////////////////////////////////////////

    /*
     * 一些内部状态，主要用于上报
     */
     class QueryInnerStatistics {
        public long mLocalQueryUseTime; // /< 本地查询总耗时
        public long mNetQueryUseTime; // /< 网络查询总耗时
        public long mLastQueryCompleteTime; // /< 最后一次查询的完成时间
        public int mNetQueryCount; // /< 网络查询次数
        public int mNetQueryFailedCount; // /< 网络查询失败次数
        public int mTotalDirQueryCount; // /< 目录查询总数
        public int mDirNetQueryCount; // /< 需要联网查询的目录总数
        public int mTotalPostSize; // /< 总计的post数据大小
        public int mTotalResponseSize; // /< 总计response数据大小
        public boolean mIsUserBreakQuery; // /< 查询是否被中断
    }

     class SysCacheFlagQueryData {
        public String  mPkgName;

        /**
         * <pre>
         * 系统缓存标记位定义
         * 对低五位做以下定义，置位为1就是开启
         * 0 android/data 下面的缓存可以清除,mask 0x1;
         * 1 data/data 下面的缓存可以清除,mask 0x2;
         * 2 android/data 下面的缓存不可以清除,mask 0x4;
         * 3 data/data 下面的缓存不可以清除,在root情况下是否去勾选，置为1时表示去勾选,mask 0x8;
         * 4 竞品标记位 android/data 下面的缓存标为不可以清除的情况下,如果竞品存在,那还是可以清除,mask 0x10;
         * 把组合也标上
         * android/data 可清  data/data 可清    二进制 0011  十进制 3
         * android/data 可清  data/data 不可清  二进制 1001  十进制 9
         * android/data 不可清 data/data 可清   二进制 0110  十进制 6
         * android/data 不可清 data/data 不可清  二进制 1100 十进制 12
         * 把可清和不可以清按位分开，主要是为了把未知和不可清进行区分
         * </br>
         * 系统缓存标记 简单的支持 0:unknown; 3:black; 12:white
         * 说明第2,3,4位供垃圾清理系统缓存扫描使用
         * </pre>
         */
        public int     mSysFlag = -1;
        public boolean mLangMissmatch;
        public String  mSysCacheAlertInfo;
        public Object  mInnerData = null;

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
         *
         * @param queryId 查询id
         *
         * @param results 结果列表
         *
         * @param queryComplete
         * 是否查询结束,如果queryComplete为true，对相同查询id的查询，后面不会再有结果回调
         */
         void onGetQueryResult(int queryId,
                               Collection<PkgQueryData> results, boolean queryComplete);

        /*
         * 查询是会不断调用这个回调来判断是否需要中止查询,返回true会中止查询，返回false继续查询
         */
         boolean checkStop();
    }

    /**
     * 初始化
     * @param first 是否清理过
     */
     boolean initialize(boolean first);

    /*
     * 反初始化,调用反初始化让内部维护的线程退出，如果有没有完成的查询也会放弃
     */
     void unInitialize();

    /*
     * 设置查询的语言,如果不设置默认使用英文:"en"
     *
     * @param language 语言字符串,如cn ,en, zh-cn,zh-tw等
     */
     boolean setLanguage(String language);

    /*
     * 获取设置查询的语言
     *
     * @return 返回语言字符串
     */
     String getLanguage();


    /**
     *
     * @param pathGetter 用来获取用户主动勾选的深度扫描的结果
     * @return 成功返回true,失败返回false
     */
     boolean setCustomCleanCarefulPathGetter(CustomCleanCarefulPathGetter pathGetter);

    /*
     * 通过包名查询路径信息, 如果本地查询(高频库和缓存)有结果但是语言信息不符合,那么还是不进行网络查询，
     *
     * @param pkgnames 包查询数据列表
     *
     * @param callback 回调接口，详细说明见IPkgQueryCallback
     *
     * @param pureAsync 是否纯异步，如果为false，查询本地数据库和缓存库部分同步，查询网络异步，所有的回调都异步
     * 如果为true,全部都是异步
     *
     * @param forceNetQuery 是否强制用网络扫描,如果为true，不利用本地缓存扫描
     */
     boolean queryByPkgName(Collection<PkgQueryParam> pkgnames,
                            PkgQueryCallback callback, boolean pureAsync, boolean forceNetQuery);

    /**
     * @param pkgNames
     * @return 返回查询结果，其中包含是否需要清理的标记
     */
     ArrayList<SysCacheFlagQueryData> queryCleanFlagByPkgName(Collection<String> pkgNames);

    /**
     * 设置包网络查询的最长持续时间控制器,网络查询超出时间限制后将不再进行网络查询
     * @param timeCalculator 控制器接口
     */
     void setPkgNetQueryTimeController(MultiTaskTimeCalculator timeCalculator);



	/*
	 * 获取一些内部状态，主要用于上报
	 * type =0 pkgquery data type =1 showinfo query data
	 * @return 返回内部状态对象，详细信息见QueryInnerStatistics的说明
	 */
    // KCacheQueryStatistics getInnerStatistics(int type);

	/*
	 * 清空内部状态数据
	 */
    // void clearInnerStatistics();


    /*
    * 设置sd卡根路径,可以设置多个sd卡路径
    * @param path sd卡根路径
    */
     boolean setSdCardRootPath(String[] paths);

    /*
    * 获取设置进去的sd卡根路径
    * @return 返回sd卡根路径
    */
     String[] getSdCardRootPath();

    /*
     * 清除枚举目录的缓存
    */
     void cleanPathEnumCache();

	/*
	 * 通过路径获取残留库的信息(本地库)
	 * @param dirname 目录名
	 * @queryParentDir 是否查询父目录，如queryParentDir为真,传入a\b\c 目录，就会查询 a,a\b,a\b\c 三个目录的信息
	 * @isGetShowInfo 是否获取描述信息
	 * @language 设置获取描述信息的语言设置，可以传空，传空为当前语言
	 * @return 结果数组
	 */
    //上层没有用，先注释掉
    // PkgQueryPathItem[] localQueryDirInfo(String dirname, boolean queryParentDir, boolean isGetShowInfo);

    /*
     * 获取指定内容分类的目录信息,只获取普通的目录信息,不获取正则目录,文件等信息
     * @param contentTypes 内容类型数组,取值参看ContentType
     */
     ArrayList<PkgQueryPathItem> localGetDirPathByContentType(int[] contentTypes);

    /*
     * 获取指定包名的目录信息,只获取普通的目录信息,不获取正则目录,文件等信息
     * @param pkgnames 包名参数列表
     * @param isGetShowInfo 是否获取展示信息
     */
     ArrayList<PkgQueryData> localGetNormalDirPathByPkgName(Collection<PkgQueryParam> pkgnames, boolean isGetShowInfo);

    /**
     * 等待扫描结束，注意不要在回调线程中调用
     *
     * @param timeout 等待的超时时间
     *
     * @param discardQueryIfTimeout 等待如果超时是否丢弃所有未完成的查询
     *
     * @return 等待结果，结果含义见CleanCloudDef.WaitResultType中的详细说明
     */

     int waitForComplete(long timeout, boolean discardQueryIfTimeout, ScanTaskCtrl ctrl);

}
