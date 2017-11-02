package com.clean.spaceplus.cleansdk.junk.engine.bean;

import android.content.Context;

import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.util.ApkParser;
import com.clean.spaceplus.cleansdk.util.PackageUtils;

import java.io.File;
import java.io.Serializable;

/**
 * @author liangni
 * @Description:
 * @date 2016/5/3 20:05
 * @copyright TCL-MIG
 */
public class APKModel extends BaseJunkBean implements Serializable, Comparable<BaseJunkBean>{
    private static final long serialVersionUID = -3498260819936152076L;

    public static final int APK_CATE_INSTALLED=1;
    public static final int APK_INSTALLED=2;
    public static final int APK_CATE_NOT_INSTALLED=3;
    public static final int APK_NOT_INSTALLED=4;

    public static final int APK_STATUS_OLD = 0;
    public static final int APK_STATUS_CUR = 1;
    public static final int APK_STATUS_NEW = 2;

    public static final int APK_FOUND_PRORITY_LOW = 0;
    public static final int APK_FOUND_PRIORITY_HIGH = 1;

    private String mPackageName;
    private String title;
    private String version;
    private String path;
    private int mFoundPriority = 1;
    private boolean checked=true;
    private long modifyTime;
    private boolean installedByApkName=true;
    private int apkInstallStatus = APK_STATUS_OLD;
    public int type = -1;    //Model类型
    private String fileName;
    private boolean broken = false;
    private int versionCode = 0;
    private int appVersionCode = 0;
    private int mCheckType = 0;
    private int mDisplayType = 0;
    private boolean mIsExpend = false;
    private boolean mIsBackup = false;
    private boolean mIsDisplay = true;
    private boolean mIsWhiteFile = false;
    private boolean mIsUninstalledNewDL = false;
    /** 是否有相同路径的垃圾文件，如果存在，则不计算它的size，for 手机瘦身 */
    private boolean mHasSamePathJunk;
    private boolean m_bIsUserFilterProbe = false;

    public boolean hasSamePathJunk() {
        return mHasSamePathJunk;
    }

    public void setHasSamePathJunk(boolean hasSamePathJunk) {
        this.mHasSamePathJunk = hasSamePathJunk;
    }

    public APKModel() {
        super(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE);
    }

    public boolean isExpended(){
        return mIsExpend;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }
    /**
     * @param packageName the apkName to set
     */
    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }
    /**
     * @return the apkName
     */
    public String getPackageName() {
        return mPackageName;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    /**
     * @param modifyTime the modifyTime to set
     */
    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }
    /**
     * @return the modifyTime
     */
    public long getModifyTime() {
        return modifyTime;
    }
    /**
     * @param installed 此名字的apk包是否已经安装(未比较版本)
     */
    public void setInstalledByApkName(boolean installed) {
        this.installedByApkName = installed;
    }
    /**
     * @return 此名字的apk包是否已经安装(未比较版本)
     */
    public boolean isInstalledByApkName() {
        return installedByApkName;
    }
    /**
     * @param apkStatus 此名字的apk包是否已经安装，或者是否新旧版本。
     */
    public void setApkInstallStatus(int apkStatus) {
        this.apkInstallStatus = apkStatus;
    }
    /**
     * @return 此名字的apk包是否已经安装，或者是否新旧版本。
     */
    public int getApkInstallStatus() {
        return apkInstallStatus;
    }
    /**
     * @param checked the checked to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
        super.setCheck(checked);
    }
    /**
     * @return the checked
     */
    public boolean isChecked() {
        return super.isCheck();
    }
    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    /**
     * @param broken the broken to set
     */
    public void setBroken(boolean broken) {
        this.broken = broken;
    }
    /**
     * @return the broken
     */
    public boolean isBroken() {
        return broken;
    }

    public void setVersionCode(int verCode) {
        versionCode = verCode;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public boolean isInstalled() {
        return type == APK_INSTALLED;
    }

    public void setIsBackup(boolean bIsBackup) {
        mIsBackup = bIsBackup;
    }

    public boolean isBackup() {
        return mIsBackup;
    }

    public void setIsDisplay(boolean bIsDisplay) {
        mIsDisplay = bIsDisplay;
    }

    public boolean isDisplay() {
        return mIsDisplay;
    }

    public void setIsUninstalledNewDL(boolean bIsUninstallNewDL) {
        mIsUninstalledNewDL = bIsUninstallNewDL;
    }

    public boolean isUninstalledNewDL() {
        return mIsUninstalledNewDL;
    }

    public void setCheckType(int checkType) {
        mCheckType = checkType;
    }

    public int getCheckType() {
        return mCheckType;
    }

    public void setDisplayType(int displayType) {
        mDisplayType = displayType;
    }

    public int getDisplayType() {
        return mDisplayType;
    }

    public void setIsWhiteFile(boolean isWhite) {
        mIsWhiteFile = isWhite;
    }

    public boolean getIsWhiteFile() {
        return mIsWhiteFile;
    }

    public void setApkFoundPriority(int level) {
        mFoundPriority = level;
    }

    public int getApkFoundPriority() {
        return mFoundPriority;
    }

    //自定义排序
    @Override
    public int compareTo(BaseJunkBean another) {
        if(null == this || null == another || this == another || this.title.length() == 0 || ((APKModel)another).title.length() == 0)
            return 0;

        int nResult = this.title.compareToIgnoreCase(((APKModel)another).title);
        return nResult;
    }
    @Override
    public String toString() {
        return "APKModel [packageName=" + mPackageName + ", title=" + title + ", size=" + getSize() + ", version=" + version
                + ", path=" + path + ", checked=" + checked + ", modifyTime=" + modifyTime + ", installedByApkName="
                + installedByApkName + ", apkInstallStatus=" + apkInstallStatus + ", type=" + type + ", fileName="
                + fileName + ", broken=" + broken + ", versionCode=" + versionCode + "]";
    }

    public static APKModel create(Context context, File target) {
        ApkParser parser = new ApkParser(context);
        APKModel x = parser.parseApkFile(target);
        if(null == x){
            return null;
        }
        if(x.isInstalledByApkName()) {
            x.setTitle(PackageUtils.getAppNameByPackageName(context, x.mPackageName));
        }
        return x;
    }

    @Override
    public String getName() {
        return getTitle();
    }

    public boolean isInUserFilterFolder() {
        return m_bIsUserFilterProbe;
    }

    public void setCurrentApkIsInUserFolder() {
        m_bIsUserFilterProbe = true;
    }
}
