//package com.clean.spaceplus.cleansdk.junk.engine.bean;
//
//import android.text.TextUtils;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Set;
//
///**
// * @author zengtao.kuang
// * @Description: 照片压缩数据Model
// * @date 2016/5/9 11:19
// * @copyright TCL-MIG
// */
//public class PhotoCompressDataModel {
//    public static final String GROUP_SCREENSHORT = "1";
//    public static final String GROUP_CAMERA = "2";
//
//    public static final String PATH_CAMERA = "/Camera";
//    public static final String PATH_DCIM_CAMERA = "/DCIM/Camera";
//    public static final String PATH_DCIM_MEDIA = "/DCIM/100MEDIA";
//    public static final String PATH_CAMERA_ANDROID = "/DCIM/100ANDRO";
//    public static final String PATH_PHOTO = "/Photo";
//    public static final String PATH_DCIM = "/DCIM";
//    public static final String PATH_ZH_PHOTO = "/我的相机";
//    public static final String PATH_ZH_DCIM = "/相机/照片";
//    public static final String PATH_ZH_CAMERA = "/相机";
//    public static final String PATH_ZH_MYPHOTO= "/我的照片";
//
//    public static final String PATH_PIC_SCREENSHOT = "/Pictures/Screenshots";
//    public static final String PATH_DCIM_SCREENSHOT = "/DCIM/Screenshots";
//    public static final String PATH_SCREENSHOT = "/Screenshots";
//    public static final String PATH_PIC_SCREENSHOT_LOWER= "/Pictures/screenshots";
//    public static final String PATH_DCIM_SCREENSHOT_LOWER = "/DCIM/screenshots";
//    public static final String PATH_SCREENSHOT_LOWER = "/screenshots";
//    public static final String PATH_PHOTO_SCREENSHOT = "/Photo/Screenshots";
//
//    private LinkedHashMap<String, ArrayList<MediaFile>> mData = new LinkedHashMap<String, ArrayList<MediaFile>>();
//
//    private HashMap<String, Boolean> mCheckStatusCache = new HashMap<String, Boolean>();
//
//    private List<MediaFile> mCompressedList = new ArrayList<MediaFile>();
//
//    private HashMap<String, Long> mCompressSizeMap;
//
//    public PhotoCompressDataModel() {
//    }
//
//    public void setData(LinkedHashMap<String, ArrayList<MediaFile>> data) {
//        if (data == null) {
//            mData.clear();
//        } else {
//            mData = data;
//        }
//    }
//
//    public void sortData(ArrayList<MediaFile> srcList) {
//        LinkedHashMap<String, ArrayList<MediaFile>> data = new LinkedHashMap<String, ArrayList<MediaFile>>();
//        ArrayList<MediaFile> cameraList = new ArrayList<MediaFile>();
//        ArrayList<MediaFile> screenshotList = new ArrayList<MediaFile>();
//
//        if (srcList != null && !srcList.isEmpty()) {
//            int cameraIndex = Integer.parseInt(PhotoCompressDataModel.GROUP_CAMERA);
//            int screenShotIndex = Integer.parseInt(PhotoCompressDataModel.GROUP_SCREENSHORT);
//
//            for (MediaFile mf : srcList) {
//                String path = mf.getPath();
//                if (!TextUtils.isEmpty(path)) {
//                    if (path.contains(PATH_SCREENSHOT) || path.contains(PATH_DCIM_SCREENSHOT)) {
//                        mf.setIndex(screenShotIndex);
//                        screenshotList.add(mf);
//                    } else {
//                        mf.setIndex(cameraIndex);
//                        cameraList.add(mf);
//                    }
//                }
//            }
//        }
//        if (!screenshotList.isEmpty()) {
//            data.put(GROUP_SCREENSHORT, screenshotList);
//        }
//        if (!cameraList.isEmpty()) {
//            data.put(GROUP_CAMERA, cameraList);
//        }
//        setData(data);
//    }
//
//	/* 勾选状态相关begin */
//
//    public boolean isGroupSelected(String groupKey, boolean force) {
//        boolean result = false;
//        if (!TextUtils.isEmpty(groupKey)) {
//            if (!force) {
//                Boolean cache = mCheckStatusCache.get(groupKey);
//                if (cache != null) {
//                    return cache.booleanValue();
//                }
//            }
//            ArrayList<MediaFile> list = mData.get(groupKey);
//            if (list != null && !list.isEmpty()) {
//                result = true;
//                for (MediaFile mf : list) {
//                    if (!mf.isCheck()) {
//                        result = false;
//                        break;
//                    }
//                }
//            }
//            mCheckStatusCache.put(groupKey, Boolean.valueOf(result));
//        }
//        return result;
//    }
//
//    public void clearGroupSelectStatus(String groupKey) {
//        mCheckStatusCache.remove(groupKey);
//    }
//
//    public void clearAllGroupSelectStatus() {
//        mCheckStatusCache.clear();
//    }
//
//    public boolean convertCheckChild(String groupKey, int pos) {
//        boolean result = false;
//        MediaFile mf = getChild(groupKey, pos);
//        if (mf != null) {
//            mf.setCheck(!mf.isCheck());
//            if (mf.isCheck()) {
//                clearGroupSelectStatus(groupKey);
//            } else {
//                mCheckStatusCache.put(groupKey, Boolean.FALSE);
//            }
//            result = true;
//        }
//        return result;
//    }
//
//    public boolean convertCheckGroup(String groupKey) {
//        boolean result = false;
//        ArrayList<MediaFile> list = getGroup(groupKey);
//        if (list != null) {
//            boolean isCheck = isGroupSelected(groupKey, false);
//            for (MediaFile mf : list) {
//                mf.setCheck(!isCheck);
//            }
//            mCheckStatusCache.put(groupKey, Boolean.valueOf(!isCheck));
//            result = true;
//        }
//        return result;
//    }
//
//    public long getSelected(ArrayList<MediaFile> list) {
//        long size = 0;
//        if (list != null) {
//            list.clear();
//        }
//        for (String key : mData.keySet()) {
//            for (MediaFile mf : mData.get(key)) {
//                if (mf.isCheck()) {
//                    if (list != null) {
//                        list.add(mf);
//                    }
//                    size += mf.getSize();
//                }
//            }
//        }
//        return size;
//    }
//
//    public ArrayList<MediaFile> getSelectedFile() {
//        ArrayList<MediaFile> result = new ArrayList<MediaFile>();
//        getSelected(result);
//        return result;
//    }
//
//	/* 勾选状态相关end */
//
//    public boolean isEmpty() {
//        Iterator<ArrayList<MediaFile>> iterator = mData.values().iterator();
//        while (iterator.hasNext()) {
//            ArrayList<MediaFile> value = iterator.next();
//            if (value == null || value.isEmpty()) {
//                iterator.remove();
//            }
//        }
//        return mData.isEmpty();
//    }
//
//    public Set<String> getAllGroup() {
//        return mData.keySet();
//    }
//
//    public int getChildCount(String groupKey) {
//        if (!TextUtils.isEmpty(groupKey)) {
//            ArrayList<MediaFile> list = mData.get(groupKey);
//            if (list != null) {
//                return list.size();
//            }
//        }
//        return 0;
//    }
//
//    public ArrayList<MediaFile> getGroup(String groupKey) {
//        if (!TextUtils.isEmpty(groupKey)) {
//            return mData.get(groupKey);
//        }
//        return null;
//    }
//
//    public MediaFile getChild(String groupKey, int pos) {
//        MediaFile mf = null;
//        ArrayList<MediaFile> list = getGroup(groupKey);
//        if (list != null) {
//            if (pos >= 0 && pos < list.size()) {
//                mf = list.get(pos);
//            }
//        }
//        return mf;
//    }
//
//    public long getAllFileSize() {
//        long size = 0;
//        for (String key : mData.keySet()) {
//            for (MediaFile mf : mData.get(key)) {
//                size += mf.getSize();
//            }
//        }
//        return size;
//    }
//
//    public long getGroupFileSize(String groupKey) {
//        ArrayList<MediaFile> list = getGroup(groupKey);
//        long size = 0;
//        if (list != null && !list.isEmpty()) {
//            for (MediaFile mf : list) {
//                size += mf.getSize();
//            }
//        }
//        return size;
//    }
//
//    public MediaFile removeChild(String groupKey, MediaFile mf) {
//        ArrayList<MediaFile> list = getGroup(groupKey);
//        if (list != null && mf != null) {
//            int index = list.indexOf(mf);
//            if (index >= 0) {
//                MediaFile resultMf = list.remove(index);
//                if (resultMf != null) {
//                    final long newSize = mf.getSize();
//                    long compressSize;
//                    if (resultMf.getSize() != newSize) {
//                        compressSize = resultMf.getSize() - newSize;
//                        resultMf.setSize(newSize);
//                        resultMf.setLastModified(mf.lastModified());
//                        resultMf.setMimeType(mf.getMimeType());
//                        mCompressedList.add(resultMf);
//                        addGroupCompressSize(groupKey, compressSize);
//                    }
//                }
//                return resultMf;
//            }
//        }
//        return null;
//    }
//
//    private void addGroupCompressSize(String groupKey, long size) {
//        if (mCompressSizeMap == null) {
//            mCompressSizeMap = new HashMap<String, Long>();
//        }
//        Long oldSize = mCompressSizeMap.get(groupKey);
//        mCompressSizeMap.put(groupKey, size + (oldSize != null ? oldSize.longValue() : 0));
//    }
//
//    public long getGroupCompressSize(String groupKey) {
//        return (mCompressSizeMap != null && !TextUtils.isEmpty(groupKey) && mCompressSizeMap.containsKey(groupKey)) ? mCompressSizeMap.get(groupKey).longValue() : 0;
//    }
//
//}
