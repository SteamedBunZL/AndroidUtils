package com.clean.spaceplus.cleansdk.junk.engine.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.clean.natv.a;
import com.clean.natv.d;
import com.clean.natv.g;
import com.clean.natv.z;
import com.clean.spaceplus.cleansdk.base.bean.SpecialFolder;
import com.clean.spaceplus.cleansdk.base.exception.FailException;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskControllerObserver;
import com.clean.spaceplus.cleansdk.base.utils.root.SuExec;
import com.clean.spaceplus.cleansdk.junk.engine.PathCallback;
import com.clean.spaceplus.cleansdk.junk.engine.PathListCallback;
import com.clean.spaceplus.cleansdk.junk.engine.PathScanCallback;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressCtrl;
import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.junk.engine.junk.EngineConfig;
import com.clean.spaceplus.cleansdk.util.EnableCacheListDir;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import space.network.util.CleanTypeUtil;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/23 17:02
 * @copyright TCL-MIG
 */
public class PathOperFunc {

    private static final String TAG = PathOperFunc.class.getSimpleName();

    /**
     * 枚举dirPath文件夹下的子项文件对象。
     * 本函数之所以存在，是为了方便兼容一些旧代码。建议新代码不要再使用本操作。
     * 从性能考量，最好使用返回值为IFilesAndFoldersStringList的listDir()函数。
     *
     * @param dirPath
     * @return
     */
    @Deprecated
    public static File[] listFiles(String dirPath) {
        StringList subsList = listDir(dirPath);
        try {
            return filenamesToFiles(dirPath, subsList);
        } finally {
            if (null != subsList) {
                subsList.release();
            }
        }
    }

    /**
     * 枚举dirPath文件夹下的子项名字
     *
     * @param dirPath 文件夹全路径
     */
    public static FilesAndFoldersStringList listDir(String dirPath) {
        return listDir(dirPath, null);
    }

    @Deprecated
    public interface PathComputeCallback {
        public void computeDir(String dir);

        public boolean onFileFilter(String filePath, long fileModifyTime);
    }


    /**
     * 枚举dirPath文件夹下的子项名字
     *
     * @param dirPath 文件夹全路径
     */
    public static FilesAndFoldersStringList listDirFilter(String dirPath, final PathComputeCallback callBack) {
        return listDir(dirPath, new NameFilterTimeLimit() {
            @Override
            public boolean accept(String parent, String sub, boolean bFolder, long fileModifyTime) {
                if (callBack != null) {
                    return callBack.onFileFilter(sub, fileModifyTime);
                }

                return true;
            }

            @Override
            public boolean accept(String parent, String sub, boolean bFolder) {
                return true;
            }
        });
    }

