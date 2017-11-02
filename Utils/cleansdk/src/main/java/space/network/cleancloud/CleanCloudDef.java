//=============================================================================
/**
 * @file CleanCloudDef.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud;

public class CleanCloudDef {
    /*
     */
    public static class WaitResultType {
        public static final int WAIT_FAILED    = -1;
        public static final int WAIT_SUCCESSED = 0;
        public static final int WAIT_TIMEOUT   = 1;
        public static final int WAIT_CANCEL   = 2;
        public static final int WAIT_COMPLETE   = 3;
    }

    /**
     * 云端的主要清理业务类型;
     * */
    public static class CloudLogicType {
        public static final int CLOUD_RESIDUAL = 1;
        public static final int CLOUD_CACHE = 2;
        public static final int CLOUD_MEMORY = 3;
        public static final int CLOUD_CPU = 4;
        public static final int CLOUD_PREINSTALL = 5;
    }

    /**
     * 统一定义云端查询接口查询状态；
     * */
    public static class CloudQueryStatus {
        public static final int QUERY_SUCCESS = 0;
        public static final int QUERY_FAIL_NO_NETWORK = 100;
        public static final int QUERY_FAIL_NO_RET_CACHE = 101;
        public static final int QUERY_FAIL_NO_POST_DATA = 102;
    }


    public  interface ScanTaskCtrl {

        /**
         * 是否需要停止
         * @return 返回为true表示需要停止扫描流程
         */
        boolean checkStop();
    }
}