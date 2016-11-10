package com.tcl.security.virusengine.cache.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Steve on 2016/5/6.
 */
public class Provider {
    //phoneguard
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.tct.phoneguard.virus";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.tct.phoneguard.virus";

    //cleaan
    //public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.hawk.space.virus";
    //public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.hawk.space.virus";


    /**
     * 病毒表
     */
    public static final class VirusCacheColumns implements BaseColumns{


        //phoneguard
        public static final String AUTHORITY = "com.tct.phoneguard.provider.virus";
        //clean
        //public static final String AUTHORITY = "com.hawk.space.provider.virus";


        /**The uri of the viruscache*/
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/viruscaches");

        /**Table name*/
        public static final String TABLE_NAME = "virus_cache";

        /**ID auto increment*/
        public static final String COLUMN_ID = "_id";

        /**Packagename column*/
        public static final String COLUMN_PACKAGENAME = "packageName";

        /**Appname column*/
        public static final String COLUMN_APP_NAME = "app_name";

        /**Cachekey column use to get cache with the key,Now key is packagename temparary*/
        public static final String COLUMN_CACHE_KEY = "cache_key";

        /**Time to last update*/
        public static final String COLUMN_TTL = "ttl";

        /**Scan state*/
        public static final String COLUMN_SCAN_STATE = "scan_state";

        /**The virus lib version*/
        public static final String COLUMN_VIRUS_LIB_VERSION = "virus_lib_version";

        /**The application version*/
        public static final String COLUMN_APPLICATION_VERSION = "application_version";

        /**Virus name*/
        public static final String COLUMN_VIRUS_NAME = "virus_name";

        /**表示查杀结果来自哪个引擎 0表示本地引擎 1表示AVEIGINE云引擎 2表示TCL云引擎*/
        public static final String COLUMN_FROM = "from_engine";

        /**病毒类型，或者说病毒家族，这个只有病毒项才有*/
        public static final String COLUM_TYPE = "type";

        /**病毒的risk_level 默认值为-10001*/
        public static final String COLUM_RISK_LEVEL = "riskLevel";

        /**客户端存储的服务器端的TCL散列值*/
        public static final String COLUM_TCL_HASH = "tcl_hash";

        /**针对病毒项给出的处理建议，这里只有病毒项才有这个值*/
        public static final String COLUM_SUGGEST = "suggest";

        /**TCL云扫描结果 -1表示异常 0表示Clean 1表示Risk 2表示未知*/
        public static final String COLUM_TCL_CLOUD_RESULT = "tcl_cloud_result";

        /**AVENGINE云扫描结果 -1表示异常 0表示Clean 1表示Risk 2表示未知*/
        public static final String COLUM_AVENGINE_CLOUD_RESULT = "avengine_cloud_result";

        /**TCL云下发的数据应该缓存时间 单位:ms 默认值为86400000 也就是24小时*/
        public static final String COLUM_CLOUD_CACHE_TIME ="cloud_cache_time";

        public static StringBuffer buildSQL(){

            StringBuffer buffer = new StringBuffer("CREATE TABLE IF NOT EXISTS ");

            buffer.append(TABLE_NAME);
            buffer.append(" ( ");

            buffer.append(COLUMN_ID);
            buffer.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");

            buffer.append(COLUMN_PACKAGENAME);
            buffer.append(" TEXT NOT NULL DEFAULT '',");

            buffer.append(COLUMN_APP_NAME);
            buffer.append(" TEXT DEFAULT '',");

            buffer.append(COLUMN_APPLICATION_VERSION);
            buffer.append(" TEXT DEFAULT '',");

            buffer.append(COLUMN_CACHE_KEY);
            buffer.append(" TEXT NOT NULL DEFAULT '',");

            buffer.append(COLUMN_VIRUS_LIB_VERSION);
            buffer.append(" TEXT DEFAULT '',");

            buffer.append(COLUMN_SCAN_STATE);
            buffer.append(" INTEGER DEFAULT 0,");

            buffer.append(COLUMN_TTL);
            buffer.append(" TEXT DEFAULT '',");

            buffer.append(COLUMN_FROM);
            buffer.append(" INTEGER DEFAULT 0,");

            buffer.append(COLUMN_VIRUS_NAME);
            buffer.append(" TEXT DEFAULT '',");

            buffer.append(COLUM_TYPE);
            buffer.append(" TEXT DEFAULT '',");

            buffer.append(COLUM_RISK_LEVEL);
            buffer.append(" INTEGER DEFAULT -10001,");

            buffer.append(COLUM_TCL_HASH);
            buffer.append(" TEXT DEFAULT '',");

            buffer.append(COLUM_SUGGEST);
            buffer.append(" TEXT DEFAULT '',");

            buffer.append(COLUM_TCL_CLOUD_RESULT);
            buffer.append(" INTEGER DEFAULT -1,");

            buffer.append(COLUM_AVENGINE_CLOUD_RESULT);
            buffer.append(" INTEGER DEFAULT -1,");

            buffer.append(COLUM_CLOUD_CACHE_TIME);
            buffer.append(" TEXT DEFAULT '86400000'");

            buffer.append(")");

            return buffer;
        }


    }

    /***
     * 隐私表
     */
    public static final class PrivacyCacheColumns implements BaseColumns{

        //phoneguard
        public static final String AUTHORITY = "com.tct.phoneguard.provider.privacy";
        //clean
        //public static final String AUTHORITY = "com.hawk.space.provider.privacy";


        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/privacycaches");

        public static final String TABLE_NAME = "privacy_cache";

        public static final String COLUMN_ID = "_id";

        public static final String COLUMN_TYPE = "type";

        public static final String COLUMN_CONTENT = "content";

        public static final String COLUMN_TITLE = "title";

        public static StringBuffer buildSQL(){
            StringBuffer buffer = new StringBuffer("create table if not exists ");

            buffer.append(TABLE_NAME);
            buffer.append(" ( ");

            buffer.append(COLUMN_ID);
            buffer.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");

            buffer.append(COLUMN_TYPE);
            buffer.append(" INTEGER DEFAULT -1,");

            buffer.append(COLUMN_CONTENT);
            buffer.append(" TEXT NOT NULL DEFAULT '',");

            buffer.append(COLUMN_TITLE);
            buffer.append(" TEXT NOT NULL DEFAULT '')");



            return buffer;
        }
    }




}