    /**
     * 枚举dirPath文件夹下满足过滤条件的子项名字
     *
     * @param dirPath 文件夹全路径
     * @param filter  过滤条件
     */
    public static FilesAndFoldersStringList listDir(String dirPath, final NameFilter filter) {
//        if (KcmutilSoLoader.doLoad(false)) {
//            f rst = a.a(dirPath, filter);
//            if (null == rst) {
//                return null;
//            }
//            return new FilesAndFoldersStringListByJni(rst);
//        } else {
//            String[] rst = null;
//            if (null == filter) {
//                rst = new File(dirPath).list();
//                if (null == rst) {
//                    return null;
//                }
//                return new KFilesAndFoldersStringListByArray(dirPath, rst);
//            } else {
//                rst = new File(dirPath).list(new FilenameFilter() {
//
//                    @Override
//                    public boolean accept(File dir, String filename) {
//                        return filter.accept(dir.getPath(), filename, new File(dir, filename).isDirectory());
//                    }
//                });
//                if (null == rst) {
//                    return null;
//                }
//                return new KFilesAndFoldersStringListByArray(dirPath, rst);
//            }
//        }

        String[] rst = null;
        if (null == filter) {
            rst = new File(dirPath).list();
            if (null == rst) {
                return null;
            }
            return new FilesAndFoldersStringListByArray(dirPath, rst);
        } else {
            rst = new File(dirPath).list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    return filter.accept(dir.getPath(), filename, new File(dir, filename).isDirectory());
                }
            });
            if (null == rst) {
                return null;
            }
            return new FilesAndFoldersStringListByArray(dirPath, rst);
        }
    }

    /**
     * Converts a String[] containing filenames to a File[].
     * Note that the filenames must not contain slashes.
     * This method is to remove duplication in the implementation
     * of File.list's overloads.
     */
    private static File[] filenamesToFiles(String dirPath, StringList filenames) {
        if (null == dirPath || filenames == null) {
            return null;
        }
        int count = filenames.size();
        File[] result = new File[count];
        for (int i = 0; i < count; ++i) {
            result[i] = new File(dirPath, filenames.get(i));
        }
        return result;
    }

    /**
     * 文件夹下子项名列表
     * 注意，本对象不使用时，一定要调用release()，否则会发生内存泄漏。
     */
    public static interface FilesAndFoldersStringList extends StringList {
        /**
         * 注意
         * 1. 本函数返回的对象也要release释放。
         * 2. 调用过当前对象的set或shrink操作后，不可再用本函数。
         * 3. 本函数返回的对象只能在当前对象release之前使用。
         *
         * @return 所有子项中的文件名列表
         */
        StringList getFileNameList();

        /**
         * 注意
         * 1. 本函数返回的对象也要release释放。
         * 2. 调用过当前对象的set或shrink操作后，不可再用本函数。
         * 3. 本函数返回的对象只能在当前对象release之前使用。
         *
         * @return 所有子项中的文件夹名列表
         */
        StringList getFolderNameList();
    }

    /**
     * 注意，本对象不使用时，一定要调用release()，否则会发生内存泄漏。
     */
    public interface StringList extends Iterable<String> {
        /**
         * @return 元素总个数
         */
        int size();

        /**
         * 取指定元素
         *
         * @param idx 元素索引
         * @return 索引对应元素对象
         */
        String get(int idx);

        /**
         * 设定指定元素
         *
         * @param idx 元素索引
         * @param s   索引处的新元素对象，可以为null。
         */
        void set(int idx, String s);

        /**
         * 收缩到指定大小。只保留最前面size个元素，后面的元素全部放弃。
         */
        void shrink(int size);

        /**
         * 释放资源。本操作后，本对象不再可用。
         * 注意，本操作一定要被调用，否则会有内存泄漏。
         */
        void release();
    }

    private static class FilesAndFoldersStringListByArray
            extends StringListByArray
            implements FilesAndFoldersStringList {

        private String mParentPath = null;
        private KStringListByArray mFileList = null;
        private KStringListByArray mFolderList = null;

        public FilesAndFoldersStringListByArray(String parentPath, String[] array) {
            super(array);
            mParentPath = parentPath;
            if (TextUtils.isEmpty(mParentPath) && PublishVersionManager.isTest()) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public void release() {
            if (null != mFolderList) {
                mFolderList.release();
                mFolderList = null;
            }
            if (null != mFileList) {
                mFileList.release();
                mFileList = null;
            }
            super.release();
        }

        @Override
        public StringList getFileNameList() {
            StringList rst = null;
            if (null != mFileList) {
                rst = mFileList;
                mFileList = null;    // 此对象由调用方在外部释放，所以不要缓存在本地。
                return rst;
            }

            splitSubNamesIntoFilesAndFolders();

            if (null != mFileList) {
                rst = mFileList;
                mFileList = null;    // 此对象由调用方在外部释放，所以不要缓存在本地。
            }

            return rst;
        }

        @Override
        public StringList getFolderNameList() {
            StringList rst = null;
            if (null != mFolderList) {
                rst = mFolderList;
                mFolderList = null;    // 此对象由调用方在外部释放，所以不要缓存在本地。
                return rst;
            }

            splitSubNamesIntoFilesAndFolders();

            if (null != mFolderList) {
                rst = mFolderList;
                mFolderList = null;    // 此对象由调用方在外部释放，所以不要缓存在本地。
            }

            return rst;
        }

        private void splitSubNamesIntoFilesAndFolders() {
            if (null == mFolderList) {
                if (null == mFileList) {
                    String[] array = mArray;
                    if (null == array) {
                        return;
                    }

                    List<String> subFileList = new ArrayList<>();
                    List<String> subFolderList = new ArrayList<>();
                    File subItem = null;
                    for (String sub : array) {
                        subItem = new File(mParentPath, sub);
                        if (subItem.isDirectory()) {
                            subFolderList.add(sub);
                        } else {
                            subFileList.add(sub);
                        }
                    }
                    subItem = null;

                    mFileList = new KStringListByArray(
                            subFileList.toArray(new String[subFileList.size()]));
                    subFileList = null;

                    mFolderList = new KStringListByArray(
                            subFolderList.toArray(new String[subFolderList.size()]));
                    subFolderList = null;
                } else {
                    String[] array = mArray;
                    if (null == array) {
                        return;
                    }

                    if (null == mFileList.mArray || 0 == mFileList.mArray.length) {
                        mFolderList = new KStringListByArray(copyOf(array, array.length));
                        return;
                    }

                    List<String> subFolderList = new ArrayList<>();
                    for (String sub : array) {
                        if (containsInArray(mFileList.mArray, sub)) {
                            continue;
                        }

                        subFolderList.add(sub);
                    }

                    mFolderList = new KStringListByArray(
                            subFolderList.toArray(new String[subFolderList.size()]));
                    subFolderList = null;
                }
            } else {
                if (null == mFileList) {
                    String[] array = mArray;
                    if (null == array) {
                        return;
                    }

                    if (null == mFolderList.mArray || 0 == mFolderList.mArray.length) {
                        mFileList = new KStringListByArray(copyOf(array, array.length));
                        return;
                    }

                    List<String> subFileList = new ArrayList<String>();
                    for (String sub : array) {
                        if (containsInArray(mFolderList.mArray, sub)) {
                            continue;
                        }

                        subFileList.add(sub);
                    }

                    mFileList = new KStringListByArray(
                            subFileList.toArray(new String[subFileList.size()]));
                    subFileList = null;
                }
            }
        }

        private boolean containsInArray(String[] arr, String item) {
            assert (null != arr && null != item);

            for (String ai : arr) {
                if (ai.equals(item)) {
                    return true;
                }
            }

            return false;
        }
    }

    private static class KStringListByArray implements StringList {
        protected String[] mArray;

        public KStringListByArray(String[] array) {
            mArray = array;
        }

        @Override
        public Iterator<String> iterator() {
            final String[] array = mArray;
            return new Iterator<String>() {

                private int mNowPos = 0;

                @Override
                public boolean hasNext() {
                    return (mNowPos < array.length);
                }

                @Override
                public String next() {
                    return array[mNowPos++];
                }

                @Override
                public void remove() {
                    // 为避免本函数被频繁使用，影响性能，所以不予实现。
                    if(PublishVersionManager.isTest()){
                        throw new UnsupportedOperationException();
                    }
                }
            };
        }

        @Override
        public int size() {
            String[] array = mArray;
            return array.length;
        }

        @Override
        public String get(int idx) {
            String[] array = mArray;
            return array[idx];
        }

        @Override
        public void set(int idx, String s) {
            String[] array = mArray;
            array[idx] = s;
        }

        @Override
        public void shrink(int size) {
            String[] array = mArray;

            if (array.length <= size) {
                return;
            }

            mArray = copyOf(array, size);
        }

        @Override
        public void release() {
            mArray = null;
        }
    }

    @SuppressLint("NewApi")
    private static String[] copyOf(String[] array, int size) {
        assert (array.length > size);

        if (Build.VERSION.SDK_INT > 8) {
            return Arrays.copyOf(array, size);
        } else {
            String[] newArray = new String[size];
            for (int i = 0; i < size; ++i) {
                newArray[i] = array[i];
            }
            return newArray;
        }
    }

    private static class StringListByArray implements StringList {
        protected String[] mArray;

        public StringListByArray(String[] array) {
            mArray = array;
        }

        @Override
        public Iterator<String> iterator() {
            final String[] array = mArray;

            return new Iterator<String>() {

                private int mNowPos = 0;

                @Override
                public boolean hasNext() {
                    return (mNowPos < array.length);
                }

                @Override
                public String next() {
                    return array[mNowPos++];
                }

                @Override
                public void remove() {
                    // 为避免本函数被频繁使用，影响性能，所以不予实现。
                    if(PublishVersionManager.isTest()){
                        throw new UnsupportedOperationException();
                    }
                }
            };
        }

        @Override
        public int size() {
            String[] array = mArray;
            return array.length;
        }

        @Override
        public String get(int idx) {
            String[] array = mArray;
            return array[idx];
        }

        @Override
        public void set(int idx, String s) {
            String[] array = mArray;
            array[idx] = s;
        }

        @Override
        public void shrink(int size) {
            String[] array = mArray;

            if (array.length <= size) {
                return;
            }

            mArray = copyOf(array, size);
        }

        @Override
        public void release() {
            mArray = null;
        }
    }

    public static class CalcSizeCallback implements ProgressCtrl {

        private boolean mTimeOut = false;
        private long mStartTime = 0L;
        public final long TIME_OUT_MAX;
        private ScanTaskController mCtrl;
        private int mObsIdx = -1;
        private TaskCtrlObserver mObs = null;

        private final int CHECK_TIME_OUT_STOP_COUNT_MAX;
        private int mCheckTimeOutStopCount = 0;

        public CalcSizeCallback(ScanTaskController ctrl, long timeOutMax, int CheckTimeOutStopCountMax) {
            mCtrl = ctrl;
            TIME_OUT_MAX = timeOutMax;
            CHECK_TIME_OUT_STOP_COUNT_MAX = CheckTimeOutStopCountMax;
        }

        @Override
        public boolean isStop() {

            if (mTimeOut) {
                return true;
            }

            if (mStartTime > 0L && (++mCheckTimeOutStopCount > CHECK_TIME_OUT_STOP_COUNT_MAX)) {
                mCheckTimeOutStopCount = 0;
                long pauseTime = 0L;
                if (null != mObs) {
                    pauseTime = mObs.getAllPauseTime();
                }
                if (SystemClock.uptimeMillis() - mStartTime - pauseTime > TIME_OUT_MAX) {
                    mTimeOut = true;
                    return true;
                }
            }

            if (null == mCtrl) {
                return false;
            }

            return mCtrl.checkStop();
        }

        /**
         * 注意，本函数调用后，一定要调用isTimeOut()函数。
         */
        public long start() {
            if (null != mCtrl) {
                mObs = new TaskCtrlObserver();
                mObsIdx = mCtrl.addObserver(mObs);
            }
            mStartTime = SystemClock.uptimeMillis();
            return mStartTime;
        }

        /**
         * 注意，start()函数与本函数要成对调用。
         */
        public boolean isTimeOut() {

            if (null != mCtrl) {
                int obsIdx = mObsIdx;
                mObsIdx = -1;
                mCtrl.removeObserver(obsIdx);
            }

            return mTimeOut;
        }
    }

    private static class TaskCtrlObserver implements ScanTaskControllerObserver {

        private long mPauseTime = 0L;
        private long mStartPauseTime = 0L;

        @Override
        public void timeout() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void resume() {
            if (mStartPauseTime > 0L) {
                mPauseTime += (SystemClock.uptimeMillis() - mStartPauseTime);
                mStartPauseTime = 0L;
            }
        }

        @Override
        public void reset() {
        }

        @Override
        public void pause(long millis) {
            if (millis > 0L) {
                mPauseTime += millis;
                return;
            }

            mStartPauseTime = SystemClock.uptimeMillis();
        }

        public long getAllPauseTime() {
            return mPauseTime;
        }
    }

    private static class PathDeque {
        public void pushAll(List<String> list) {
            if (null == list || list.isEmpty()) {
                return;
            }

            if (null == mListList) {
                mListList = new LinkedList<List<String>>();
            }

            final int BUCKET_SIZE = 24;
            ArrayList<String> newList = null;
            if (!mListList.isEmpty()) {
                newList = (ArrayList<String>) mListList.peek();
                if (newList.size() >= BUCKET_SIZE) {
                    newList = new ArrayList<String>();
                    mListList.addFirst(newList);
                }
            } else {
                newList = new ArrayList<String>();
                mListList.addFirst(newList);
            }

            Iterator<String> iter = list.iterator();
            while (iter.hasNext()) {
                if (newList.size() >= BUCKET_SIZE) {
                    newList = new ArrayList<String>();
                    mListList.addFirst(newList);
                }
                newList.add(iter.next());
            }
        }

        public String pop() {

            if (isEmpty()) {
                return null;
            }

            List<String> temp = mListList.peek();
            int lastIndex = temp.size() - 1;
            String path = temp.get(lastIndex);
            temp.remove(lastIndex);
            if (temp.isEmpty()) {
                mListList.removeFirst();
            }

            return path;
        }

        public boolean isEmpty() {
            if (null == mListList || mListList.isEmpty()) {
                return true;
            }

            if (mListList.size() > 1) {
                return false;
            }

            List<String> temp = mListList.peek();
            if (null == temp || temp.isEmpty()) {
                return true;
            }

            return false;
        }

        LinkedList<List<String>> mListList = null;
    }

    /**
     * 判断是否空文件夹。
     *
     * @param folder                 要判断的文件(夹)路径
     * @param maxLevel               最多检查子文件夹层数，如果存在超出层数的子文件夹，就判为非空文件夹。
     * @param progCtrl               流程控制对象
     * @param subEmptyFolderList     本参数若不为null，则本函数会将folder及其之下的空文件夹记录在本对象内。
     * @param uncheckedSubFolderList 本参数若不为null，则本函数会将folder之下的未完全检查的文件夹记录在本对象内。
     * @return 空文件夹返回true，否则返回false。
     */
    public static boolean isEmptyFolder(
            String folder,
            int maxLevel,
            ProgressCtrl progCtrl,
            List<String> subEmptyFolderList,
            List<String> uncheckedSubFolderList) {
//        boolean rc = false;

        //if (!SoLoader.doLoad(false)) {
            return isFolderEmpty(folder, maxLevel);
        //}

//        try {
//            rc = a.a(folder, maxLevel, progCtrl, subEmptyFolderList, uncheckedSubFolderList);
//        } catch (NullPointerException e) {
//            throw e;
//        } catch (FailException e) {
//            throw e;
//        } catch (Exception e) {
//        }
//
//        return rc;
    }

    /**
     * 判断文件夹是否为空
     * @param folder
     * @param maxLevel
     * @return
     */
    private static boolean isFolderEmpty(String folder, int maxLevel){
        File file = new File(folder);
        try {
            String[] childs = file.list();

            if(childs == null || childs.length < 1){
                return true;
            }

            boolean isEmpty = true;
            for(String child : childs){
                if(checkFolderNotEmpty(child, maxLevel, 1)){
                    isEmpty = false;
                    break;
                }
            }

            return isEmpty;
        } catch (Exception e) {

        }

        return false;
    }

    /**
     * 判断文件夹是否不为空
     * @param dir
     * @param maxLevel 文件夹层级超过maxLevel则认为是空文件夹
     * @param currentLevel
     * @return true 不为空，fase 为空
     */
    private static boolean checkFolderNotEmpty(String dir, int maxLevel, int currentLevel){
        if(currentLevel >= maxLevel){
            return false;
        }

        File file = new File(dir);
        if(file.exists()){
            if(file.isDirectory()){
                String[] childs = file.list();
                if(childs != null && childs.length > 0){
                    boolean isEmpty = true;
                    ++currentLevel;
                    for(String child : childs){
                        if(checkFolderNotEmpty(child, maxLevel, currentLevel)){
                            isEmpty = false;
                            break;
                        }
                    }

                    return !isEmpty;
                }
            }
            else{
                return true;
            }
        }

        return false;
    }

    /**
     * 计算文件大小
     *
     * @param result 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     */
    public static boolean computePatchFileSize(List<String> path, boolean isFilterNoMedia,
                                               boolean isTimeLine, int timeLine, long[] result, long[] resultCleanTime, ProgressCtrl progCtrl,
                                               List<String> targetFiles, PathListCallback pathCallback,FilterFileCallback filterCallback) {
        boolean bFoundNomediaName = false;
        if(path==null) return false;
        NLog.d("sdCardCacheScanTask:PathOperFunc:computePatchFileSize",path.toString());
        boolean doLoad = false ;//= SoLoader.doLoad(false);
        if (!doLoad) {
            // 容错。一般都不会失败
            //NLog.i("soLoader", "load fail");
            for (String str : path) {
                computeFileSize(new File(str), result, progCtrl,filterCallback);
            }

            //上述计算没有设置resultCleanTime的值，在此处赋值，用于后续计算
            resultCleanTime[0] = result[0];
            resultCleanTime[1] = result[2];
            return bFoundNomediaName;
        }
        //NLog.i("soLoader", "load success");
        final int maxLevel = 2;

        ArrayList<String> uncheckedSubFolderList = new ArrayList<String>();
        bFoundNomediaName = computePatchFileSize(path, maxLevel, isFilterNoMedia, isTimeLine, timeLine, progCtrl, result, resultCleanTime, uncheckedSubFolderList, targetFiles, pathCallback, false);

        if (null != progCtrl && progCtrl.isStop()) {
            return bFoundNomediaName;
        }

        PathDeque pathDeque = new PathDeque();
        pathDeque.pushAll(uncheckedSubFolderList);
        uncheckedSubFolderList.clear();
        uncheckedSubFolderList.trimToSize();

        path = null;
        boolean bRst = false;
        String subFolderPath = null;

        while (!pathDeque.isEmpty()) {
            subFolderPath = pathDeque.pop();
            if (TextUtils.isEmpty(subFolderPath)) {
                continue;
            }

            List<String> subFolderList = new ArrayList<String>();
            subFolderList.add(subFolderPath);

            bRst = computePatchFileSize(subFolderList, maxLevel, isFilterNoMedia, isTimeLine, timeLine, progCtrl, result, resultCleanTime, uncheckedSubFolderList, targetFiles, pathCallback, false);
            if (!bFoundNomediaName) {
                bFoundNomediaName = bRst;
            }

            // 减掉重复计算的个数
            --result[1];

            pathDeque.pushAll(uncheckedSubFolderList);
            uncheckedSubFolderList.clear();
            uncheckedSubFolderList.trimToSize();

            if (null != progCtrl && progCtrl.isStop()) {
                break;
            }
        }

        return bFoundNomediaName;
    }

    /**
     * 计算文件大小
     *
     * @param path                   要计算的文件或文件夹路径
     * @param maxLevel               最大深度
     * @param isFilterNoMedia        是否过滤.nomedia文件，并且当存在.nomedia文件(夹)时，本函数返回值为true
     * @param isTimeLine             是否记录按时间线清理的数据
     * @param timeLine               时间线(单位：天)
     * @param progCtrl               流程控制对象
     * @param result                 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     * @param resultCleanTime        数组大小为2, resultCleanTime[0]:时间线前的文件大小，resultCleanTime[1]：时间线前的文件数
     * @param uncheckedSubFolderList 本参数若不为null，则本函数会将path之下因深
     *                               度限制而未深入枚举的文件夹路径记录在本对象内。
     *                               注意，此对象中记录的文件夹个数已计算在result[1]内。
     * @param targetFiles            满足要求的文件列表，由native函数添加内容
     * @param pathCallback           文件全路径回调。不可以为null（每100个文件调用一次）
     * @param bCalcSparseFileBlkSize 是否计算真实大小
     * @return 当isFilterNoMedia为false时，返回值无意义；当isFilterNoMedia为true时，返回值表示当前文件夹下是否存在.nomedia文件(夹)名字
     * @throws IllegalArgumentException result.length小于3时抛出
     * @throws NullPointerException     初始化状态异常时可能抛出
     * @throws FailException            执行计算和结果数据转换失败时抛出
     * @throws OutOfMemoryError         内存不足时抛出
     */
    public static boolean computePatchFileSize(
            List<String> path,
            int maxLevel,
            boolean isFilterNoMedia,
            boolean isTimeLine,
            int timeLine,
            ProgressCtrl progCtrl,
            long[] result,
            long[] resultCleanTime,
            List<String> uncheckedSubFolderList,
            List<String> targetFiles,
            final PathListCallback pathCallback,
            boolean bCalcSparseFileBlkSize) {
//        try {
//            return a.c(path, maxLevel, isFilterNoMedia, isTimeLine, timeLine, progCtrl, result, resultCleanTime, uncheckedSubFolderList, targetFiles,
//                    new g() {
//
//                        @Override
//                        public void a(int sizes) {
//                            if(null != pathCallback ){
//                                pathCallback.onFile(sizes);
//                            }
//                        }
//                    },bCalcSparseFileBlkSize);
//        } catch (IllegalArgumentException e) {
//            throw e;
//        } catch (NullPointerException e) {
//            throw e;
//        } catch (FailException e) {
//            throw e;
//        } catch (UnsatisfiedLinkError e) {
//            CheckMoreCrashInfo();
//        } catch (Exception e) {
//        }

        return false;
    }

    private static void CheckMoreCrashInfo() {

    }

    /**
     * 计算文件大小
     *
     * @param result 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     */
    public static void computeFileSize(String path, long[] result, ProgressCtrl progCtrl, PathCallback pathCallback, List<String> filterSubDirList) {
        computeFileSize(path, result, progCtrl, pathCallback, false, filterSubDirList);
    }

    private static void computeFileSize(String path, long[] result,
                                        ProgressCtrl progCtrl, final PathCallback pathCallback, boolean bCalcSparseFileBlkSize, List<String> filterSubDirList) {
        NLog.d("sdCardCacheScanTask:PathOperFunc:computeFileSize1",path);
        boolean loaded = false;//SoLoader.doLoad(false);
        NLog.d(TAG, "computeFileSize loaded = "+loaded);
        if (!loaded) {
            // 容错。一般都不会失败
            computeFileSizeWithFilter(new File(path), result, progCtrl, new PathComputeCallback() {
                @Override
                public void computeDir(String dir) {

                }

                @Override
                public boolean onFileFilter(String filePath, long fileModifyTime) {
                    if (pathCallback != null) {
                        return pathCallback.OnFilter(filePath, fileModifyTime);
                    }

                    return true;
                }
            });
            return;
        }

//        final int maxLevel = 2;
//
//        ArrayList<String> uncheckedSubFolderList = new ArrayList<>();
//        computeFileSize(path, maxLevel, progCtrl, result, uncheckedSubFolderList, pathCallback, bCalcSparseFileBlkSize, filterSubDirList);
//
//        if (null != progCtrl && progCtrl.isStop()) {
//            return;
//        }
//
//        PathDeque pathDeque = new PathDeque();
//        pathDeque.pushAll(uncheckedSubFolderList);
//        uncheckedSubFolderList.clear();
//        uncheckedSubFolderList.trimToSize();
//
//        path = null;
//        String subFolderPath = null;
//
//        while (!pathDeque.isEmpty()) {
//            subFolderPath = pathDeque.pop();
//            if (TextUtils.isEmpty(subFolderPath)) {
//                continue;
//            }
//
//            computeFileSize(subFolderPath, maxLevel, progCtrl, result, uncheckedSubFolderList, pathCallback, bCalcSparseFileBlkSize, filterSubDirList);
//
//            // 减掉重复计算的个数
//            --result[1];
//
//            pathDeque.pushAll(uncheckedSubFolderList);
//            uncheckedSubFolderList.clear();
//            uncheckedSubFolderList.trimToSize();
//
//            if (null != progCtrl && progCtrl.isStop()) {
//                break;
//            }
//        }
    }

    /**
     * 计算文件大小
     *
     * @param result 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     */
    @Deprecated
    public static void computeFileSizeWithFilter(File paramFile, long[] result, ProgressCtrl progCtrl, PathComputeCallback pathCallBack) {
        final int maxLevel = 2;
        if(paramFile==null || !paramFile.exists())return;
        NLog.d("sdCardCacheScanTask:PathOperFunc:computeFileSizeWithFilter",paramFile.getPath());
        ArrayList<String> uncheckedSubFolderList = new ArrayList<String>();
        computeFileSizeFilter(paramFile, maxLevel, progCtrl, result, uncheckedSubFolderList, pathCallBack);

        if (null != progCtrl && progCtrl.isStop()) {
            return;
        }

        PathDeque pathDeque = new PathDeque();
        pathDeque.pushAll(uncheckedSubFolderList);
        uncheckedSubFolderList.clear();
        uncheckedSubFolderList.trimToSize();

        paramFile = null;
        String subFolderPath = null;

        while (!pathDeque.isEmpty()) {
            subFolderPath = pathDeque.pop();
            if (TextUtils.isEmpty(subFolderPath)) {
                continue;
            }

            computeFileSizeFilter(new File(subFolderPath), maxLevel, progCtrl, result, uncheckedSubFolderList, pathCallBack);

            // 减掉重复计算的个数
            --result[1];

            pathDeque.pushAll(uncheckedSubFolderList);
            uncheckedSubFolderList.clear();
            uncheckedSubFolderList.trimToSize();

            if (null != progCtrl && progCtrl.isStop()) {
                break;
            }
        }
    }

    /**
     * 计算文件大小
     *
     * @param paramFile              要计算的文件或文件夹路径
     * @param maxLevel               最大深度
     * @param progCtrl               流程控制对象
     * @param result                 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     * @param uncheckedSubFolderList 本参数若不为null，则本函数会将path之下因深
     *                               度限制而未深入枚举的文件夹路径记录在本对象内。
     *                               注意，此对象中记录的文件夹个数已计算在result[1]内。
     * @param callback               回调对象
     */
    @Deprecated
    public static void computeFileSizeFilter(
            File paramFile,
            int maxLevel,
            ProgressCtrl progCtrl,
            long[] result,
            List<String> uncheckedSubFolderList,
            PathComputeCallback callback) {

        if (null == paramFile || !paramFile.exists() || result.length < 3 || maxLevel < 0) {
            return;
        }
        NLog.d("sdCardCacheScanTask:PathOperFunc:computeFileSizeFilter",paramFile.getPath());
        if (null != progCtrl && progCtrl.isStop()) {
            return;
        }

        if (callback != null) {
            callback.computeDir(paramFile.getAbsolutePath());
        }

        StringList arrayOfFileName = null;
        int length = 0;
        if (paramFile.isDirectory()) {
            ++result[1];

            if (maxLevel > 0) {
                arrayOfFileName = PathOperFunc.listDirFilter(paramFile.getPath(), callback);
                if (arrayOfFileName != null) {
                    try {
                        int folderNum = 0;
                        length = arrayOfFileName.size();
                        for (int i = 0; i < length; ++i) {
                            if (null != progCtrl && progCtrl.isStop()) {
                                return;
                            }

                            String fileName = arrayOfFileName.get(i);
                            File file = new File(FileUtils.addSlash(paramFile.getPath()) + fileName);
                            if (!file.isDirectory()) {
                                PathOperFunc.computeFileSizeFilter(
                                        file, maxLevel - 1, progCtrl, result,
                                        null, callback);
                                arrayOfFileName.set(i, null);
                            } else {
                                if (i != folderNum) {
                                    arrayOfFileName.set(folderNum, fileName);
                                    arrayOfFileName.set(i, null);
                                }
                                ++folderNum;
                            }
                        }

                        if (folderNum > 0) {
                            arrayOfFileName.shrink(folderNum);

                            for (int i = 0; i < folderNum; ++i) {
                                if (null != progCtrl && progCtrl.isStop()) {
                                    return;
                                }
                                File file = new File(FileUtils.addSlash(paramFile.getPath()) + arrayOfFileName.get(i));
                                PathOperFunc.computeFileSizeFilter(
                                        file, maxLevel - 1, progCtrl, result,
                                        uncheckedSubFolderList, callback);
                                arrayOfFileName.set(i, null);
                            }
                        }
                    } finally {
                        arrayOfFileName.release();
                    }
                }
            } else {
                if (null != uncheckedSubFolderList) {
                    uncheckedSubFolderList.add(paramFile.getPath());
                }
            }
        } else if (paramFile.isFile()) {
            if (callback != null && callback.onFileFilter(paramFile.getPath(), paramFile.lastModified())) {
                ++result[2];
                result[0] += paramFile.length();
            }
        }
    }

//    /**
//     * 计算文件大小
//     *
//     * @param path                   要计算的文件或文件夹路径
//     * @param maxLevel               最大深度
//     * @param progCtrl               流程控制对象
//     * @param result                 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
//     * @param uncheckedSubFolderList 本参数若不为null，则本函数会将path之下因深
//     *                               度限制而未深入枚举的文件夹路径记录在本对象内。
//     *                               注意，此对象中记录的文件夹个数已计算在result[1]内。
//     * @param pathCallback           文件全路径回调。可以为null
//     */
//    public static void computeFileSize(
//            String path,
//            int maxLevel,
//            ProgressCtrl progCtrl,
//            long[] result,
//            List<String> uncheckedSubFolderList,
//            final PathCallback pathCallback,
//            boolean bCalcSparseFileBlkSize,
//            List<String> filterSubDirList) {
//        NLog.d("sdCardCacheScanTask:PathOperFunc:computeFileSize2",path);
//        try {
//            a.b(path, maxLevel, progCtrl, result, uncheckedSubFolderList,
//                    null == pathCallback ? null : new d() {
//                        @Override
//                        public void a(String a, long s, int at, int mt, int ct) {
//                            pathCallback.onFile(a, s, at, mt, ct);
//                        }
//
//                        @Override
//                        public void b(String a, String b, long s) {
//                            pathCallback.onFeedback(a, b, s);
//                        }
//
//                        @Override
//                        public void c(String strRootDir) {
//                            // TODO Auto-generated method stub
//
//                        }
//
//                        @Override
//                        public void e(String strRootDir, String strSubFile) {
//                            // TODO Auto-generated method stub
//
//                        }
//
//                        @Override
//                        public void f(String strRootDir, String strSubFolder) {
//                            // TODO Auto-generated method stub
//
//                        }
//
//                        @Override
//                        public void g(String strRootDir) {
//                            // TODO Auto-generated method stub
//
//                        }
//
//                        @Override
//                        public void h(String a, boolean b, boolean c, int d) {
//                            // TODO Auto-generated method stub
//
//                        }
//
//                        @Override
//                        public boolean z(String filePath, long fileModifyTime) {
//                            if (pathCallback != null) {
//                                return pathCallback.OnFilter(filePath, fileModifyTime);
//                            }
//
//                            return true;
//                        }
//                    }, bCalcSparseFileBlkSize, filterSubDirList);
//        } catch (IllegalArgumentException e) {
//            throw e;
//        } catch (NullPointerException e) {
//            throw e;
//        } catch (FailException e) {
//            throw e;
//        } catch (UnsatisfiedLinkError e) {
//            CheckMoreCrashInfo();
//        } catch (Exception e) {
//        }
//    }

    /**
     * 结合MediaStore计算文件大小的接口
     * 由nNeedMSCalcFilesLimit限制哪些子目录可以使用MediaStore方式计算文件大小，则不用NDK计算直接通过msCalcCallback回调由MediaStore方式算目录大小
     *
     * @param path                    路径
     * @param maxLevel                扫描层级
     * @param progCtrl                控制接口
     * @param result                  返回结果 ， 如果有目录通过msCalcCallback回调传出，则不会记录此目录的大小，但是会记录此目录的文件和目录个数。
     * @param uncheckedSubFolderList  为检查的子目录列表
     * @param pathCallback            路径回调
     * @param pathListCallback        路径回调2
     * @param bCalcSparseFileBlkSize  是否按块大小来计算文件大小的标志
     * @param nNeedMSCalcFilesLimit   需要使用MediaStore计算单层目录文件个数限制。只有等于或超过此限制，才能使用MediaStore计算大小。为0时表示无效
     * @param nNeedMSCalcMTimeLimit_s 时间限制,单位s.表示目录的最后修改时间与当前时间的差必须大于nNeedMSCalcMTimeLimit_s,才会启用MediaStore.
     *                                此参数与nNeedMSCalcFilesLimit配合使用。是and的关系。为0时表示无效
     * @param msCalcCallback          符合nNeedMSCalcFilesLimit的目录回调接口
     */
    public static void computeFileSizeNeedMSCalc(
            String path,
            int maxLevel,
            ProgressCtrl progCtrl,
            long[] result,
            List<String> uncheckedSubFolderList,
            final PathCallback pathCallback,
            final PathListCallback pathListCallback,
            boolean bCalcSparseFileBlkSize,
            int nNeedMSCalcFilesLimit,
            long nNeedMSCalcMTimeLimit_s,
            final NeedMSCalcCallback msCalcCallback) {
        NLog.d("sdCardCacheScanTask:PathOperFunc:computeFileSizeNeedMSCalc",path);
        try {

            d mypathCallback = (null == pathCallback) ? null : new d() {
                @Override
                public void a(String a, long s, int at, int mt, int ct) {
                    pathCallback.onFile(a, s, at, mt, ct);
                }

                @Override
                public void b(String a, String b, long s) {

                }

                @Override
                public void c(String strRootDir) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void e(String strRootDir, String strSubFile) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void f(String strRootDir, String strSubFolder) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void g(String strRootDir) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void h(String a, boolean b, boolean c, int d) {
                    // TODO Auto-generated method stub

                }

                @Override
                public boolean z(String filePath, long fileModifyTime) {
                    if (pathCallback != null) {
                        return pathCallback.OnFilter(filePath, fileModifyTime);
                    }

                    return true;
                }
            };

            g mypathListCallback = (null == pathListCallback) ? null : new g() {
                @Override
                public void a(int sizes) {
                    pathListCallback.onFile(sizes);
                }
            };

            a.ea mymsCalcCallback = (msCalcCallback == null) ? null : new a.ea() {
                @Override
                public void a(String strPath, long nModifiedTime_s, long FilesCount) {
                    msCalcCallback.onFolder(strPath, nModifiedTime_s, FilesCount);
                }
            };
            a.b(path, maxLevel, progCtrl, result, uncheckedSubFolderList,
                    mypathCallback, mypathListCallback,
                    bCalcSparseFileBlkSize, nNeedMSCalcFilesLimit, nNeedMSCalcMTimeLimit_s, mymsCalcCallback);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NullPointerException e) {
            throw e;
        } catch (FailException e) {
            throw e;
        } catch (UnsatisfiedLinkError e) {
            CheckMoreCrashInfo();
        } catch (Exception e) {
        }
    }

    public static void computeFileSizeNeedMSCalc(String path,
                                                 long[] result,
                                                 ProgressCtrl progCtrl,
                                                 final PathCallback pathCallback,
                                                 final PathListCallback pathListCallback,
                                                 boolean bCalcSparseFileBlkSize,
                                                 int nNeedMSCalcFilesLimit,
                                                 long nNeedMSCalcMTimeLimit_s,
                                                 final NeedMSCalcCallback msCalcCallback) {

        boolean doLoad = false ;//= SoLoader.doLoad(false);
        if (!doLoad) {
            // 容错。一般都不会失败
            computeFileSizeWithFilter(new File(path), result, progCtrl, new PathComputeCallback() {
                @Override
                public void computeDir(String dir) {

                }

                @Override
                public boolean onFileFilter(String filePath, long fileModifyTime) {
                    if (pathCallback != null) {
                        return pathCallback.OnFilter(filePath, fileModifyTime);
                    }

                    return true;
                }
            });
            return;
        }

//        final int maxLevel = 2;
//
//        ArrayList<String> uncheckedSubFolderList = new ArrayList<String>();
//        computeFileSizeNeedMSCalc(path, maxLevel, progCtrl, result, uncheckedSubFolderList, pathCallback, pathListCallback, bCalcSparseFileBlkSize, nNeedMSCalcFilesLimit, nNeedMSCalcMTimeLimit_s, msCalcCallback);
//
//        if (null != progCtrl && progCtrl.isStop()) {
//            return;
//        }
//
//        PathDeque pathDeque = new PathDeque();
//        pathDeque.pushAll(uncheckedSubFolderList);
//        uncheckedSubFolderList.clear();
//        uncheckedSubFolderList.trimToSize();
//
//        path = null;
//        String subFolderPath = null;
//
//        while (!pathDeque.isEmpty()) {
//            subFolderPath = pathDeque.pop();
//            if (TextUtils.isEmpty(subFolderPath)) {
//                continue;
//            }
//
//            computeFileSizeNeedMSCalc(subFolderPath, maxLevel, progCtrl, result, uncheckedSubFolderList, pathCallback, pathListCallback, bCalcSparseFileBlkSize, nNeedMSCalcFilesLimit, nNeedMSCalcMTimeLimit_s, msCalcCallback);
//
//            // 减掉重复计算的个数
//            --result[1];
//
//            pathDeque.pushAll(uncheckedSubFolderList);
//            uncheckedSubFolderList.clear();
//            uncheckedSubFolderList.trimToSize();
//
//            if (null != progCtrl && progCtrl.isStop()) {
//                break;
//            }
//        }
    }

    public static void computeNewFileSize(
            String path,
            ProgressCtrl progCtrl,
            long[] result,
            final PathCallback pathCallback,
            final PathListCallback pathListCallback,
            boolean bCalcSparseFileBlkSize,
            List<String> knownFileList) {
        NLog.d("sdCardCacheScanTask:PathOperFunc:computeNewFileSize3",path);
        boolean doLoad = false ;//= SoLoader.doLoad(false)
        if (!doLoad) {
            // 容错。一般都不会失败
            computeFileSizeWithFilter(new File(path), result, progCtrl, new PathComputeCallback() {
                @Override
                public void computeDir(String dir) {

                }

                @Override
                public boolean onFileFilter(String filePath, long fileModifyTime) {
                    if (pathCallback != null) {
                        return pathCallback.OnFilter(filePath, fileModifyTime);
                    }

                    return true;
                }
            });
            return;
        }
        try {

            d mypathCallback = (null == pathCallback) ? null : new d() {
                @Override
                public void a(String a, long s, int at, int mt, int ct) {
                    pathCallback.onFile(a, s, at, mt, ct);
                }

                @Override
                public void b(String a, String b, long s) {

                }

                @Override
                public void c(String strRootDir) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void e(String strRootDir, String strSubFile) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void f(String strRootDir, String strSubFolder) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void g(String strRootDir) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void h(String a, boolean b, boolean c, int d) {
                    // TODO Auto-generated method stub

                }

                @Override
                public boolean z(String filePath, long fileModifyTime) {
                    if (pathCallback != null) {
                        return pathCallback.OnFilter(filePath, fileModifyTime);
                    }

                    return true;
                }
            };

            g mypathListCallback = (null == pathListCallback) ? null : new g() {
                @Override
                public void a(int sizes) {
                    pathListCallback.onFile(sizes);
                }
            };


            a.b(path, progCtrl, result,
                    mypathCallback, mypathListCallback,
                    bCalcSparseFileBlkSize, knownFileList);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NullPointerException e) {
            throw e;
        } catch (FailException e) {
            throw e;
        } catch (UnsatisfiedLinkError e) {
            CheckMoreCrashInfo();
        } catch (Exception e) {
        }
    }


    /**
     * 计算文件及文件夹大小
     * @param result数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     */
    /**
     * 计算文件及文件夹大小
     * @param path      要计算的文件或文件夹路径
     * @param result    数组大小为3，result[0]:文件及文件夹大小，result[1]：文件夹数，result[2]：文件数
     * @param progCtrl  流程控制对象
     */
    public static void computeRealSize(String path, long[] result, ProgressCtrl progCtrl ){
        computeRealSize(path, result, progCtrl, null);
    }

    /**
     * 计算文件及文件夹大小
     * @param result数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     */
    /**
     * 计算文件及文件夹大小
     * @param path      要计算的文件或文件夹路径
     * @param result    数组大小为3，result[0]:文件及文件夹大小，result[1]：文件夹数，result[2]：文件数
     * @param progCtrl  流程控制对象
     * @param callback
     */
    public static void computeRealSize(String path, long[] result, ProgressCtrl progCtrl, PathComputeCallback callback) {
        NLog.d("sdCardCacheScanTask:PathOperFunc:computeRealSize",path);
        boolean doLoad = false ;//= SoLoader.doLoad(false)
        if (!doLoad) {
            // 容错。一般都不会失败
            computeFileSize(new File(path), result, progCtrl, callback,null);
            return;
        }

        final int maxLevel = 2;

        ArrayList<String> uncheckedSubFolderList = new ArrayList<>();
        computeRealSize(path, maxLevel, progCtrl, result, uncheckedSubFolderList);

        if (null != progCtrl && progCtrl.isStop()) {
            return;
        }

        PathDeque pathDeque = new PathDeque();
        pathDeque.pushAll(uncheckedSubFolderList);
        uncheckedSubFolderList.clear();
        uncheckedSubFolderList.trimToSize();

        path = null;
        String subFolderPath = null;

        while (!pathDeque.isEmpty()) {
            subFolderPath = pathDeque.pop();
            if (TextUtils.isEmpty(subFolderPath)) {
                continue;
            }

            computeRealSize(subFolderPath, maxLevel, progCtrl, result, uncheckedSubFolderList);

            // 减掉重复计算的个数
            --result[1];

            pathDeque.pushAll(uncheckedSubFolderList);
            uncheckedSubFolderList.clear();
            uncheckedSubFolderList.trimToSize();

            if (null != progCtrl && progCtrl.isStop()) {
                break;
            }
        }
    }
    /**
     * 计算文件及文件夹大小
     * @param path      要计算的文件或文件夹路径
     * @param maxLevel  最大深度
     * @param progCtrl  流程控制对象
     * @param result    数组大小为3，result[0]:文件及文件夹大小，result[1]：文件夹数，result[2]：文件数
     * @param uncheckedSubFolderList 本参数若不为null，则本函数会将path之下因深
     *                                度限制而未深入枚举的文件夹路径记录在本对象内。
     *                                注意，此对象中记录的文件夹本身的size和文件夹个数已计算在result[0]和result[1]内。
     */
    public static void computeRealSize(
            String path,
            int maxLevel,
            ProgressCtrl progCtrl,
            long[] result,
            List<String> uncheckedSubFolderList) {
        NLog.d("sdCardCacheScanTask:PathOperFunc:computeRealSize1",path);
        try {
            a.a(path, maxLevel, progCtrl, result, uncheckedSubFolderList);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NullPointerException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 计算文件大小
     *
     * @param result 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     */
    @Deprecated
    public static void computeFileSize(File paramFile, long[] result, ProgressCtrl progCtrl,FilterFileCallback filterCallback) {
        computeFileSize(paramFile, result, progCtrl, null,filterCallback);
    }

    /**
     * 计算文件大小
     *
     * @param result 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     */
    public static void computeFileSize(String path, long[] result, ProgressCtrl progCtrl) {
        computeFileSize(path, result, progCtrl, null, null);
    }

    /**
     * 计算文件大小
     *
     * @param result 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     */
    @Deprecated
    public static void computeFileSize(File paramFile, long[] result, ProgressCtrl progCtrl, PathComputeCallback callback,FilterFileCallback filterCallback) {
        if(paramFile==null || !paramFile.exists())return;
        NLog.d("sdCardCacheScanTask:PathOperFunc:computeFileSize",paramFile.getPath());
        final int maxLevel = 2;

        ArrayList<String> uncheckedSubFolderList = new ArrayList<String>();
        computeFileSize(paramFile, maxLevel, progCtrl, result, uncheckedSubFolderList, callback,filterCallback);

        if (null != progCtrl && progCtrl.isStop()) {
            return;
        }

        PathDeque pathDeque = new PathDeque();
        pathDeque.pushAll(uncheckedSubFolderList);
        uncheckedSubFolderList.clear();
        uncheckedSubFolderList.trimToSize();

        paramFile = null;
        String subFolderPath = null;

        while (!pathDeque.isEmpty()) {
            subFolderPath = pathDeque.pop();
            if (TextUtils.isEmpty(subFolderPath)) {
                continue;
            }

            computeFileSize(new File(subFolderPath), maxLevel, progCtrl, result, uncheckedSubFolderList, callback,filterCallback);

            // 减掉重复计算的个数
            --result[1];

            pathDeque.pushAll(uncheckedSubFolderList);
            uncheckedSubFolderList.clear();
            uncheckedSubFolderList.trimToSize();

            if (null != progCtrl && progCtrl.isStop()) {
                break;
            }
        }
    }

    /**
     * 计算文件大小
     *
     * @param paramFile              要计算的文件或文件夹路径
     * @param maxLevel               最大深度
     * @param progCtrl               流程控制对象
     * @param result                 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     * @param uncheckedSubFolderList 本参数若不为null，则本函数会将path之下因深
     *                               度限制而未深入枚举的文件夹路径记录在本对象内。
     *                               注意，此对象中记录的文件夹个数已计算在result[1]内。
     * @param callback               回调对象
     */
    @Deprecated
    public static void computeFileSize(
            File paramFile,
            int maxLevel,
            ProgressCtrl progCtrl,
            long[] result,
            List<String> uncheckedSubFolderList,
            PathComputeCallback callback,FilterFileCallback filterCallback) {
        if (null == paramFile || !paramFile.exists() || result.length < 3 || maxLevel < 0) {
            return;
        }

        if (null != progCtrl && progCtrl.isStop()) {
            return;
        }

        if (callback != null) {
            callback.computeDir(paramFile.getAbsolutePath());
        }
        NLog.d("sdCardCacheScanTask:PathOperFunc:computeFileSize5",paramFile.getPath());
        StringList arrayOfFileName = null;
        int length = 0;
        if (paramFile.isDirectory()) {
            ++result[1];

            if (maxLevel > 0) {
                arrayOfFileName = PathOperFunc.listDir(paramFile.getPath());
                if (arrayOfFileName != null) {
                    try {
                        int folderNum = 0;
                        length = arrayOfFileName.size();
                        for (int i = 0; i < length; ++i) {
                            if (null != progCtrl && progCtrl.isStop()) {
                                return;
                            }

                            String fileName = arrayOfFileName.get(i);
                            File file = new File(FileUtils.addSlash(paramFile.getPath()) + fileName);
                            if (!file.isDirectory()) {
                                PathOperFunc.computeFileSize(
                                        file, maxLevel - 1, progCtrl, result,
                                        null, callback,filterCallback);
                                arrayOfFileName.set(i, null);
                            } else {
                                if (i != folderNum) {
                                    arrayOfFileName.set(folderNum, fileName);
                                    arrayOfFileName.set(i, null);
                                }
                                ++folderNum;
                            }
                        }

                        if (folderNum > 0) {
                            arrayOfFileName.shrink(folderNum);

                            for (int i = 0; i < folderNum; ++i) {
                                if (null != progCtrl && progCtrl.isStop()) {
                                    return;
                                }
                                File file = new File(FileUtils.addSlash(paramFile.getPath()) + arrayOfFileName.get(i));
                                PathOperFunc.computeFileSize(
                                        file, maxLevel - 1, progCtrl, result,
                                        uncheckedSubFolderList, callback,filterCallback);
                                arrayOfFileName.set(i, null);
                            }
                        }
                    } finally {
                        arrayOfFileName.release();
                    }
                }
            } else {
                if (null != uncheckedSubFolderList) {
                    uncheckedSubFolderList.add(paramFile.getPath());
                }
            }
        } else if (paramFile.isFile()) {
            //这里需要根据时间线来进行过滤
            if(filterCallback!=null && filterCallback.onFilterFile(paramFile)){
                return;
            }
            ++result[2];
            result[0] += paramFile.length();
        }
    }

    /**
     * 计算文件时候的回调接口
     */
    public interface FilterFileCallback{
        //返回true则不去计算
        boolean onFilterFile(File f);
    }

    /**
     * 回调符合条件的目录
     */
    public interface NeedMSCalcCallback {
        /**
         * 回调符合条件的目录
         *
         * @param strPath         目录路径
         * @param nModifiedTime_s 最后修改时间，单位：s
         * @param FilesCount      单层目录文件个数
         */
        void onFolder(String strPath, long nModifiedTime_s, long FilesCount);
    }


    public interface EmptyFolderCallback {
        boolean onFilter(String path);

        void onFoundEmptyFolder(String path, int num);

        void onStatus(String path);

        void onStepNum(int n);

        void onAddStep();
    }

    private static long TIME_LIMIT = 5*1000;
    public static void getAllEmptyFolders(Context ctx, ProgressCtrl progCtrl, EmptyFolderCallback cb) {
        if (null == ctx) {
            return;
        }
        ContentResolverHelper cr = new ContentResolverHelper(ctx.getContentResolver());
        if (null == cr) {
            return;
        }
        if (null != progCtrl && progCtrl.isStop()) {
            return;
        }
        long nStartTime = SystemClock.uptimeMillis();
        int count = 0;
        HashMap<String, Integer> setPath = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = cr.query(MediaStore.Files.getContentUri("external"),
                    new String[] { MediaStore.Files.FileColumns.DATA },
                    "format = 12289 and _id not in (select distinct(parent) from files)",
                    null,
                    "_data desc", progCtrl, 11*1000 );

            if (null != cursor && cursor.moveToFirst()) {
                File nowPath = null;
                String path = null;
                FilesAndFoldersStringList subs = null;
                do {
                    if (null != progCtrl && progCtrl.isStop()) {
                        break;
                    }

                    path = cursor.getString(0);
                    if (TextUtils.isEmpty(path)) {
                        continue;
                    }

                    //白名单过滤空文件夹名字
                    if (cb != null && cb.onFilter(path)) {
                        continue;
                    }

                    subs = EnableCacheListDir.listDir(path);

                    StringList fileNames = null;
                    StringList folderNames = null;
                    try {

                        if (null != subs) {
                            fileNames = subs.getFileNameList();
                            if (null == fileNames || fileNames.size() > 0) {
                                continue;
                            }
                            //folder number
                            folderNames = subs.getFolderNameList();
                            if (null == folderNames || folderNames.size() > 0) {
                                continue;
                            }
                            setPath.put(path, 1); //1 empty folder
                        }

                    } finally {
                        if (null != fileNames) {
                            fileNames.release();
                            fileNames = null;
                        }
                        if (null != folderNames) {
                            folderNames.release();
                            folderNames = null;
                        }
                        if (null != subs) {
                            subs.release();
                            subs = null;
                        }
                    }

                    if (null != cb) {
                        if ((++count & 0xf) == 0) {  //每16个回报一次
                            cb.onStatus(path);
                        }
                        cb.onAddStep();
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        }
        long nDiffTime = SystemClock.uptimeMillis()-nStartTime;
        if ( nDiffTime > TIME_LIMIT ) {
            NLog.d( "TimeTrace", "EmptyFolders query usetime:"+nDiffTime+" found:"+setPath.size() );
        }

        if (null != progCtrl && progCtrl.isStop()) {
            return;
        }
        nStartTime = SystemClock.uptimeMillis();
        getAncestorEmptyFolder(setPath, progCtrl, cb);
        nDiffTime = SystemClock.uptimeMillis()-nStartTime;
        if ( nDiffTime > TIME_LIMIT ) {
            NLog.d( "TimeTrace", "EmptyFolders getAncestor usetime:"+nDiffTime+" found:"+setPath.size() );
        }
    }

    private static void getAncestorEmptyFolder(
            HashMap<String,Integer> mapPath, ProgressCtrl progCtrl, EmptyFolderCallback cb) {

        int maxLevel = 16;
        if (null == mapPath || mapPath.isEmpty()) {
            return;
        }

        ArrayList<String> storagePathList = new StorageList().getMountedVolumePaths();
        if (null == storagePathList || storagePathList.isEmpty()) {
            return;
        }

        for (int i = 0; i < storagePathList.size(); ++i) {
            storagePathList.set(i, FileUtils.removeSlash(storagePathList.get(i)));
        }

        if (null != cb) {
            cb.onStepNum(mapPath.size() * 3);
        }

        HashMap<String,Integer> resultPath = new HashMap<>();
        int count = 0;
        do {

            //recursive 寻找限制 maxLevel 次数
            if (count > maxLevel) {
                break;
            }

            String parentPath = null;
            String lastParentPath = null;
            HashMap<String, Integer> checkPathList = new HashMap<> ();
            HashMap<String, Integer> setNeedCheckPath = new HashMap<>();
            Set<String> setPath = mapPath.keySet();
            String[] sortedPathArray = setPath.toArray(new String[mapPath.size()]);
            Arrays.sort(sortedPathArray, String.CASE_INSENSITIVE_ORDER);
            for (int idx = 0; idx < sortedPathArray.length; idx++) {

                String path = sortedPathArray[idx];

                assert(!TextUtils.isEmpty(path));

                if (null != progCtrl && progCtrl.isStop()) {
                    break;
                }

                //是sdcard根目录
                if (isRootPathOfVolume(path, storagePathList)) {
                    continue;
                }

                //找出父目录也是空文件夹的，避免删完之後又多出一个空文件夹。
                parentPath = getParent(path);
                if (null == parentPath) {
                    continue;
                }

                if (null != cb && ((idx & 0xf) == 0)) {  //每16个回报一次status
                    cb.onStatus(parentPath);
                }

                long nStartTime = SystemClock.uptimeMillis();
                //null == lastParentPath 是第一个
                if ((null != lastParentPath && !parentPath.equals(lastParentPath))) {
                    if (!isParentEmptyFolder(lastParentPath, setPath, setNeedCheckPath.keySet(), resultPath.keySet(), storagePathList, cb)) {
                        //当父目录不是empty，代表已经找到最上层空目录，加入resultPath 以及整合已经存放到resultPath里面的数值。
                        for (String checkPath : checkPathList.keySet()) {
                            int resultPathNum = 0;
                            if (resultPath.get(checkPath) != null && resultPath.get(checkPath) >= 1) {
                                resultPathNum = resultPath.get(checkPath) - 1;
                            }
                            resultPath.put(checkPath, checkPathList.get(checkPath) + resultPathNum);
                        }
                        if (null != cb) {
                            cb.onAddStep();
                        }
                    } else {
                        //is Parent Empty folder
                        int listDirNum = 0;
                        for (String checkPath : checkPathList.keySet()) {
                            listDirNum += checkPathList.get(checkPath);
                        }
                        setNeedCheckPath.put(lastParentPath, listDirNum + 1); //加入parentPath本身
                    }
                    checkPathList.clear();
                }
                lastParentPath = parentPath;
                checkPathList.put(path, mapPath.get(path));
                long nDiffTime = SystemClock.uptimeMillis()-nStartTime;
                if ( nDiffTime > TIME_LIMIT ) {
                    NLog.d( "TimeTrace", "EmptyFolders isEmptyFolder usetime:"+nDiffTime+" parentPath:"+parentPath );
                }

                if (null != cb) {
                    cb.onAddStep();
                }
            }

            if (!checkPathList.isEmpty()) {
                if (!isParentEmptyFolder(lastParentPath, setPath, setNeedCheckPath.keySet(), resultPath.keySet(), storagePathList, cb)) {
                    //整合已经存放到resultPath里面的数值。
                    for (String checkPath : checkPathList.keySet()) {
                        int resultPathNum = 0;
                        if (resultPath.get(checkPath) != null && resultPath.get(checkPath) >= 1) {
                            resultPathNum = resultPath.get(checkPath) - 1;
                        }
                        resultPath.put(checkPath, checkPathList.get(checkPath) + resultPathNum);
                    }
                } else {
                    int listDirNum = 0;
                    for (String checkPath : checkPathList.keySet()) {
                        listDirNum += checkPathList.get(checkPath);
                    }
                    setNeedCheckPath.put(lastParentPath, listDirNum + 1); //加入parentPath本身文件夹
                }
            }

            if (setNeedCheckPath.isEmpty()) {
                break;
            }

            mapPath = setNeedCheckPath;
            count += 1;

            if (null != cb) {
                cb.onStepNum(setPath.size() * 3);
            }
        } while (true);

        HashMap<String, Integer> rstList = new HashMap<>();

        if (null != cb) {
            cb.onStepNum(resultPath.size() * 2);
        }

        if (null != cb) {
            cb.onStepNum(resultPath.size());
        }
        //会有一些子目录先加入到resultPath里面，做整合排序去重的
        long nStartTime = SystemClock.uptimeMillis();
        cleanSubFolders(resultPath, cb, progCtrl);
        long nDiffTime = SystemClock.uptimeMillis()-nStartTime;
        if ( nDiffTime > TIME_LIMIT ) {
            NLog.d( "TimeTrace", "EmptyFolders cleanSubFolders usetime:"+nDiffTime+" resultPath:"+resultPath.size());
        }

    }

    private static void cleanSubFolders(
            HashMap<String, Integer> folderMap, EmptyFolderCallback cb, ProgressCtrl progCtrl) {
        if (null == folderMap || folderMap.isEmpty()) {
            return;
        }

        Set<String> setPath = folderMap.keySet();
        int nSize = folderMap.size();
        String[] sortedPathArray = setPath.toArray(new String[folderMap.size()]);
        Arrays.sort(sortedPathArray, String.CASE_INSENSITIVE_ORDER);

        String nowPath = sortedPathArray[0];
        HashMap<String, Integer> result = new HashMap<>();
        int listDirNum = folderMap.get(nowPath);
        nowPath = FileUtils.addSlash(nowPath);
        for (int i = 1; i < nSize ; ++i) {

            if (null != progCtrl && progCtrl.isStop()) {
                break;
            }

            if (null != cb) {
                cb.onAddStep();
            }

            String checkPath = sortedPathArray[i];
            if (checkPath.startsWith(nowPath)) {
                listDirNum += folderMap.get(checkPath);
                continue;
            }

            cb.onFoundEmptyFolder(nowPath, listDirNum);

            nowPath = checkPath;
            listDirNum = folderMap.get(nowPath);
            nowPath = FileUtils.addSlash(nowPath);
        }
        //last foler path
        cb.onFoundEmptyFolder(nowPath, listDirNum);

    }
    private static boolean isParentEmptyFolder(
            String parentPath,
            Set<String> setPath,
            Set<String> setNeedCheckPath,
            Set<String> resultPath,
            List<String> storagePathList,
            EmptyFolderCallback cb) {
        boolean isParentEmpty = false;
        //父目录不为sdard根目录
        if (!isRootPathOfVolume(parentPath, storagePathList)) {
            //父目录没有被过滤
            if (null != cb && !cb.onFilter(parentPath)) {
                if (isEmptyFolder(parentPath, setPath, setNeedCheckPath, resultPath)) {
                    isParentEmpty = true;
                }
            }
        }
        return isParentEmpty;
    }

    private static boolean isEmptyFolder(
            String path,
            Set<String> emptyPathSet0,  //total empty folder path list
            Set<String> emptyPathSet1,  //parent empty folder path list
            Set<String> emptyPathSet2   //result empty folder path lis
    ) {       //folder num

        assert(!TextUtils.isEmpty(path));
        assert(null != emptyPathSet0);
        assert(null != emptyPathSet1);
        assert(null != emptyPathSet2);

        if (emptyPathSet1.contains(path) || emptyPathSet2.contains(path)) {
            return true;
        }

        StringList subsList = null;
        String[] subs = null;

        subsList = EnableCacheListDir.listDir(path);

        if (null != subsList) {
            try {
                int size = subsList.size();
                subs = new String[size];
                for (int i = 0; i < size; ++i) {
                    subs[i] = subsList.get(i);
                }

            } finally {
                subsList.release();
                subsList = null;
            }
        }

        if (null == subs) {
            return false;
        }

        if (0 == subs.length) {
            return true;
        }

        for (String s : subs) {
            s = FileUtils.addSlash(path) + s;
            if (TextUtils.isEmpty(s)) {
                continue;
            }

            if ((!emptyPathSet0.contains(s)) && (!emptyPathSet1.contains(s)) && (!emptyPathSet2.contains(s))) {
                return false;
            }
        }

        return true;
    }

    private static String getParent(String path) {
        assert(!TextUtils.isEmpty(path));

        int rc = path.lastIndexOf(File.separatorChar);
        if (rc <= 0) {
            return null;
        }

        return path.substring(0, rc);
    }


    private static boolean isRootPathOfVolume(String path, List<String> storagePathList) {
        assert(!TextUtils.isEmpty(path));
        assert(null != storagePathList && (!storagePathList.isEmpty()));

        for (String rootPath : storagePathList) {
            if (0 == rootPath.compareToIgnoreCase(path)) {
                return true;
            }
        }

        return false;
    }





    /**
     * 删除文件
     * 注意：不要直接调用此接口，请使用Commons.DeleteFileOrFolderWithConfig()， Commons提供的接口使用了一些优化策略，可以提升删除文件效率。
     * @param result			数组大小为3, result[0]:删除成功或失败  0:失败 1:成功  ，result[1]：文件夹数，result[2]：文件数
     * @param path				要删除的文件或文件夹路径
     * @param delFlags     	    删除的控制参数
     * @param delFileTimeLimit  删除的控制时间参数
     * @param fileWhiteList   	文件白名单
     * @param folderWhiteList 	文件夹白名单制
     * @param pathCallback	          删除回调。注意：如果bSecondSdCardCanWriteable为true, pathCallback必须实现onStart,onFile，onFolder，onDone接口实现。在接口实现中负责把相关的文件和文件夹删除。否则会导致文件和文件夹无法删除的问题！
     * @param bSecondSdCardCanWriteable 第二张SD卡是否可写标记。如果不可写，将使用策略删除（root用户直接使用root删除，非root用户使用MediaStore批量删除，批量删除需要pathCallback提供onStart,onFile等回调的实现。否则会导致无法删除！）。
     * @param strFirstSdCardRootPath 第一张SD卡根路径
     * @param recycleConfig     照片回收站配置回调
     */
    public static boolean deleteFileOrFolderWithConfig(
            int[] result,
            List<String> path,
            int delFlags,
            int delFileTimeLimit,
            List<String> fileWhiteList,
            List<String> folderWhiteList,
            List<String> feedbackFileList,
            List<String> feedbackFolderList,
            final PathCallback pathCallback,
            boolean bSecondSdCardCanWriteable,
            String strFirstSdCardRootPath,
            final EngineConfig recycleConfig,
            List<String> ExternalStoragePaths,
            final boolean needRecycle) {
        try {
            if (delFileTimeLimit == CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE) {
                delFileTimeLimit = 0;
            }
            boolean doLoad = false ;//= SoLoader.doLoad(false)
            if (doLoad) {
                a.c(result, path, delFlags, delFileTimeLimit,
                        fileWhiteList, folderWhiteList,
                        feedbackFileList, feedbackFolderList,
                        null == pathCallback ? null : new d() {
                            @Override
                            public void a(String a, long s, int at, int mt, int ct) {
                                pathCallback.onFile(a, s, at, mt, ct);
                            }

                            @Override
                            public void b(String a, String b, long s) {
                                pathCallback.onFeedback(a, b, s);
                            }

                            @Override
                            public void c(String strRootDir) {
                                pathCallback.onStart(strRootDir);
                            }

                            @Override
                            public void e(String strRootDir, String strSubFile) {
                                pathCallback.onFile(strRootDir, strSubFile);
                            }

                            @Override
                            public void f(String strRootDir, String strSubFolder) {
                                pathCallback.onFolder(strRootDir, strSubFolder);
                            }

                            @Override
                            public void g(String strRootDir) {
                                pathCallback.onDone(strRootDir);
                            }

                            @Override
                            public void h(String a, boolean b, boolean c, int d) {
                                pathCallback.onError(a, b, c, d);
                            }

                            @Override
                            public boolean z(String filePath, long fileModifyTime) {
                                if (pathCallback != null) {
                                    return pathCallback.OnFilter(filePath, fileModifyTime);
                                }

                                return true;
                            }

                        }, bSecondSdCardCanWriteable, strFirstSdCardRootPath, new z() {
                            @Override
                            public int a(int key) {
                                if (recycleConfig != null) {
                                    return recycleConfig.getCfgInt(key, -1);
                                }
                                return -1;
                            }

                            @Override
                            public String b(int key) {
                                String valueDefault = "cleanmaster_cn";
                                if (recycleConfig != null) {
                                    return recycleConfig.getCfgString(key, valueDefault);
                                }

                                return valueDefault;
                            }

                            @Override
                            public void c(List<String> list) {
                                if (recycleConfig != null) {
                                    recycleConfig.setCfgList(EngineConfig.ENG_CFG_ID_SET_RECYCLE_ITEM_LIST, list);
                                }
                            }
                        }, ExternalStoragePaths, needRecycle, SuExec.getInstance().checkRoot() );
                return true; //load .so file success
            } else {
                return false; //load .so file fail
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NullPointerException e) {
            throw e;
        } catch (UnsatisfiedLinkError e) {
            CheckMoreCrashInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 遍历目录获取 log,tmp,apk,大文件列表
     * @param strPath 要遍历的目录
     * @param progCtrl 流程控制对象
     * @param nMaxLevel 遍历的最大深度
     * @param nNotFindFileNumberLimit 某级目录中扫描文件和文件夹超过限制仍未找到nScanTypes标识的文件，则退出此目录的遍历. 为0时不限制
     * @param nPureFolderNumberLimit 纯目录个数限制。为0时无限制。
     * @param pignoreFolders 忽略的目录列表。可以为null
     * @param specialFolders 特殊目录列表。可以为null
     * @param nScanTypes 需要获取的文件类型。IPachScanCallback。TYPE_*的组合
     * @param scanCallback 找到文件后的回调接口
     */
    public static void pathScan(String strPath,
                                ProgressCtrl progCtrl,
                                int nMaxLevel,
                                int nNotFindFileNumberLimit,
                                int nPureFolderNumberLimit,
                                final List<String> pignoreFolders,
                                List<SpecialFolder> specialFolders,
                                int nScanTypes,
                                PathScanCallback scanCallback ){
        ioPathScanImpl(strPath,true,progCtrl,nMaxLevel,nNotFindFileNumberLimit,nPureFolderNumberLimit,pignoreFolders,specialFolders,nScanTypes,scanCallback);
    }

    private static boolean checkStop(ProgressCtrl progCtrl){
        if(progCtrl!=null&&progCtrl.isStop()){
            return true;
        }
        return false;
    }

    private static void ioPathScanImpl(String pzPath,
                                       boolean bFirstTimeIn,
                                       ProgressCtrl piStopCtrl,
                                       int nLevel,
                                       int nNotFindFileNumberLimit,
                                       int nPureFolderNumberLimit,
                                       List<String> pignoreFolders,
                                       List<SpecialFolder> pmapSpecialFolders,
                                       int nScanTypes,
                                       PathScanCallback pScanCallback){
        if(checkStop(piStopCtrl)){
            return;
        }
        String strPathLower = pzPath.toLowerCase();
        if( pignoreFolders != null && pignoreFolders.size()!=0 ){
            for(String folderStr:pignoreFolders){
                if(folderStr.equalsIgnoreCase(strPathLower)||folderStr.equalsIgnoreCase(FileUtils.removeSlash(strPathLower))){
                    return ;
                }
            }
        }
        int nTotalCount = 0;
        boolean bFindItem = false;
        List<String> folderList = new ArrayList<String>();
        List<String> fileList = new ArrayList<String>();
        File pzfile = new File(pzPath);
        File[] files = pzfile.listFiles();
        if(files==null||files.length==0){
            return;
        }
        for(File subFile:files){
            if(checkStop(piStopCtrl)){
                return;
            }
            boolean bIsDir = false;
            if (subFile.isDirectory()){
                bIsDir = true;
            }
            if ( bIsDir ){
                if (0 == nLevel){
                    continue;
                }
                folderList.add(subFile.getName());
            }else{
                if(!bFirstTimeIn && nPureFolderNumberLimit > 0 && pzPath.equals(".nomedia")){
                    continue;
                }
                fileList.add(subFile.getName());
            }
        }

        for(String fileStr:fileList){
            nTotalCount++;
            boolean bRet = ioPathScanFileHandle(pzPath, pzPath.toLowerCase(), fileStr, pmapSpecialFolders, nScanTypes, pScanCallback );
            if( bRet ){
                bFindItem = true;
            }
            if( !bFirstTimeIn && !bFindItem && nNotFindFileNumberLimit > 0 && nTotalCount > nNotFindFileNumberLimit ){
                return;
            }
        }

        if( !bFirstTimeIn && nPureFolderNumberLimit > 0 && fileList.size() == 0 && folderList.size() > nPureFolderNumberLimit ) {
            return;
        }

        for(String folderStr:folderList){
            nTotalCount++;
            String strNowPath = FileUtils.addSlash(FileUtils.addSlash(pzPath)+folderStr);
            ioPathScanImpl(strNowPath, false, piStopCtrl, nLevel-1, nNotFindFileNumberLimit, nPureFolderNumberLimit, pignoreFolders, pmapSpecialFolders, nScanTypes, pScanCallback );
        }

    }


    static boolean ioPathScanFileHandle(
		String pzParentPath,
		String pzParentPathLower,
		String pzName,
		List<SpecialFolder> pmapSpecialFolders,
		int nScanTypes,
		PathScanCallback pScanCallback )
    {
        boolean bFindFile = false;
        boolean bGetStat = false;
        int nFindScanType = PathScanCallback.TYPE_UNKNOWN;

        String szPath = FileUtils.addSlash(pzParentPath)+pzName;
        if( pmapSpecialFolders != null&&pmapSpecialFolders.size()!=0 ){
            for(SpecialFolder specialFolder:pmapSpecialFolders){
                if(specialFolder.mStrPath.equals(pzParentPathLower) ||specialFolder.mStrPath.equalsIgnoreCase(FileUtils.removeSlash(pzParentPathLower))){
                    if( specialFolderHandle( szPath, pzName, specialFolder, bGetStat ) ) {
                        bFindFile = true;
                    }
                }
            }
        }

        if( (nScanTypes & PathScanCallback.TYPE_BIG_10M)!=0 ){
            File pzPathFile = new File(szPath);
            long nSize = pzPathFile.length();
            if( nSize > 10*1024*1024 ){
                bFindFile = true;
                pScanCallback.onFile(szPath
                        ,pzPathFile.length()
                        ,PathScanCallback.TYPE_BIG_10M
                        ,pzPathFile.lastModified()
                        ,pzPathFile.lastModified()
                        ,pzPathFile.lastModified()
                        ,0/*暂时*/);
            }
        }

        int nNameLen = pzName.length();
        if( nNameLen < 4 ){
            return bFindFile;
        }
        if( ( nScanTypes & PathScanCallback.TYPE_TMP )!=0
                && (pzName.toLowerCase().endsWith(".tmp")||pzName.toLowerCase().endsWith(".temp")) )
        {
            nFindScanType = PathScanCallback.TYPE_TMP;
        }
        else if( ( nScanTypes & PathScanCallback.TYPE_LOG )!=0
                && pzName.toLowerCase().endsWith(".log") )
        {
            nFindScanType = PathScanCallback.TYPE_LOG;
        }
        else if( ( nScanTypes & PathScanCallback.TYPE_APK )!=0
                && pzName.toLowerCase().endsWith(".apk") )
        {
            nFindScanType = PathScanCallback.TYPE_APK;
        }

        if( nFindScanType == PathScanCallback.TYPE_UNKNOWN ){
            if( (nScanTypes & PathScanCallback.TYPE_OTHER)!=0 ) {
                return bFindFile;
            }
            nFindScanType = PathScanCallback.TYPE_OTHER;
        }
        bFindFile = true;
        File pzPathFile = new File(szPath);
        pScanCallback.onFile(szPath
                ,pzPathFile.length()
                ,nFindScanType
                ,pzPathFile.lastModified()
                ,pzPathFile.lastModified()
                ,pzPathFile.lastModified()
                ,0/*暂时*/);
        return bFindFile;
    }

    static boolean specialFolderHandle(
		String pzPath,
		String pzName,
        SpecialFolder pspecialFolder,
		boolean bGetStated )
    {
        if ( pspecialFolder.mTimeLine > 0 ) {
            if( !bGetStated ) {
                bGetStated = true;
            }
            return false; // 由于该时间线定义不明，所以默认配了该字段的都返回false； by:chaohao.zhou
        }
        else if(!TextUtils.isEmpty(pspecialFolder.mStrRegex))
        {
            Pattern pattern = Pattern.compile(pspecialFolder.mStrRegex);
            Matcher matcher = pattern.matcher(pzName);
            if(!matcher.matches()){
                return false;
            }
        }else
        {
            boolean bFind = false;
            if(pspecialFolder.mFullsMatchArr!=null&&!pspecialFolder.mFullsMatchArr.isEmpty()){
                for(String fullMatchStr:pspecialFolder.mFullsMatchArr){
                    Pattern pattern = Pattern.compile(fullMatchStr);
                    Matcher matcher = pattern.matcher(pzName.toLowerCase());
                    if(matcher.matches()){
                        bFind = true;
                        break;
                    }
                }
            }

            if( !bFind ){
                if ((pspecialFolder.mStartsWithArr == null || pspecialFolder.mStartsWithArr.isEmpty())
                        && (pspecialFolder.mEndsWithArr == null || pspecialFolder.mEndsWithArr.isEmpty())
                        && (pspecialFolder.mContainsArr == null || pspecialFolder.mContainsArr.isEmpty())
                        && (pspecialFolder.mNotContainsArr == null || pspecialFolder.mNotContainsArr.isEmpty())) {
                    return false;
                }

                if(pspecialFolder.mStartsWithArr!=null&&!pspecialFolder.mStartsWithArr.isEmpty()){
                    boolean success = false;
                    for(String startStr:pspecialFolder.mStartsWithArr){
                        if (pzName.startsWith(startStr)) {
                            success = true;
                            break;
                        }
                    }
                    if(!success){
                        return false;
                    }
                }

                if(pspecialFolder.mEndsWithArr!=null&&!pspecialFolder.mEndsWithArr.isEmpty()){
                    boolean success = false;
                    for(String ednStr:pspecialFolder.mEndsWithArr){
                        if (pzName.endsWith(ednStr)) {
                            success = true;
                            break;
                        }
                    }
                    if(!success){
                        return false;
                    }
                }

                if(pspecialFolder.mContainsArr!=null&&!pspecialFolder.mContainsArr.isEmpty()){
                    boolean success = false;
                    for(String containStr:pspecialFolder.mContainsArr){
                        if (pzName.contains(containStr)) {
                            success = true;
                            break;
                        }
                    }
                    if(!success){
                        return false;
                    }
                }

                if(pspecialFolder.mNotContainsArr!=null&&!pspecialFolder.mNotContainsArr.isEmpty()){
                    for(String notContainStr:pspecialFolder.mNotContainsArr){
                        if (pzName.contains(notContainStr)) {
                            return false;
                        }
                    }
                }
            }
        }
        File pzPathFile = new File(pzPath);
        pspecialFolder.mCallback.onFile(pzPath
                ,pzPathFile.length()
                ,PathScanCallback.TYPE_UNKNOWN
                ,pzPathFile.lastModified()
                ,pzPathFile.lastModified()
                ,pzPathFile.lastModified()
                ,0/*暂时*/);
        return true;
    }

}
