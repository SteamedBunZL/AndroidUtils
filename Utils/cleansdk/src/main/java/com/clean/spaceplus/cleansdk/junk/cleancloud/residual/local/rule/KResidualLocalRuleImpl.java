package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.local.rule;

import android.content.Context;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
import com.clean.spaceplus.cleansdk.junk.cleancloud.residual.KResidualPathHelper;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.core.residual.KResidualCommonData;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/21 14:53
 * @copyright TCL-MIG
 */
public class KResidualLocalRuleImpl {
    //////////////////////////////////////////////////////////////////////
    private static final long THREE_DAY_TIME = 72 * 3600 * 1000;
    private static  final int MAX_SUB_FOLDER = 10;

    private String mSdCardRootPath = null;
    private PackageChecker mPackageChecker = null;


    //android-data
    private String mAndroidDataRubbishName = "";
    private String mAndroidDataRubbishDesc = "";
    private int mAndroidDataShowProbability = -1;
    private boolean misShowAndroidDataResult = false;

    //android-obb
    //private Pattern mAndroidObbSubfilePattern = null;
    //private final static String msAndroidObbSubfileRegString = "(patch|main)\\.(d+)\\.(.+)\\.obb";
    private final static String CACHE_FOLDER_NAME = "cache";
    private final static String FILES_FOLDER_NAME = "files";

    /**
     * package过滤检测工具类
     * @author 
     * @date 2015.1.14
     * */
    private class PackageChecker {
        private final static String ANDROID_DATA_DIR = "android/data/";
        private final static String ANDROID_OBB_DIR  = "android/obb/";

        private volatile HashSet<String> mAllPkgSet = null;

        private void initialize(KResidualCloudQuery.PackageChecker packageChecker){
            synchronized(this) {
                if (mAllPkgSet != null)
                    return;

                if (null == packageChecker)
                    return;

                HashSet<String> pkgSet = null;
                Collection<String> pkgs = packageChecker.getAllPackageNames();
                if (pkgs != null && !pkgs.isEmpty()) {
                    pkgSet = new HashSet<String>();
                    for (String pkg : pkgs) {
                        if (TextUtils.isEmpty(pkg))
                            continue;

                        pkgSet.add(StringUtils.toLowerCase(pkg));
                    }
                }
                mAllPkgSet      = pkgSet;

                //try{
                //    mAndroidObbSubfilePattern = Pattern.compile(msAndroidObbSubfileRegString);
                //}catch(Exception e){
                //    mAndroidObbSubfilePattern = null;
                //    CMPushLog.getLogInstance().log("obb rule's regular formate error!");
                //}
            }
        }

        private boolean isPackageInstalled(String strPkg) {
            if (TextUtils.isEmpty(strPkg))
                return false;

            HashSet<String> pkgSet = mAllPkgSet;
            if (pkgSet == null)
                return true;

            return pkgSet.contains(strPkg);
        }

