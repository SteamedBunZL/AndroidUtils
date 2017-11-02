package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/6 17:15
 * @copyright TCL-MIG
 */
public class FalseFilterFactory {
    private FalseFilterFactory(){}
    private static final FalseFilterManager msMgr = new FalseFilterManagerImpl();
    public static FalseFilterManager getFalseFilterManagerInstance() {
        return msMgr;
    }
}
