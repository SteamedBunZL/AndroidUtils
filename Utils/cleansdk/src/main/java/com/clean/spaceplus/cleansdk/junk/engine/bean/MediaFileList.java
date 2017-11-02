package com.clean.spaceplus.cleansdk.junk.engine.bean;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.engine.junk.PicRecycleCache;
import com.clean.spaceplus.cleansdk.util.PackageUtils;
import com.clean.spaceplus.cleansdk.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 13:48
 * @copyright TCL-MIG
 */
public class MediaFileList implements Parcelable{
    // 1.以下是一些key， 不能重复
    public static final String CMARERA = "camera";
    public static final String SIMILAR = "similar";
    public static final String BEAUTIFY_KEY_PREFIX = "beautify_";

    // 2.以下是一些照片的路径名
    private static final String DCIM = "dcim";
    /** 图片保存路径为/sdcard/Snapeee/ */
    private static final String SNAPEEE = "snapeee";
    private static final String CYMERA = "cymera2";
    /** com.venticake.retrica,拍照保存目录为/sdcard/pictures/retrica/ */
    private static final String RETRICA = "retrica";
    private static final String PICTURES = "pictures";
    private static final String PHOTOS = "photos";
    private static final String SCREENSHORT = "screenshot";//截图
    private static final String SCREENSHORTS = "screenshots";
    private static final String THUMB = ".thumbnails";//缩略图隐藏目录

    /** 美化后图片保存路径为 dcim/camera
     * 注意这里之所以去掉dcim/前缀，是因为 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) 这个接口是照片的公共目录，
     * 但是有的厂商会修改这个的返回值，返回的并不是dcim，比如魅族手机在这个接口返回的是Camera，同样，在魅族手机上，Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
     * 返回的是Photo,并不是pictures
     * */
    private static final String BEAUTIFY_PATH_CAMERA = "/camera";
    /** 美化后图片保存路径为 dcim/100andro */
    private static final String BEAUTIFY_PATH_100ANDRO = "/100andro";
    /** Camera MX拍照和美化图片保存路径 */
    private static final String BEAUTIFY_PATH_CAMERA_MX = "/camera mx";
    /** Pixlr拍照保存路径 #DIRECTORY_PICTURES/pixlrcamera */
    private static final String BEAUTIFY_PATH_PIXLR_SRC = "/pixlrcamera";
    /** Pixlr美化图片保存路径 */
    private static final String BEAUTIFY_PATH_PIXLR_EDIT = "/autodesk/pixlr";
    /** Snapseed美化图片保存路径 */
    private static final String BEAUTIFY_PATH_SNAPSEED = "/snapseed";
    // 2.以上是一些照片的路径名

    // 3.以下是一些美化图片的flag
    private static final String BEAUTIFY_FLAG_MEITU = "_mh";
    private static final String BEAUTIFY_FLAG_CAMERA_MX = "_fx";
    private static final String BEAUTIFY_FLAG_QUICK_PIC = "~";
    private static final String BEAUTIFY_FLAG_PIXLR = "_\\d{17}"/*"^[^_]+_[\\d]+"*/;
    private static final String BEAUTIFY_FLAG_SNAPSEED = "^captured_by_snapseed_[\\d]";
    private Pattern mPixlrPattern;
    private Pattern mSnapseedPattern;
    // 3.以上是一些美化图片的flag

    // 4.以下美化app包名
    private static final String PKGNAME_MEITU = "com.mt.mtxx.mtxx";
    private static final String PKGNAME_CAMERAMX = "com.magix.camera_mx";
    private static final String PKGNAME_QUICKPIC = "com.alensw.PicFolder";
    private static final String PKGNAME_PIXLR = "com.pixlr.express";
    private static final String PKGNAME_SNAPSEED = "com.niksoftware.snapseed";
    //	private String mMeiTuAppName;
//	private String mCameraMXName;
//	private String mQuickPicName;
//	private String mPixlrName;
    private boolean mHasMeituApp;
    private boolean mHasCameraMXApp;
    private boolean mHasQuickPicApp;
    private boolean mHasPixlrApp;
    private boolean mHasSnapseedApp;
    // 4.以上美化app包名

