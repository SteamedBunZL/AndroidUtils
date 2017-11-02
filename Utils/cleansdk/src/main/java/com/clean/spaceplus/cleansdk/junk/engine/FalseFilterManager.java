package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author dongdong.huang
 * @Description:错误过滤器
 * @date 2016/4/26 14:01
 * @copyright TCL-MIG
 */
public interface FalseFilterManager {
     interface FalseSignFilter{
         boolean filter(int id);

         void acquireReference();

         void releaseReference();
    }

     FalseSignFilter getFalseDataByCategory(int key);

    class CategoryKey {
        public static final int KEY_CACHE = 1;
        public static final int KEY_RESIDUAL = 2;
        public static final int KEY_RESIDUAL_DIR = 3;
        public static final int KEY_RESIDUAL_REGEX = 4;
        public static final int KEY_CACHE_DIR = 5;
        public static final int KEY_MAX = 6;
    }
}
