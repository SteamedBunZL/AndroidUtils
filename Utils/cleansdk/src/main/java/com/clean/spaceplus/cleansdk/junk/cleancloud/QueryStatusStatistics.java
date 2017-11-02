package com.clean.spaceplus.cleansdk.junk.cleancloud;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 15:48
 * @copyright TCL-MIG
 */
public abstract class QueryStatusStatistics {
    public static final String STATISTICS_TBL 	= "cm_cleancloud_querystatus";
    ///////////////////////////////////
    //private long m_uptime2 = 0;      ///< 生成埋点时间
    protected int m_mytype = 0;       ///< 内部自定义类型
    protected int m_network_type;      ///< 网络类型(1:有线网,2:wifi,3:2g,4:3g,5:4g)
//    protected int m_connect_cnt;       ///< 连接数(包含失败重试)
//    protected int m_connect_failed_cnt;///< 连接失败数
//    protected int m_online_failed_cnt; ///< 在线情况下失败数
//    protected int m_net_batches_cnt;   ///< 网络查询批次
//    protected int m_net_batches_failed_cnt;       ///< 网络查询批次失败数
//    protected int m_net_online_batches_failed_cnt;///< 在线情况下网络查询批次失败数
//    protected int m_upsize;          ///< 上行大小
//    protected int m_downsize;        ///< 下行大小
//    protected int m_total_query_cnt; ///< 请求特征数
//    protected int m_net_query_cnt;   ///< 网络请求特征数
    protected int m_total_hit_cnt;   ///< 全部命中数
    protected int m_cache_hit_cnt;   ///< 缓存库命中数
    protected int m_hr_hit_cnt;      ///< 高频库命中数 //m_hr_hit_cnt本来应该是hr_hit_cnt，但是申请的时候写错了，就将错就错了
    protected int m_net_hit_cnt;     ///< 网络命中数
//    protected int m_err1_cnt;        ///< 失败1数量
//    protected int m_err2_cnt;        ///< 失败2数量 目前用于网络请求特征失败数,和m_net_query_cnt对应
    protected int m_err3_cnt;        ///< 失败3数量 目前用于网络请求特征失败数,联网情况下,和m_net_query_cnt对应
//    protected int m_err4_cnt;        ///< 失败4数量
//    protected int m_net_query_time;  ///< 网络查询时间
//    protected int m_local_query_time;/// <本地库查询时间
//    protected int m_total_query_time;///< 总共查询时间
//    protected String m_server_host_ip = "";///< 服务端ip
//    protected int m_net_err_host_cnt;      ///< 网络查询host找不到错误数
//    protected int m_net_err_timeout_cnt;   ///< 网络查询timeout错误数
//    protected int m_net_err_io_cnt;        ///< 网络查询IO错误数
//    protected int m_net_err_response_cnt;  ///< 网络查询response错误数
//    protected int m_net_err_other_cnt;     ///< 网络查询其他错误数
//    protected int m_net_err_response_404_cnt;///< 网络查询404错误数
//    protected int m_local_lib_ver;           ///<	本地高频库版本	2015-01-12新增
//    protected int m_false_lib_ver1;          ///<	误报库版本1(常规去误报)	2015-01-12新增
//    protected int m_false_lib_ver2;          ///<	误报库版本2(暂定用于缓存目录去误报库,缓存包去误报库版本用false_lib_ver1)	2015-01-12新增
//    protected int m_emergency_false_lib_ver; ///<	紧急去误报版本	2015-01-12新增
//    protected int m_scanid;                  ///<	扫描的id 为了关联其他表的数据和云端数据	2015-03-26新增
//    protected int m_scan_start_time;         ///<	扫描开始时间，时间戳，单位秒	2015-03-26新增
//    protected int m_scan_end_time;           ///<	扫描结束时间，时间戳，单位秒	2015-03-26新增
//    protected int m_net_query_start_time;    ///<	网络扫描开始时间，时间戳，单位秒	2015-03-26新增
//    protected int m_net_query_stop_time;     ///<	网络扫描结束时间，时间戳，单位秒	2015-03-26新增
//    protected int m_net_query_real_time;     ///<	网络扫描耗时,考虑多线程扫描的情况,会记录真实的耗时,和net_query_time是不考虑多线程的情况,只是把每次查询的时间累加，单位：毫秒	2015-03-26新增
    protected QueryStatusStatistics() {
    }


