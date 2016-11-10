package com.tcl.security.virusengine.func_interface;

import com.tcl.security.cloudengine.CloudResponse;
import com.tcl.security.virusengine.entry.CloudTask;
import com.tcl.security.virusengine.entry.ScanEntity;
import com.tcl.security.virusengine.entry.ScanEntry;

import java.util.List;
import java.util.Map;

/**
 * 云查杀流程接口
 * Created by Steve on 2016/8/12.
 */
public interface CloudScheduling {

    /**
     * 批量上传
     * @param list
     * @throws Exception
     */
    void uploading(List<ScanEntry> list) throws Exception;

    /**
     * 实时监控上传
     * @param entry
     */
    void uploading(ScanEntry entry);

    /**
     * TCL云查失败，走mcafee流程
     * @param cloudTask
     * @param packages
     * @param entryList
     * @param entryMap
     */
    void parseListToScanSource(CloudTask cloudTask, List<String> packages, List<ScanEntry> entryList, Map<String, ScanEntry> entryMap);


    /**
     * 处理TCL云查杀结果   未知情况要走其他流程 多个线程回调，同步处理
     * @param list
     * @param entryList
     */
    void parseNeedQueryByVSMToScanSource(CloudTask cloudTask, List<CloudResponse> list, List<ScanEntry> entryList, Map<String, ScanEntry> entryMap);

    /**
     * 走Mcafee云上传流程
     * @param
     * @param entryList
     * @throws Exception
     */
    void uploadMcafeeCloud(final CloudTask cloudTask, final List<ScanEntity> entityList, final List<ScanEntry> entryList, final Map<String, ScanEntry> entryMap, final boolean isTclCloudError) throws Exception;
}