    public static final String EXTRA_MEDIALIST_INDEX = "extra_medialist_index";

    public static final int CLEAN_FROM_THUMBNAILS = 1;
    public static final int CLEAN_FROM_GROUP = 2;
    public static final int CLEAN_FROM_DOWNLOAD = 3;

    private Long mSize = 0L;

    private ArrayList<MediaFile> mList = new ArrayList<MediaFile>();
    private Map<String, List<MediaFile>> mSortMap = new HashMap<String, List<MediaFile>>();
    private ArrayList<String> mKeysArrayList = new ArrayList<String>();
    private Map<String, Long> mKeysSize = new HashMap<String, Long>();
    private ArrayList<MediaFile> mMediaDeletedList = new ArrayList<MediaFile>();
    private ArrayList<MediaFile> mCurMediaDeletedList;
    private Map<String, Long> mCleanedReportInfo = new HashMap<String, Long>();		//key：清理过的目录    value：该目录清理的size
    private Map<String, Integer> mCleanedReportFrom = new HashMap<String, Integer>();		//key：清理过的目录    value：1.从缩略图进入清理  2.从大卡片进入清理  3.两种方式都有

    private boolean mOnlyForSimilar;

    public MediaFileList(List<?> myPhotoList, boolean onlyForSimilar) {
        this.mOnlyForSimilar = onlyForSimilar;
        Context context = SpaceApplication.getInstance().getContext();
        mHasMeituApp = PackageUtils.isHasPackage(context, PKGNAME_MEITU)/* ? PackageUtils.getAppNameByPackageName(context, PKGNAME_MEITU) : null*/;
        mHasCameraMXApp = PackageUtils.isHasPackage(context, PKGNAME_CAMERAMX)/* ? PackageUtils.getAppNameByPackageName(context, PKGNAME_CAMERAMX) : null*/;
        mHasQuickPicApp = PackageUtils.isHasPackage(context, PKGNAME_QUICKPIC)/* ? PackageUtils.getAppNameByPackageName(context, PKGNAME_QUICKPIC) : null*/;
        mHasPixlrApp = PackageUtils.isHasPackage(context, PKGNAME_PIXLR)/* ? PackageUtils.getAppNameByPackageName(context, PKGNAME_PIXLR) : null*/;
        mHasSnapseedApp = PackageUtils.isHasPackage(context, PKGNAME_SNAPSEED);
        if (null != myPhotoList) {
            for (Object object : myPhotoList) {
                mList.add((MediaFile)object);
            }
            sortMediaList();
        }
    }

    public MediaFileList(List<?> myPhotoList) {
        this(myPhotoList, false);
    }

