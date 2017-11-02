package com.clean.spaceplus.cleansdk.junk.engine.junk;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;

import space.network.util.compress.Base64;

import com.clean.spaceplus.cleansdk.junk.engine.DBColumnFilterManager;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest.EM_JUNK_DATA_TYPE;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 15:03
 * @copyright TCL-MIG
 */
public class JunkOfflineService {
    private static final String TAG = "JunkOfflineService";
    private static long mScreenOffTime = 5 * 60 * 1000L;
    private static long mVideoResultExpiredTime = 24 * 60 * 60 * 1000L;
    private static long mVideoResultNotOperExpiredTime = 3 * 24 * 60 * 60 * 1000L;
    private static long mVideoResultPopSize = 200 * 1024 * 1024L;
    private static long mLastScanTime = 0L;
    private static long mOneDayTime = 24 * 60 * 60 * 1000L;
    private static long mOneMinuteTime = 60 * 1000L;
    private long mCurrentScanSize = 0;
    private int mCurrentScanCount = 0;
    private int mDBColumnFilterST = Integer.valueOf(DBColumnFilterManager.EXPAND_FILTER_ID_OFFLINE0);
    private int mDBColumnFilterSP = Integer.valueOf(DBColumnFilterManager.EXPAND_FILTER_ID_OFFLINE9);
    private boolean [] mOfflineScanPkgFilter = new boolean[mDBColumnFilterSP-mDBColumnFilterST+1];
    private String defaultSdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String secondarySdCardPath = null;
    private ArrayList<String> mSdCardPathList = null; //default then secondary

    private HashMap<EM_JUNK_DATA_TYPE, ArrayList<BaseJunkBean>> mOfflineResultMap =
            new HashMap<EM_JUNK_DATA_TYPE, ArrayList<BaseJunkBean>>();

    private boolean mIsStart = false;
    private Context mContext;
    private Handler mHandler;
    long mCurTime = 0;

    public static String [] mVideoOfflineScanPkg = {
            new String(Base64.decode("Y29tLnNvaHUuc29odXZpZGVv")), //com.sohu.sohuvideo
            new String(Base64.decode("Y29tLnlvdWt1LnBob25l")),     //com.youku.phone
            new String(Base64.decode("Y29tLnR1ZG91LmFuZHJvaWQ=")), //com.tudou.android
            new String(Base64.decode("Y29tLnRlbmNlbnQucXFsaXZl")), //com.tencent.qqlive
            new String(Base64.decode("Y29tLmlqaW5zaGFuLmJyb3dzZXJfZmFzdA==")), //com.ijinshan.browser_fast
            new String(Base64.decode("Y29tLnRlbmNlbnQubXR0")), //com.tencent.mtt
            new String(Base64.decode("Y29tLnN0b3JtLnNtYXJ0")), //com.storm.smart
            new String(Base64.decode("Y29tLnFpeWkudmlkZW8=")), //com.qiyi.video
            new String(Base64.decode("Y29tLnFpaG9vLnZpZGVv")), //com.qihoo.video
            new String(Base64.decode("Y29tLnBwbGl2ZS5hbmRyb2lkcGhvbmU="))  //com.pplive.androidphone
    };

    public static String [] mVideoOfflineScanPath = {
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5zb2h1LnNvaHV2aWRlby90ZW1wVmlkZW8=")), //android/data/com.sohu.sohuvideo/tempVideo
            new String(Base64.decode("eW91a3Uvb2ZmbGluZWRhdGE=")), //youku/offlinedata
            new String(Base64.decode("dHVkb3Uvb2ZmbGluZWRhdGE=")),  //tudou/offlinedata
            new String(Base64.decode("QW5kcm9pZC9kYXRhL2NvbS50ZW5jZW50LnFxbGl2ZS9maWxlcy92aWRlb3M=")),  //Android/data/com.tencent.qqlive/files/videos
            new String(Base64.decode("a2Jyb3dzZXJfZmFzdC9kb3dubG9hZC9WaWRlbw==")),  //kbrowser_fast/download/Video
            new String(Base64.decode("UVFCcm93c2VyL+inhumikQ==")),  //QQBrowser/视频
            new String(Base64.decode("YmFvZmVuZy8uZG93bmxvYWQ=")),  //baofeng/.download
            new String(Base64.decode("QW5kcm9pZC9kYXRhL2NvbS5xaXlpLnZpZGVvL2ZpbGVz")),  //Android/data/com.qiyi.video/files
            new String(Base64.decode("MzYwdmlkZW8vMzYwdmlkZW9jYWNoZQ==")),  //360video/360videocache
            new String(Base64.decode("cHB0di9kb3dubG9hZA=="))   //pptv/download
    };

