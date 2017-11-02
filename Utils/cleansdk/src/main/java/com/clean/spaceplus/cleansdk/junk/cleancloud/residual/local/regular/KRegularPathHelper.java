package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.local.regular;

import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/21 13:56
 * @copyright TCL-MIG
 */
public class KRegularPathHelper {
    /**
     * 枚举子目录的Filter;目前不会有过滤条件，只是统统列举;
     * */
    private static class SubPathEnumFilter implements NameFilter {
        public SubPathEnumFilter() {
        }

        @Override
        public boolean accept(String parent, String sub, boolean bFolder) {
            return true;
        }
    }

    /**
     * 枚举指定路径下的子目录；
     * 提示：目前只枚举目录类型
     * */
    public static PathOperFunc.StringList enumPath(String parent){
        PathOperFunc.FilesAndFoldersStringList subList = PathOperFunc.listDir(parent, new SubPathEnumFilter());
        if(subList != null){
            return subList.getFolderNameList();
        }
        return null;
    }
}
