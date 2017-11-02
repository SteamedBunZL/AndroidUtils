package com.clean.spaceplus.cleansdk.setting.update.mgmt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.util.ArrayMap;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.exception.ProbeCrash;
import com.clean.spaceplus.cleansdk.base.utils.system.VersionUtil;
import com.clean.spaceplus.cleansdk.util.ComponentUtils;
import com.clean.spaceplus.cleansdk.util.Env;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.IniResolver;
import com.clean.spaceplus.cleansdk.util.Miscellaneous;
import com.clean.spaceplus.cleansdk.util.SharePreferenceUtil;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import space.network.util.RuntimeCheck;

/**
 * @author dongdong.huang
 * @Description:本地升级管理
 * @date 2016/5/3 10:50
 * @copyright TCL-MIG
 */
public class UpdateManager {
    public static synchronized UpdateManager getInstance() {
        if (updateMgr == null) {
            updateMgr = new UpdateManager();
        }
        return updateMgr;
    }

    // 初始化（程序启动时调用）
    public synchronized boolean initialize(Context context) {
        if (mbInited) {
            return true;
        }
        mbInited = true;
        ProbeCrash.ProbeAsset(context);

        try {
            this.context = context;

            // 加载资源中的版本信息文件
            final IniResolver resVersionIni = new IniResolver();
            {
                AssetManager assertManager = context.getAssets();

                InputStream verIStream = null;
                InputStreamReader verReader = null;

                try {
                    verIStream = assertManager.open(VERSION_INI);
                    verReader = new InputStreamReader(verIStream, ENCODING);

                    if (!resVersionIni.load(verReader)) {
                        return false;
                    }
                } finally {
                    if (verReader != null) {
                        verReader.close();
                    }
                    if (verIStream != null) {
                        verIStream.close();
                    }
                }
            }

            // 读取配置
            {
                urlRoot = FileUtils.addSlash(resVersionIni.getValue(FileUtils.ID_CONFIG, INI_KEY_URL_ROOT));
                urlDLRoot = FileUtils.addSlash(resVersionIni.getValue(FileUtils.ID_CONFIG, INI_KEY_DL_URL_ROOT));
                versionApkIni = resVersionIni.getValue(FileUtils.ID_CONFIG,
                        INI_KEY_VERSION_APK_INI);
                versionDataIni = resVersionIni.getValue(FileUtils.ID_CONFIG,
                        INI_KEY_VERSION_DATA_INI);
                versionApkNewIni = resVersionIni.getValue(FileUtils.ID_CONFIG,
                        INI_KEY_VERSION_APK_NEW_INI);
                if (Miscellaneous.isEmpty(versionApkNewIni)) {
                    versionApkNewIni = versionApkIni;
                }
            }

            // 初始化apk版本与数据目录
            {
                PackageManager packManager = context.getPackageManager();

                ApplicationInfo appInfo = packManager.getApplicationInfo(
                        context.getPackageName(), 0);
                apkPackPath = appInfo.sourceDir;

                // 外部扩展目录
                pathExternal = resVersionIni.getValue(FileUtils.ID_DATA,
                        INI_KEY_PATH_EXTERNAL);

                datadirPkg = appInfo.dataDir + File.separator + "updatedata";

                (new File(datadirPkg)).mkdirs();

                // 内部数据目录
                dataDir = FileUtils.addSlash(appInfo.dataDir)
                        + resVersionIni.getValue(FileUtils.ID_DATA,
                        INI_KEY_PATH_DATA);
                dataDir = FileUtils.addSlash(dataDir);
                (new File(dataDir)).mkdirs();

                // Cache目录
//                UpdateCache updateCache = UpdateCache.getInstance();
//                updateCache.initialize(dataDir + resVersionIni.getValue(FileUtils.ID_DATA, INI_KEY_PATH_CACHE));
//				}

                // 创建内部数据目录
                String pathOther = resVersionIni.getValue(FileUtils.ID_DATA,
                        INI_KEY_PATH_OTHER);
                if (!Miscellaneous.isEmpty(pathOther)) {
                    String[] pathList = pathOther.split(";");
                    if (pathList != null && RuntimeCheck.IsUIProcess()) {
                        for (final String path : pathList) {
                            (new File(dataDir + path)).mkdirs();
                        }
                    }
                }

                PackageInfo packInfo = packManager.getPackageInfo(
                        context.getPackageName(), 0);
                apkVersion = VersionUtil.translateDecimal(packInfo.versionCode);

                if (RuntimeCheck.IsUIProcess()) {
                    //BUGBUGBUG
                    deleteTmpFile(getApkExternalPath());
                    deleteTmpFile(getApkInternalPath());
                    new File(getApkInternalPath()).delete();
                }
            }

            // 更新数据版本
            if ( RuntimeCheck.IsServiceProcess() )
            {
                SharedPreferences updateInfo = context.getSharedPreferences(
                        updateInfoFileName, 0);

                String resVersion = resVersionIni.getValue(FileUtils.ID_DATA, INI_KEY_VERSION);
                String locVersion = getDataVersion(); //updateInfo.getString(CFG_DATA_VERSION, null);
                if (VersionUtil.compare(resVersion, locVersion) > 0) {
                    // 保存数据版本
//					Editor editor = updateInfo.edit();
//					editor.putString(CFG_DATA_VERSION, resVersion);
//					SharePreferenceUtil.applyToEditor(editor);
                    NLog.d("Updatemanager", "set ressource version ");
                    setDataVersion(resVersion);
                }else{
                    NLog.d("Updatemanager", "not set ressource version ");
                }

                if (!updateInfo.getString(CFG_CHECK_IS_FROM_UPDATE,
                        apkVersion + "_" + "false").equalsIgnoreCase(
                        apkVersion + "_" + "true")) {
                    if (getInstallTime() != 0) {
                        setInstallTime(apkVersion);
                    }
                    SharedPreferences.Editor editor = updateInfo.edit();
                    editor.putString(CFG_CHECK_IS_FROM_UPDATE, apkVersion + "_" + "true");
                    SharePreferenceUtil.applyToEditor(editor);
                }
            }

            itemMap = new ArrayMap<String, Item>();

            // 确保assets文件最新版在指定路径下
//            if (Env.isMultiProc) {
//                BackgroundThread.getHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!RuntimeCheck.IsServiceProcess()) {
//                            try {
//                                SyncIpcCtrl.getIns().getIPCClient().initUpdateManagerAssetsFiles();
//                            } catch (RemoteException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        AssetsFileVersionControll.getInstance().controllVersionSync(resVersionIni,RuntimeCheck.IsServiceProcess());
//                    }
//                });
//            } else {
//
//                BackgroundThread.getHandler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!RuntimeCheck.IsServiceProcess()) {
//                            try {
//                                SyncIpcCtrl.getIns().getIPCClient().initUpdateManagerAssetsFiles();
//                            } catch (RemoteException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        AssetsFileVersionControll.getInstance().controllVersionSync(resVersionIni,RuntimeCheck.IsServiceProcess());
//                    }
//                }, 300);
//            }

            return true;
        } catch (Exception e) {
            // 这里还是不要返回失败，而是让它崩溃，更有利于我们改进。
            NLog.printStackTrace(e);
            return false;
        }


    }

