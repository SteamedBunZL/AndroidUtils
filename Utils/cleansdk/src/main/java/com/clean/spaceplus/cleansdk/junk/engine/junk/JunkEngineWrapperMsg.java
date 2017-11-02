package com.clean.spaceplus.cleansdk.junk.engine.junk;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/27 20:57
 * @copyright TCL-MIG
 */
public class JunkEngineWrapperMsg {

    // 注意，这里定义的ID值域是[1000000000, 2000000000)，不要超过范围定义，以免造成冲突。
    // 引擎信息更新
    // arg1: 无用
    // arg2: 无用
    // pkgName: 类型为JunkEngineWrapperUpdateInfo，不可长期持有此回调对象，如果需要，请复制保存。
    public static final int MSG_HANDLER_UPDATE_INFO = 1000000001;

    /**
     * 检出单项大小
     * arg1: EM_JUNK_DATA_TYPE的成员 ordinal，如：EM_JUNK_DATA_TYPE.MYAUDIO.ordinal()
     * arg2: 是否checked标志。 0 为未checked。 1为checked.
     * pkgName: long类型此检出项大小
     */
    public static final int MSG_HANDLER_FOUND_ITEM_SIZE = 1000000002;
}
