package com.clean.spaceplus.cleansdk.junk.cleancloud;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.EnableCacheListDir;

import java.io.File;
import java.util.TreeMap;

import space.network.util.hash.KQueryMd5Util;

/**
 * @author dongdong.huang
 * @Description:路径转换器
 * @date 2016/4/27 16:10
 * @copyright TCL-MIG
 */
public class CleanCloudPathConverter {
    private static final String TAG = CleanCloudPathConverter.class.getSimpleName();
    private static class PathItem {
        public boolean mIsDirectory;
        public volatile boolean mIsEnumerated;
        private TreeMap<String, PathItem> mSubPaths;

        public boolean addSubPath(String name, boolean isDirectory) {
            if (TextUtils.isEmpty(name))
                return false;

            PathItem item = new PathItem();
            item.mIsDirectory = isDirectory;

            synchronized(this) {
                if (null == mSubPaths) {
                    mSubPaths = new TreeMap<String, PathItem>(String.CASE_INSENSITIVE_ORDER);
                }
                mSubPaths.put(name, item);
            }
            return true;
        }

        public PathItem findSubItem(String name) {
            if (!mIsDirectory)
                return null;

            PathItem result = null;
            synchronized(this) {
                if (!mIsEnumerated)
                    return null;

                if (null == mSubPaths)
                    return null;

                result = mSubPaths.get(name);
            }
            return result;
        }
    }

    public static class Md5Cache {
        public String put(String dirnameMd5, String dirname) {
            if (TextUtils.isEmpty(dirnameMd5) || TextUtils.isEmpty(dirname)) {
                return dirname;
            }

            synchronized (mMd5Cache) {
                return mMd5Cache.put(dirnameMd5, dirname);
            }
        }
        public String get(String dirname) {
            synchronized (mMd5Cache) {
                return (dirname != null) ? mMd5Cache.get(dirname) : null;
            }
        }
        public void clear() {
            synchronized (mMd5Cache) {
                mMd5Cache.clear();
            }
        }
        private TreeMap<String, String> mMd5Cache = new TreeMap<String, String>();
    }

    private boolean mIsOwnMd5Cache = false;
    private Md5Cache mMd5Cache = null;
    private String mSdCardRootPath = "";
    private PathItem mRootItem = new PathItem();

    /*
     * 一定要在设置setSdCardRootPath前调用setMd5Cache才能共享cache
     */
    public void setMd5Cache(Md5Cache cache) {
        synchronized (this) {
            if (null == mMd5Cache) {
                mMd5Cache = cache;
                mIsOwnMd5Cache = false;
            }
        }
    }

    public boolean setSdCardRootPath(String path) {
        synchronized (this) {
            if (TextUtils.isEmpty(path))
                return false;

            int len = path.length();
            if (path.charAt(len - 1) == File.separatorChar) {
                mSdCardRootPath = path.substring(0, len - 1);
            } else {
                mSdCardRootPath = path;
            }

            if (null == mMd5Cache) {
                mMd5Cache = new Md5Cache();
                mIsOwnMd5Cache = true;
            }
        }
        return true;
    }

    public CleanCloudPathConverter() {
        mRootItem.mIsDirectory = true;
    }

    public String getSdCardRootPath() {
        return mSdCardRootPath;
    }

    public void cleanPathEnumCache() {
        synchronized (this) {
            synchronized (mRootItem) {
                mRootItem.mIsEnumerated = false;
                if (mRootItem.mSubPaths != null) {
                    mRootItem.mSubPaths.clear();
                }
            }
            if (mIsOwnMd5Cache) {
                mMd5Cache.clear();
            }
        }
    }

    public String getDirPath(String str) {
        boolean[] isDirectoryResult = new boolean[1];
        String result = getPath(str, true, isDirectoryResult);
        if (!isDirectoryResult[0]) {
            result = null;
        }
        return result;
    }

    /**
     * str格式是 "头两级目录md5字符串+明文子路径"
     *只有1级目录就是一个md5串
     *两级目录是md51+md52
     *三级目录是md51+md52+第三级目录明文
     *如果超过三级目录，第三级目录后的都是明文
     *如: f1f184bb9e89b93a57238d8091f33f8d+39a6cf92a7f386634028d838d2170a42+com.muzhiwan.market.hd
     * @param str
     * @param isDirectory
     * @param isDirectoryResult
     * @return
     */
    public String getPath(String str, boolean isDirectory, boolean[] isDirectoryResult) {
        if (TextUtils.isEmpty(str))
            return null;
        String result = null;
        String md51   = null;
        String md52   = null;
        String remain = null;
        int plusPos1  = -1;
        int plusPos2  = -1;
        plusPos1 = str.indexOf('+');
        if (-1 == plusPos1) {
            md51 = str;
        } else {
            md51 = str.substring(0, plusPos1);
            plusPos2 = str.indexOf('+', plusPos1 + 1);
            if (-1 == plusPos2) {
                md52 = str.substring(plusPos1 + 1);
            } else {
                md52 = str.substring(plusPos1 + 1, plusPos2);
                remain = str.substring(plusPos2 + 1);
            }
        }

        String dir1 = null;
        String dir2 = null;

        if (md51 != null) {
            boolean currentPathIsDir = (md52 != null)  ? true : isDirectory;
            dir1 = getOriString(File.separator, md51, currentPathIsDir);
        }
        if (dir1 != null && md52 != null) {
            boolean currentPathIsDir = (remain != null)  ? true : isDirectory;
            dir2 = getOriString(dir1, md52, currentPathIsDir);
        }

        if (remain != null) {
            if (dir1 != null && dir2 != null) {
                result = dir1;
                result += File.separator;
                result += dir2;
                result += File.separator;
                result += remain;
                if (isDirectoryResult != null) {
                    String path = getFullPathFromRelativePath(result);
                    File file = new File(path);
                    isDirectoryResult[0] = file.isDirectory();
                }
            }
        } else if (md52 != null) {
            if (dir1 != null && dir2 != null) {
                result = dir1;
                result += File.separator;
                result += dir2;
                if (isDirectoryResult != null) {
                    isDirectoryResult[0] = isDirectory;
                }
            }
        } else {
            if (dir1 != null) {
                result = dir1;
                if (isDirectoryResult != null) {
                    isDirectoryResult[0] = isDirectory;
                }
            }
        }
        return result;
    }

