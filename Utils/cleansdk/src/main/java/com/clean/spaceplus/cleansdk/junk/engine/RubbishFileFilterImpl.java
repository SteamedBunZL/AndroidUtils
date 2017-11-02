package com.clean.spaceplus.cleansdk.junk.engine;

import java.io.File;

import space.network.cleancloud.KResidualCloudQuery;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/29 20:37
 * @copyright TCL-MIG
 */
public class RubbishFileFilterImpl implements RubbishFileFilter {

    private int mCleanTime = -1;
    private KResidualCloudQuery.FileChecker mRubbishFilter = null;
    private KResidualCloudQuery.FileCheckerData mFileterData = new KResidualCloudQuery.FileCheckerData();

    public RubbishFileFilterImpl() {
    }

    public void reset() {
        mRubbishFilter = null;
    }

    public void setRubbishCleanTime(int cleanTime) {
        mCleanTime = cleanTime;
    }

    public int getRubbishCleanTime() {
        return mCleanTime;
    }

    public boolean setFilterInterface(KResidualCloudQuery.FileChecker filter) {
        mRubbishFilter = filter;
        return true;
    }

    public boolean setFilterData(KResidualCloudQuery.FileCheckerData fileterData) {

        if (fileterData != null) {
            mFileterData.globalSuffixCatIds = fileterData.globalSuffixCatIds;
            if (fileterData.whiteSuffixFilter != null) {
                mFileterData.whiteSuffixFilter = fileterData.whiteSuffixFilter;
            }
            if (fileterData.blackSuffixFilter != null) {
                mFileterData.blackSuffixFilter = fileterData.blackSuffixFilter;
            }
            return true;
        }

        return false;
    }

    public KResidualCloudQuery.FileCheckerData getFilterData() {
        return mFileterData;
    }

    /**
     * @param filePath  文件路径
     * @return true--文件需要在RubbishScan中计算大小并且加入到删除任务中
     *         false-文件不需要在RubbishScan中计算大小并且不能删除。
     */
    public boolean isNeedFilterByPath(final String filePath) {
        if (mRubbishFilter == null || mFileterData == null) {
            return true;
        }

        int nPos = filePath.lastIndexOf(File.separator);
        if (nPos != -1) {
            String fileName = filePath.substring(nPos + 1);
            return mRubbishFilter.removable(fileName, mFileterData);
        }

        return true;
    }

    @Override
    public long getFileTimeLimit() {
        if (mCleanTime == -1 || mCleanTime == 0) {
            return -1;
        }

        return mCleanTime * 24 * 60 * 60;
    }
}
