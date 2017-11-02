//=============================================================================
/**
 * @file KResidualCloudQueryHelper.java
 * @brief
 */
//=============================================================================
package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.cloud;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudPathConverter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.util.EnableCacheListDir;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.KResidualCloudQuery.DirQueryData;
import space.network.cleancloud.KResidualCloudQuery.DirResultType;
import space.network.cleancloud.core.residual.KResidualCommonData.DirQueryInnerData;
import space.network.util.hash.KQueryMd5Util;


public class KResidualCloudQueryHelper {

    private static final String TAG = KResidualCloudQueryHelper.class.getSimpleName();

    public  interface PkgDirFilter {
        boolean isInFilter(String strPkg);
    }

    public static DirQueryData getDirQueryDatas(MessageDigest md, String dirname, String lang) {
        if (null == md)
            return null;

        DirQueryData data = new DirQueryData();
        DirQueryInnerData innerData = KResidualCloudQueryHelper.getDirQueryInnerData(md, dirname);
        data.mResult 	= new KResidualCloudQuery.DirQueryResult();
        data.mLanguage 	= lang;
        data.mInnerData = innerData;
        data.mDirName   = dirname;

        return data;
    }

    public static DirQueryInnerData getDirQueryInnerData(MessageDigest md, String dirname) {
        DirQueryInnerData result = new DirQueryInnerData();
        NLog.d(TAG, "getDirQueryInnerData dirname = " +dirname);
        String[] pathQueryData = KQueryMd5Util.getPathQueryData(md, dirname);
        result.mOriginalKey = dirname;
        if (pathQueryData != null) {
            if (pathQueryData.length == 1) {
                result.mDirNameMd5 = pathQueryData[0];
                result.mLocalQueryKey = pathQueryData[0];
            } else {
                result.mDirNameMd5 = KQueryMd5Util.getDirQueryMd5(md, dirname);
                StringBuilder builder = new StringBuilder();
                boolean bFirst = true;
                for ( String queryDataString : pathQueryData ) {
                    if ( bFirst ) {
                        bFirst = false;
                    }else {
                        builder.append('+');
                    }
                    builder.append(queryDataString);
                }
                result.mLocalQueryKey = builder.toString();
            }
        }
        return result;
    }

    private Collection<DirQueryData> getDirQueryDatas(Collection<String> dirnames) {
        MessageDigest md = KQueryMd5Util.getMd5Digest();
        if (null == md)
            return null;

        ArrayList<DirQueryData> result = new ArrayList<>(dirnames.size());
        for (String dirname : dirnames) {
            DirQueryData data = KResidualCloudQueryHelper.getDirQueryDatas(md, dirname, "en");
            result.add(data);
        }
        return result;
    }



    public static Collection<String> getSecondaryQueryDirs(
            Collection<DirQueryData> results,
            CleanCloudPathConverter cleanCloudPathConverter,
            IPkgDirFilter pkgdirFilter) {
        String sdcardPath = cleanCloudPathConverter.getSdCardRootPath();
        int sdcardDirRootPos = sdcardPath.length() + 1;
        String filepath;
        TreeSet<String> secondQueryDirs = new TreeSet<String>();
        for (DirQueryData result : results) {

            if (result.mErrorCode != 0)
                continue;

            if (result.mResult.mQueryResult != DirResultType.DIR_LIST
                    && result.mResult.mQueryResult != DirResultType.DIR_QUERY_LIST)
                continue;

            if (result.mResult.mQueryResult == DirResultType.DIR_LIST) {
                getSubDirPathFromResidualCloudDirSign(
                        cleanCloudPathConverter, result.mDirName, result.mResult.mDirs, secondQueryDirs);
            } else if (result.mResult.mQueryResult == DirResultType.DIR_QUERY_LIST) {
                filepath = sdcardPath;
                filepath += File.separator;
                filepath += result.mDirName;
                getSubDirPathFromResidualCloudDirQuerySign(
                        sdcardPath, sdcardDirRootPos, filepath, result.mResult.mDirs, secondQueryDirs, pkgdirFilter);
            }
        }
        Collection<String> dirs = null;
        if (!secondQueryDirs.isEmpty()) {
            dirs = new ArrayList<>(secondQueryDirs.size());
            dirs.addAll(secondQueryDirs);
        }
        return dirs;
    }