    public boolean initializeAssetsFilesSync(Context context) {
        ProbeCrash.ProbeAsset(context);
        try {

            // 加载资源中的版本信息文件
            IniResolver resVersionIni = new IniResolver();
            {
                AssetManager assertManager = context.getAssets();

                InputStream verIStream = null;
                InputStreamReader verReader = null;

                try {
                    verIStream = assertManager.open(VERSION_INI);
                    verReader = new InputStreamReader(verIStream, ENCODING);

                    if (!resVersionIni.load(verReader)) {
                        return false;
                    }
                } finally {
                    if (verReader != null) {
                        verReader.close();
                    }
                    if (verIStream != null) {
                        verIStream.close();
                    }
                }
            }

//            AssetsFileVersionControll.getInstance().controllVersionSync(resVersionIni,RuntimeCheck.IsServiceProcess());
            copyBSPatchFromAssert();
            return true;
        } catch (Exception e) {
            // 这里还是不要返回失败，而是让它崩溃，更有利于我们改进。
            NLog.printStackTrace(e);
            return false;
        }
    }
    public String getBSPatchPath(){
        return mBS_PATCH_PATH;
    }
    private void copyBSPatchFromAssert(){
        if(mBS_PATCH_PATH != null && mBS_PATCH_PATH.length() > 0){
            return;
        }
        Context context = SpaceApplication.getInstance().getContext();
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            NLog.d("UpdateManager","copy bs patch from assert, name not found error");
            e.printStackTrace();
            return;
        } catch (Exception e) {
            NLog.d("UpdateManager","copy bs patch from assert, exception");
            e.printStackTrace();
            return;
        }

