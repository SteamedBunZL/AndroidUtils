package com.clean.spaceplus.cleansdk.junk.engine.fixedata;

import com.clean.spaceplus.cleansdk.junk.engine.task.ApkScanTask;

/**
 * @author wangtianbao
 * @Description:
 * @date 2016/8/25 20:42
 * @copyright TCL-MIG
 */

public class ApkTaskData {


    public static ApkScanTask.TargetFolderParam[] getFixedDatas(){
        ApkScanTask.TargetFolderParam[] fixedFolders = {

                new ApkScanTask.TargetFolderParam(".android_msg/update/msg/apk"), //
                // .android_msg/update/msg/apk

                // Android Data
                new ApkScanTask.TargetFolderParam("Android/data", 5, 1000, Integer.MAX_VALUE, Integer
                        .MAX_VALUE), // Android/data  with no limit

                new ApkScanTask.TargetFolderParam("binco/download/dn-android.qbox.me/android/soft"), //
                // binco/download/dn-android.qbox.me/android/soft

                new ApkScanTask.TargetFolderParam(".AlipayGphone/cmd/download"), // center.com.eg.android
                // .AlipayGphone/cmd/download

                new ApkScanTask.TargetFolderParam("download", 3, 1000, Integer.MAX_VALUE, Integer
                        .MAX_VALUE), //download
                new ApkScanTask.TargetFolderParam("bluetooth", 3, 1000, Integer.MAX_VALUE, Integer
                        .MAX_VALUE), //bluetooth

                // htcmarket
                new ApkScanTask.TargetFolderParam("htcmarket/app/data/apk", 3, -1), //
                // htcmarket/app/data/apk
                new ApkScanTask.TargetFolderParam("htcmarket/app/data/apk/003/002/001"), //
                // htcmarket/app/data/apk/003/002/001

                new ApkScanTask.TargetFolderParam("LesyncDownload/data/com.lenovo.leos.cloud.sync/apps/SUSDownload"), //
                // LesyncDownload/data/com.lenovo.leos.cloud.sync/apps/SUSDownload

                new ApkScanTask.TargetFolderParam("PandaHome2/myphone/mybackup/app", 2, -1),
                // PandaHome2/myphone/mybackup/app

                new ApkScanTask.TargetFolderParam("sina/weibo/SinaAppMarket/apk", 0, 150), //
                // sina/weibo/SinaAppMarket/apk
                // System
                new ApkScanTask.TargetFolderParam("System/APK/COMMON/apk"), // System/APK/COMMON/apk
                new ApkScanTask.TargetFolderParam("System/APK/RU/apk"), // System/APK/RU/apk

                new ApkScanTask.TargetFolderParam("tencent", 3, 5000), // tencent

                new ApkScanTask.TargetFolderParam("tencent/opensdk/logs", 10, 1000), //
                // tencent/opensdk/logs
                new ApkScanTask.TargetFolderParam("games/com.mojang/minecraftworlds", 10,
                        1000), // games/com.mojang/minecraftworlds
                new ApkScanTask.TargetFolderParam("tencent/cloudsdk/logs", 10, 1000), //
                // tencent/cloudsdk/logs
                new ApkScanTask.TargetFolderParam("tencent/wns/logs", 10, 1000), // tencent/wns/logs
                new ApkScanTask.TargetFolderParam("tencent/tmassistantsdk/download", 2, 1000)
                , // tencent/tmassistantsdk/download
                new ApkScanTask.TargetFolderParam("tencent/qqhd/plugin/market/apk"), //
                // tencent/qqhd/plugin/market/apk
                new ApkScanTask.TargetFolderParam("tencent/msflogs/com/tencent/mobileqq"), //
                // tencent/msflogs/com/tencent/mobileqq

                new ApkScanTask.TargetFolderParam("android/data/com.linecorp.lgrgs/cache",
                        6, 1000), //android/data/com.linecorp.lgrgs/cache
                new ApkScanTask.TargetFolderParam("android/data/com.linecorp.lgstage/cache",
                        6, 1000), //android/data/com.linecorp.lgstage/cache
                new ApkScanTask.TargetFolderParam("android/data/cn.wps.moffice_eng/.cache",
                        6, 1000), //android/data/cn.wps.moffice_eng/.cache

                new ApkScanTask.TargetFolderParam("baidu/searchbox/books", 10, 1000), //
                // baidu/searchbox/books
                new ApkScanTask.TargetFolderParam("baidu/flyflow/novel", 10, 1000), //
                // baidu/flyflow/novel
                new ApkScanTask.TargetFolderParam("cloudagent/cache/dropbox", 10, 1000), //
                // cloudagent/cache/dropbox
                new ApkScanTask.TargetFolderParam("tapatalk4/cache/longterm", 10, 1000), //
                // tapatalk4/cache/longterm
                new ApkScanTask.TargetFolderParam("cloudagent/cache/root", 10, 1000), //
                // cloudagent/cache/root
                new ApkScanTask.TargetFolderParam("tencent/QQfile_recv", 2, 1000, Integer
                        .MAX_VALUE, Integer.MAX_VALUE), // tencent/QQfile_recv
        };
        return fixedFolders;
    }
}
