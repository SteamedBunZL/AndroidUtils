package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/29 20:38
 * @copyright TCL-MIG
 */
public interface RubbishFileFilter {
    public boolean isNeedFilterByPath(final String filePath);
    public long getFileTimeLimit();


}
