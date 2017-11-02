package com.clean.spaceplus.cleansdk.junk.engine.bean;

import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.db.DatabaseHelper;
import com.clean.spaceplus.cleansdk.util.Commons;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.util.md5.Md5Util;
import com.hawkclean.framework.log.NLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import space.network.cleancloud.KCacheCloudQuery;
import space.network.util.CleanTypeUtil;

/**
 * @author dongdong.huang
 * @Description: 缓存信息类
 * @date 2016/4/23 19:55
 * @copyright TCL-MIG
 */
public class CacheInfo extends BaseJunkBean{
    public final static int INFOTYPE_SYSTEMCACHE = 0;
    public final static int INFOTYPE_SYSTEMCACHEITEM = 1;
    public final static int INFOTYPE_APPCACHE = 2;
    public final static int INFOTYPE_SYSFIXEDFILE = 3;
    public final static int INFOTYPE_SYSFIXEDFIELITEM = 4;
    /**
     * 新增类型 17：扩展的大文件（缓存）
     */
    public static final int TYPE_BIG_FILE_EXTEND_CACHE = 17;
    private String mFilePath;
    private int mCleanTime;
    private PackageInfo mPkgInfo;
    public String mPkgName;
    private String mAppName;
    private String mRealAppName;//当appName不是真实名字时，用此成员变量来保存它的真实软件名。
    private int mInfoType;
    private int mRsid = -1;
    private boolean bSelectedAppName = false;
    private String mStrWarning;
    private boolean bIsAdv2StdItem = false;
    private int mCleanFileFlag;
    private ArrayList<String> mCleanTimeFileList;
    private long mCacheFileNum;
    private long mCacheFolderNum;
    private int mCacheId;
    private byte mHaveNotCleaned;
    private int mPrivacyType;
    private String mDescription;
    private byte mCacheTableType;
    private byte mResultResource;
    private int isNeedCheck;
    private String mLanguage;
    private int mDeleteType;
    private int mContentType;
    private boolean mIsCanAddToPersonalCleanPlan;
    private long mlAdv2StdTime;
    private int contentType;
    private int appCount = 1;
    public CacheInfo(){
        super(BaseJunkBean.JUNK_CACHE);
    }

