//=============================================================================
/**
 * @file KCleanCloudEnv.java
 * @brief
 */
//=============================================================================
package space.network.commondata;
public class KCleanCloudEnv {

    ///////////////////////////////////////////////////////////////////////
    //预估的包头大小(抓包后观察得出，只是近似值)，用于计算流量预估http包头的大小
    public static final int POST_HEADER_ESTIMATE_LENGTH = 0x140;
    public static final int POST_RESPONSE_HEADER_ESTIMATE_LENGTH = 0x90;

    public static final int GET_HEADER_ESTIMATE_LENGTH = 240;
    public static final int GET_RESPONSE_HEADER_ESTIMATE_LENGTH = 244;

    ///////////////////////////////////////////////////////////////////////
    public static final int SQL_IN_MAGIC_NUMBER = 96;
    //timeout setting
    public static final int DEFAULT_POST_TIMEOUT = 12000;
    ///////////////////////////////////////////////////////////////////////
    //清理云查询(小米)
    //渠道ID 3001
    //渠道KEY ejLYZ9LMs#2v3kxs

    //public static final short  DEFAULT_CHANNEL_ID	 = 3001;
    //public static final String DEFAULT_CHANNEL_KEY = "ejLYZ9LMs#2v3kxs";

    //about channel
    public static final short  DEFAULT_CHANNEL_ID	 = 1002;
    public static final String DEFAULT_CHANNEL_KEY	 = "BcpjBhC*8kZ&=0Oo";
    public static final String DEFAULT_RESPONSE_KEY  = "%^ZHGrLSqV=ZLWv)";



    public static final String DEFAULT_CHANNEL_KEY_AES	 = ")cpj_@*8k!&=Aa/>";
    public static final String DEFAULT_RESPONSE_KEY_AES  = "~L&9$s2%-w+J<M?,";



    ///////////////////////////////////////////////////////////////////////
    public static final int SECURITY_CLOUD_STATISTICS_TYPE = 10;
    ///////////////////////////////////////////////////////////////////////


    public static final String EXTERNAL_DATA_ROOT_DIR_NAME = "cm_cleancloud";

    public static final String PROVIDER_ROOT_PATH_NAME = "cm_cleancloud";

    //现在特征差异和网络错误上报类型都没有分国内和国外，但是统计分了,有些怪异,但是先这样了
    public static class CloudQueryType {
        public static final int RESIDUAL_DIR_QUERY = 1;
        public static final int RESIDUAL_DIR_QUERY_ABROAD = 2;

        public static final int RESIDUAL_PKG_QUERY = 3;
        public static final int RESIDUAL_PKG_QUERY_ABROAD = 4;

        public static final int CACHE_PKG_QUERY    = 5;
        public static final int CACHE_PKG_QUERY_ABROAD = 6;

        public static final int CACHE_SHOW_QUERY   = 7;
        public static final int CACHE_SHOW_QUERY_ABROAD = 8;

        public static final int CACHE_DESC_QUERY   = 9;
        public static final int CACHE_DESC_QUERY_ABROAD = 10;

        public static final int PREINSTALL_QUERY   = 11;
        public static final int PREINSTALL_QUERY_ABROAD = 12;

        public static final int SECURITY_QUERY     = 13;
        public static final int SECURITY_QUERY_ABROAD = 14;

        public static final int APPINFO_QUERY   = 15;
        public static final int APPINFO_QUERY_ABROAD = 16;

        public static final int MEM_QUERY   = 17;
        public static final int MEM_QUERY_ABROAD = 18;

        public static final int CPU_QUERY   = 19;
        public static final int CPU_QUERY_ABROAD = 20;

        public static final int RESIDUAL_FALSE_QUERY = 21;
        public static final int RESIDUAL_FALSE_QUERY_ABROAD = 22;

        public static final int CACHE_FALSE_QUERY = 23;
        public static final int CACHE_FALSE_QUERY_ABROAD = 24;

        public static final int VIRUS_DESC_QUERY = 25;
        public static final int VIRUS_DESC_QUERY_ABROAD = 26;

        public static final int SCAN_RESULT_REPORT = 27;

        public static final int RESIDUAL_REGULAR_DIR_QUERY = 29;
        public static final int RESIDUAL_REGULAR_DIR_QUERY_ABROAD = 30;
    }