    private static boolean getSubDirPathFromResidualCloudDirQuerySign(
            String sdcardPath,
            int sdcardDirRootPos,
            String subDir,
            Collection<String> dirSigns,
            Collection<String> result,
            IPkgDirFilter pkgdirFilter) {

        if (null == dirSigns || dirSigns.isEmpty())
            return false;

        TreeSet<String> dirSignSet = null;
        boolean needEnumCurrentDir = false;
        //现在目标特征的格式是 "明文字符串", 如 ".","data"等，其中"."代表要遍历这个目录,"data"代表要遍历data子目录
        for (String dirsign : dirSigns) {
            if (0 == dirsign.compareTo(".")) {
                needEnumCurrentDir = true;
            } else {
                if(null == dirSignSet) {
                    dirSignSet = new TreeSet<>();
                }
                dirSignSet.add(dirsign);
            }
        }

        DirFileFilter dirFilter = new DirFileFilter();
        if (needEnumCurrentDir) {
            //如果需要遍历当前目录，二次查询时需要把其他子目录排除掉
            File file = new File(subDir);
            getQueryDir(file, sdcardDirRootPos, dirFilter, null, result, pkgdirFilter);
        }

        if (dirSignSet != null) {
            for (String dirsign : dirSignSet) {
                String testDir = subDir + File.separator + dirsign;
                File testDirFile = new File(testDir);

                getQueryDir(testDirFile, sdcardDirRootPos, dirFilter, null, result, pkgdirFilter);
            }
        }
        return true;
    }

    private static boolean getSubDirPathFromResidualCloudDirSign(
            CleanCloudPathConverter cleanCloudPathConverter,
            String parentDir,
            Collection<String> dirSigns,
            Collection<String> result) {

        if (null == dirSigns || dirSigns.isEmpty())
            return false;

        for (String dirsign : dirSigns) {
            String path = cleanCloudPathConverter.getDirPath(parentDir, dirsign);
            if (TextUtils.isEmpty(path))
                continue;

            result.add(path);
        }
        return true;
    }

    public  interface IPkgDirFilter {
        boolean isInFilter(String strPkg);
    }
    public static class DirFileFilter implements NameFilter {

        @Override
        public boolean accept(String parent, String sub, boolean bFolder) {
            return bFolder;
        }
    }


    private static void getQueryDir(
            File dirFile,
            int sdcardDirRootPos,
            DirFileFilter dirFilter,
            TreeSet<String> excludeFileName,
            Collection<String> result,
            IPkgDirFilter pkgdirFilter) {
        PathOperFunc.FilesAndFoldersStringList fileAndFoldersList = EnableCacheListDir.listDir(dirFile.getPath(), dirFilter); //dirFile.listFiles(dirFilter);
        if (null == fileAndFoldersList)
            return;

        PathOperFunc.StringList folderList = fileAndFoldersList.getFolderNameList();
        if (folderList != null) {
            String name;
            int count = folderList.size();
            for (int i = 0; i < count; ++i) {
                name = folderList.get(i);
                if (excludeFileName != null) {
                    //服务端返回的路径名都是转了小写的，所以也转换一下
                    if (excludeFileName.contains(StringUtils.toLowerCase(name))) {
                        continue;
                    }
                }
                File file = new File( dirFile, name);
                String dirPath = file.getPath();
                String addPath = dirPath.substring(sdcardDirRootPos);
                if (!isInFilter(addPath, pkgdirFilter)) {
                    result.add(addPath);
                }
            }
            folderList.release();
            folderList = null;
        }

        fileAndFoldersList.release();
        fileAndFoldersList = null;
    }

    private static boolean isInFilter(String addPath, IPkgDirFilter pkgdirFilter) {
        if (null == pkgdirFilter)
            return false;

        return pkgdirFilter.isInFilter(addPath);
    }
}