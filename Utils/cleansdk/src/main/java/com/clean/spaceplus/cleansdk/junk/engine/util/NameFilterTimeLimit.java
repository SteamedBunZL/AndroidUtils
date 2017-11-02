package com.clean.spaceplus.cleansdk.junk.engine.util;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/23 19:35
 * @copyright TCL-MIG
 */
public interface NameFilterTimeLimit extends NameFilter{
    public boolean accept(String parent, String sub, boolean bFolder, long fileModifyTime);
}
