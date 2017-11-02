package com.clean.spaceplus.cleansdk.base.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.hu.andun7z.AndUn7z;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.io.IOException;

/**
 * @author shunyou.huang
 * @Description:
 * @date 2016/4/14 16:30
 * @copyright TCL-MIG
 */

public class DatabaseHelper {
    public static final String SELECT = " SELECT ";
    public static final String FROM = " FROM ";
    public static final String WHERE = " WHERE ";
    public static final String LIKE = " LIKE ";
    public static final String AND = " AND ";
    public static final String NOT = " NOT ";
    public static final String IN = " IN ";
    public static final String LEFT = " LEFT ";
    public static final String JOIN = " JOIN ";
    public static final String ON = " ON ";

    public static final String TABLE_NAME_PRIVACY 		    = "privacy";
    public static final String TABLE_NAME_PRIVACY_NEW       = "privacy_new";
    public static final String TABLE_NAME_SEARCHHISTORY 	= "searchhistory";
    public static final String TABLE_NAME_SOFTDETAIL 		= "softdetail";
    public static final String TABLE_NAME_CACHE1 			= "cache1";
    public static final String TABLE_NAME_CACHE2 			= "cache2";
    public static final String TABLE_NAME_PRIVACY_CACHE	= "privacycache";
    public static final String TABLE_NAME_SYS_CACHE 		= "syscache";
    public static final String TABLE_NAME_CACHE_W 		= "cachew";
    public static final String TABLE_NAME_APPSLOW 		= "appslow";
    public static final String TABLE_NAME_CACHE1_ITEMNAME	= "cache1_t_itemname";
    public static final String TABLE_NAME_CACHE1_PKGNAME	= "cache1_t_pkgname";
    public static final String TABLE_NAME_CACHE1_ALERTINFO	= "cache1_t_alertinfo";
    public static final String TABLE_NAME_CACHE1_DESC		= "cache1_t_desc";
    public static final String TABLE_NAME_CACHE1_FILEPATH	= "cache1_t_filepath";

    public static final String COL_VAL				= "val";
    public static final String COL_SRSID				= "srsid";
    public static final String COL_TIPS				= "tips";
    public static final String COL_PKGNAME			= "pkgname";
    public static final String COL_AUTHORITY			= "authority";
    public static final String COL_USELESS			= "useless";
    public static final String COL_SOFTENGLISHNAME	= "softEnglishname";
    public static final String COL_APKNAME			= "apkname";
    public static final String COL_FILEPATH			= "filepath";
    public static final String COL_STYPE				= "stype";
    public static final String COL__ID				= "_id";
    public static final String COL_ALERTINFO			= "alertInfo";
    public static final String COL_DESCRIBEINFO		= "describeinfo";
    public static final String COL_PATH				= "path";
    public static final String COL_ITEMNAME			= "itemname";
    public static final String COL_ALERT_INFO			= "alertinfo";
    public static final String COL_SUBPATH			= "subpath";
    public static final String COL_SL					= "sl";
    public static final String COL_DESC				= "desc";
    public static final String COL_TYPE				= "type";
    public static final String COL_REGPKGNAME			= "regpkgname";
    public static final String COL_ISDELETEDIR		= "isdeletedir";
    public static final String COL_CONTENT_TYPE		= "contenttype";
    // 隐私找垃圾文件的索引
    public static final String COL_CACHE_ID			= "cacheid";
    public static final String COL_TABLE_TAG			= "tabletag";
    public static final String COL_ORDERS				= "orders";
    // 隐私app data库
    public static final String COL_CHECK_TYPE       = "checktype" ;
    public static final String COL_PRI_PATH         = "path" ;
    public static final String COL_IS_PRIORITY      = "ispriority" ;

    public static final int TABLE_ID_CACHE1 			= 1;
    public static final int TABLE_ID_CACHE2 			= 2;
    public static final int TABLE_ID_SYS_CACHE 		= 3;
    public static final int TABLE_ID_HF 				= 4;
    public static final int TABLE_ID_APP_CACHE 		= 5;
    public static final int TABLE_ID_CLOUD 			= 6;
    private static final String TAG = "DatabaseHelper";

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "cache_hf_en_1.0.0.db";
    public static final String PROCESS_DB_NAME = "process.db";

