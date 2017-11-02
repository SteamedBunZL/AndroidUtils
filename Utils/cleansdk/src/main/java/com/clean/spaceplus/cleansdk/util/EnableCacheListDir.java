package com.clean.spaceplus.cleansdk.util;

import android.app.ActivityManager;
import android.content.Context;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liangni
 * @Description: 启用缓存机制的PathOperFunc.listDir实现
 * @date 2016/4/23 16:28
 * @copyright TCL-MIG
 */
public class EnableCacheListDir {
    /**
     * 开启缓存机制，在调用listdir之前必须调用此接口才能启用缓存机制
     */
    public static void openCache() {
        //主界面 open ，再到垃圾扫描里面的时候不需要清理缓存
        if (mFileMapsMaxSize != -1) {
            return;
        }
        synchronized (sSyncObject) {
            if (mFileMapsMaxSize == -1) {
                cleanCache();
                sEnableCache = true;
                getFileMapsMaxSize();
            }
        }
    }

    /**
     * 关闭缓存机制。
     */
    public static void closeCache() {
        synchronized (sSyncObject) {
            mFileMapsMaxSize = -1;
            sEnableCache = false;
            cleanCache();
        }
    }

    private static void cleanCache() {
        Collection<SoftReference<EnableCacheFilesAndFoldersStringList>> valueslist = sFileMapsMap.values();
        for (SoftReference<EnableCacheFilesAndFoldersStringList> enableCacheFilesAndFoldersStringList : valueslist) {
            if ( enableCacheFilesAndFoldersStringList != null ) {
                EnableCacheFilesAndFoldersStringList list = enableCacheFilesAndFoldersStringList.get();
                if ( list != null ) {
                    list.release();
                }
            }
        }
        valueslist.clear();
        valueslist = null;
        sFileMapsMap.clear();
    }
    /**
     * 枚举dirPath文件夹下的子项名字
     * @param dirPath 文件夹全路径
     */
    public static PathOperFunc.FilesAndFoldersStringList listDir(String dirPath) {
        return listDir(dirPath, null);
    }

    /**
     * 枚举dirPath文件夹下满足过滤条件的子项名字
     * @param dirPath 文件夹全路径
     * @param filter  过滤条件
     */
    public static PathOperFunc.FilesAndFoldersStringList listDir(String dirPath, final NameFilter filter) {
        if (!sEnableCache || mFileMapsMaxSize <= 0 ) {
            return PathOperFunc.listDir(dirPath, filter);
        }

        dirPath = FileUtils.addSlash(dirPath);
        String dirPath_lower = StringUtils.toLowerCase(dirPath);
        EnableCacheFilesAndFoldersStringList cacheFilesAndFoldersStringList = null;
        synchronized (sSyncObject) {
            if( sFileMapsMap.containsKey(dirPath_lower) ){
                SoftReference<EnableCacheFilesAndFoldersStringList> listReference = sFileMapsMap.get(dirPath_lower);
                if ( listReference != null ) {
                    EnableCacheFilesAndFoldersStringList list = listReference.get();
                    if( list != null ) {
                        cacheFilesAndFoldersStringList = new EnableCacheFilesAndFoldersStringList( list );
                    }
                }
            }
        }
        if (cacheFilesAndFoldersStringList == null) {
            PathOperFunc.FilesAndFoldersStringList list = PathOperFunc.listDir(dirPath, null);
            if( list == null){
                return null;
            }
            if ( list.size() > 500 ) {
                if ( filter == null ) {
                    return list;
                }
                list.release();
                list = null;
                return  PathOperFunc.listDir( dirPath, filter );
            }
            cacheFilesAndFoldersStringList = new EnableCacheFilesAndFoldersStringList();
            cacheFilesAndFoldersStringList.add(list);
            list.release();
            list = null;
            synchronized (sSyncObject) {
                sFileMapsMap.put(dirPath_lower, new SoftReference<>(new EnableCacheFilesAndFoldersStringList(cacheFilesAndFoldersStringList)) );
            }
        }
        return cacheFilesAndFoldersStringList.filter(dirPath, filter);
    }

    private static int getFileMapsMaxSize(){
        if (mFileMapsMaxSize == -1 ) {
            ActivityManager am = (ActivityManager) SpaceApplication.getInstance().getContext().getSystemService(Context.ACTIVITY_SERVICE);
            int nMaxMemory = am.getMemoryClass();
            long nCurMemory = Runtime.getRuntime().totalMemory() /1024/1024;
            int nLeftoverMemory = nMaxMemory - (int)nCurMemory;
            //内存大于8M才启用缓存。
            if ( nLeftoverMemory < 8 ) {
                mFileMapsMaxSize = 0;
            }else {
                //预留3M内存空间，预防内存不足
                nLeftoverMemory -= 3;
                //缓存目录个数计算方法：根据当前可用内存大小，每M内存存储20个目录，最多500个。
                mFileMapsMaxSize = nLeftoverMemory*20;
                if ( mFileMapsMaxSize > 500 ) {
                    mFileMapsMaxSize = 500;
                }
            }
        }
        return mFileMapsMaxSize;
    }
    private static int mFileMapsMaxSize = -1;
    private static Object sSyncObject = new Object();
    private static boolean sEnableCache = false;
    private static Map<String, SoftReference<EnableCacheFilesAndFoldersStringList>> sFileMapsMap = new LinkedHashMap<String, SoftReference<EnableCacheFilesAndFoldersStringList>>( 200, 0.75f, true){
        @Override
        protected boolean removeEldestEntry(Entry<String, SoftReference<EnableCacheFilesAndFoldersStringList>> eldest) {
            if ( size() >= getFileMapsMaxSize() ) {
                SoftReference<EnableCacheFilesAndFoldersStringList> lReference = eldest.getValue();
                if ( lReference != null ) {
                    EnableCacheFilesAndFoldersStringList list = lReference.get();
                    if ( list != null ) {
                        list.release();
                    }
                }
                return true;
            }else {
                return false;
            }
        }
    };