        mBS_PATCH_PATH = (FileUtils.addSlash(appInfo.dataDir) + "bspatch");

        InputStream is = null;
        FileOutputStream os = null;

        AssetManager assertManager = context.getAssets();
        try {
            is = assertManager.open("bspatch");
            os = new FileOutputStream(mBS_PATCH_PATH);

            final int BUF_SIZE = 4096;
            byte[] buffer = new byte[BUF_SIZE];

            int bytes = 0;
            do {
                bytes = is.read(buffer);
                if (bytes > 0) {
                    os.write(buffer, 0, bytes);
                } else {
                    break;
                }
            } while (true);
            os.flush();
            os.close();
            os = null;
        }catch(Exception e){
            NLog.d("UpdateManager"," copy bspatch from assert to /data/data/cleanmaster_cn failed");
        }finally{
            try {
                if(is != null){
                    is.close();
                    is = null;
                }
                if(os != null){
                    os.close();
                }
            } catch (Exception e2) {
                // TODO: handle exception
            }

        }
    }

    public  boolean copyFileFromRaw(int resId, File file,Context context) {
        Resources rs = context.getResources();
        if (file.exists()) {
            AssetFileDescriptor assetFD = rs.openRawResourceFd(resId);
            if (null != assetFD) {
                try {
                    if (file.length() == assetFD.getLength()) {
                        return false;
                    }
                } finally {
                    try {
                        assetFD.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        FileOutputStream out = null;
        InputStream is = null;
        try {
            out = new FileOutputStream(file, false);
            is = context.getResources().openRawResource(resId);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final String getUrlRoot() {
        return urlRoot;
    }

    public final String getDLUrlRoot() {
        return urlDLRoot;
    }

//	public String getVersionApkIni() {
//		return versionApkIni;
//	}

    public String getVersionApkNewIni() {
        return versionApkNewIni;
    }

    public String getVersionDataIni() {
        return versionDataIni;
    }

    public void putItem(String id, Item item){
        if(null==id || null==item || null==itemMap){
            return;
        }
        itemMap.put(id, item);
    }

    public boolean hasItem(String id) {
        if(null==itemMap){
            return false;
        }

        Item item = itemMap.get(id);
        return item != null;
    }

    public int getItemFileType(String id) {
        Item item = itemMap.get(id);
        if (item != null) {
            return item.fileType;
        }
        return FileUtils.TYPE_UNKNOWN;
    }

    public String getItemPath(String id) {
        if(null == itemMap){
            return null;
        }

        Item item = itemMap.get(id);
        if (item != null) {
            return dataDir + item.dataPath;
        }
        return null;
    }

    public String getItemVersion(String id) {
        Item item = itemMap.get(id);
        if (item != null) {
            NLog.d("UpdateManager","the path=" + dataDir + item.dataPath);
            return FileUtils.getFileVersion(item.fileType, dataDir
                    + item.dataPath);
        }
        return null;
    }

    public String getItemDescription(String id) {
        Item item = itemMap.get(id);
        if (item != null) {
            return item.description;
        }
        return null;
    }

    public boolean isForceUpdate() {
        SharedPreferences updateInfo = context.getSharedPreferences(
                updateInfoFileName, 0);
        String version = updateInfo.getString(CFG_IS_FORCE_UPDATE, null);
        return (version != null && version.compareToIgnoreCase(getApkVersion()) == 0);
    }

    public boolean isPreferFullUpdate(){
        boolean isprefer = true;
        SharedPreferences updateInfo = context.getSharedPreferences(
                updateInfoFileName, 0);
        String prefer = updateInfo.getString(CFG_PREFER_FULL_UPDATE_BRANCH, null);

        //defalut is not wild branch
        if(prefer == null){
            isprefer = false;
            SharedPreferences.Editor editor = updateInfo.edit();
            editor.putString(CFG_PREFER_FULL_UPDATE_BRANCH, UN_PREFER_FULL_UPDATE);
            editor.commit();
        }else if(prefer != null && PREFER_FULL_UPDATE.equals(prefer)){
            isprefer = true;
        }else if(prefer != null && UN_PREFER_FULL_UPDATE.equals(prefer)){
            isprefer = false;
        }
        return isprefer;
    }

    public void setPreferFullUpdate(boolean isPreferFullUpdate){
        SharedPreferences updateInfo = context.getSharedPreferences(
                updateInfoFileName, 0);
        SharedPreferences.Editor editor = updateInfo.edit();
        String sPrferString = null;
        if(isPreferFullUpdate){
            sPrferString = PREFER_FULL_UPDATE;
        }else{
            sPrferString = UN_PREFER_FULL_UPDATE;
        }
        editor.putString(CFG_PREFER_FULL_UPDATE_BRANCH, sPrferString);
        editor.commit();
    }
    public void setForceUpdate() {
        SharedPreferences updateInfo = context.getSharedPreferences(
                updateInfoFileName, 0);
        SharedPreferences.Editor editor = updateInfo.edit();
        editor.putString(CFG_IS_FORCE_UPDATE, getApkVersion());
        SharePreferenceUtil.applyToEditor(editor);
    }

    public long getInstallTime() {
        SharedPreferences updateInfo = context.getSharedPreferences(
                updateInfoFileName, 0);
        String installTime = updateInfo.getString(CFG_UPDATE_TIME, null);
        if (installTime != null) {
            if (installTime.startsWith(getApkVersion())) {
                return Long
                        .parseLong(installTime.substring(
                                installTime.lastIndexOf("_") + 1,
                                installTime.length()));
            }
        }
        return 0;
    }

    public void setInstallTime(String version) {
        long currentTime = System.currentTimeMillis();
        SharedPreferences updateInfo = context.getSharedPreferences(
                updateInfoFileName, 0);
        SharedPreferences.Editor editor = updateInfo.edit();
        editor.putString(CFG_UPDATE_TIME, version + "_" + currentTime);
        SharePreferenceUtil.applyToEditor(editor);
    }

    public String getDataDownPath(String id) {
        String path = getItemPath(id);
        if (path == null) {
            return null;
        }
        return path + SUFFIX_DOWN;
    }
    public String getUpdateDataPath(String id) {

        if(datadirPkg == null)
            return null;
        File f = new File(datadirPkg);
        f.mkdirs();
        String path = null;
        if(id == null || id.length() == 0){
            path = datadirPkg + File.separator;
        }else{
            path = datadirPkg + File.separator + id + SUFFIX_DOWN;
        }

        return path;
    }

    public boolean initApkDownDir() {

        if (!FileUtils.isValidExternalStorage()) {

            File file = new File(FileUtils.addSlash(context.getApplicationInfo().dataDir) + "tmp" + File.separatorChar);

            if(file.exists()){

                return true;
            }
            return file.mkdirs();
        }

        String path = FileUtils.getExternalStoragePath() + pathExternal;

        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();
    }

    public boolean checkApkExisted(String updateVersion) {

        PackageManager pkgMgr = context.getPackageManager();
        PackageInfo pkgInfo = null;

        File internal = new File(getApkInternalPath());
        File external = new File(getApkExternalPath());

        if(!internal.exists() && !external.exists()){

            return false;
        }else

        if(!internal.exists() && external.exists()){

            try {

                pkgInfo = pkgMgr.getPackageArchiveInfo(getApkExternalPath(), 0);

                return updateVersion.equals(VersionUtil
                        .translateDecimal(pkgInfo.versionCode));

            } catch (Exception e) {
                return false;
            }

        }else

        if(internal.exists() && !external.exists()){

            try {

                pkgInfo = pkgMgr.getPackageArchiveInfo(getApkInternalPath(), 0);

                return updateVersion.equals(VersionUtil
                        .translateDecimal(pkgInfo.versionCode));

            } catch (Exception e) {
                return false;
            }

        }else

        if(internal.exists() && external.exists()){

            try {

                pkgInfo = pkgMgr.getPackageArchiveInfo(getApkExternalPath(), 0);

                if(updateVersion.equals(VersionUtil
                        .translateDecimal(pkgInfo.versionCode))){

                    return true;
                }

            } catch (Exception e) {}

            try {

                pkgInfo = pkgMgr.getPackageArchiveInfo(getApkInternalPath(), 0);

                if(updateVersion.equals(VersionUtil
                        .translateDecimal(pkgInfo.versionCode))){

                    return true;
                }

            } catch (Exception e) {}


        }

        return false;

    }

    public String getDataDir(){
        return dataDir;
    }

    public void setDataDir(String dir){
        dataDir = dir;
    }

    public String getApkDownPath() {
        String path = getApkPath();
        if (path == null) {
            return null;
        }
        return path + SUFFIX_DOWN;
    }

    public boolean installApk() {

        try {

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);

            PackageManager pkgMgr = context.getPackageManager();

            File internal = new File(getApkInternalPath());
            File external = new File(getApkExternalPath());

            File file = null;

            if(external.exists() && !internal.exists()){

                file = external;
            }
            else if(!external.exists() && internal.exists()){

                try{
                    Runtime.getRuntime().exec("chmod 744 " + getApkInternalPath());
                }
                catch(Exception e){}

                try{
                    String chmod = "chmod 755 " + new File(getApkInternalPath()).getParent();
                    Runtime.getRuntime().exec(chmod);
                }
                catch(Exception e){}

                file = internal;
            }
            else if(external.exists() && internal.exists()){

                PackageInfo externalInfo = pkgMgr.getPackageArchiveInfo(getApkExternalPath(),
                        PackageManager.GET_ACTIVITIES);

                PackageInfo internalInfo = pkgMgr.getPackageArchiveInfo(getApkInternalPath(),
                        PackageManager.GET_ACTIVITIES);

                if(VersionUtil.compare(VersionUtil.translateDecimal(externalInfo.versionCode),
                        VersionUtil.translateDecimal(internalInfo.versionCode)) > 0){

                    file = external;
                }else {

                    try{
                        Runtime.getRuntime().exec("chmod 744 " + getApkInternalPath());
                    }
                    catch(Exception e){}

                    try{
                        String chmod = "chmod 755 " + new File(getApkInternalPath()).getParent();
                        Runtime.getRuntime().exec(chmod);
                    }
                    catch(Exception e){}

                    file = internal;
                }
            }


            PackageInfo packageInfo = pkgMgr.getPackageArchiveInfo(file.getAbsolutePath(),
                    PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                setInstallTime(VersionUtil
                        .translateDecimal(packageInfo.versionCode));
            }


            final String type = "application/vnd.android.package-archive";
            intent.setDataAndType(Uri.fromFile(file), type);
            return ComponentUtils.startActivity(context, intent);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据指定路径，安装下载的apk文件
     * */
    public boolean installApk(String path){
        try {

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);

            PackageManager pkgMgr = context.getPackageManager();

            File file = new File(path);

            PackageInfo packageInfo = pkgMgr.getPackageArchiveInfo(file.getAbsolutePath(),
                    PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                setInstallTime(VersionUtil
                        .translateDecimal(packageInfo.versionCode));
            }


            final String type = "application/vnd.android.package-archive";
            intent.setDataAndType(Uri.fromFile(file), type);
            return ComponentUtils.startActivity(context, intent);
        } catch (Exception e) {
            return false;
        }
    }

    public String getApkInternalPath(){

        return FileUtils.addSlash(context.getApplicationInfo().dataDir)
                + "tmp" + File.separatorChar
                + FileUtils.ID_APK;
    }

    public String getApkExternalPath(){
        if(FileUtils.isValidExternalStorage()){
            String storagepath = FileUtils.getExternalStoragePath();
            if(storagepath != null){
                return FileUtils.addSlash(storagepath
                        + pathExternal)
                        + FileUtils.ID_APK;
            }
        }

        return null;
    }

    public String getSdCardExternalPath(){
        return pathExternal;
    }


    public String getApkPath() {

        if (!FileUtils.isValidExternalStorage()) {

            return getApkInternalPath();

        }

        return getApkExternalPath();
    }

    public String getApkVersion() {
        return apkVersion;
    }

    public String getDataVersion() {
        if (null == context) {
            return null;
        }

        SharedPreferences updateInfo = context.getSharedPreferences(
                updateInfoFileName, 0);
        return updateInfo.getString(CFG_DATA_VERSION, null);
    }

    public void setDataVersion(String version) {
        if (null == context) {
            return;
        }

        SharedPreferences updateInfo = context.getSharedPreferences(
                updateInfoFileName, 0);
        SharedPreferences.Editor editor = updateInfo.edit();
        editor.putString(CFG_DATA_VERSION, version);
        SharePreferenceUtil.applyToEditor(editor);
    }

    //
    // 提交非升级任务
    //
    // @REMARK
    // 1.仅允许提交非升级任务
    // 2.任务必须短小精悍，否则会影响正常的升级任务
    //
    public synchronized void submitTask(Runnable task, long delayMillis) {
        if (updateThread == null) {
            updateThread = new HandlerThread("update_manager");
            updateThread.start();

            updateHandler = new Handler(updateThread.getLooper());
        }

        updateHandler.postDelayed(task, delayMillis);
    }

    public static final String[] SQLITE_POSTFIX = new String[]{"-journal", "-wal", "-shm"};
    public static void deleteTmpDatabaseFile(String path) {
        File file = new File(path + SUFFIX_BAK);
        file.delete();

        file = new File(path + SUFFIX_DOWN);
        file.delete();

        file = new File(path + SUFFIX_UNZIP);
        file.delete();

        file = new File(path + SUFFIX_PATCH);
        file.delete();

        for ( String post : SQLITE_POSTFIX){
            file = new File(path + post);
            file.delete();
        }
    }

    public static void deleteTmpFile(String path) {
        File file = new File(path + SUFFIX_BAK);
        file.delete();

        file = new File(path + SUFFIX_DOWN);
        file.delete();

        file = new File(path + SUFFIX_UNZIP);
        file.delete();

        file = new File(path + SUFFIX_PATCH);
        file.delete();
    }

    // 本地数据项
    public static class Item {
        public int fileType;
        public String dataPath;
        public String description;

        public void initFromIni(IniResolver ini, String sectionName) {
            fileType = typeConverter(ini.getValue(sectionName, INI_KEY_TYPE));
            dataPath = ini.getValue(sectionName, INI_KEY_PATH_DATA);
            if (Miscellaneous.isEmpty(dataPath)) {
                //
                // @NOTE
                // 如果数据路径为空，则使用资源路径
                //
                dataPath = ini.getValue(sectionName, INI_KEY_PATH_RES);
            }
            description = ini.getValue(sectionName, INI_KEY_DESCRIPTION);
        }

        private int typeConverter(String type) {
            if (null == type) {
                return FileUtils.TYPE_UNKNOWN;
            }

            if ("apk".compareToIgnoreCase(type) == 0) {
                return FileUtils.TYPE_APK;
            } else if ("binary".compareToIgnoreCase(type) == 0) {
                return FileUtils.TYPE_BINARY;
            } else if ("sqlite".compareToIgnoreCase(type) == 0) {
                return FileUtils.TYPE_SQLITE;
            } else if ("edb".compareToIgnoreCase(type) == 0) {
                return FileUtils.TYPE_ENCRYPT_SQLITE;
            } else {
                return FileUtils.TYPE_UNKNOWN;
            }
        }
    }


    private Context context;

    private String datadirPkg;
    private String dataDir;
    private String pathExternal;
    private String apkVersion;
    private String apkPackPath;
    private String urlRoot;
    private String urlDLRoot;
    private String versionApkIni;
    private String versionApkNewIni;
    private String versionDataIni;
    private Map<String, Item> itemMap;

    private Handler updateHandler;
    private HandlerThread updateThread;
    private boolean mbInited = false;
    private int mnInitedTimes = 0;

    private static final String ENCODING = "utf-8";

    private static final String VERSION_INI = "version.ini";
    private static final String INI_KEY_URL_ROOT = "url_root";
    private static final String INI_KEY_DL_URL_ROOT = "url_dl_root";
    private static final String INI_KEY_VERSION_APK_INI = "version_apk_ini";
    private static final String INI_KEY_VERSION_APK_NEW_INI = "version_apk_new_ini";
    private static final String INI_KEY_VERSION_DATA_INI = "version_data_ini";
    public  static final String INI_KEY_VERSION = "version";
    private static final String INI_KEY_TYPE = "type";
    private static final String INI_KEY_PATH_CACHE = "path_cache";
    public  static final String INI_KEY_PATH_RES = "path_res";
    public static final String INI_KEY_PATH_DATA = "path_data";
    private static final String INI_KEY_DESCRIPTION = "description";
    private static final String INI_KEY_PATH_EXTERNAL = "path_external";
    private static final String INI_KEY_PATH_OTHER = "path_other";

    private static final String SUFFIX_BAK = ".bak"; // 原始备份文件
    private static final String SUFFIX_DOWN = ".dwn"; // 下载升级文件
    private static final String SUFFIX_UNZIP = ".unz"; // 解压后文件
    private static final String SUFFIX_PATCH = ".pat"; // 合并后文件
    private static final String SUFFIX_ZIP = ".zip"; // 压缩文件
    private static final String CFG_DATA_VERSION = "version_data";
    private static final String CFG_IS_FORCE_UPDATE = "is_force_update";
    private static final String CFG_UPDATE_TIME = "update_time";
    private static final String CFG_CHECK_IS_FROM_UPDATE = "check_is_from_update";
    private static final String updateInfoFileName = Env.getPkgName(SpaceApplication.getInstance().getContext()) + ".update.UpdateManager";
    private static UpdateManager updateMgr;

    private static final String CFG_PREFER_FULL_UPDATE_BRANCH   = "update_data_prefer_full_update_branch";
    private static final String PREFER_FULL_UPDATE 		= "yes";
    private static final String UN_PREFER_FULL_UPDATE 	= "no";
    private static  String mBS_PATCH_PATH = null;
}