    private PackageInfo pkgInfo;
    public CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE junkType) {
        super(junkType);
    }
    @Override
    public String getName() {
        return null;
    }

    @Override
    public int compareTo(BaseJunkBean another) {
        return 0;
    }

    private int extendType = 1;
    public void setExtendType(int extendType) {
        this.extendType = extendType;
    }

    /**
     * 获取文件路径
     * @return
     */
    public String getFilePath(){
        return mFilePath;
    }
    public void setAppCount(int appCount) {
        this.appCount = appCount;
    }
    /**
     * 设置文件路径
     * @param strFilePath
     */
    public void setFilePath(String strFilePath){
        mFilePath = strFilePath;
    }

    /**
     * 获取清理时间
     * @return
     */
    public int getCleanTime() {
        return mCleanTime;
    }

    /**
     * 设置清理时间
     * @param nCleanTime
     */
    public void setCleanTime(int nCleanTime) {
        mCleanTime = nCleanTime;
    }

    /**
     * 获取pkg信息
     * @return
     */
    public PackageInfo getPackageInfo() {
        return mPkgInfo;
    }

    /**
     * 获取pkg name
     * @return
     */
    public String getPackageName() {
        if(null == mPkgInfo || null == mPkgInfo.applicationInfo){
            return "";
        }
        return mPkgInfo.applicationInfo.packageName;
    }

    /**
     * 设置pkgInfo
     * @param pkgInfo
     */
    public void setPackageInfo(PackageInfo pkgInfo) {
        mPkgInfo = pkgInfo;
    }

    /**
     * 获取info类型
     * @return
     */
    public int getInfoType() {
        return mInfoType;
    }

    /**
     * 设置info类型
     * @param infoType
     */
    public void setInfoType(int infoType) {
        mInfoType = infoType;
    }

    /**
     * 获取app名
     * @return
     */
    public String getAppName() {
        if (null == mAppName && INFOTYPE_SYSTEMCACHEITEM == mInfoType) {
            String strAppName = strAppName = mPkgInfo.applicationInfo.loadLabel(
                    SpaceApplication.getInstance().getContext().getPackageManager()).toString();

            if (!TextUtils.isEmpty(strAppName)) {
                mAppName = strAppName;
            }
        }

        if(this.mAppName != null && mRsid != -1 && !bSelectedAppName){
            String stableNameString = getTableName();
            String sAppname = this.mAppName;

            if(getCacheTableType() == DatabaseHelper.TABLE_ID_CACHE1){
                String newAppname = Commons.getLocalStringResourceOfDatabaseStringData(
                        DatabaseHelper.TABLE_NAME_CACHE1, DatabaseHelper.COL_ITEMNAME,
                        Md5Util.getPackageNameMd5(mPkgInfo.applicationInfo.packageName),
                        mRsid, sAppname);
                if (null != newAppname) {
                    sAppname = newAppname;
                    bSelectedAppName = true;
                }
            } else {
                sAppname = Commons.getLocalStringResourceOfDatabaseStringData(
                        stableNameString, DatabaseHelper.COL_ITEMNAME, mRsid, sAppname);
                bSelectedAppName = true;
            }

            setAppName(sAppname);
        }

        return mAppName;
    }

    /**
     * 设置app name
     * @param appName
     */
    public void setAppName(String appName) {
        mAppName = appName;
    }

    /**
     * 获取真实app name
     * @return
     */
    public String getRealAppName() {
        if (null == mRealAppName) {
            String strAppName = mPkgInfo.applicationInfo.loadLabel(SpaceApplication.getInstance().getContext()
                    .getPackageManager()).toString();

            if (!TextUtils.isEmpty(strAppName)) {
                mRealAppName = strAppName;
            }
        }

        return mRealAppName;
    }


    public void appendAllCleanTimeFileList(List<String> strPath) {
        if (null == strPath) {
            return;
        }

        if (null == mCleanTimeFileList) {
            mCleanTimeFileList = new ArrayList<String>();
        }
        mCleanTimeFileList.addAll(strPath);
    }

    /**
     * 获取表名
     * @return
     */
    private String getTableName(){
        String stableNameString = "";
        if(getCacheTableType() == DatabaseHelper.TABLE_ID_CACHE1){
            stableNameString = DatabaseHelper.TABLE_NAME_CACHE1;
        }else if(getCacheTableType() == DatabaseHelper.TABLE_ID_CACHE2){
            stableNameString = DatabaseHelper.TABLE_NAME_CACHE2;
        }else if(getCacheTableType() == DatabaseHelper.TABLE_ID_SYS_CACHE){
            stableNameString = DatabaseHelper.TABLE_NAME_SYS_CACHE;
        }
        return stableNameString;
    }

    public void setSrsid(int srsid){
        mRsid = srsid;
    }

    /**
     * 获取表类型
     * @return
     */
    public byte getCacheTableType() {
        return mCacheTableType;
    }

    public boolean isExistWaring() {
        return !TextUtils.isEmpty(mStrWarning);
    }

    public void setWarning(String strWarning) {
        mStrWarning = strWarning;
    }

    public int getCacheId() {
        return mCacheId;
    }

    public boolean isAdv2StdItem() {
        return bIsAdv2StdItem;
    }

    public void appendCleanTimeFileList(String strPath) {
        if (TextUtils.isEmpty(strPath)) {
            return;
        }

        if (null == mCleanTimeFileList) {
            mCleanTimeFileList = new ArrayList<String>();
        }
        mCleanTimeFileList.add(strPath);
    }

    public ArrayList<String> getCleanTimeFileList() {
        if (mCleanTimeFileList == null) {
            mCleanTimeFileList = new ArrayList<String>();
        }
        return mCleanTimeFileList;
    }

    public void setCacheFileNum(long num) {
        mCacheFileNum = num;
    }

    public long getCacheFileNum() {
        return mCacheFileNum;
    }

    public void setCacheFolderNum( long num ) {
        mCacheFolderNum = num;
    }

    public long getCacheFolderNum() {
        return mCacheFolderNum;
    }

    public void setAdv2StdItemFlag(boolean adv2Std) {
        bIsAdv2StdItem = adv2Std;
    }

    public void setCleanFileFlag(int cleanFileFlag){
        mCleanFileFlag = cleanFileFlag;
    }

    public byte getHaveNotCleaned() {
        return mHaveNotCleaned;
    }

    public void setHaveNotCleaned(byte haveNotCleaned) {
        this.mHaveNotCleaned = haveNotCleaned;
    }

    public void setPrivacyType(int privacyType) {
        mPrivacyType = privacyType;
    }

    public void setDescption(String descption) {
        mDescription = descption;
    }

    public void setCacheTableTypeId(int tableType, int signId) {
        mCacheTableType=(byte) tableType;
        mCacheId = signId;
    }

    public void setResultSource(byte resultSource) {
        mResultResource = resultSource;
    }

    public void setNeedCheck(int isNeedCheck) {
        this.isNeedCheck = isNeedCheck;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public void setDeleteType(int deleteType) {
        mDeleteType = deleteType;
    }

    public void setContentType(int contentType) {
        mContentType = contentType;
    }

    public void configIsCanAddToPersonalCleanPlan(int nCleanTime) {
        if (nCleanTime > 0 && nCleanTime < CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE) {
            mIsCanAddToPersonalCleanPlan = false;
        } else {
            mIsCanAddToPersonalCleanPlan = true;
        }
    }

    public void setAdv2StdTime(long lAdv2StdTime) {
        mlAdv2StdTime = lAdv2StdTime;
    }
    public boolean isVideoType() {
        return contentType == 12;
    }

    public boolean isApkType() {
        return contentType == 19||contentType == 17;
    }

    public boolean isMapType() {
        return contentType == 14;
    }

    public boolean isMusicType() {
        return contentType == 11;
    }

    public boolean isImageType() {
        return contentType == 2 || contentType == 3 || contentType == 4 || contentType == 9 || contentType == 13
                || contentType == 15;
    }

    public int getCleanFileFlag(){
        return mCleanFileFlag;
    }

    public int getDeleteType() {
        return mDeleteType;
    }

    public boolean isPicRecycleType(){
        return (contentType == 20)
                || KCacheCloudQuery.CleanMediaFlagUtil.isToRecycleBin(mCleanFileFlag);
    }

    public String getDescritpion() {
        return mDescription;
    }

    @Override
    public String toString() {
        return "CacheInfo{" +
                "mFilePath='" + mFilePath + '\'' +
                ", mbCheck=" + mbCheck +
                ", mCleanTime=" + mCleanTime +
                ", mPkgInfo=" + mPkgInfo +
                ", mAppName='" + mAppName + '\'' +
                ", mRealAppName='" + mRealAppName + '\'' +
                ", mInfoType=" + mInfoType +
                ", mRsid=" + mRsid +
                ", bSelectedAppName=" + bSelectedAppName +
                ", mStrWarning='" + mStrWarning + '\'' +
                ", bIsAdv2StdItem=" + bIsAdv2StdItem +
                ", mCleanFileFlag=" + mCleanFileFlag +
                ", mCleanTimeFileList=" + mCleanTimeFileList +
                ", mCacheFileNum=" + mCacheFileNum +
                ", mCacheFolderNum=" + mCacheFolderNum +
                ", mCacheId=" + mCacheId +
                ", mHaveNotCleaned=" + mHaveNotCleaned +
                ", mPrivacyType=" + mPrivacyType +
                ", mDescription='" + mDescription + '\'' +
                ", mCacheTableType=" + mCacheTableType +
                ", mResultResource=" + mResultResource +
                ", isNeedCheck=" + isNeedCheck +
                ", mLanguage='" + mLanguage + '\'' +
                ", mDeleteType=" + mDeleteType +
                ", mContentType=" + mContentType +
                ", mIsCanAddToPersonalCleanPlan=" + mIsCanAddToPersonalCleanPlan +
                ", mlAdv2StdTime=" + mlAdv2StdTime +
                ", contentType=" + contentType +
                ", appCount=" + appCount +
                ", pkgInfo=" + pkgInfo +
                ", extendType=" + extendType +
                '}';
    }

    /**
     * SystemCache
     * @return
     */
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("mFilePath", mFilePath);
            obj.put("mbCheck", mbCheck);
            obj.put("mCleanTime", mCleanTime);
            if (TextUtils.isEmpty(getPackageName())){
                obj.put("mPkgName", mPkgName);
            }else {
                obj.put("mPkgName", getPackageName());
            }
            obj.put("mAppName", mAppName);
            obj.put("mInfoType", mInfoType);
            obj.put("mFileType", getFileType());
            obj.put("mJunkInfoType", getJunkDataType());//"SYSCACHE"
            obj.put("mSize", mSize);
            obj.put("mJunkType",getJunkType());
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
        return obj;
    }
}