    private static class SingletonHolder {
        public static final DatabaseHelper singleton = new DatabaseHelper();
    }

    public static final DatabaseHelper getInstance() {
        return SingletonHolder.singleton;
    }

    /**
     * 初始化数据库文件
     *
     * @param context
     */
    private static Object lock = new Object();
    public static boolean initDBFile(Context context){
        int tryCount = 0;
        boolean ret = false;
        while (tryCount < 3 && !ret) {
            synchronized (lock) {
                ret = copyAssertDBFile(context, ".db");
                if (!ret) {
                    tryCount++;
                    continue;
                }
                break;
            }
        }
        //return copyAssertDBFile(context, ".db");
        return ret;
    }

    /**
     * 拷贝Assert目录下所有的DB文件到database目录
     *
     * @param context
     * @param suffix
     */
    public static boolean copyAssertDBFile(Context context, String suffix) {
        boolean ret = false;

        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            ret = false;
        }
        if (files == null || files.length == 0){
            return false;
        }
        for (String filename : files) {
            if (filename.endsWith(".7z")) {
                String srcFile = filename;
                String dstFile = context.getDatabasePath(filename).getParent();//data/data/com.clean.spaceplus/databases/
                //如果所有文件都已经存在不用拷贝,则直接返回true
                ret = true;
                if (!needUpdateDatabase(dstFile+File.separator+srcFile.substring(0,srcFile.lastIndexOf(".7z")))) {
                    continue;
                }
                try{
                    //验证后发现:解压成功返回false，失败是true
                    boolean failed = AndUn7z.extractAssets(context, srcFile, dstFile);
                    ret = failed?false:true;
                    if (failed){
                        break;
                    }
                }catch (Exception e){
                    ret = false;
                    NLog.printStackTrace(e);
                }catch (Error e){
                    ret = false;
                    NLog.printStackTrace(e);
                }
//                FileUtils.copyAssertFile(assetManager, srcFile, dstFile);
            }
        }
        return ret;
    }

    /**
     * 是否需要更新拷贝数据库文件
     *
     * @param dbName
     * @return
     */
    private static boolean needUpdateDatabase(String dbName) {
        boolean ret = false;
        File file = FileUtils.checkPath(dbName);
        if (null == file) {
            ret = true;
        }
        return ret;
    }

//    /**
//     * 获取字符串库
//     * @param name
//     * @return
//     */
//    public static String getStringDbPathByTableName(String name) {
//        return "";
//    }
//
//    /**
//     * 获取Assert DB升级信息
//     * @param fileName
//     * @return
//     */
//    public static List<UpdateDbBean.DataBean> getUpdateDbList(String fileName) {
//
//        AssetManager assertManager = SpaceApplication.getInstance().getContext().getAssets();
//        if (assertManager != null) {
//            try {
//                InputStream inputStream = null;
//                inputStream = assertManager.open(fileName);
//                int size = inputStream.available();
//                byte buffer[] = new byte[size];
//                inputStream.read(buffer);
//                inputStream.close();
//                String json = new String(buffer);
//                Gson gson = new Gson();
//                UpdateDbBean updateBen = gson.fromJson(json, UpdateDbBean.class);
//                return updateBen.data;
//            } catch (Exception e) {
//                NLog.printStackTrace(e);
//            }
//        }
//
//        return null;
//    }

    /**
     * 打开数据库
     * @param path
     * @return
     */
    public static SQLiteDatabase OpenDatabaseProperly(String path){
        SQLiteDatabase db = null;

        try{
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        }
        catch (SQLException e) {
            db = null;
        } catch (Exception e) {
            return null;
        }

        if ( db == null ){
            try{
                //表明不能用open_readonly 的方式打开成功， 所以， 我们应该让db尽量全量升级
                ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).setDbUpdaetIsNeedFull(true);
                db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            }
            catch (SQLException e) {
                db = null;
            } catch (Exception e) {
                return null;
            }
        }
        return db;
    }
}
