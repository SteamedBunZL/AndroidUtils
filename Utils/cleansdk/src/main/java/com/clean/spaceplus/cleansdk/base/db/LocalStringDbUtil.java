package com.clean.spaceplus.cleansdk.base.db;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.config.CommonConfigSharedPref;
import com.clean.spaceplus.cleansdk.base.db.strings2_cache.AdvfolderDescribeinfoTable;
import com.clean.spaceplus.cleansdk.base.db.strings2_cache.Strings2CacheProvider;
import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.clean.spaceplus.cleansdk.util.bean.LanguageCountry;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.HashMap;


/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/28 14:35
 * @copyright TCL-MIG
 */
public class LocalStringDbUtil {
    private static String TAG = LocalStringDbUtil.class.getSimpleName();
    private static LocalStringDbUtil instance = new LocalStringDbUtil();
//    private Map<String, SQLiteDatabase> mDBMap = new HashMap<String, SQLiteDatabase>();

    private LocalStringDbUtil(){}

    public class LangStr {
        public String primaryStr;
        public String secondaryStr;
    }

    public static LocalStringDbUtil getInstance(){
        return instance;
    }

    private static String SUPPORTED_LANG[] = new String[] {
            "ar"
            , "de"
            , "el"
            , "es"
            , "fr"
            , "hu"
            , "in"
            , "it"
            , "iw"
            , "ja"
            , "ko"
            , "nb"
            , "nl"
            , "pl"
            , "pt"
            , "ru"
            , "sk"
            , "en"
            , "th"
            , "tr"
            , "uk"
            , "vi"  } ;


//    public static final String[] PROJECTION_STRINGS = new String[] {"value"};

    /**
     * TODO: 增加个缓存哇
     * @param lang
     * @return
     */
    private static String getSupportedLanguage(final String lang) {
        for(String x : SUPPORTED_LANG) {
            if(x.equalsIgnoreCase(lang)) {
                return x;
            }
        }
        return "en";
    }

//    public  void closeAllDB(){
//        synchronized (TAG) {
//            Collection<SQLiteDatabase> c = mDBMap.values();
//            for(SQLiteDatabase link : c){
//                if(link != null  &&  link.isOpen()){
//                    link.close();
//                }
//            }
//        }
//    }



    /**
     * 检测当前的语言环境下是否已经得到本地化支持
     * @param context
     * @return
     */
    private static String getProperLanguage(Context context) {
        LanguageCountry language = CommonConfigSharedPref.getInstanse(context).getLanguageSelected(context);
        String lang = null;

        lang = language.getLanguage();
        if("zh".equals(lang)) {
            if(language.getCountry().equals("TW")) {
                lang+="_TW";
            } else if(language.getCountry().equals("CN")) {
                lang+="_CN";
            }
        } else {
            lang = getSupportedLanguage(lang);
        }

        return lang;
    }

    public  interface SrsidCheckCallback {
        void existSrsid(boolean exist);

        class Stub implements SrsidCheckCallback{
            public boolean exists() {
                return mExist;
            }

            @Override
            public void existSrsid(boolean exist) {
                mExist = exist;
            }

            private boolean mExist = false;
        }
    }

    /**
     * 查询一个数据库字符串列的本地化字符串资源，如果查不到，则返回defaultStringData。
     * 注意：云端查询接入以后， 如非必要，请不要直接调用本函数，本函数不包含云端查询。
     * 请从Commons.getLocalStringResourceOfDatabaseStringData()调用，以包含云端数据查询。
     */
    public  String getLocalStringResourceOfDatabaseStringDataWithCacheDB(
            String tableName,
            String columnName,
            int    stringResourceId,
            String defaultStringData,
            SrsidCheckCallback cb) {
        return "";
    }

