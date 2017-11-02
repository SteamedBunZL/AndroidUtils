package com.clean.spaceplus.cleansdk.junk.engine;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.EnableCacheListDir;
import com.clean.spaceplus.cleansdk.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import space.network.cleancloud.KCacheCloudQuery.PkgQueryCallback;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/13 19:31
 * @copyright TCL-MIG
 */
public class CacheFindRegSubPaths {
    public static final class Counting {
        public int folderCount;
    }

    static public ArrayList<File> matchSubPath(
            File parentPath,
            String subPath,
            PkgQueryCallback callback,
            boolean isDirectory, Counting counting ) {
        return matchSubPath( parentPath, subPath, callback, isDirectory, counting, null );
    }
    static public ArrayList<File> matchSubPath(
            File parentPath,
            String subPath,
            PkgQueryCallback callback,
            boolean isDirectory, Counting counting,
            PatternCache patternCache ) {

        if (TextUtils.isEmpty(subPath)) {
            return null;
        }

        String[] regSectionArray = subPath.split("/");
        if (null == regSectionArray || regSectionArray.length < 1) {
            return null;
        }

        return matchOneLevelSubPath(parentPath, regSectionArray, 0, callback, isDirectory, counting, patternCache);
    }
    static private ArrayList<File> matchOneLevelSubPath(
            File parentPath,
            String[] regSectionArray,
            int nowSection,
            PkgQueryCallback callback,
            boolean isDirectory, Counting counting,
            PatternCache patternCache ) {

        int regSecCnt = regSectionArray.length;
        assert (null != regSectionArray);
        assert (nowSection < regSecCnt);

        ArrayList<File> matchedPath = new ArrayList<File>();

        //String[] allSubPath = listFile(parentPath, new MatchSubLevelPathName(regSectionArray[nowSection]));
        boolean currentPathIsDir = ((nowSection + 1) == regSecCnt) ? isDirectory : true;

        String[] allSubPath = listFile2(parentPath, new MatchSubLevelPathName2(regSectionArray[nowSection], currentPathIsDir, patternCache), currentPathIsDir);
        if (null == allSubPath || 0 == allSubPath.length) {
            return null;
        }
        if (counting != null) {
            counting.folderCount += allSubPath.length;
        }
        if (checkStop(callback)) {
            return null;
        }

        ++nowSection;

        if (regSectionArray.length > nowSection) {
            for ( String nameString : allSubPath ) {
                ArrayList<File> subResult = matchOneLevelSubPath(
                        new File(parentPath, nameString),
                        regSectionArray,
                        nowSection,
                        callback,
                        isDirectory, counting, patternCache);
                if (null != subResult && (!subResult.isEmpty())) {
                    matchedPath.addAll(subResult);
                }
            }
        } else {
            for ( String nameString : allSubPath ) {
                matchedPath.add(new File(parentPath, nameString));
            }
        }

        return matchedPath.isEmpty() ? null : matchedPath;
    }




    static private String[] listFile2(File parentPath, final NameFilter filter, boolean isDirectory) {
        String[] result = null;
        String path = parentPath.getPath();

        PathOperFunc.FilesAndFoldersStringList fileAndFoldersList = EnableCacheListDir.listDir(path, filter);
        if (fileAndFoldersList == null) {
            return result;
        }

        PathOperFunc.StringList pathList = null;
        if (isDirectory) {
            pathList = fileAndFoldersList.getFolderNameList();
            if (pathList != null) {
                int size = pathList.size();
                if (size > 0) {
                    result = new String[size];
                    int count = 0;
                    for ( String folder : pathList ) {
                        result[count++] = folder;
                    }
                }
                pathList.release();
            }
        } else {
            pathList = fileAndFoldersList.getFileNameList();
            if (pathList != null) {
                int size = pathList.size();
                if (size > 0) {
                    result = new String[size];
                    int count = 0;
                    for ( String folder : pathList ) {
                        result[count++] = folder;
                    }
                }
                pathList.release();
            }
        }
        fileAndFoldersList.release();
        return result;

    }

    private static class MatchSubLevelPathName2 implements NameFilter {

        private Pattern mSubPathRegexPattern = null;
        private boolean mTargetIsDir = true;
        public MatchSubLevelPathName2(String regPattern, boolean targetIsDir, PatternCache patternCache ) {

            if (TextUtils.isEmpty(regPattern)) {
                return;
            }
            Pattern subPathRegexPattern = null;
            try {
                if ( patternCache != null ) {
                    subPathRegexPattern = patternCache.compile(regPattern);
                }else {
                    subPathRegexPattern = Pattern.compile(regPattern);
                }
            } catch (Exception e) {
                subPathRegexPattern = null;
            }
            if (null == subPathRegexPattern) {
                return;
            }
            mSubPathRegexPattern = subPathRegexPattern;
            mTargetIsDir = targetIsDir;
        }

        @Override
        public boolean accept(String parent, String sub, boolean bFolder) {
            //先只支持目录
            return acceptFile(parent, sub, mSubPathRegexPattern, bFolder, mTargetIsDir);
        }
    }

    static private boolean acceptFile(String parent, String filename, Pattern pattern, boolean isDirectory, boolean targetIsDir) {
        if (isDirectory != targetIsDir) {
            return false;
        }

        if (null == pattern) {
            return false;
        }

        assert (!TextUtils.isEmpty(filename));

        Matcher matcher = null;
        matcher = pattern.matcher(StringUtils.toLowerCase(filename));

        if (null != matcher && matcher.matches()) {
            return true;
        }

        return false;
    }

    static private boolean checkStop(PkgQueryCallback callback) {
        return callback != null && callback.checkStop();
    }
}
