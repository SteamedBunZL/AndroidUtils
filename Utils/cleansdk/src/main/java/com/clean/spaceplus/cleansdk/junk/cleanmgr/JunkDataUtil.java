package com.clean.spaceplus.cleansdk.junk.cleanmgr;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportCleanBean;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Analytics;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
import com.clean.spaceplus.cleansdk.base.utils.analytics.bean.CleanAllEvent;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcScanResult;
import com.clean.spaceplus.cleansdk.boost.engine.scan.BoostScanTask;
import com.clean.spaceplus.cleansdk.boost.util.ProcessWhiteListMarkHelper;
import com.clean.spaceplus.cleansdk.junk.engine.DataTypeInterface;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkChildType;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkGroupTitle;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkSubChildType;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.task.AdvFolderScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.ApkScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.RubbishFileScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SdCardCacheScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SysCacheScanTask;
import com.clean.spaceplus.cleansdk.util.PackageUtils;
import com.clean.spaceplus.cleansdk.util.ResUtil;
import com.clean.spaceplus.cleansdk.util.SizeUtil;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.clean.spaceplus.cleansdk.boost.engine.process.ProcessAdvInfo.getBoostKeepReason;
import static com.clean.spaceplus.cleansdk.junk.engine.bean.JunkGroupTitle.ITEM_MEMCACHE_FLAG;

/**
 * @author wangtianbao
 * @Description: 垃圾清理数据工具
 * @date 2016/7/19 14:26
 * @copyright TCL-MIG
 */

public class JunkDataUtil {
    public static final int DISPLAY_WALVE_SIZE = 10;
    private static JunkGroupTitle getJunkGroupByFlag(List<JunkGroupTitle> groupList,int flag) {
        for (JunkGroupTitle junkGroup : groupList) {
            if (junkGroup.groupFlag == flag) {
                return junkGroup;
            }
        }
        return null;
    }