        /**
         * 检测指定String的前缀、后缀是否是安装列表中的pkg
         * @author 
         * @date 2015.1.14
         * */
        private boolean isPrefixOrSuffix(String str){
            if(!TextUtils.isEmpty(str) && mAllPkgSet != null){
                for(String pkg : mAllPkgSet){
                    if(str.startsWith(pkg)){
                        return true;
                    }

                    if(str.endsWith(pkg)){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public KResidualLocalRuleImpl(){
        mPackageChecker = new PackageChecker();
        mAndroidDataShowProbability = CloudCfgDataWrapper.getCloudCfgIntValue(
                CloudCfgKey.CLEAN_CLOUD_SWITCH,
                CloudCfgKey.CLEAN_CLOUD_RESIDUAL_ANDROID_DIR_RULE_SHOW_PROBABILITY,
                100);
        misShowAndroidDataResult = this.isCouldShowAndroidDataResult();
    }

    /**
     * 初始化
     * @author 
     * @date 2015.1.14
     * */
    public void initialize(Context context){
        initAndroidDataRubbishShowInfoByLanguage(context);
    }

    /**
     * @author 
     * @date 2015.1.14
     * */
    public boolean setPackageChecker(KResidualCloudQuery.PackageChecker packageChecker){
        if (null == packageChecker) {
            return false;
        }

        mPackageChecker.initialize(packageChecker);

        return true;
    }

    public boolean setSdCardRootPath(String path){
        mSdCardRootPath = path;
        return true;
    }

    /**
     * 执行android/data/pkg本地检出规则;
     * @author 
     * @date 2015.1.14
     * */
    public boolean processAndroidDataRule(KResidualCloudQuery.DirQueryData data){
//        CMPushLog.getLogInstance().log("android-data-pkg rule: Start!");
        String pkg = getPKGStringFromAndroidDataPath(data);
        if(!TextUtils.isEmpty(pkg)){
//            CMPushLog.getLogInstance().log("android-data-pkg rule: check dir: "+pkg);
            if(isAndroidDataPkgRubbish(pkg)){
//                CMPushLog.getLogInstance().log("android-data-pkg rule: take one rubbish dir: "+pkg);
                return true;
            }
        }
//        CMPushLog.getLogInstance().log("android-data-pkg rule: End!");
        return false;
    }

    String getPKGStringFromAndroidPath(String headStr, KResidualCloudQuery.DirQueryData data) {
        String pkg = null;
        if(data != null && !TextUtils.isEmpty(data.mDirName)){
            String path = StringUtils.toLowerCase(data.mDirName);
            pkg = getPKGFromAndroidDataPath(headStr, path);
            if(!TextUtils.isEmpty(pkg)){
                int pos = pkg.indexOf('/');
                if(pos != -1){
                    pkg = null;
                }
            }
        }
        return pkg;
    }

    /**
     * 获取Android/data/pkg字符串中的PKG部分;
     * PS : 只处理android/data/pkg这样的三级目录;
     * @author 
     * @date 2014.01.13
     * */
    private String getPKGStringFromAndroidDataPath(KResidualCloudQuery.DirQueryData data){
        return getPKGStringFromAndroidPath(PackageChecker.ANDROID_DATA_DIR, data);
    }

    /**
     * 检测此DirQueryData是否是Android/data/pkg类型的垃圾;
     * @author 
     * @date 2015.01.13
     * */
    private boolean isAndroidDataPkgRubbish(String pkg){
        boolean isRubbish = false;
        if(mPackageChecker != null && !TextUtils.isEmpty(pkg)){
            if(!mPackageChecker.isPackageInstalled(pkg)){
                // check android/data/pkg local rule, only when the pkg do not in install list.
                if(isConformAndroidDataPkgLocalRule(pkg)){
                    isRubbish = true;
                }
            }
        }
        return isRubbish;
    }

    /**
     * 依照Android/data/pkg本地规则，检测是否DirQueryData是检出特征;
     * @author 
     * @date 2015.01.13
     * */
    private boolean isConformAndroidDataPkgLocalRule(String pkg){
        if(!TextUtils.isEmpty(pkg)){
            /**
             * 检测pkg的字符串格式，看是否符合本地规则
             * */
            if(!isConformAndroidDataPkgStringFormateLocalRule(pkg)){
                return false;
            }

            /**
             * 检测其以及子路径是否符合本地规则;
             * */
            if(!isConformAndroidDataPkgSubDirLocalRule(pkg)){
                return false;
            }

            return true;
        }
        return false;
    }

    /**
     * 检测Android/data/pkg路径是否符合pkg的前缀、后缀检测本地规则;
     * @author 
     * @date 2014.01.14
     * */
    private boolean isConformAndroidDataPkgStringFormateLocalRule(String pkg){
        if(TextUtils.isEmpty(pkg)) {
            return false;
        }

        String checkName = extractPkgNameFromExtDir(pkg);
        if (null == checkName) {
            checkName = pkg;
        }

        if (!isMaybePkgDirName(checkName)) {
            return false;
        }

        /**
         * 检测pkg的前缀、后缀是否符合本地规则
         * */
        if(mPackageChecker != null
                && !mPackageChecker.isPrefixOrSuffix(pkg)){
            return true;
        }

        return false;
    }

    ///////////////////////////////////////////////////
    //write by qiuruifeng 2015.3.2

    //结尾是-ext的把前面的字符串提取出来
    static String extractPkgNameFromExtDir(String name) {
        if (name == null) {
            return null;
        }
        int length = name.length();
        if (length <= 4) {
            return null;
        }
        int pos = name.indexOf("-ext", length - 4);
        if (-1 == pos) {
            return null;
        }
        String result = name.substring(0, pos);
        return result;
    }

    // 对疑似包名的串按点分割用简单的状态机进行检查
    // 由于是用于本地的检出规则,不需要完全的包名合法性检查,实际上包名可以是其他非英文字符
    // 不过一般的包名都不会这么用,就简化处理了
    // 规则如下
    // 1 包名的字符串必须是英文字母以及数字以及下划线
    // 2 用点分割后的串首字符只能是英文字母或下划线
    static boolean isMaybePkgDirName(String name) {
        if (name == null) {
            return false;
        }

        int pos = name.indexOf('.');
        if (pos <= 0) {
            return false;
        }
        boolean result = true;
        boolean bContiune = true;

        char ch = 0;
        int length = name.length();

        //0为初始状态,
        //1为首字符状态,
        //2为非点非首字符状态,
        //3为点字符状态(字符'.')
        int status = 0;
        boolean isLetter;
        for (int i = 0; i < length; ++i) {
            if (!bContiune)
                break;

            ch = name.charAt(i);
            isLetter = (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
            if (isLetter
                    || (ch >= '0' && ch <= '9')
                    || ch == '_'
                    || ch == '.') {

                switch (ch) {
                    case '.':
                        if (status == 0 || status == 3) {
                            // 连续的'.'字符,或者第一个字符为'.',不合法
                            result = false;
                            bContiune = false;
                            status = 3;
                        } else {
                            status = 3;
                        }
                        break;
                    default:
                        if (status == 0) {
                            status = 1;
                        } else if (status == 1) {
                            status = 2;
                        }// else if (status == 2) {} //状态依旧是2,就不需要写了
                        else if (status == 3) {
                            // 上个字符是点,所以当前字符是首字符
                            status = 1;
                        }
                        if (status == 1 && !isLetter) {
                            //首字符是不是英文字母则不合法
                            result = false;
                            bContiune = false;
                        }
                }
            } else {
                //字符不是英文字母,数字,下划线以及点的不合法
                result = false;
                bContiune = false;
            }
        }

        //最后一个字符是'.'也不合法
        if (result && status == 3) {
            result = false;
        }
        return result;
    }

    /*
    	static class MaybePkgNameTestCase {
		public String mStr;
		public boolean mResult;
		public MaybePkgNameTestCase(String str, boolean result) {
			mStr = str;
			mResult = result;
		}
	}
	static void testCase_isMaybePkgDirName() {

		MaybePkgNameTestCase[] testCases = {
			new MaybePkgNameTestCase("com.abc.kkk", true),
			new MaybePkgNameTestCase("com.ABC.kkk", true),
			new MaybePkgNameTestCase("com.A1B2C.k_kk", true),
			new MaybePkgNameTestCase("abc._123.kkk", false),
			new MaybePkgNameTestCase(null, false),
			new MaybePkgNameTestCase("", false),
			new MaybePkgNameTestCase("com", false),
			new MaybePkgNameTestCase(".", false),
			new MaybePkgNameTestCase("..", false),
			new MaybePkgNameTestCase(".abc.", false),
			new MaybePkgNameTestCase("abc.123.kkk", false),
			new MaybePkgNameTestCase("abc._123.~kkk", false),
		};

		for (int i = 0; i < testCases.length; ++i) {
			boolean testResult = (isMaybePkgDirName(testCases[i].mStr) == testCases[i].mResult);
			System.out.println("test result: "+ (testResult ? "OK" : "Failed!") + " case: "+testCases[i].mStr);
		}
	}

	static void testCase_extractPkgNameFromExtDir() {
		MaybePkgNameTestCase[] testCases = {
				new MaybePkgNameTestCase("com.tencent.aa-ext", true),
				new MaybePkgNameTestCase("com.tencent.aa-ex", false),
				new MaybePkgNameTestCase("-ex", false),
				new MaybePkgNameTestCase("com.tencent.aa-ext0", false),
		};

		for (int i = 0; i < testCases.length; ++i) {
			boolean testResult = ((extractPkgNameFromExtDir(testCases[i].mStr) != null ) == testCases[i].mResult);
			System.out.println("test result: "+ (testResult ? "OK" : "Failed!") + " case: "+testCases[i].mStr);
		}
	}
    */
    //////////////////////////////////////////////////////

    /**
     * 检测Android/data/pkg路径是否符合子路径检测本地规则;
     * @author 
     * @date 2014.01.13
     * */
    private boolean isConformAndroidDataPkgSubDirLocalRule(String pkg){
        boolean haveCacheOrFilesFolder = false;
        boolean allSubFolderNoChangeRecent = true;  // no change at recent 3 days.
        PathOperFunc.StringList subFolderList = null;
        PathOperFunc.StringList secondSubFolderList = null;

        try{
            if(!TextUtils.isEmpty(pkg) && !TextUtils.isEmpty(mSdCardRootPath)){
                String fullPath = mSdCardRootPath + File.separator + PackageChecker.ANDROID_DATA_DIR + File.separator + pkg;
                subFolderList = KResidualPathHelper.enumFolder(fullPath);

                if(subFolderList == null){
                    return false;
                }

                // only check sub folder's count less than 10.
                if(subFolderList.size() > MAX_SUB_FOLDER){
                    return false;
                }

                // sub folder's local rule.
                Iterator<String> it =  subFolderList.iterator();
                while (null != it && it.hasNext()){
                    String subFolder = it.next();
                    if(TextUtils.isEmpty(subFolder)){
                        continue;
                    }

                    // cache, files folder rule.
                    if(!haveCacheOrFilesFolder){
                        if(subFolder.equalsIgnoreCase(CACHE_FOLDER_NAME)){
                            haveCacheOrFilesFolder = true;
                        }else if(subFolder.equalsIgnoreCase(FILES_FOLDER_NAME)){
                            haveCacheOrFilesFolder = true;
                        }
                    }

                    // recent 3 day no change rule.
                    String subPath = fullPath + File.separator + subFolder;
                    if(!isFolderNoChangeMoreThan3Days(subPath)){
                        // need check this folder's sub-folder's change time.
                        secondSubFolderList = KResidualPathHelper.enumFolder(subPath);
                        if(secondSubFolderList == null){
                            return false;
                        }

                        // only check sub folder's sub-folder count less than 20.
                        if(secondSubFolderList.size() > MAX_SUB_FOLDER *2){
                            return false;
                        }
                        Iterator<String> secit =  secondSubFolderList.iterator();
                        while (secit != null && secit.hasNext()){
                            String secondSubFolder = secit.next();
                            if(TextUtils.isEmpty(secondSubFolder)){
                                continue;
                            }

                            String secondSubPath = subPath + File.separator + secondSubFolder;
                            if(!isFolderNoChangeMoreThan3Days(secondSubPath)){
                                return false;
                            }
                        }

                        secondSubFolderList.release();
                        secondSubFolderList = null;
                    }
                }

                subFolderList.release();
                subFolderList = null;

            }
        }finally {
            if(secondSubFolderList != null){
                secondSubFolderList.release();
            }

            if(subFolderList != null){
                subFolderList.release();
            }
        }
        return haveCacheOrFilesFolder && allSubFolderNoChangeRecent;
    }

    /**
     * 检测指定的文件夹是否超过3天未修改;
     * @author 
     * @date 2015.1.14
     * */
    private boolean isFolderNoChangeMoreThan3Days(String folderPath){
        if(!TextUtils.isEmpty(folderPath)){
            long nowTime = System.currentTimeMillis();
            File subFile = new File(folderPath);
            long modifyTime = subFile.lastModified();
            if(nowTime - modifyTime < THREE_DAY_TIME){
                return false;
            }else{
                return true;
            }
        }
        return false;
    }

    /**
     * 构造android/data/pkg本地策略检出的结果的结构体对象
     * doing
     * @author 
     * @date 2015.1.14
     * */
    public boolean makeAndroidDataResult(KResidualCloudQuery.DirQueryData data, int ruleType){
        if(data != null && data.mResult != null){
            if(data.mResult.mShowInfo == null){
                data.mResult.mShowInfo 	= new KResidualCloudQuery.ShowInfo();
            }

            // base sample info.
            data.mErrorCode = 0;
            if(misShowAndroidDataResult){
                data.mIsDetected = true;
            }
            data.mResultSource = KResidualCloudQuery.ResultSourceType.LOCAL_RULE;
            data.mResultExpired = false;
            data.mResult.mSignId = ruleType;
            data.mResult.mQueryResult = KResidualCloudQuery.DirResultType.PKG_LIST;
            data.mResult.mCleanType = KResidualCloudQuery.DirCleanType.SUGGESTED;
            data.mResult.mPkgsMD5HexString = null;  // ok: could null
            data.mResult.mPkgsMD5High64 = null;  // ok: could null
            data.mResult.mPackageRegexs = null; // ok: could null
            data.mResult.mDirs = null;
            data.mResult.mContentType = 0;  // ok: could 0
            data.mResult.mNameAlert = null;
            data.mResult.mTestFlag = 0;
            ((KResidualCommonData.DirQueryInnerData)data.mInnerData).mFilterSubDirDatas = null;

            // show info.
            if (null == data.mResult.mShowInfo) {
                data.mResult.mShowInfo = new KResidualCloudQuery.ShowInfo();
            }
            data.mResult.mShowInfo.mName = String.valueOf(mAndroidDataRubbishName);
            data.mResult.mShowInfo.mDescription = String.valueOf(mAndroidDataRubbishDesc);
            data.mResult.mShowInfo.mResultLangMissmatch = false;
            data.mResult.mShowInfo.mAlertInfo = null;
        }
        return true;
    }

    private String getPKGFromAndroidDataPath(final String prefix, String path) {
        if (!path.startsWith(prefix))
            return null;

        String dirname = null;
        int pos = path.indexOf('/', prefix.length());
        if (pos != -1) {
            dirname = path.substring(prefix.length(), pos);
        } else {
            dirname = path.substring(prefix.length());
        }
        return dirname;
    }

    /**
     * 根据手机语言，正确载入多语言文本信息;
     * doing
     * @author 
     * @date 2015.1.14
     * */
    private boolean initAndroidDataRubbishShowInfoByLanguage(Context context){
        String name = null;
        if (context != null) {
            try {
                name = context.getString(R.string.RF_LocalRule);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!TextUtils.isEmpty(name)){
            mAndroidDataRubbishName = name;
        } else {
            mAndroidDataRubbishName = "Misc. residual files";
        }
        mAndroidDataRubbishDesc = "";
        return false;
    }

    private static Random msShowRandomObject = null;
    private static synchronized int getShowRndomNum() {
        if (null == msShowRandomObject) {
            msShowRandomObject = new Random();
        }
        return msShowRandomObject.nextInt(100);
    }
    /**
     * android/data本地策略检出结果展示概率计算
     * @author 
     * @date 2015.1.22
     * */
    private boolean isCouldShowAndroidDataResult(){
        boolean result = false;
        if (mAndroidDataShowProbability >= 100) {
            result = true;
        } else if (mAndroidDataShowProbability <= 0) {
            result = false;
        } else {
            int random = getShowRndomNum();
            if(random <= mAndroidDataShowProbability) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 构建本地规则的infoc上报数据;
     * @param isClean : true - 已清理
     * @author 
     * @date 2015.1.15
     * */
    /*public static Collection<IKCleanCloudLocalRuleResultReporter.LocalRuleResultData> makeReportData(Collection<KResidualCloudQuery.DirQueryData> results, byte isClean, String rootPath){
        if(results != null && !results.isEmpty() && !TextUtils.isEmpty(rootPath)){
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }

            if(!rootPath.endsWith(File.separator)){
                rootPath = rootPath + File.separator;
            }
            long[] folderInfo = new long[3];
            Collection<IKCleanCloudLocalRuleResultReporter.LocalRuleResultData> reportDatas = new ArrayList<IKCleanCloudLocalRuleResultReporter.LocalRuleResultData>();
            for(KResidualCloudQuery.DirQueryData result : results){
                if(result != null && result.mResult != null && !TextUtils.isEmpty(result.mDirName)){
                    IKCleanCloudLocalRuleResultReporter.LocalRuleResultData data = new IKCleanCloudLocalRuleResultReporter.LocalRuleResultData();
                    data.mFunctionId = IKCleanCloudLocalRuleResultReporter.FunctionType.RESIDUAL_SCAN;
                    data.mSignId = result.mResult.mSignId;
                    data.mCleanType = (byte)result.mResult.mCleanType;
                    data.mIsCleaned = isClean;
                    data.mSignSource = (byte)result.mResultSource;

                    //boolean isBeta = true;
                    //if(isBeta){
                    // if(result.mDirName.length() > 32){
                    //    data.mResultDirMD5 = result.mDirName.substring(result.mDirName.length() - 32);
                    //}else{
                    data.mResultDirMD5 = result.mDirName;
                    //}
                    //}else{
                    //    String dirname = result.mDirName.toLowerCase();
                    //    md.update(dirname.getBytes());
                    //    data.mResultDirMD5 = EnDeCodeUtils.byteToHexString(md.digest());
                    //    md.reset();
                    //}

                    PathOperFunc.computeFileSize(rootPath+result.mDirName, folderInfo, null);
                    data.mFileCount = (int)folderInfo[2];
                    data.mFileSize = (int)folderInfo[0];
                    reportDatas.add(data);
                }
            }
            return reportDatas;
        }
        return null;
    }*/

    /**
     * 执行android/obb/pkg本地检出规则;
     * @author 
     * @date 2015.1.23
     * */
    public boolean processAndroidObbRule(KResidualCloudQuery.DirQueryData data){
        //CMPushLog.getLogInstance().log("android-obb-pkg rule: Start!");
        String pkg = getPKGStringFromAndroidObbPath(data);
        if(!TextUtils.isEmpty(pkg)){
            //CMPushLog.getLogInstance().log("android-obb-pkg rule: check dir: "+pkg);
            if(isAndroidObbRubbish(pkg)){
                //CMPushLog.getLogInstance().log("android-obb-pkg rule: take one rubbish dir: "+pkg);
                return true;
            }
        }
        //CMPushLog.getLogInstance().log("android-obb-pkg rule: End!");
        return false;
    }

    /**
     * 获取Android/obb/pkg字符串中的PKG部分;
     * PS : 只处理android/obb/pkg这样的三级目录;
     * @author 
     * @date 2014.01.13
     * */
    private String getPKGStringFromAndroidObbPath(KResidualCloudQuery.DirQueryData data){
        return getPKGStringFromAndroidPath(PackageChecker.ANDROID_OBB_DIR, data);
    }

    /**
     * 检测此DirQueryData是否是Android/obb/pkg类型的垃圾;
     * @author 
     * @date 2015.01.23
     * */
    private boolean isAndroidObbRubbish(String pkg){
        boolean isRubbish = false;
        if(mPackageChecker != null && !TextUtils.isEmpty(pkg)){
            if(!mPackageChecker.isPackageInstalled(pkg)){
                // check android/obb/pkg local rule, only when the pkg do not in install list.
                if(isConformAndroidObbLocalRule(pkg)){
                    isRubbish = true;
                }
            }
        }
        return isRubbish;
    }

    /**
     * 依照Android/obb/pkg本地规则，检测是否DirQueryData是检出特征;
     * @author 
     * @date 2015.01.23
     * */
    private boolean isConformAndroidObbLocalRule(String pkg){
        if(!TextUtils.isEmpty(pkg)){
            /**
             * 检测其以及子路径是否符合本地规则;
             * */
            if(!isConformAndroidObbPkgSubDirLocalRule(pkg)){
                return false;
            }

            return true;
        }
        return false;
    }

    /**
     * 检测Android/data/pkg路径是否符合子路径检测本地规则;
     * @author 
     * @date 2014.01.13
     * */
    private boolean isConformAndroidObbPkgSubDirLocalRule(String pkg){
        boolean subFileConform = false;  // no change at recent 3 days.
        PathOperFunc.StringList subFileList = null;
        try{
            if(!TextUtils.isEmpty(pkg) && !TextUtils.isEmpty(mSdCardRootPath) && mPackageChecker != null){
                String fullPath = mSdCardRootPath + File.separator + PackageChecker.ANDROID_OBB_DIR + File.separator + pkg;
                subFileList = KResidualPathHelper.enumFile(fullPath);

                if(subFileList == null){
                    return false;
                }

                Iterator<String> it =  subFileList.iterator();
                while (null != it && it.hasNext()){
                    String subFile = it.next();
                    if(TextUtils.isEmpty(subFile)){
                        continue;
                    }

                    String subFileLowerCase = StringUtils.toLowerCase(subFile);
                    StringBuilder sb = new StringBuilder(pkg.length() + 5);
                    sb.append('.');
                    sb.append(pkg);
                    sb.append(".obb");
                    String strSuffix = sb.toString();
                    if (subFileLowerCase.endsWith(strSuffix)
                            && (subFileLowerCase.startsWith("patch.") || subFileLowerCase.startsWith("main."))) {
                        subFileConform = true;
                        //CMPushLog.getLogInstance().log("obb sub file: "+subFile);
                        break;
                    }
                }

                subFileList.release();
                subFileList = null;

            }
        }finally {
            if(subFileList != null){
                subFileList.release();
            }
        }

        return subFileConform;
    }
}