    public String getDirPath(String parent, String str) {
        boolean[] isDirectoryResult = new boolean[1];
        String result = getPath(parent, str, true, isDirectoryResult);
        if (!isDirectoryResult[0]) {
            result = null;
        }
        return result;
    }
    private String getPath(String parent, String str, boolean isDirectory, boolean[] isDirectoryResult) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(parent))
            return null;
        // str格式是 "一级目录md5字符串+明文子路径"
        // 只有1级目录就是一个md5串
        // 两级目录是一个md5串用+第二级目录明文
        // 如: 39a6cf92a7f386634028d838d2170a42+com.muzhiwan.market.hd
        String result = null;
        String md51 = null;
        String remain = null;

        int plusPos1 = -1;
        plusPos1 = str.indexOf('+');
        if (-1 == plusPos1) {
            md51 = str;
        } else {
            md51 = str.substring(0, plusPos1);
            remain = str.substring(plusPos1 + 1);
        }

        String dir1 = null;
        if (md51 != null) {
            boolean currentPathIsDir = (remain != null) ? true : isDirectory;
            dir1 = getOriString(parent, md51, currentPathIsDir);
        }

        if (dir1 != null) {
            result = parent;
            result += File.separator;
            result += dir1;
            if (remain != null) {
                result += File.separator;
                result += remain;
                if (isDirectoryResult != null) {
                    String path = getFullPathFromRelativePath(result);//...h 添加sd card 根路径
                    File file = new File(path);
                    isDirectoryResult[0] = file.isDirectory();
                }
            } else {
                if (isDirectoryResult != null) {
                    isDirectoryResult[0] = isDirectory;
                }
            }
        }
        return result;
    }

    public String getFullPathFromRelativePath(String path) {
        String result = null;
        if (path != null) {
            result = mSdCardRootPath;
            result += File.separator;
            result += path;
        }
        return result;
    }

    //获取原始数据
    String getOriString(String parent, String md5, boolean isDirectory) {
        String result = null;
        if (null == mMd5Cache)
            return result;

        if (!mRootItem.mIsEnumerated) {
            synchronized(mRootItem) {
                if (!mRootItem.mIsEnumerated) {
                    enumDir(File.separator, mRootItem);
                    mRootItem.mIsEnumerated = true;
                }
            }
        }
        PathItem parentItem = null;
        if (File.separator.compareTo(parent) == 0) {//根目录
            parentItem = mRootItem;
        } else {
            parentItem = mRootItem.findSubItem(parent);//子目录
        }

        if (null == parentItem) {
            return result;
        }
        String name = null;
        PathItem item = null;
        synchronized(parentItem) {
            if (parentItem.mIsDirectory) {
                if (!parentItem.mIsEnumerated) {
                    enumDir(parent, parentItem);//枚举当前文件夹子路径，包括文件、子文件夹等
                    parentItem.mIsEnumerated = true;
                }
                name = mMd5Cache.get(md5);
                if (!TextUtils.isEmpty(name)) {
                    item = parentItem.findSubItem(name);
                    if (item != null && item.mIsDirectory == isDirectory) {
                        result = name;
                    }
                }
            }
        }
        return result;
    }

    //////////////////////////////////////////////////
    //优化目录枚举，所以引入了 cleanmaster.util.PathOperFunc
    //如要不引入 cleanmaster.util.PathOperFunc，用下面注释掉的版本
    void enumDir(String dir, PathItem item) {
        String path = mSdCardRootPath;
        path += File.separator;
        if (File.separator.compareTo(dir) != 0) {
            path += dir;
        }
        PathOperFunc.FilesAndFoldersStringList fileAndFoldersList = EnableCacheListDir.listDir(path);

        if (fileAndFoldersList == null) {
            return;
        }
        String name;
        PathOperFunc.StringList folderList = fileAndFoldersList.getFolderNameList();
        if (folderList != null) {
            int count = folderList.size();
            for (int i = 0; i < count; ++i) {
                name = folderList.get(i);
                if (TextUtils.isEmpty(name)) {
                    continue;
                }
                item.addSubPath(name, true);
                String nameMd5 = KQueryMd5Util.getDirQueryMd5(name);
                synchronized (mMd5Cache) {
                    mMd5Cache.put(nameMd5, name);
                }
            }
            folderList.release();
        }
        PathOperFunc.StringList fileList = fileAndFoldersList.getFileNameList();
        if (fileList != null) {
            int count = fileList.size();
            for (int i = 0; i < count; ++i) {
                name = fileList.get(i);
                item.addSubPath(name, false);
                String nameMd5 = KQueryMd5Util.getDirQueryMd5(name);
                synchronized (mMd5Cache) {
                    mMd5Cache.put(nameMd5, name);
                }
            }
            fileList.release();
        }
        fileAndFoldersList.release();
    }
}