    static class EnableCacheStringList implements PathOperFunc.StringList {

        public EnableCacheStringList() {
            mFolderStrings = new ArrayList<String>();
            mFileStrings = new ArrayList<String>();
        }
        public EnableCacheStringList( EnableCacheStringList aCacheStringList ){
            mFolderStrings = new ArrayList<String>(aCacheStringList.mFolderStrings);
            mFileStrings = new ArrayList<String>(aCacheStringList.mFileStrings);
        }

        public EnableCacheStringList(List<String> folderStrings, List<String> fileStrings ) {
            if( folderStrings != null ) {
                mFolderStrings = folderStrings;
            }else {
                mFolderStrings = new ArrayList<String>();
            }

            if( fileStrings != null ) {
                mFileStrings = fileStrings;
            }else {
                mFileStrings = new ArrayList<String>();
            }
        }

        public List<String> mFolderStrings = null;
        public List<String> mFileStrings = null;

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {

                private int mNowPos = 0;

                @Override
                public boolean hasNext() {
                    return (mNowPos < size());
                }

                @Override
                public String next() {
                    return get(mNowPos++);
                }

                @Override
                public void remove() {
                    // 为避免本函数被频繁使用，影响性能，所以不予实现。
                    if (PublishVersionManager.isTest()) {
                        throw new UnsupportedOperationException();
                    }
                }
            };
        }

        @Override
        public int size() {
            return mFolderStrings.size() + mFileStrings.size();
        }

        @Override
        public String get(int idx) {
            if ( mFolderStrings.size() > idx ) {
                return mFolderStrings.get(idx);
            }
            return mFileStrings.get(idx-mFolderStrings.size());
        }

        @Override
        public void set(int idx, String s) {
            if ( mFolderStrings.size() > idx ) {
                mFolderStrings.set(idx, s);
                return;
            }
            mFileStrings.set(idx-mFolderStrings.size(), s);
        }

        @Override
        public void shrink(int size) {
            if ( mFolderStrings.size() > size ) {
                mFolderStrings = mFolderStrings.subList( 0, size);
                mFileStrings.clear();
            }else {
                mFileStrings = mFileStrings.subList( 0, size-mFolderStrings.size());
            }
        }

        @Override
        public void release() {
            mFolderStrings = null;
            mFileStrings = null;
        }

        public void realRelease() {
            mFolderStrings.clear();
            mFolderStrings = null;
            mFileStrings.clear();
            mFileStrings = null;
        }
    }

    static class EnableCacheFilesAndFoldersStringList extends EnableCacheStringList implements PathOperFunc.FilesAndFoldersStringList {
        public EnableCacheFilesAndFoldersStringList() {
            super();
        }
        public EnableCacheFilesAndFoldersStringList( EnableCacheFilesAndFoldersStringList enableCacheFilesAndFoldersStringList ) {
            super(enableCacheFilesAndFoldersStringList);
        }
        public void add( PathOperFunc.FilesAndFoldersStringList list ) {

            PathOperFunc.StringList nFilelList = list.getFileNameList();
            PathOperFunc.StringList nFolderList = list.getFolderNameList();
            if (nFilelList != null) {
                for (String name:nFilelList) {
                    add(name, false);
                }
                nFilelList.release();
                nFilelList = null;
            }
            if (nFolderList != null) {
                for (String name:nFolderList) {
                    add(name, true);
                }
                nFolderList.release();
                nFolderList = null;
            }
        }

        @Override
        public PathOperFunc.StringList getFileNameList() {
            return new EnableCacheStringList( null, this.mFileStrings);
        }
        @Override
        public PathOperFunc.StringList getFolderNameList() {
            return new EnableCacheStringList( this.mFolderStrings, null );
        }

        @Override
        public void release() {
            realRelease();
        }

        public void add( String name, boolean bFolder ) {
            if (!bFolder) {
                this.mFileStrings.add(name);
            }else{
                this.mFolderStrings.add(name);
            }
        }

        public EnableCacheFilesAndFoldersStringList filter( String dirPath, final NameFilter filter ){
            if ( filter == null ) {
                return this;
            }

            List<String> deletes = new ArrayList<String>();
            for (String name:this.mFileStrings) {
                if ( !filter.accept(dirPath, name, false)) {
                    deletes.add(name);
                }
            }
            if ( !deletes.isEmpty() ) {
                this.mFileStrings.removeAll(deletes);
            }
            deletes.clear();
            for (String name:this.mFolderStrings) {
                if ( !filter.accept(dirPath, name, true)) {
                    deletes.add(name);
                }
            }
            if ( !deletes.isEmpty() ) {
                this.mFolderStrings.removeAll(deletes);
            }
            return this;
        }
    }
}