    public static void notifyUpdateChange(List<JunkModel> junkList,List<JunkGroupTitle> groupList) {
        int sum = 0;
        if (junkList != null && junkList.size() > 0) {
            List<Event> events=new LinkedList<Event>();
            for (JunkModel jm : junkList) {
                if (jm == null) {
                    continue;
                }
                //应用缓存
                if (jm.getType() == DataTypeInterface.TYPE_APP_CACHE) {
                    if (jm.getFileSize() <= 10) {
                        continue;
                    }
                    CacheInfo ca = jm.getCacheInfo();
//                    NLog.i(TAG, "ca pkgname end %s %s %s", ca.getPackageName(), ca.getAppName(), ca.getRealAppName());
                    JunkGroupTitle group = getJunkGroupByFlag(groupList,JunkGroupTitle.ITEM_APPCACHE_FLAG);
                    if (group == null) {
                        continue;
                    }
                    JunkChildType junkChild = new JunkChildType(group);
//                junkChild.setChildIcon(R.drawable.facebook);
                    junkChild.junkpkgname = ca.getPackageName();
                    if (!TextUtils.isEmpty(ca.getRealAppName())) {
                        junkChild.childTypeName = ca.getRealAppName();
                    }
                    junkChild.junkChildSize = SizeUtil.formatSizeForJunkHeader(jm.getFileSize());
                    junkChild.junkSize = jm.getFileSize();
                    junkChild.junkSuggestion = ResUtil.getString(R.string.junk_suggest_clean);
                    junkChild.isChildChecked = ca.isCheck();
                    junkChild.junkModelType = jm.getType();
                    junkChild.junkModel = jm;
                    //应用缓存的明细项
                    List<CacheInfo> cacheInfoList=jm.getChildList();
                    if(cacheInfoList!= null && cacheInfoList.size()>0){
                        for(CacheInfo infoItem:cacheInfoList){
                            JunkSubChildType subChildType=new JunkSubChildType(junkChild);
                            subChildType.subChildTypeName=infoItem.getAppName();
                            try {
                                subChildType.subJunkChildSize = SizeUtil.formatSizeForJunkHeader(infoItem.getSize());
                                subChildType.subDescritpion= String.format(infoItem.getDescritpion(),infoItem.getRealAppName(), infoItem.getAppName());
                            } catch (Exception e) {
                            }
                            subChildType.subRoute=infoItem.getFilePath();
                            subChildType.isChildChecked = ca.isCheck();
                            subChildType.subPackageName=infoItem.getPackageName();
                            subChildType.mCachInfo=infoItem;
                            subChildType.subJunkSize=infoItem.getSize();
                            subChildType.junkModelType=jm.getType();
                            subChildType.junkModel = jm;
                            junkChild.addChild(subChildType);
                        }
                        Collections.sort(junkChild.getChildren());
                    }
                    group.addChild(junkChild);
                    reportCleanAll(junkChild,jm.getType(),events);
                }
                //卸载残留
                if (jm.getType() == DataTypeInterface.TYPE_APP_LEFT) {
                    SDcardRubbishResult sr = jm.getSdcardRubbishResult();
                    if (sr.getSize() < DISPLAY_WALVE_SIZE) {
                        continue;
                    }
//                    NLog.i(TAG, "sr appleft end %s %s %s", sr.getApkName(), sr.getChineseName(), sr.getStrDirPath());
                    JunkGroupTitle group = getJunkGroupByFlag(groupList,JunkGroupTitle.ITEM_LEFTCACHE_FLAG);
                    if (group == null) {
                        continue;
                    }
                    JunkChildType junkChild = new JunkChildType(group);
                    junkChild.childIcon = R.drawable.junk_apk_file;
                    if (!TextUtils.isEmpty(sr.getApkName())) {
                        junkChild.childTypeName = sr.getApkName();
                    }
                    try {
                        junkChild.junkChildSize = SizeUtil.formatSizeForJunkHeader(sr.getSize());
                    } catch (Exception e) {
                    }
                    junkChild.junkSize = sr.getSize();
                    junkChild.junkSuggestion = ResUtil.getString(R.string.junk_suggest_clean);
                    junkChild.isChildChecked = sr.isCheck();
                    junkChild.junkModelType = jm.getType();
                    junkChild.junkModel = jm;
                    group.addChild(junkChild);
                    reportCleanAll(junkChild,jm.getType(),events);
                }
                //广告垃圾
                if (jm.getType() == DataTypeInterface.TYPE_TEMP_FILE || jm.getType() == DataTypeInterface.TYPE_AD_FILE) {
                    SDcardRubbishResult result = jm.getSdcardRubbishResult();
                    if (jm.getFileSize() < DISPLAY_WALVE_SIZE) {
                        continue;
                    }
                    JunkGroupTitle group = getJunkGroupByFlag(groupList,JunkGroupTitle.ITEM_ADCACHE_FLAG);
                    if (group == null) {
                        continue;
                    }
                    if (result != null && !TextUtils.isEmpty(result.getApkName())) {
                        JunkChildType junkChild = new JunkChildType(group);
                        junkChild.childIcon = R.drawable.junk_ad_logo;
                        junkChild.childTypeName = result.getApkName();
                        junkChild.junkChildSize = SizeUtil.formatSizeForJunkHeader(jm.getFileSize());
                        junkChild.junkSize = jm.getFileSize();
                        junkChild.junkSuggestion = ResUtil.getString(R.string.junk_suggest_clean);
                        junkChild.isChildChecked = result.isCheck();
                        //NLog.d(AdvFolderScanTask.TAG, "临时文件或者广告文件 isChildChecked = " + result.isCheck());
                        junkChild.junkModelType = jm.getType();
                        junkChild.junkModel = jm;
                        group.addChild(junkChild);
                        reportCleanAll(junkChild,jm.getType(),events);
                    }
                }
                //无用安装包
                if (jm.getType() == DataTypeInterface.TYPE_APK_FILE) {
                    APKModel am = jm.getApkModel();
                    if (am.getSize() < DISPLAY_WALVE_SIZE) {
                        continue;
                    }
//                    NLog.i(TAG, "ca apkfile end %s %s", am.getTitle(), am.getPath());
                    JunkGroupTitle group = getJunkGroupByFlag(groupList,JunkGroupTitle.ITEM_APKCACHE_FLAG);
                    if (group == null) {
                        continue;
                    }
                    JunkChildType junkChild = new JunkChildType(group);
                    junkChild.childIcon = R.drawable.junk_useless_apk;
                    junkChild.childTypeName = am.getTitle();
                    junkChild.junkChildSize = SizeUtil.formatSizeForJunkHeader(am.getSize());
                    junkChild.junkSize = am.getSize();
                    String version = am.getVersion();
                    int attrStrId = 0;
                    switch (am.getDisplayType()) {
                        case 1:
                            attrStrId = R.string.junk_tag_junk_apk_backup;
                            break;
                        case 4:
                            attrStrId = R.string.junk_tag_junk_apk_fonts;
                            break;
                        default:
                            if (am.type == APKModel.APK_NOT_INSTALLED) {
                                if (am.isUninstalledNewDL()) {
                                    attrStrId = R.string.junk_tag_junk_apk_new_download;
                                } else {
                                    attrStrId = R.string.junk_tag_fm_list_apk_type_not_installed;
                                }
                            }
                            if (APKModel.APK_INSTALLED == am.type) {
                                if (APKModel.APK_STATUS_NEW == am.getApkInstallStatus()) {
                                    attrStrId = R.string.fm_list_apk_item_summary_new;  // 新版本
                                } else if (APKModel.APK_STATUS_CUR == am.getApkInstallStatus()) {
                                    attrStrId = R.string.junk_tag_fm_list_apk_type_installed;  // 已安装
                                } else {
                                    attrStrId = R.string.fm_list_apk_item_summary_old;  // 旧版本
                                }

                            }
                            break;
                    }
                    if (version != null && !version.isEmpty()) {
                        junkChild.junkSuggestion = "[" + ResUtil.getString(attrStrId) + "]" + version;
                    } else {
                        junkChild.junkSuggestion = "[" + ResUtil.getString(attrStrId) + "]"; // 仅仅在新下载，并且包损坏的情况下
                    }
                    junkChild.isChildChecked = am.isCheck();
                    junkChild.junkModelType = jm.getType();
                    junkChild.junkModel = jm;
                    group.addChild(junkChild);
                    reportCleanAll(junkChild,jm.getType(),events);
                }

                //根据需求调整系统缓存位置在无用安装包与内存缓存中间
                if (jm.getType() == DataTypeInterface.TYPE_SYSTEM_CACHE) {
                    List<CacheInfo> cacheInfos = new ArrayList<CacheInfo>(jm.getChildList());
                    JunkGroupTitle group = getJunkGroupByFlag(groupList,JunkGroupTitle.ITEM_SYSCACHE_FLAG);
                    if (group == null) {
                        continue;
                    }
                    for (CacheInfo cacheInfo : cacheInfos) {
                        if (cacheInfo.getSize() < DISPLAY_WALVE_SIZE) {
                            continue;
                        }
                        JunkChildType junkChild = new JunkChildType(group);
                        junkChild.junkpkgname = cacheInfo.getPackageName();
                        junkChild.childTypeName = PackageUtils.getAppNameByPackageName(SpaceApplication.getInstance().getContext(), cacheInfo.getPackageName());
                        junkChild.junkChildSize = SizeUtil.formatSizeForJunkHeader(cacheInfo.getSize());
                        junkChild.junkSize = cacheInfo.getSize();
                        junkChild.junkSuggestion = ResUtil.getString(R.string.junk_suggest_clean);
                        junkChild.isChildChecked = cacheInfo.isCheck();
                        junkChild.junkModelType = jm.getType();
                        junkChild.junkModel = jm;
                        junkChild.cacheInfo = cacheInfo;
                        group.addChild(junkChild);
                        reportCleanAll(junkChild,jm.getType(),events);
                    }
                }

                if (jm.getType() == DataTypeInterface.TYPE_SYS_FIXED_CACHE) {
                    List<CacheInfo> cacheInfos = new ArrayList<CacheInfo>(jm.getChildList());
                    JunkGroupTitle group = getJunkGroupByFlag(groupList,JunkGroupTitle.ITEM_SYSCACHE_FLAG);
                    if (group == null) {
                        continue;
                    }
                    for (CacheInfo cacheInfo : cacheInfos) {
                        if (cacheInfo.getSize() <= 10) {
                            continue;
                        }
                        JunkChildType junkChild = new JunkChildType(group);
                        junkChild.childTypeName = cacheInfo.getAppName();
                        junkChild.childIcon = R.drawable.junk_apk_file;
                        junkChild.junkChildSize = SizeUtil.formatSizeForJunkHeader(cacheInfo.getSize());
                        junkChild.junkSize = cacheInfo.getSize();
                        junkChild.junkSuggestion = ResUtil.getString(R.string.junk_suggest_clean);
                        junkChild.isChildChecked = cacheInfo.isCheck();
                        junkChild.junkModelType = jm.getType();
                        junkChild.junkModel = jm;
                        junkChild.cacheInfo = cacheInfo;
                        group.addChild(junkChild);
                        reportCleanAll(junkChild,jm.getType(),events);
                    }
                }

                if (jm.getType() == DataTypeInterface.TYPE_PROCESS) {
                    ProcessModel pm = jm.getProcessModel();
//                    NLog.i(TAG, "ca apkfile end %s %s", pm.getPkgName(), pm.getTitle());
                    JunkGroupTitle group = getJunkGroupByFlag(groupList, ITEM_MEMCACHE_FLAG);
                    if (group == null) {
                        continue;
                    }
                    JunkChildType junkChild = new JunkChildType(group);
                    if (!TextUtils.isEmpty(pm.getPkgName())) {
                        junkChild.junkpkgname = pm.getPkgName();
                    }
                    junkChild.childTypeName = pm.getTitle();
                    junkChild.junkChildSize = SizeUtil.formatSizeForJunkHeader(pm.getMemory());
                    junkChild.junkSize = pm.getMemory();
                    int suggestionResId;
                    if (!pm.isInFlexibleWhiteListState() && ProcessWhiteListMarkHelper.isDefaultIgnore(pm.getIgnoreMark())) {
                        if (pm.isInMemoryCheckEx()) {
                            suggestionResId = R.string.boost_pm_item_mc;
                        } else if (pm.getExtKillStrategy() != ProcScanResult.STRATEGY_NORMAL) {
                            suggestionResId = R.string.boost_kill_social_proc_tips;
                        } else {
                            suggestionResId = getBoostKeepReason(pm);
                        }
                    } else {
                        suggestionResId = R.string.junk_suggest_clean;
                    }
                    junkChild.junkSuggestion = ResUtil.getString(suggestionResId);
                    junkChild.isChildChecked = jm.isProcessChecked();
                    junkChild.junkModelType = jm.getType();
                    junkChild.junkModel = jm;
                    junkChild.mProcessModel=pm;
                    group.addChild(junkChild);
                    reportCleanAll(junkChild,jm.getType(),events);
                }
            }

            if(events!=null && events.size()>0){
                DataReportFactory.getDefaultDataReport().putEvents(events,false);
            }

            JunkChildType.JunkChildTypeComparator comparator = new JunkChildType.JunkChildTypeComparator();
            for (int i = 0; i < groupList.size(); i++) {
                //fix bug SC-84:
                System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
                Collections.sort(groupList.get(i).getChildren(), comparator);
                JunkGroupTitle title = groupList.get(i);
                title.refreshCheckStatus();
            }
        }
    }