    public MediaFileList() {
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mList);
        dest.writeMap(mSortMap);
        dest.writeList(mKeysArrayList);
        dest.writeList(mMediaDeletedList);
        dest.writeMap(mKeysSize);
        dest.writeMap(mCleanedReportInfo);
        dest.writeMap(mCleanedReportFrom);
    }

    public static final Parcelable.Creator<MediaFileList> CREATOR = new Creator<MediaFileList>() {

        @Override
        public MediaFileList[] newArray(int size) {
            return new MediaFileList[size];
        }

        @Override
        public MediaFileList createFromParcel(Parcel source) {
            MediaFileList info = new MediaFileList();
            source.readList(info.mList, MediaFile.class.getClassLoader());
            source.readMap(info.mSortMap, MediaFile.class.getClassLoader());
            source.readList(info.mKeysArrayList, MediaFile.class.getClassLoader());
            source.readList(info.mMediaDeletedList, MediaFile.class.getClassLoader());
            source.readMap(info.mKeysSize, MediaFile.class.getClassLoader());
            source.readMap(info.mCleanedReportInfo, MediaFile.class.getClassLoader());
            source.readMap(info.mCleanedReportFrom, MediaFile.class.getClassLoader());
            return info;
        }
    };

    public void addAll( List<BaseJunkBean> list ){
        if (null == list || list.isEmpty()) {
            return;
        }

        synchronized (this) {
            mList.ensureCapacity(mList.size() + list.size());
            for (BaseJunkBean item : list) {
                mList.add((MediaFile)item);
            }
        }
    }

    public void add( MediaFile mediaFile ){
        synchronized (this) {
            mList.add(mediaFile);
        }
    }

    public boolean remove(List<MediaFile> delFiles_exp, int cleanFrom ){
        ArrayList<MediaFile> delFiles = new ArrayList<MediaFile>();
        for (MediaFile mediaFile:delFiles_exp) {
            if ( mList.contains(mediaFile) ) {
                delFiles.add(mediaFile);
            }
        }
        if (delFiles.size() == 0 ) {
            return false;
        }
        MediaFileList tempMediaFileList = new MediaFileList();
        tempMediaFileList.getList().addAll(delFiles);
        tempMediaFileList.sortMediaList();
        for (String key:tempMediaFileList.getKeyList()) {
            List<MediaFile> subDelList = tempMediaFileList.getSortListMap().get(key);
            long nDelSize = tempMediaFileList.getSizeMap().get(key);
            removeKey(key, subDelList, nDelSize, cleanFrom);
        }
        return true;
    }

//    public boolean removeByPathList(List<String> delFilesPath, int cleanFrom){
//        ArrayList<MediaFile> delFiles = new ArrayList<MediaFile>();
//
//        //TODO :  not good for search path any good idea?
//        for (MediaFile mediaFile : mList) {
//            String mediaFilePath = mediaFile.getPath();
//            for (String delPath : delFilesPath) {
//                if (StringUtils.toLowerCase(mediaFilePath).equals(StringUtils.toLowerCase(delPath))) {
//                    delFiles.add(mediaFile);
//                    break;
//                }
//            }
//        }
//        if (delFiles.size() == 0 ) {
//            return false;
//        }
//        MediaFileList tempMediaFileList = new MediaFileList();
//        tempMediaFileList.getList().addAll(delFiles);
//        tempMediaFileList.sortMediaList();
//        for (String key:tempMediaFileList.getKeyList()) {
//            List<MediaFile> subDelList = tempMediaFileList.getSortListMap().get(key);
//            long nDelSize = tempMediaFileList.getSizeMap().get(key);
//            removeKey(key, subDelList, nDelSize, cleanFrom);
//        }
//        return true;
//    }
//
//    public boolean removeKey( int nIdx ){
//        if ( nIdx >= mKeysArrayList.size() || nIdx < 0 ) {
//            return false;
//        }
//        return removeKey(mKeysArrayList.get(nIdx));
//    }
//
//
//    public boolean removeKey( int nIdx, List<MediaFile> delFiles, long nDelSize, int cleanFrom ){
//        if ( nIdx >= mKeysArrayList.size() || nIdx < 0 ) {
//            return false;
//        }
//        return removeKey(mKeysArrayList.get(nIdx), delFiles, nDelSize, cleanFrom );
//    }

    private boolean  removeKey( String strKeyString,  List<MediaFile> delFiles, long nDelSize, int cleanFrom ){
        if (strKeyString == null) {
            return false;
        }
        List<MediaFile> tempMediaFiles = mSortMap.get(strKeyString);
        if (tempMediaFiles == null) {
            return false;
        }
        updateCleanReportInfo(strKeyString, nDelSize, cleanFrom);
        if (tempMediaFiles.size() == delFiles.size()|| tempMediaFiles.size() == 0 ) {
            return removeKey( strKeyString );
        }
        tempMediaFiles.removeAll(delFiles);
        synchronized (this) {
            mList.removeAll(delFiles);
            mMediaDeletedList.addAll(delFiles);
            mKeysSize.put(strKeyString, mKeysSize.get(strKeyString)-nDelSize);
            mSortMap.put(strKeyString, tempMediaFiles);
        }
        return true;
    }
    private boolean  removeKey( String strKeyString ){
        if (strKeyString == null) {
            return false;
        }
        if (!mSortMap.containsKey(strKeyString)) {
            return false;
        }
        synchronized (this) {
            List<MediaFile> tempMediaFiles = mSortMap.remove(strKeyString);
            mKeysArrayList.remove(strKeyString);
            mKeysSize.remove(strKeyString);
            mList.removeAll(tempMediaFiles);
            mMediaDeletedList.addAll(tempMediaFiles);
            tempMediaFiles.clear();
        }
        return true;
    }
    public ArrayList<MediaFile> getList(){
        return mList;
    }
    public Map<String, List<MediaFile>> getSortListMap(){
        return mSortMap;
    }
    public ArrayList<String> getKeyList(){
        return mKeysArrayList;
    }
    public Map<String, Long> getSizeMap(){
        return mKeysSize;
    }
    ///<DEAD CODE>///     public Map<String, Long> getCleanedReportInfo(){
