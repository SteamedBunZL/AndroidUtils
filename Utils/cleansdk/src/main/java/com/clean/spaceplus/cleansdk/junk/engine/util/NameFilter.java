package com.clean.spaceplus.cleansdk.junk.engine.util;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/23 19:32
 * @copyright TCL-MIG
 */
public interface NameFilter {
     boolean accept(String parent, String sub, boolean bFolder);
}