    /**
     * Get all the current language resource string from DB
     *
     * @return A map list of resource string with ID as key
     */
    public HashMap<String, LangStr> getAllLocalStringFromDB(Strings2CacheProvider provider) {
        //todo 数据库strings2_cache表中读取语言数据
        HashMap<String, LangStr> localStringMap = null;
        String lang = getProperLanguage(SpaceApplication.getInstance().getContext());
        synchronized (TAG) {
            Cursor cursor = null;
            try {
                //String sql = "select id, value from advfolder_describeinfo where lang = \'" + lang + "\'";

                String[] selection = new String[]{AdvfolderDescribeinfoTable.ID,AdvfolderDescribeinfoTable.VALUE};
                String sql = String.format(SqlUtil.appendSqlString(AdvfolderDescribeinfoTable.TABLE_NAME,selection) +" where "+ AdvfolderDescribeinfoTable.LANG +" = '%s'", lang);
                NLog.d(TAG, "getAllLocalStringFromDB sql = "+ sql);
                if (provider == null) {
                    NLog.e(TAG, "数据库还未初始化完成");
                    return null;
                }
                cursor = provider.rawQuery(sql, null);
                if (cursor != null && cursor.getCount() > 0) {
                    NLog.d(TAG, "cursor .getCount() = %d", cursor.getCount());
                    if (null == localStringMap) {
                        localStringMap = new HashMap<>();
                    }
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String result = cursor.getString(cursor.getColumnIndex(AdvfolderDescribeinfoTable.VALUE));
                            String id = Integer.valueOf(cursor.getInt(cursor.getColumnIndex(AdvfolderDescribeinfoTable.ID))).toString();
                            if (!TextUtils.isEmpty(result) &&
                                    !TextUtils.isEmpty(id)) {
                                LangStr tmpStr = new LangStr();
                                tmpStr.primaryStr = result;
                                localStringMap.put(id, tmpStr);
                            }
                            cursor.moveToNext();
                        }
                    }
                }

            } catch (Exception e) {
                NLog.printStackTrace(e);
            } finally {
                IOUtils.closeSilently(cursor);
            }

            try {
                if (lang.equals("zh_TW")) {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                    //String newSql = "select id, value from advfolder_describeinfo where lang =  " + " \'zh_CN\'";
                    String newSql = String.format("select "+ AdvfolderDescribeinfoTable.ID +", "+AdvfolderDescribeinfoTable.VALUE +" from "
                            +AdvfolderDescribeinfoTable.TABLE_NAME +" where "+ AdvfolderDescribeinfoTable.LANG +" = \'"+"%s\'", "zh_CN");
                    cursor = provider.rawQuery(newSql, null);

                    if (cursor != null && cursor.getCount() > 0) {
                        if (null == localStringMap) {
                            localStringMap = new HashMap<>();
                        }
                        if (cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {
                                String result = cursor.getString(cursor.getColumnIndex("value"));
                                String id = Integer.valueOf(cursor.getInt(cursor.getColumnIndex("id"))).toString();
                                if (!TextUtils.isEmpty(result) &&
                                        !TextUtils.isEmpty(id)) {
                                    LangStr origLangStr = localStringMap.get(id);
                                    if (null != origLangStr) {
                                        origLangStr.secondaryStr = result;
                                    } else {
                                        LangStr newLangStr = new LangStr();
                                        newLangStr.primaryStr = result;

                                        localStringMap.put(id, newLangStr);
                                    }
                                }
                                cursor.moveToNext();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                NLog.printStackTrace(e);
            } finally {
                IOUtils.closeSilently(cursor);
            }
        }
        return localStringMap;
    }



//    private boolean isSrsidExist(SQLiteDatabase db, String table, int stringResourceId) {
//
//        assert(null != db);
//        assert(!TextUtils.isEmpty(table));
//
//        boolean exist = false;
//        Cursor cursor = null;
//        try {
//            cursor = db.query(table, new String[] {"_id"}, "id=?",
//                    new String[] { String.valueOf(stringResourceId) },
//                    null, null, null);
//            if (cursor != null && cursor.getCount() > 0) {
//                exist = true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        return exist;
//    }

    public static interface LocalStringCheckCloudCallback {
        /**
         * 查询结果
         * @param str 查到的本地化字符串或传入的默认串
         */
        public void onResultString(String str);

        public static class Base implements LocalStringCheckCloudCallback {

            public Base(String defString) {
                mRst = defString;
            }

            @Override
            public void onResultString(String str) {
                mRst = str;
            }

            public String getResultString() {
                return mRst;
            }

            protected String mRst;
        }
    }




}
