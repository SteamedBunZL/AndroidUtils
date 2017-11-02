package com.clean.spaceplus.cleansdk.junk.engine.junk.junkext.stub;

import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/6 15:09
 * @copyright TCL-MIG
 */
//警告：只准添加接口，不能删除和修改参数名字
public interface JunkExtItem {
     int TYPE_CACHE_TEMP_DIR 		= 1;
     int TYPE_CACHE_TEMP_FILE_LISTS 	= 2;

     int SIGN_ID_START	= 2000001;

     String getDesc();
     int getSignId();
     int getType();
     List<String> getPathLists();
     String getPathDir();
}