//    	return mCleanedReportInfo;
//    }
///<DEAD CODE>///     public Map<String, Integer> getCleanedReportFrom(){
//    	return mCleanedReportFrom;
//    }
///<DEAD CODE>///     public ArrayList<MyMediaFile>getDeleteList(){
//    	return mMediaDeletedList;
//    }
    public ArrayList<MediaFile> getCurDeleteList(){
        return mCurMediaDeletedList;
    }
//    public void setCurDeleteList(ArrayList<MediaFile> curDelList){
//        mCurMediaDeletedList = curDelList;
//    }
//    public void rstCurDeleteList(){
//        mCurMediaDeletedList = null;
//    }
//
//    public void clearDeleteList(){
//        synchronized (this) {
//            mMediaDeletedList.clear();
//            return ;
//        }
//    }

    public void reset(){
        synchronized (this) {
            mList.clear();
            mSortMap.clear();
            mKeysArrayList.clear();
            mKeysSize.clear();
            mMediaDeletedList.clear();
        }
    }

    public void report(){
//        Set<String> keySet = null;
//        Iterator<String> it = null;
//        synchronized (this) {
//            keySet = mKeysSize.keySet();
//            it = keySet.iterator();
//            while(it.hasNext()){
//                String key = it.next();
//                long remainSize = mKeysSize.get(key);
//                long cleanedSize = 0L;
//                if(mCleanedReportInfo.containsKey(key)){
//                    cleanedSize = mCleanedReportInfo.get(key);
//                    mCleanedReportInfo.remove(key);
//                }
//                int cleanFrom = mCleanedReportFrom.containsKey(key) ? mCleanedReportFrom.get(key) : 0;
//                new cm_gallerymanager_detail().type1(1).path(key).size(remainSize + cleanedSize).isclean(cleanedSize > 0 ? 1 : 0).cleansize(cleanedSize).click(cleanFrom).frompage(PhotoGridPathActivity.EXTRA_FROM_ADV).report();
//            }
//            if(mCleanedReportInfo.isEmpty()){
//                return;
//            }
//            keySet = mCleanedReportInfo.keySet();
//            it = keySet.iterator();
//            while(it.hasNext()){
//                String key = it.next();
//                long cleanedSize = mCleanedReportInfo.get(key);
//                int cleanFrom = mCleanedReportFrom.containsKey(key) ? mCleanedReportFrom.get(key) : 0;
//                new cm_gallerymanager_detail().type1(1).path(key).size(cleanedSize).isclean(1).cleansize(cleanedSize).click(cleanFrom).frompage(PhotoGridPathActivity.EXTRA_FROM_ADV).report();
//            }
//        }
    }

    /**
     *
     * @param key	清理的目录
     * @param cleanSize	该目录下清理的size
     * @param cleanFrom 1从缩略图进入清理  2从大卡片进入清理
     */
    private void updateCleanReportInfo(String key, long cleanSize, int cleanFrom){
        synchronized (this) {
            if(!mCleanedReportInfo.containsKey(key)){
                mCleanedReportInfo.put(key, cleanSize);
            }else{
                mCleanedReportInfo.put(key, mCleanedReportInfo.get(key) + cleanSize);
            }
            if(mCleanedReportFrom.containsKey(key)){
                if(mCleanedReportFrom.get(key) != cleanFrom){
                    mCleanedReportFrom.put(key, 3);
                }
            }else{
                mCleanedReportFrom.put(key, cleanFrom);
            }
        }
    }

    private String parseParentPath(MediaFile mediaFile) {
        File file = new File(mediaFile.getPath());
        String name = file.getName();
        int nIndex = name.indexOf(".");
        if (nIndex != -1) {
            mediaFile.mColorAlgoFinger = name.substring(0, nIndex);
        } else {
            mediaFile.mColorAlgoFinger = name;
        }

        return file.getParent();
    }

    private String identifyDirName(MediaFile file,String dir,boolean isMeizu) {
        String name = dir;
//        boolean isDir = false;
        if (!TextUtils.isEmpty(dir)) {
//            for (String dir : mDirs) {
//                if (!path.equals(dir)) {
//					if (path.startsWith(dir)) {
//						if (path.charAt(dir.length()) == File.separatorChar) {
//							isDir = true;
//						}
//					}
//				} else {
//					isDir = true;
//				}
//                if (isDir) {
//                	   name = checkIsCameraFile(name,isMeizu);
//            			return name;
//            		}
//            }
            name = checkIsCameraFile(file,dir,isMeizu);
        }
        return name;
    }

    private boolean isMeizu(){
//        String build = BaseTracer.brand();
//        if(!TextUtils.isEmpty(build)){
//            if(build.equalsIgnoreCase("meizu")){
//                return true;
//            }
//        }
        return false;
    }

    private String checkIsCameraFile(MediaFile mediaFilefile, String dir, boolean isMeizu) {
        String name = dir;
        String dirLowerCase = StringUtils.toLowerCase(dir);
        int idx = dirLowerCase.lastIndexOf("/");
        if (idx != -1) {
            // 先转成小写，避免多次转换的消耗
            String lastname = dirLowerCase.substring(idx + 1, dirLowerCase.length());

            if (PicRecycleCache.RECOVERY_PATH_SUFFIX.equalsIgnoreCase(lastname)) {
                return PicRecycleCache.RECOVERY_PATH_SUFFIX;
            }

            if (dirLowerCase.endsWith(BEAUTIFY_PATH_CAMERA) || dirLowerCase.endsWith(BEAUTIFY_PATH_100ANDRO)) {
                String fileName = mediaFilefile.mColorAlgoFinger;
                if (!TextUtils.isEmpty(fileName)) {
                    /** 美图 */
                    if (mHasMeituApp) {
                        int flagIndex = fileName.indexOf(BEAUTIFY_FLAG_MEITU);
                        if (flagIndex != -1) {
//							mediaFilefile.mBeautify = mMeiTuAppName;
                            mediaFilefile.mColorAlgoFinger = fileName.substring(0, flagIndex);
                            return BEAUTIFY_KEY_PREFIX;
                        }
                    }

                    /** QuickPic */
                    if (mHasQuickPicApp) {
                        int flagIndex = fileName.indexOf(BEAUTIFY_FLAG_QUICK_PIC);
                        if (flagIndex != -1) {
//							mediaFilefile.mBeautify = mQuickPicName;
                            mediaFilefile.mColorAlgoFinger = fileName.substring(0, flagIndex);
                            return BEAUTIFY_KEY_PREFIX;
                        }
                    }
                }
                return CMARERA;
            }

            /** Camera MX */
            if (mHasCameraMXApp && dirLowerCase.endsWith(BEAUTIFY_PATH_CAMERA_MX)) {
                String fileName = mediaFilefile.mColorAlgoFinger;
                if (!TextUtils.isEmpty(fileName)) {
                    int flagIndex = fileName.indexOf(BEAUTIFY_FLAG_CAMERA_MX);
                    if (flagIndex != -1) {
//						mediaFilefile.mBeautify = mCameraMXName;
                        mediaFilefile.mColorAlgoFinger = fileName.substring(0, flagIndex);
                    }
                }
                return BEAUTIFY_KEY_PREFIX;
            }

            /** Pixlr */
            if (mHasPixlrApp && (dirLowerCase.endsWith(BEAUTIFY_PATH_PIXLR_SRC) || dirLowerCase.endsWith(BEAUTIFY_PATH_PIXLR_EDIT))) {
                String fileName = mediaFilefile.mColorAlgoFinger;
                if (!TextUtils.isEmpty(fileName)) {
                    Matcher matcher = mPixlrPattern.matcher(fileName);
                    if (matcher.find()) {
//						mediaFilefile.mBeautify = mPixlrName;
                        int subEndIndex = fileName.startsWith("collage_") || fileName.startsWith("Pixlr_") ? matcher.end() : matcher.start();
                        mediaFilefile.mColorAlgoFinger = fileName.substring(0, subEndIndex);
                    }
                }
                return BEAUTIFY_KEY_PREFIX;
            }

            /** Snapseed */
            if (mHasSnapseedApp && dirLowerCase.endsWith(BEAUTIFY_PATH_SNAPSEED)) {
                String fileName = mediaFilefile.mColorAlgoFinger;
                if (!TextUtils.isEmpty(fileName)) {
                    Matcher matcher = mSnapseedPattern.matcher(fileName);
                    if (matcher.find()) {
//						mediaFilefile.mBeautify = mPixlrName;
                        mediaFilefile.mColorAlgoFinger = matcher.group();
                    }
                }
                return BEAUTIFY_KEY_PREFIX;
            }

            /** CAMERA360（相机360）,MYXJ（美颜相机）,MTXX（美图秀秀），拍照的保存路径都是在/sdcard/DCIM/Camera/目录下，不需要重复判断 */
            if (DCIM.equals(lastname)
//					|| CAMERA360.equals(lastname)
//					|| MYXJ.equals(lastname)
//					|| MTXX.equals(lastname)
                    || RETRICA.equals(lastname)
                    || SNAPEEE.equals(lastname)
                    || CYMERA.equals(lastname)
                    || PICTURES.equals(lastname)
                    || PHOTOS.equals(lastname)
                    || dirLowerCase.contains("/dcim/")
                    || dirLowerCase.contains("/camera/")) {
                return CMARERA;
            } else if (SCREENSHORT.equals(lastname) || SCREENSHORTS.equals(lastname)) {
                return dir;
            }
            name = dirLowerCase.substring(0, idx);
            //meizu手机取sd卡根目录下camera下所有的图片,作为默认相册
            if (isMeizu) {
                File file = Environment.getExternalStorageDirectory();
                if (file != null) {
                    if (CMARERA.equals(lastname) && name.equalsIgnoreCase(file.getAbsolutePath())) {
                        return CMARERA;
                    } else {
                        idx = name.lastIndexOf("/");
                        if (idx != -1) {
                            String tmpname = name.substring(idx + 1, name.length());
                            String tmppath = name.substring(0, idx);
                            if (!THUMB.equals(lastname) && CMARERA.equals(tmpname) && tmppath.equalsIgnoreCase(file.getAbsolutePath()))
                                return CMARERA;
                        }
                    }
                }
            }
            idx = name.lastIndexOf("/");
            if (idx != -1) {
                name = name.substring(idx + 1, name.length());
                if (DCIM.equals(name)) {
                    if (THUMB.equals(lastname)) {
                        name = dir;//DCIM下的缩略图
                    } else {
                        name = CMARERA;
                    }
                } else {
                    name = dir;
                }
            } else {
                name = dir;
            }
        }
        return name;
    }

    /**
     * 对传过来imageList进行排序
     */
    public void sortMediaList() {
        if ((mList != null) && (mList.size() > 0)) {
            if (mHasPixlrApp) {
                mPixlrPattern = Pattern.compile(BEAUTIFY_FLAG_PIXLR);
            }
            if (mHasSnapseedApp) {
                mSnapseedPattern = Pattern.compile(BEAUTIFY_FLAG_SNAPSEED);
            }
            for (MediaFile file : mList) {
                String pPath = parseParentPath(file);
                String name = identifyDirName(file,pPath,isMeizu());
                if (!TextUtils.isEmpty(name)) {
                    //相册目录之下的  美化目录之下的  美化图片的原图目录
                    if (CMARERA.equals(name) || PicRecycleCache.RECOVERY_PATH_SUFFIX.equals(name)) {
                        insetToSortMap(SIMILAR, file);
                    }

                    //美化目录 要插入三个目录，SIMILAR，CMARERA和自己的key目录下。
                    if (name.equals(BEAUTIFY_KEY_PREFIX)) {
                        insetToSortMap(SIMILAR, file);
                        insetToSortMap(BEAUTIFY_KEY_PREFIX, file);
                        if (!mOnlyForSimilar) {
                            insetToSortMapAndKeyArray(CMARERA, file);
                        }
                    } else {
                        if (!mOnlyForSimilar) {
                            insetToSortMapAndKeyArray(name, file);
                        }
                    }

                }//end name not empty
            }//end for
            if (!mOnlyForSimilar && mKeysArrayList.size() > 0) {
                synchronized (this) {
                    Collections.sort(mKeysArrayList, new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            if ((!TextUtils.isEmpty(lhs)) && (!TextUtils.isEmpty(rhs))) {
                                if (lhs.equalsIgnoreCase(CMARERA)) {
                                    return -1;//Camera放在第一位，其他的以字母序列摆放
                                } else if (rhs.equalsIgnoreCase(CMARERA)) {
                                    return 1;
                                } else {
                                    return lhs.compareTo(rhs);
                                }
                            }
                            return 0;
                        }
                    });
                }
            }
        }
    }

    private void insetToSortMapAndKeyArray(String name,MediaFile file){
        name = StringUtils.toLowerCase(name);
        if(!mSortMap.containsKey(name)) {
            ArrayList<MediaFile> fileList = new ArrayList<MediaFile>();
            fileList.add(file);
            synchronized (this) {
                mSortMap.put(name, fileList);
                mKeysArrayList.add(name);
                mKeysSize.put(name, file.getSize());
            }
        } else {
            ArrayList<MediaFile> tmpList = (ArrayList<MediaFile>)mSortMap.get(name);
            tmpList.add(file);
            long size = mKeysSize.get(name);
            size += file.getSize();
            synchronized (this) {
                mKeysSize.put(name, size);
            }
        }
    }

    private void insetToSortMap(String key,MediaFile file){
        if(!mSortMap.containsKey(key)) {
            ArrayList<MediaFile> fileList = new ArrayList<MediaFile>();
            fileList.add(file);
            synchronized (this) {
                mSortMap.put(key, fileList);
            }
        } else {
            ArrayList<MediaFile> tmpList = (ArrayList<MediaFile>)mSortMap.get(key);
            tmpList.add(file);
        }
    }


    public long getSize() {
        mSize = 0L;

        if (mList == null || mList.isEmpty()) {
            return 0L;
        }

        synchronized (this) {
            for (MediaFile mediaFile : mList) {
                mSize += mediaFile.getSize();
            }
        }
        return mSize;

    }

//    public String[] getLatestFourThumbnails() {
//        if (mList == null || mList.isEmpty()) {
//            return null;
//        }
//        String[] paths = null;
//        if (mList.size() >= 4) {
//            paths = new String[4];
//        } else {
//            paths = new String[mList.size()];
//        }
//        for (int i = 0; i < paths.length; i++) {
//            MediaFile mediaFile = mList.get(i);
//            String path = mediaFile.getPath();
//            if (mediaFile.getMediaType() == MediaFile.MEDIA_TYPE_VIDEO) {
//                path = MediaFileDownloader.OtherScheme.VIDEO.wrap(path);
//            }
//            paths[i] = path;
//        }
//        return paths;
//    }

    public int getNum(){
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }
}
