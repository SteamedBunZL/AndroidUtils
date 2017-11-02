package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/6 15:16
 * @copyright TCL-MIG
 */
public interface EmergencyFalseSignFilter {
    public static class FilterType {
        /**无效参数*/
        public static final int INVAILD   = 0;

        /**缓存目录id过滤器*/
        public static final int CACHE_DIR     = 1;

        /**残留目录id过滤器*/
        public static final int RESIDUAL_DIR  = 2;

    }

    public static class NotifySignUpdateData {
        public int type;
        public int newVersion;
    }

    /**
     * 误报特征过滤
     *
     * @param id 特征id
     *
     * @return 返回true为被过滤掉不应该检出,返回false为可以检出
     */
    public boolean filter(int id);
}