    ///////////////////////////////////
//    public void reset() {
//        synchronized (this) {
//            //m_uptime2 = 0;      ///< 生成埋点时间
//            //m_mytype = 0;       ///< 内部自定义类型 用于标识类型,就不重置了
//            m_network_type = 0;      ///< 网络类型(1:有线网,2:wifi,3:2g,4:3g,5:4g)
//            m_connect_cnt = 0;       ///< 连接数(包含失败重试)
//            m_connect_failed_cnt = 0;///< 连接失败数
//            m_online_failed_cnt = 0; ///< 在线情况下失败数
//            m_net_batches_cnt = 0;   ///< 网络查询批次
//            m_net_batches_failed_cnt = 0;       ///< 网络查询批次失败数
//            m_net_online_batches_failed_cnt = 0;///< 在线情况下网络查询批次失败数
//            m_upsize = 0;          ///< 上行大小
//            m_downsize = 0;        ///< 下行大小
//            m_total_query_cnt = 0; ///< 请求特征数
//            m_net_query_cnt = 0;   ///< 网络请求特征数
//            m_total_hit_cnt = 0;   ///< 全部命中数
//            m_cache_hit_cnt = 0;   ///< 缓存库命中数
//            m_hr_hit_cnt = 0;      ///< 高频库命中数
//            m_net_hit_cnt = 0;     ///< 网络命中数
//            m_err1_cnt = 0;        ///< 失败1数量
//            m_err2_cnt = 0;        ///< 失败2数量
//            m_err3_cnt = 0;        ///< 失败3数量
//            m_err4_cnt = 0;        ///< 失败4数量
//            m_net_query_time = 0;  ///< 网络查询时间
//            m_local_query_time = 0;/// <本地库查询时间
//            m_total_query_time = 0;///< 总共查询时间
//            m_server_host_ip = "";///< 服务端ip
//            m_net_err_host_cnt = 0;      ///< 网络查询host找不到错误数
//            m_net_err_timeout_cnt = 0;   ///< 网络查询timeout错误数
//            m_net_err_io_cnt = 0;        ///< 网络查询IO错误数
//            m_net_err_response_cnt = 0;  ///< 网络查询response错误数
//            m_net_err_other_cnt = 0;     ///< 网络查询其他错误数
//            m_net_err_response_404_cnt = 0;///< 网络查询404错误数
//            m_local_lib_ver = 0;           ///<	本地高频库版本	2015-01-12新增
//            m_false_lib_ver1 = 0;          ///<	误报库版本1(常规去误报)	2015-01-12新增
//            m_false_lib_ver2 = 0;          ///<	误报库版本2(暂定用于缓存目录去误报库,缓存包去误报库版本用false_lib_ver1)	2015-01-12新增
//            m_emergency_false_lib_ver = 0; ///<	紧急去误报版本	2015-01-12新增
//            m_scanid = 0;                  ///<	扫描的id 为了关联其他表的数据和云端数据	2015-03-26新增
//            m_scan_start_time = 0;         ///<	扫描开始时间，时间戳，单位秒	2015-03-26新增
//            m_scan_end_time = 0;           ///<	扫描结束时间，时间戳，单位秒	2015-03-26新增
//            m_net_query_start_time = 0;    ///<	网络扫描开始时间，时间戳，单位秒	2015-03-26新增
//            m_net_query_stop_time = 0;     ///<	网络扫描结束时间，时间戳，单位秒	2015-03-26新增
//            m_net_query_real_time = 0;     ///<	网络扫描耗时,考虑多线程扫描的情况,会记录真实的耗时,和net_query_time是不考虑多线程的情况,只是把每次查询的时间累加，单位：毫秒	2015-03-26新增
//        }
//    }

//    public void setNetWorkType(int type) {
//        synchronized (this) {
//            m_network_type = type;
//        }
//    }

    public void addError3Count(int value) {
        synchronized (this) {
            m_err3_cnt += value;
        }
    }

