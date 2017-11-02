package com.clean.spaceplus.cleansdk.junk.cleancloud.residual;

import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/21 14:57
 * @copyright TCL-MIG
 */
public class KResidualPathHelper {
    /**
     * 枚举子目录的Filter;目前不会有过滤条件，只是统统列举;
     * @author 
     * @date 2014.12.04
     * */
    private static class SubPathEnumFilter implements NameFilter {
        public SubPathEnumFilter() {
        }

        @Override
        public boolean accept(String parent, String sub, boolean bFolder) {
            return bFolder;
        }
    }

    /**
     * 枚举子目录的Filter;目前不会有过滤条件，只是统统列举;
     * @author 
     * @date 2014.12.04
     * */
    private static class SubFileEnumFilter implements NameFilter {
        public SubFileEnumFilter() {
        }

        @Override
        public boolean accept(String parent, String sub, boolean bFolder) {
            return !bFolder;
        }
    }

    /**
     * 枚举指定路径下的子目录；
     * 提示：目前只枚举目录类型
     * @author 
     * @date 2014.12.04
     * */
    public static PathOperFunc.StringList enumFolder(String parent){
        return PathOperFunc.listDir(parent, new SubPathEnumFilter());
    }

    /**
     * 枚举指定路径下的子文件；
     * @author 
     * @date 2015.1.27
     * */
    public static PathOperFunc.StringList enumFile(String parent){
        return PathOperFunc.listDir(parent, new SubFileEnumFilter());
    }
}
