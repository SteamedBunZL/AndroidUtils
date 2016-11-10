package com.tcl.security.virusengine.scan_strategy;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.tcl.security.virusengine.Constants;
import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.func_interface.ScanResultDelivery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * 快速扫描方式
 * Created by Steve on 2016/5/1.
 */
public class VirusQuickScan extends VirusScan {

    public VirusQuickScan() {
        super();
    }

    @Override
    protected void addEntryToQueue(VirusScanQueue queue,List<String> ignoreList) {
        //boolean connected = NetworkUtils.isConnected(mContext);
        //mQueue.getDelivery().postScanInfo(ScanResultDelivery.DELIVERY_EVENT_NETSTATE,null,connected);
        List<PackageInfo> packageInfos = VirusScanQueue.getInstance().getContext().getPackageManager().getInstalledPackages(0);
        List<PackageInfo> appList = new ArrayList<>();
        List<ScanEntry> entryList = new LinkedList<>();
        mQueue.getDelivery().postScanInfo(ScanResultDelivery.DELIVERY_EVENT_PRE, null);

        for (PackageInfo info : packageInfos) {
            if (((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                    &&!ignoreList.contains(info.packageName)//忽略列表过滤
                    &&!VirusScanQueue.getInstance().getContext().getPackageName().equals(info.packageName)//过滤自己,此项用于SDK时,还要加一条过滤Hi Security
                    &&!Constants.PACKGENAME_SECURITY.equals(info.packageName)//过滤Hi Security
                    &&!Constants.PACKAGENAME_APPLOCK.equals(info.packageName)//过滤applock
                    &&!Constants.PACKAGENAME_NOTIFYBOX.equals(info.packageName)//过滤notifybox
                    ) {
                appList.add(info);//如果非系统应用，则添加至appList
            }
        }
        //获取扫描的总数，返回给上层
        mQueue.getDelivery().postScanInfo(ScanResultDelivery.DELIVERY_EVENT_NUMERIC, null, appList.size());
        
        //遍历扫描
        ScanEntry entry = null;
        ApplicationInfo appInfo;
        String publicSourceDir = null;
        for (PackageInfo info : appList) {
            entry = new ScanEntry();
            String appName = String.valueOf(info.applicationInfo.loadLabel(VirusScanQueue.getInstance().getContext().getPackageManager()));
            if (TextUtils.isEmpty(appName))
                appName = info.packageName;
            appInfo = info.applicationInfo;
            if (appInfo!=null)
                publicSourceDir = appInfo.publicSourceDir;
            entry = buildScanEntry(entry, ScanEntry.PASS_CACHE, info.packageName, appName, info.versionName,null, info.versionCode,publicSourceDir);
            entryList.add(entry);
        }
        queue.add(entryList);
    }

    @Override
    protected ScanEntry buildScanEntry(ScanEntry entry, Object... obj) {
        entry.shouldCache = true;
        entry.priority = (int) obj[0];
        entry.packageName = (String) obj[1];
        entry.appName = (String) obj[2];
        entry.appVersion = (String) obj[3];
        entry.cacheKey = entry.packageName;
        if (!TextUtils.isEmpty((String)obj[4])){
            entry.virusEngine = (String) obj[4];
        }
        entry.appVersionCode = (int) obj[5];
        entry.publicSourceDir = (String) obj[6];
        return entry;
    }



}