    /**
     * <pre>
     * private int m_total_hit_cnt;// 全部命中数
     * private int m_cache_hit_cnt;// 缓存库命中数
     * private int m_hr_hit_cnt;// 高频库命中数
     * private int m_net_hit_cnt;// 网络命中数
     * </pre>
     *
     * @param hitCountHF
     * @param hitCountCache
     * @param hitCountCloud
     * @param hitCountTotal
     */
    public synchronized void addHitCountData(int hitCountHF, int hitCountCache,
                                             int hitCountCloud, int hitCountTotal) {
        m_total_hit_cnt += hitCountTotal;
        m_cache_hit_cnt += hitCountCache;
        m_hr_hit_cnt += hitCountHF;
        m_net_hit_cnt += hitCountCloud;
    }

//    public String getStatisticsString() {
//        StringBuilder builder = new StringBuilder();
//        //builder.append( "uptime2=");//1
//        //builder.append(m_uptime2);
//        builder.append("mytype=");//2
//        builder.append(m_mytype);
//        builder.append("&network_type=");//3
//        builder.append(m_network_type);
//        builder.append("&connect_cnt=");//4
//        builder.append(m_connect_cnt);
//        builder.append("&connect_failed_cnt=");//5
//        builder.append(m_connect_failed_cnt);
//        builder.append("&online_failed_cnt=");//6
//        builder.append(m_online_failed_cnt);
//        builder.append("&net_batches_cnt=");//7
//        builder.append(m_net_batches_cnt);
//        builder.append("&net_batches_failed_cnt=");//8
//        builder.append(m_net_batches_failed_cnt);
//        builder.append("&net_online_batches_failed_cnt=");//9
//        builder.append(m_net_online_batches_failed_cnt);
//        builder.append("&upsize=");//10
//        builder.append(m_upsize);
//        builder.append("&downsize=");//11
//        builder.append(m_downsize);
//        builder.append("&total_query_cnt=");//12
//        builder.append(m_total_query_cnt);
//        builder.append("&net_query_cnt=");//13
//        builder.append(m_net_query_cnt);
//        builder.append("&total_hit_cnt=");//14
//        builder.append(m_total_hit_cnt);
//        builder.append("&cache_hit_cnt=");//15
//        builder.append(m_cache_hit_cnt);
//        builder.append("&hr_hit_cnt=");//16
//        builder.append(m_hr_hit_cnt);
//        builder.append("&net_hit_cnt=");//17
//        builder.append(m_net_hit_cnt);
//        builder.append("&err1_cnt=");//18
//        builder.append(m_err1_cnt);
//        builder.append("&err2_cnt=");//19
//        builder.append(m_err2_cnt);
//        builder.append("&err3_cnt=");//20
//        builder.append(m_err3_cnt);
//        builder.append("&err4_cnt=");//21
//        builder.append(m_err4_cnt);
//        builder.append("&net_query_time=");//22
//        builder.append(m_net_query_time);
//        builder.append("&local_query_time=");//23
//        builder.append(m_local_query_time);
//        builder.append("&total_query_time=");//24
//        builder.append(m_total_query_time);
//        builder.append("&server_host_ip=");//25
//        builder.append(m_server_host_ip);
//        builder.append("&net_err_host_cnt=");//26
//        builder.append(m_net_err_host_cnt);
//        builder.append("&net_err_timeout_cnt=");//27
//        builder.append(m_net_err_timeout_cnt);
//        builder.append("&net_err_io_cnt=");//28
//        builder.append(m_net_err_io_cnt);
//        builder.append("&net_err_response_cnt=");//29
//        builder.append(m_net_err_response_cnt);
//        builder.append("&net_err_other_cnt=");//30
//        builder.append(m_net_err_other_cnt);
//        builder.append("&net_err_response_404_cnt=");//31
//        builder.append(m_net_err_response_404_cnt);
//        builder.append("&local_lib_ver=");//32
//        builder.append(m_local_lib_ver);
//        builder.append("&false_lib_ver1=");//33
//        builder.append(m_false_lib_ver1);
//        builder.append("&false_lib_ver2=");//34
//        builder.append(m_false_lib_ver2);
//        builder.append("&emergency_false_lib_ver=");//35
//        builder.append(m_emergency_false_lib_ver);
//        builder.append("&scanid=");//36
//        builder.append(m_scanid);
//        builder.append("&scan_start_time=");//37
//        builder.append(m_scan_start_time);
//        builder.append("&scan_end_time=");//38
//        builder.append(m_scan_end_time);
//        builder.append("&net_query_start_time=");//39
//        builder.append(m_net_query_start_time);
//        builder.append("&net_query_stop_time=");//40
//        builder.append(m_net_query_stop_time);
//        builder.append("&net_query_real_time=");//41
//        builder.append(m_net_query_real_time);
//
//        return builder.toString();
//    }
}