    //上报扫描到的每条数据信息
    private static void reportCleanAll(JunkChildType junkChild,int type,List<Event> events){
        String typeStr= DataReportCleanBean.SCAN_TYPE_SDCACHE;
        String time="";
        String route="";
        try{
            switch (type){
                case DataTypeInterface.TYPE_APP_CACHE:
                    typeStr=DataReportCleanBean.SCAN_TYPE_SDCACHE;
                    time= Analytics.formatTimeSize(Analytics.getInstance().getTaskTime(SdCardCacheScanTask.class));
                    if(junkChild.junkModel.getCacheInfo()!=null){
                        route=junkChild.junkModel.getCacheInfo().getFilePath();
                    }
                    break;
                case DataTypeInterface.TYPE_APP_LEFT:
                    typeStr=DataReportCleanBean.SCAN_TYPE_LEFTCACHE;
                    time=Analytics.formatTimeSize(Analytics.getInstance().getTaskTime(RubbishFileScanTask.class));
                    if(junkChild.junkModel.getSdcardRubbishResult()!=null){
                        route=junkChild.junkModel.getSdcardRubbishResult().getStrDirPath();
                    }
                    break;
                case DataTypeInterface.TYPE_TEMP_FILE:
                case DataTypeInterface.TYPE_AD_FILE:
                    typeStr=DataReportCleanBean.SCAN_TYPE_ADVCACHE;
                    time=Analytics.formatTimeSize(Analytics.getInstance().getTaskTime(AdvFolderScanTask.class));
                    if(junkChild.junkModel.getSdcardRubbishResult()!=null){
                        route=junkChild.junkModel.getSdcardRubbishResult().getStrDirPath();
                    }
                    break;
                case DataTypeInterface.TYPE_APK_FILE:
                    typeStr=DataReportCleanBean.SCAN_TYPE_APKCACHE;
                    time=Analytics.formatTimeSize(Analytics.getInstance().getTaskTime(ApkScanTask.class));
                    if(junkChild.junkModel.getApkModel()!=null){
                        route=junkChild.junkModel.getApkModel().getPath();
                    }
                    break;
                case DataTypeInterface.TYPE_SYSTEM_CACHE:
                case DataTypeInterface.TYPE_SYS_FIXED_CACHE:
                    typeStr=DataReportCleanBean.SCAN_TYPE_SYSCACHE;
                    time=Analytics.formatTimeSize(Analytics.getInstance().getTaskTime(SysCacheScanTask.class));
                    break;
                case DataTypeInterface.TYPE_PROCESS:
                    typeStr=DataReportCleanBean.SCAN_TYPE_PROCESS;
                    time=Analytics.formatTimeSize(Analytics.getInstance().getTaskTime(BoostScanTask.class));
                    break;
            }
        }catch (Exception e){

        }
        //路径这里先空了
        CleanAllEvent event=new CleanAllEvent(typeStr,String.valueOf(junkChild.junkSize),junkChild.junkpkgname,route,time);
        events.add(event);
    }
}