    //用于预先准备长连接的url
    public static final String CLEAN_CLOUD_TEST_URL = "http://beha.ksmobile.com/c";//国内
    public static final String CLEAN_CLOUD_TEST_URL_ABROAD = "http://behacdn.ksmobile.net/c";//国外,cdn

    //public static final boolean CACHE_SCAN_ENGINE_NEW_ENABLED = false;
    ///////////////////////////////////////////////////////////////////////
    public static final String URL_HEAD = "http://";
    //public static final String CLEAN_CLOUD_QUERY_HOST = "184.169.148.127";//海外1
    //public static final String CLEAN_CLOUD_QUERY_HOST = "184.169.154.195";//海外2
    //public static final String CLEAN_CLOUD_QUERY_HOST = "119.147.146.159";//测试
    public static final String CLEAN_CLOUD_QUERY_HOST = "beha.ksmobile.com";//自动区分海外,国内
    //public static final String CLEAN_CLOUD_QUERY_HOST = "beha.cloud.duba.net";//国内
    public static final String CLEAN_CLOUD_QUERY_SRV  = URL_HEAD+CLEAN_CLOUD_QUERY_HOST;

    //////////////////////////////
    //防误报上报系统相关域名
    public static final String CLEAN_CLOUD_REPORT_HOST = "p-beha.ksmobile.com";//国内
    //public static final String CLEAN_CLOUD_REPORT_HOST_ABROAD = "p-beha.ksmobile.net";//国外
    public static final String CLEAN_CLOUD_REPORT_HOST_ABROAD = "p-behacdn.ksmobile.net";//国外,cdn
    //public static final String CLEAN_CLOUD_REPORT_HOST = "p-beha.cloud.duba.net";//国内
    public static final String CLEAN_CLOUD_REPORT_SRV = URL_HEAD+CLEAN_CLOUD_REPORT_HOST;
    public static final String CLEAN_CLOUD_REPORT_SRV_ABROAD  = URL_HEAD+CLEAN_CLOUD_REPORT_HOST_ABROAD;
    //end
    //////////////////////////////

    //////////////////////////////
    //清理云相关域名和ip
    //public static final String CLOUD_HOST_ABROAD = "beha.ksmobile.net";//国外cdn
    //public static final String CLEAN_CLOUD_HOST_ABROAD = "119.147.146.159";//国外cdn
    //public static final String CLEAN_CLOUD_HOST = "119.147.146.159";
    public static final String CLEAN_CLOUD_HOST_ABROAD = "behacdn.ksmobile.net";//国外cdn
    public static final String CLEAN_CLOUD_HOST = "beha.ksmobile.com";

    public static final String CLEAN_CLOUD_IP_1 = "221.228.204.33";
    public static final String CLEAN_CLOUD_IP_2 = "122.193.207.33";
    public static final String CLEAN_CLOUD_IP_ABROAD_1 = "54.193.42.169";
    public static final String CLEAN_CLOUD_IP_ABROAD_2 = "54.193.2.171";
    //end
    //////////////////////////////


    //////////////////////////////
    //信息云云相关域名和ip
    //public static final String APPINFO_CLOUD_QUERY_HOST_ABROAD = "appinfo.ksmobile.net"; //国际版使用
    public static final String APPINFO_CLOUD_HOST_ABROAD = "appinfocdn.ksmobile.net"; //国际版使用,cdn方式的域名
    public static final String APPINFO_CLOUD_HOST = "appinfo.ksmobile.com"; //国内版使用
    //public static final String APPINFO_CLOUD_QUERY_HOST = "119.147.146.92"; //test server

    //国内ip
    public static final String APPINFO_CLOUD_IP_1 = "221.228.204.37";///< 国内，电信
    public static final String APPINFO_CLOUD_IP_2 = "122.193.207.37";///< 国内，联通

    //海外ip 目前全是美国
    public static final String APPINFO_CLOUD_IP_ABROAD_1 = "54.193.110.22";
    public static final String APPINFO_CLOUD_IP_ABROAD_2 = "54.193.88.86";

    public static final short  APPINFO_QUERY_CHANNEL_ID = 2001;
    public static final String APPINFO_QUERY_CHANNEL_KEY = "]9+ffA0#]UAIdMNU";
    public static final String APPINFO_QUERY_RESPONSE_KEY = "RvUZ)6x1$zfr3$@v";
    //////////////////////////////

}