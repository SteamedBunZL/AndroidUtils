package com.clean.spaceplus.cleansdk.junk.engine.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.base.db.process_list.dao.APKParserCacheDAOHelper;
import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.ApkParseData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: APK解析类
 * @date 2016/5/10 20:38
 * @copyright TCL-MIG
 */
public class ApkParser {
    public static final String TAG = ApkParser.class.getSimpleName();

    public ApkParser(Context ctx) {
        mCtx = ctx;
    }
    private int  tid = android.os.Process.myTid();
    private static int PID =android.os.Process.myPid();

    public boolean initApkParser() {
        if (null == mCtx) {
            return false;
        }

        if (null != mInstalledPackageInfo && null != apkParserBaseDaoImp) {
            return true;
        }

        try{
            if (null == mInstalledPackageInfo) {
                mInstalledPackageInfo = PackageManagerWrapper.getInstance().getPkgInfoList();
            }

            if (null == apkParserBaseDaoImp) {
                apkParserBaseDaoImp = APKParserCacheDAOHelper.getInstance();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        mStop = false;

        return true;
    }

//    private boolean checkZipFile2(final File zipFile){
//        //long nStart = System.currentTimeMillis();
//        boolean soLoadResult = false;//SoLoader.doLoad(false);
//        NLog.d(TAG, "checkZipFile2 soLoadResult = %b", soLoadResult);
//        if ( !soLoadResult )
//            return false;
//
//        boolean bres = com.clean.natv.a.a(zipFile.getAbsolutePath());
//        //CMLog.d("APKParser", "time:" + (System.currentTimeMillis()-nStart));
//        return bres;
//    }

//    public static final long LOCSIG = 0x4034b50;
//
//    static class RAFStream extends InputStream {
//        private final RandomAccessFile sharedRaf;
//        private long endOffset;
//        private long offset;
//
//        public RAFStream(RandomAccessFile raf, long initialOffset)
//                throws IOException {
//            sharedRaf = raf;
//            offset = initialOffset;
//            endOffset = raf.length();
//        }
//
//        @Override
//        public int available() throws IOException {
//            return (offset < endOffset ? 1 : 0);
//        }
//
//        @Override
//        public int read() throws IOException {
//            return Streams.readSingleByte(this);
//        }
//
//        @Override
//        public int read(byte[] buffer, int byteOffset, int byteCount)
//                throws IOException {
//            synchronized (sharedRaf) {
//                final long length = endOffset - offset;
//                if (byteCount > length) {
//                    byteCount = (int) length;
//                }
//                sharedRaf.seek(offset);
//                int count = sharedRaf.read(buffer, byteOffset, byteCount);
//                if (count > 0) {
//                    offset += count;
//                    return count;
//                } else {
//                    return -1;
//                }
//            }
//        }
//
//        @Override
//        public long skip(long byteCount) throws IOException {
//            if (byteCount > endOffset - offset) {
//                byteCount = endOffset - offset;
//            }
//            offset += byteCount;
//            return byteCount;
//        }
//
//    }

    /**
     * 是否 相等
     * @param newModel
     * @param oldModel
     * @return
     */
    private boolean compare(APKModel newModel, APKModel oldModel) {

        if (newModel != null && oldModel != null) {
            // 路径相等
            if (newModel.getPath().equals(oldModel.getPath())) {
                // 大小相等
                if (newModel.getSize() == oldModel.getSize()) {
                    // 最后修改时间相等
                    if (newModel.getModifyTime() == oldModel.getModifyTime()) {
                        //不需要更新
                        return true;
                    }
                }
            }

        }
        return false;
    }


    /**
     *
     * @param apkModel
     * @return
     */
    private boolean setPackageInfo(APKModel apkModel) throws PackageManager.NameNotFoundException
    {
        try {
            ApplicationInfo appInfo = null;
            PackageManager pm = mCtx.getApplicationContext().getPackageManager();

            PackageInfo packageInfo = pm.getPackageArchiveInfo(
                    apkModel.getPath(),
                    0);
            if (packageInfo != null) {
                appInfo = packageInfo.applicationInfo;
                appInfo.sourceDir = apkModel.getPath();
                appInfo.publicSourceDir = apkModel.getPath();

                if(appInfo != null)
                {
                    apkModel.setPackageName(appInfo.packageName);
                }

                CharSequence clabel = appInfo.loadLabel(pm);
                if ( clabel != null ){
                    apkModel.setTitle(clabel.toString());
                }
                else{

                    return false;
                }

                apkModel.setVersion(packageInfo.versionName);
                apkModel.setVersionCode(packageInfo.versionCode);
            }
            return true;
        } catch (OutOfMemoryError e) {
            // TODO: handle exception
        }
        return false;
    }


    //缓存列表
    private ArrayMap<String , APKModel> mCacheArrayMap = null;

    //更新列表
    private List<String> mUpdateList = null;

    public boolean isUpdateBlock()
    {
        return null != apkParserBaseDaoImp&&apkParserBaseDaoImp.isUpdateBlock;
    }

    public void updateCache()
    {
        if (null != apkParserBaseDaoImp) {
            apkParserBaseDaoImp.updateCahce(mCacheArrayMap, mUpdateList);
        }
    }

    private boolean apkErrorMonitorStartParser(String filePath)
    {
        ////FIXME BY Davis
        boolean bRet = true;

//        try {
//            bRet = SyncIpcCtrl.getIns().getIPCClient().scanApkFile(PID,tid,filePath, ApkScanMonitor.TYPE_SCAN_APK_BEGIN);
//        } catch (RemoteException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        return bRet;
    }

    private void apkErrorMonitorEndParser(String filePath){

        //FIXME BY Davis
//        try {
//            SyncIpcCtrl.getIns().getIPCClient().scanApkFile(PID,tid,filePath, ApkScanMonitor.TYPE_SCAN_APK_END);
//        } catch (RemoteException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    public APKModel parseApkFile(ApkParseData apkFile){
        APKModel apkModel = null;
        if(!apkErrorMonitorStartParser(apkFile.getApkParseDataFile().getAbsolutePath())){
            return null;
        }
        /**
         * 添加 异常捕获  过滤  非APK包损坏  造成的奔溃
         */
        try {
            apkModel = parseApkFileInternal(apkFile);
        } catch (Exception e) {
            // TODO: handle exception
        }
        apkErrorMonitorEndParser(apkFile.getApkParseDataFile().getAbsolutePath());
        return apkModel;
    }

    public APKModel parseApkFile(File apkFile){
        APKModel apkModel = null;
        if(!apkErrorMonitorStartParser(apkFile.getAbsolutePath())){
            return null;
        }
        /**
         * 添加 异常捕获  过滤  非APK包损坏  造成的奔溃
         */
        try {
            apkModel = parseApkFileInternal(apkFile);
        } catch (Exception e) {
            // TODO: handle exception
        }
        apkErrorMonitorEndParser(apkFile.getAbsolutePath());
        return apkModel;
    }

    private void fixAPKTitle(APKModel apkModel) {
        if (null == apkModel || TextUtils.isEmpty(apkModel.getTitle())) {
            return;
        }

        if (0 == apkModel.getTitle().compareToIgnoreCase("com.alipay.mobile.command.logMonitor")) {
            apkModel.setTitle(mCtx.getString(R.string.apk_title_alipay_plugin));
        }

    }

    public boolean parseApkFile(APKModel apkModel) {
        try {
            APKModel cacheModel =  mCacheArrayMap.get(apkModel.getPath());

            boolean isUpdateCache = !compare(apkModel, cacheModel);
            //不存在库中    或者      与库中版本不一致
            if(cacheModel == null||isUpdateCache)
            {
                //解析apk包 获取信息
                if(!setPackageInfo(apkModel))
                {
                    //解析失败  直接返回
                    return false;
                }
            }else
            {
                apkModel.setTitle(cacheModel.getTitle());
                apkModel.setVersion(cacheModel.getVersion());
                apkModel.setVersionCode(cacheModel.getVersionCode());
                apkModel.setPackageName(cacheModel.getPackageName());
            }

            fixAPKTitle(apkModel);

            if (mStop) {
                return false;
            }

            if ( !TextUtils.isEmpty(apkModel.getPackageName())  ){
                boolean installedByApkName = false;
                int apkInstallStatus = APKModel.APK_STATUS_OLD;
                if (null != mInstalledPackageInfo) {
                    for (PackageInfo item : mInstalledPackageInfo) {
                        if (mStop) {
                            break;
                        }

                        if (item.applicationInfo.packageName
                                .equals(apkModel.getPackageName())) {

                            installedByApkName = true;
                            apkModel.setAppVersionCode(item.versionCode);
                            if (apkModel.getVersionCode() < item.versionCode) {
                                apkInstallStatus = APKModel.APK_STATUS_OLD;
                            }
                            else if (apkModel.getVersionCode() > item.versionCode) {
                                apkInstallStatus = APKModel.APK_STATUS_NEW;
                            }
                            else if (apkModel.getVersion().compareToIgnoreCase(item.versionName) == 0){
                                // 防止部分apk包不修改versionCode，只改versionName的情况。
                                apkInstallStatus = APKModel.APK_STATUS_CUR;
                            }
                            else if (apkModel.getVersion().compareToIgnoreCase(item.versionName) < 0){
                                // 防止部分apk包不修改versionCode，只改versionName的情况。
                                apkInstallStatus = APKModel.APK_STATUS_OLD;
                            }
                            else {
                                // 防止部分apk包不修改versionCode，只改versionName的情况。
                                apkInstallStatus = APKModel.APK_STATUS_NEW;
                            }

                            break;
                        }
                    }
                    if(isUpdateCache)
                    {

                        if(mUpdateList ==null){
                            mUpdateList = new ArrayList<String>();
                        }

                        mCacheArrayMap.put(apkModel.getPath(), apkModel);
                        mUpdateList.add(apkModel.getPath());

                    }
                    if (mStop) {
                        return false;
                    }
                }

                apkModel.setInstalledByApkName(installedByApkName);

                if (!installedByApkName) // 是否已安装
                {
                    apkModel.type = APKModel.APK_NOT_INSTALLED;
                } else {
                    apkModel.setApkInstallStatus(apkInstallStatus);
                    apkModel.type = APKModel.APK_INSTALLED;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public APKModel parseApkFileInternal(ApkParseData apkFile) {
        File file = apkFile.getApkParseDataFile();
//        if(!checkZipFile2(file)) // TODO 这个是判断文件是否损坏的，但是so库有问题，暂时调用不了
//        {
//            return null;
//        }

        if (mCacheArrayMap == null) {
            if(apkParserBaseDaoImp != null){
                mCacheArrayMap = apkParserBaseDaoImp.getAllApkCache();
            }

            // 库中无缓存
            if (mCacheArrayMap == null) {
                mCacheArrayMap = new ArrayMap<String, APKModel>();
            }
        }

        if (file.length() <= 0) {
            return null;
        }

        APKModel apkModel = new APKModel();
        apkModel.setSize(file.length());
        apkModel.setPath(file.getAbsolutePath());
        apkModel.setFileName(file.getName());
        apkModel.setModifyTime(file.lastModified());
        apkModel.setIsWhiteFile(apkFile.getApkParseDataWhiteInfo().getIsWhiteFile());
        apkModel.setDisplayType(apkFile.getApkParseDataWhiteInfo().getDisplayType());
        apkModel.setCheckType(apkFile.getApkParseDataWhiteInfo().getCheckType());
        if (parseApkFile(apkModel)) {
            return apkModel;
        }
        return null;
    }

    public APKModel parseApkFileInternal(File apkFile) {
        //先注释掉这里 因为安装时检查残留安装包的时候 checkZipFile2方法里面没有价值so库 导致一直返回false
      /*  if(!checkZipFile2(apkFile))
        {
            return null;
        }*/

        if (mCacheArrayMap == null) {
            if(apkParserBaseDaoImp != null){
                mCacheArrayMap = apkParserBaseDaoImp.getAllApkCache();
            }

            // 库中无缓存
            if (mCacheArrayMap == null) {
                mCacheArrayMap = new ArrayMap<String, APKModel>();
            }
        }

        if (apkFile.length() <= 0) {
            return null;
        }

        APKModel apkModel = new APKModel();
        apkModel.setSize(apkFile.length());
        apkModel.setPath(apkFile.getAbsolutePath());
        apkModel.setFileName(apkFile.getName());
        apkModel.setModifyTime(apkFile.lastModified());

        if (parseApkFile(apkModel)) {
            return apkModel;
        }
        return null;
    }

/*	public List<PackageInfo> getInstalledPackageInfo() {
		return mInstalledPackageInfo;
	}*/

    public void notifyStop() {
        mStop = true;
    }

    private boolean 			mStop = false;
    private Context				mCtx = null;
    private List<PackageInfo>	mInstalledPackageInfo = null;
    private APKParserCacheDAOHelper apkParserBaseDaoImp = null;
}