    public static String [] mVideoOfflineScanPathSec = {
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5zb2h1LnNvaHV2aWRlby90ZW1wVmlkZW8=")), //android/data/com.sohu.sohuvideo/tempVideo
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS55b3VrdS5waG9uZS95b3VrdS9vZmZsaW5lZGF0YQ==")), //android/data/com.youku.phone/youku/offlinedata
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS50dWRvdS5hbmRyb2lkL3R1ZG91L29mZmxpbmVkYXRh")),  //android/data/com.tudou.android/tudou/offlinedata
            new String(Base64.decode("QW5kcm9pZC9kYXRhL2NvbS50ZW5jZW50LnFxbGl2ZS9maWxlcy92aWRlb3M=")),  //Android/data/com.tencent.qqlive/files/videos
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5pamluc2hhbi5icm93c2VyX2Zhc3QvZmlsZXMvZG93bmxvYWQvVmlkZW8=")),  //android/data/com.ijinshan.browser_fast/files/download/Video
            new String(Base64.decode("UVFCcm93c2VyL+inhumikQ==")),  //QQBrowser/视频
            new String(Base64.decode("YmFvZmVuZy8uZG93bmxvYWQ=")),  //baofeng/.download
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5xaXlpLnZpZGVvL2ZpbGVz")),  //android/data/com.qiyi.video/files
            new String(Base64.decode("MzYwdmlkZW8vMzYwdmlkZW9jYWNoZQ==")),  //360video/360videocache
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5wcGxpdmUuYW5kcm9pZHBob25l"))   //android/data/com.pplive.androidphone
    };

    //Android SDK 19  4.4 up
    public static String [] mVideoOfflineScanPathSec2 = {
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5zb2h1LnNvaHV2aWRlby90ZW1wVmlkZW8=")), //android/data/com.sohu.sohuvideo/tempVideo
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS55b3VrdS5waG9uZS95b3VrdS9vZmZsaW5lZGF0YQ==")), //android/data/com.youku.phone/youku/offlinedata
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS50dWRvdS5hbmRyb2lkL3R1ZG91L29mZmxpbmVkYXRh")),  //android/data/com.tudou.android/tudou/offlinedata
            new String(Base64.decode("QW5kcm9pZC9kYXRhL2NvbS50ZW5jZW50LnFxbGl2ZS9maWxlcy92aWRlb3M=")),  //Android/data/com.tencent.qqlive/files/videos
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5pamluc2hhbi5icm93c2VyX2Zhc3QvZmlsZXMvZG93bmxvYWQvVmlkZW8=")),  //android/data/com.ijinshan.browser_fast/files/download/Video
            new String(Base64.decode("UVFCcm93c2VyL+inhumikQ==")),  //QQBrowser/视频
            new String(Base64.decode("YmFvZmVuZy8uZG93bmxvYWQ=")),  //baofeng/.download
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5xaXlpLnZpZGVvL2ZpbGVz")),  //android/data/com.qiyi.video/files
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5xaWhvby52aWRlby8zNjB2aWRlb2NhY2hl")),  //android/data/com.qihoo.video/360videocache
            new String(Base64.decode("YW5kcm9pZC9kYXRhL2NvbS5wcGxpdmUuYW5kcm9pZHBob25l"))   //android/data/com.pplive.androidphone
    };
}
