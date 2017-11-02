package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.local.regular;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.MySQLiteDB;
import com.clean.spaceplus.cleansdk.base.db.SqlUtil;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.LangqueryalertTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfDirQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfLangQueryNameTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfRegDirQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.ResidualDirHfProvider;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudGlue;
import com.clean.spaceplus.cleansdk.junk.engine.FalseFilterFactory;
import com.clean.spaceplus.cleansdk.junk.engine.FalseFilterManager;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.commondata.KCleanCloudEnv;
import space.network.util.KMiscUtils;
import space.network.util.hash.KQueryMd5Util;
import space.network.util.net.KJsonUtils;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/21 13:52
 * @copyright TCL-MIG
 */
public class KResudialRegularLocalQuery {

    public static final String TAG = KResudialRegularLocalQuery.class.getSimpleName();
    private volatile long mCurrentTime;
    private String mDefaultLanguage = "en";
    private String mLanguage = "en";

    //init db 前增加计数，不使用db时减少计数
    //结合最近访问db的时间进行智能释放db
    private AtomicInteger mAccessCount = new AtomicInteger();


    private ReentrantLock mDirFilterLock = new ReentrantLock();
    private volatile FalseFilterManager.FalseSignFilter mDirFilter = null;

    /**
     * 正则路径特征的数据信息；用户在扫描时，内存存放正则路径表的数据；以增加比较速度；
     * @author 
     * @date 2014.12.05
     * */
    private static class RegularPathData{
        public String mRegPath;
        public String mDirId;
        public String mRegPkg;
        public Pattern mDirPattern;
    }
    private HashMap<String, RegularPathData> mRegularPathDatas;
    private ResidualDirHfProvider mPkgQueryHfProvider;

    public KResudialRegularLocalQuery(Context context, CleanCloudGlue cleanCloudGlue) {
        mCurrentTime = System.currentTimeMillis();
        mPkgQueryHfProvider = ResidualDirHfProvider.getInstance();
    }

    public void unInitDb() {

    }

    public boolean tryUnInitDb() {
        boolean result = false;
        int accessCount = mAccessCount.get();
        if (accessCount > 0) {
            return result;
        }

        unInitDb();
        result = true;
        return result;
    }

    public boolean setLanguage(String language) {
        if (TextUtils.isEmpty(language))
            return false;
        mLanguage = KMiscUtils.toSupportedLanguage(language);

        return true;
    }

    public String getLanguage(){
        return mLanguage;
    }

    /**
     * @return
     */
///<DEAD CODE>///     public String getDefaultLanguage() {
//        return mDefaultLanguage;
//    }

    private void setCurrentTime(long time) {
        mCurrentTime = time;
    }

///<DEAD CODE>///     private long getCurrentTime() {
//        return mCurrentTime;
//    }

    /**
     * @date 2014.12.04
     * */
    void handleSQLiteDatabaseIllegalStateException(IllegalStateException e,
                                                   MySQLiteDB dbHolder,
                                                   MySQLiteDB.MyDBData dbData,
                                                   boolean isHFDb,
                                                   CleanCloudGlue cleanCloudGlue) {
       /* String msg = e.getMessage();
        if (msg.contains("database not open")
                || (msg.contains("re-open") && msg.contains("SQLiteDatabase"))
                || (msg.contains("database") && msg.contains("already closed"))) {

            KCleanCloudCommonError err = new KCleanCloudCommonError();
            err.mytype = KCleanCloudCommonError.MyType_Residual_Sqlite_IllegalStateException;
            if (isHFDb) {
                err.sub_type = KCleanCloudCommonError.SubType_Residual_Sqlite_IllegalStateException_HFDB;
            } else {
                err.sub_type = KCleanCloudCommonError.SubType_Residual_Sqlite_IllegalStateException_CACHEDB;
            }
            err.setExceptionMsgToDetailMsg(e);
            err.reportToServer(cleanCloudGlue);

            if (dbData != null) {
                dbHolder.releaseReference(dbData);
                dbHolder.unInitDb();
            }
        } else {
            throw e;
        }*/
    }

    /**
     * 路径进行正则匹配，然后查询残留数据;
     * @author 
     * @date 2014.12.04
     * */
    public boolean queryByDir(int queryId, Collection<KResidualCloudQuery.DirQueryData> results) {
        return queryByDir(queryId, results, true, mLanguage);
    }

    private boolean queryByDir(int queryId, Collection<KResidualCloudQuery.DirQueryData> results, boolean getShowInfo, String language) {
        if (null == results || results.isEmpty())
            return false;

        long start = System.currentTimeMillis();
        long end = 0;
        for (KResidualCloudQuery.DirQueryData result : results) {
            result.mErrorCode = 0;
            result.mResultSource = KResidualCloudQuery.ResultSourceType.INVAILD;
            result.mResult.mQueryResult = KResidualCloudQuery.DirResultType.UNKNOWN;
        }

        mAccessCount.incrementAndGet();

        initDirFalseFilter();

        setCurrentTime(System.currentTimeMillis());

      /*  try {
            highFrequentDb = mHighFreqDbHolder.getDatabaseAndAcquireReference();
            if (highFrequentDb != null) {
                if(initRegularDirData(highFrequentDb.mDb)){
                    queryRegDirByHighFreqDb(highFrequentDb.mDb, results, getShowInfo, language);
                    end = System.currentTimeMillis();
                    CMPushLog.getLogInstance().log("Local query finished: query id = "+queryId+", start time = "+start+", end time = "+end+", timecost = "+(end - start));
                }
                mHighFreqDbHolder.releaseReference(highFrequentDb);
                highFrequentDb = null;
            }
        } catch (IllegalStateException e) {
            handleSQLiteDatabaseIllegalStateException(e, mHighFreqDbHolder, highFrequentDb, true, mHighFreqDbHolder.getCleanCloudGlue());
        }*/
        SQLiteDatabase mDb = mPkgQueryHfProvider.getDatabase();
        try {
            if(initRegularDirData(mDb)){
                queryRegDirByHighFreqDb(mDb, results, getShowInfo, language);
                end = System.currentTimeMillis();
            }


        } catch (Exception e) {
        }



        mAccessCount.decrementAndGet();
        return true;
    }

    /**
     * 初始化残留路径的误报数据;
     * @author 
     * @date 2014.12.05
     * */
    private void initDirFalseFilter() {
        if (null != mDirFilter) {
            return;
        }
        mDirFilterLock.lock();
        try {
            if (null == mDirFilter) {
                mDirFilter = FalseFilterFactory
                        .getFalseFilterManagerInstance()
                        .getFalseDataByCategory(FalseFilterManager.CategoryKey.KEY_RESIDUAL_DIR);
            }
            if (null != mDirFilter) {
                mDirFilter.acquireReference();
            }
        } finally {
            mDirFilterLock.unlock();
        }
    }

    /**
     * 从数据库读取正则路径特征数据，加速运算；
     * @return :是否可以进行正则路径检索;
     * */
    private boolean initRegularDirData(SQLiteDatabase db){
        if(db == null){
            return false;
        }

        if(mRegularPathDatas == null){
            mRegularPathDatas = new HashMap<>();
        }else{
            return !mRegularPathDatas.isEmpty();
        }

        //String sql = "select regdir, dirid, repkgs from regdirquery";

        String sql = String.format("select %s, %s, %s from %s",
                 PkgQueryHfRegDirQueryTable.REGDIR,PkgQueryHfRegDirQueryTable.DIRID,PkgQueryHfRegDirQueryTable.REPKGS,PkgQueryHfRegDirQueryTable.TABLE_NAME);
        NLog.d(TAG, "initRegularDirData sql = %s", sql);
        Cursor cursorSign = null;
        try{
            cursorSign = db.rawQuery(sql, null);
            if (cursorSign == null || cursorSign.getCount() == 0) {
                return false;
            }

            while (cursorSign.moveToNext()) {
                RegularPathData regData = new RegularPathData();
                String dirid = String.valueOf(cursorSign.getInt(1));
                regData.mRegPath = cursorSign.getString(0);
                regData.mDirId = dirid;
                regData.mRegPkg = cursorSign.getString(2);
                regData.mDirPattern = Pattern.compile(regData.mRegPath);
                mRegularPathDatas.put(dirid, regData);
            }
        }catch(Exception e){
            mRegularPathDatas.clear();
        } finally {
            if (cursorSign != null) {
                cursorSign.close();
            }
        }

        return !mRegularPathDatas.isEmpty();
    }

    /**
     * 在高频库检索数据;
     * */
    private int queryRegDirByHighFreqDb(SQLiteDatabase db, Collection<KResidualCloudQuery.DirQueryData> results,  boolean isGetShowInfo, String language) {
        int sizeResult = 0;
        HashMap<String, KResidualCloudQuery.DirQueryData> diridQueryDataMap = new HashMap<>(
                results.size());
        HashMap<String, String> pathDirIdMap = new HashMap<>();
        /**
         * 对输入目录进行正则匹配;
         * */
        for (KResidualCloudQuery.DirQueryData result : results) {
            if (result.mResultSource == KResidualCloudQuery.ResultSourceType.INVAILD
                    || result.mResult.mQueryResult == KResidualCloudQuery.DirResultType.UNKNOWN) {

                String path = ((KResidualRegularCloudQuery.RegularDirQueryInnerData) result.mInnerData).mDirName;
                String dirid = matchPathWithRegular(path);
                if(!TextUtils.isEmpty(dirid)){
                    String regpkg = mRegularPathDatas.get(dirid).mRegPkg;
                    if(result.mResult.mPackageRegexs == null){
                        result.mResult.mPackageRegexs = new ArrayList<>();
                    }
                    if(regpkg != null){
                        result.mResult.mPackageRegexs.add(regpkg);
                    }
                    pathDirIdMap.put(path, dirid);
                    diridQueryDataMap.put(path, result);
                }
            }
        }

        // take the dirid which should be key query from db.
        Collection<String> dirids =  pathDirIdMap.values();
        if(dirids == null || dirids.isEmpty()){
            return sizeResult;
        }

        // prepare query from db.
        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum  = 0;
        //String sql = "select dirid,queryresult,cleantype,contenttype,cmtype,langnamealert,test,subdirs from dirquery where dirid in ";

        String[] selection = new String[]{
                PkgQueryHfDirQueryTable.DIRID,PkgQueryHfDirQueryTable.QUERYRESULT,PkgQueryHfDirQueryTable.CLEANTYPE,
                PkgQueryHfDirQueryTable.CONTENTTYPE,PkgQueryHfDirQueryTable.CMTYPE,PkgQueryHfDirQueryTable.LANGNAMEALERT,
                PkgQueryHfDirQueryTable.TEST,PkgQueryHfDirQueryTable.SUBDIRS
        };
        String sql = SqlUtil.appendSqlString(PkgQueryHfDirQueryTable.TABLE_NAME,selection) +" where " + PkgQueryHfDirQueryTable.DIRID +" in ";
        String r;
        Cursor cursorSign = null;
        HashMap<String, KResidualCloudQuery.DirQueryData> diridDbDataMap = new HashMap<String, KResidualCloudQuery.DirQueryData>(
                dirids.size());
        ArrayList<KResidualCloudQuery.DirQueryData> toGetShowInfoResults = null;
        if (isGetShowInfo) {
            toGetShowInfoResults = new ArrayList<>(dirids.size());
        }

        // collection data info from db.
        while ((r = SqlUtil.collectionToSQLInStringIncreasing(
                dirids, pageSize, pageNum++, false)) != null) {
            try {
                NLog.d(TAG, "queryRegDirByHighFreqDb exec sql = %s", sql + r);
                cursorSign = db.rawQuery(sql + r, null);
                if (cursorSign == null || cursorSign.getCount() == 0) {
                    continue;
                }

                while (cursorSign.moveToNext()) {
                    String dirid = cursorSign.getString(0);
                    KResidualCloudQuery.DirQueryData dbData = KResidualRegularCloudQueryHelper.getDirQueryDatas("temp", mLanguage);
                    if (dbData != null && fillDirQueryDataByHighFreqDb(db, cursorSign, dbData)) {
                        diridDbDataMap.put(dirid, dbData);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorSign != null) {
                    cursorSign.close();
                    cursorSign = null;
                }
            }
        }

        // fill db data to result for each path;
        for (String path : pathDirIdMap.keySet()){
            if(TextUtils.isEmpty(path)){
                continue;
            }
            String dirid = pathDirIdMap.get(path);
            KResidualCloudQuery.DirQueryData dbData = diridDbDataMap.get(dirid);
            if(dbData != null){
                KResidualCloudQuery.DirQueryData result = diridQueryDataMap.get(path);
                if(result != null){
                    // copy dbData info to result pkgName.
                    copyDirQueryData(dbData, result);

                    // get regular group info.
                    RegularPathData regularData = mRegularPathDatas.get(dirid);
                    getRegularMatchGroup((KResidualRegularCloudQuery.RegularDirQueryInnerData)result.mInnerData, regularData);
                    if (KResidualCloudQuery.DirQueryResultUtil.isHavePackageList(result.mResult) && isGetShowInfo) {
                        toGetShowInfoResults.add(result);
                    }
                    ++sizeResult;
                }
            }
        }

        // get show info data.
        if (isGetShowInfo && toGetShowInfoResults != null) {
            getShowInfoFromHighFreqDb(db, toGetShowInfoResults, language);
        }
        return sizeResult;
    }

    /**
     * 将路径与正则式匹配，返回匹配结果；
     * @return ：返回匹配到的正则路径对应的dirid.
     * @author 
     * @date 2014.12.05
     * */
    private String matchPathWithRegular(String path){
        if (mRegularPathDatas == null){
            return null;
        }

        for(String dirid : mRegularPathDatas.keySet()){
            RegularPathData data = mRegularPathDatas.get(dirid);
            Matcher matcher = data.mDirPattern.matcher(path);
            if(matcher != null && matcher.matches()){
                return data.mDirId;
            }
        }

        return null;
    }

    /**
     * 获得一个String，相对于一个正则表达式，正则匹配的部分;
     * @author 
     * @date 2014.12.05
     * */
    private String getRegularMatchGroup(KResidualRegularCloudQuery.RegularDirQueryInnerData innerData, RegularPathData regularData){
        if(regularData == null || innerData == null || innerData.mDirName == null){
            return null;
        }

        Matcher m = regularData.mDirPattern.matcher(innerData.mDirName);
        while (m.find()){
            int count = m.groupCount();
            if(count >= 1){
                innerData.mRegularGroup = m.group(1);
            }
            break;
        }

        return null;
    }

    /**
     * 索引数据，填充查询结果;
     * @author 
     * @date 2014.12.05
     * */
    private boolean fillDirQueryDataByHighFreqDb(SQLiteDatabase db, Cursor cursorSign, KResidualCloudQuery.DirQueryData result) {
        boolean ret = false;
        int  dirid = 0;
        int  queryResult = 0;
        int  cleanType = 0;
        int  contenttype = 0;
        int  cmtype = 0;
        int testFlag = 0;
        boolean integrityOk = false;

        ArrayList<String> filterDirIds = null;
        ArrayList<KResidualCloudQuery.FilterDirData> filterSubDirDatas = null;
        String namealert;

        integrityOk = true;
        dirid       = cursorSign.getInt(0);
        queryResult = cursorSign.getInt(1);
        cleanType   = cursorSign.getInt(2);
        contenttype = cursorSign.getInt(3);
        cmtype      = cursorSign.getInt(4);
        namealert = cursorSign.getString(5);
        testFlag	= cursorSign.getInt(6);
        FalseFilterManager.FalseSignFilter filter = this.mDirFilter;
        if (filter!= null && filter.filter(dirid)) {
            return false;
        }

        if (!cursorSign.isNull(7)) {
            String strDirIs = cursorSign.getString(7);
            filterDirIds = KJsonUtils.getStringArrayFromNoBracketJsonArrayString(strDirIs);
            if (filterDirIds != null) {
                filterSubDirDatas = getFilterDirDataFromDirIds(db, filterDirIds);

                //数据完整性检查
                if (filterSubDirDatas == null || filterSubDirDatas.size() != filterDirIds.size()) {
                    integrityOk = false;
                }
            }
        }
        if (integrityOk) {
            result.mResultSource        = KResidualCloudQuery.ResultSourceType.HFREQ;
            result.mResult.mSignId      = dirid;
            result.mResult.mQueryResult = queryResult;
            result.mResult.mCleanType   = cleanType;
            result.mResult.mNameAlert   = namealert;
            result.mResultExpired       = false;

            result.mResult.mDirs = null;
            result.mResult.mPkgsMD5High64 = null;
            result.mResult.mContentType = contenttype;
            result.mResult.mCleanMediaFlag = cmtype;
            result.mResult.mTestFlag = testFlag;
            ((KResidualRegularCloudQuery.RegularDirQueryInnerData)result.mInnerData).mFilterSubDirDatas = filterSubDirDatas;
            ret = true;
        }
        return ret;
    }

    /**
     * 将一个对象的信息，拷贝给另外一个对象;
     * @author 
     * @date 2014.12.11
     * */
    private void copyDirQueryData(KResidualCloudQuery.DirQueryData src, KResidualCloudQuery.DirQueryData des){
        if(src != null && des != null){
            des.mResultSource        = src.mResultSource;
            des.mResult.mSignId      = src.mResult.mSignId;
            des.mResult.mQueryResult = src.mResult.mQueryResult;
            des.mResult.mCleanType   = src.mResult.mCleanType;
            des.mResult.mNameAlert   = src.mResult.mNameAlert;
            des.mResultExpired       = src.mResultExpired;

            des.mResult.mDirs = null;
            des.mResult.mPkgsMD5High64 = null;
            des.mResult.mContentType = src.mResult.mContentType;
            des.mResult.mCleanMediaFlag = src.mResult.mCleanMediaFlag;
            des.mResult.mTestFlag = src.mResult.mTestFlag;
            ((KResidualRegularCloudQuery.RegularDirQueryInnerData)des.mInnerData).mFilterSubDirDatas =
                    ((KResidualRegularCloudQuery.RegularDirQueryInnerData)src.mInnerData).mFilterSubDirDatas;
        }
    }

    /**
     * 获取子路径的精细化数据;
     * */
    private ArrayList<KResidualCloudQuery.FilterDirData> getFilterDirDataFromDirIds(SQLiteDatabase db, Collection<String> dirids) {
        ArrayList<KResidualCloudQuery.FilterDirData> datas
                = new ArrayList<KResidualCloudQuery.FilterDirData>(dirids.size());
        StringBuilder builder = new StringBuilder();
        //builder.append("select dir,dirid,cleantype from dirquery where dirid in ");
        StringBuffer buffer = new StringBuffer();
        String[] selection = new String[]{PkgQueryHfDirQueryTable.DIR,PkgQueryHfDirQueryTable.DIRID};
        buffer.append(SqlUtil.appendSqlString(PkgQueryHfDirQueryTable.TABLE_NAME,selection) +" where " + PkgQueryHfDirQueryTable.DIRID +" in ");

        appendSqlInExpString(builder, dirids);
        String sql = builder.toString();
        String dir = null;
        int dirId = 0;
        int cleanType = 0;
        Cursor cursorDir = null;
        try {
            cursorDir = db.rawQuery(sql, null);
            if (cursorDir != null && cursorDir.getCount() > 0) {
                while (cursorDir.moveToNext()) {
                    dir = KQueryMd5Util.getPathStringFromBytes(cursorDir.getBlob(0));
                    dirId = cursorDir.getInt(1);
                    cleanType = cursorDir.getInt(2);
                    if (!TextUtils.isEmpty(dir)) {
                        KResidualCloudQuery.FilterDirData data = new KResidualCloudQuery.FilterDirData();
                        data.mSingId = dirId;
                        data.mPath = dir;
                        data.mCleanType = cleanType;
                        datas.add(data);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorDir != null) {
                cursorDir.close();
            }
        }
        return datas;
    }

    private void appendSqlInExpString(StringBuilder builder, Collection<String> strs) {
        if (strs == null || strs.isEmpty())
            return ;

        int i = 0;
        builder.append("(");
        for (String str : strs) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(str);
            ++i;
        }
        builder.append(")");
    }

    static class ParseLangData {
        public int	  dirid;
        public String langnamealert;
        public String nameid;
        public String alertinfoid;
        public KResidualCloudQuery.DirQueryData result;
    }

    private boolean getShowInfoFromHighFreqDb(SQLiteDatabase db, ArrayList<KResidualCloudQuery.DirQueryData> toGetShowInfoResults, String language) {
        boolean ret = false;
        ArrayList<ParseLangData> parseDatas = new ArrayList<ParseLangData>(toGetShowInfoResults.size());

        for(KResidualCloudQuery.DirQueryData result : toGetShowInfoResults){
            int key = result.mResult.mSignId;
            ParseLangData data = new ParseLangData();
            data.result = result;
            data.dirid  =  key;

            if (!ret) {
                ret = true;
            }
            if (null == result.mResult.mShowInfo) {
                result.mResult.mShowInfo = new KResidualCloudQuery.ShowInfo();
            }

            if(!TextUtils.isEmpty(result.mResult.mNameAlert)){
                data.langnamealert = result.mResult.mNameAlert;
            }

            parseDatas.add(data);
            parseLangNameAlertString(data, language);
        }

        fillShowInfoDatas(
                parseDatas,
                getShowInfoNames(db, parseDatas),
                getShowInfoAlertInfos(db, parseDatas));

        return ret;
    }

    private void fillShowInfoDatas(ArrayList<ParseLangData> parseDatas, HashMap<String, String> names, HashMap<String, String> alertInfos) {
        for (ParseLangData data : parseDatas) {
            if (names != null && !TextUtils.isEmpty(data.nameid)) {
                data.result.mResult.mShowInfo.mName = names.get(data.nameid);
            } else {
                data.result.mResult.mShowInfo.mName = "";
            }
            if (alertInfos != null && !TextUtils.isEmpty(data.alertinfoid)) {
                data.result.mResult.mShowInfo.mAlertInfo = alertInfos.get(data.alertinfoid);
            } else {
                data.result.mResult.mShowInfo.mAlertInfo = "";
            }
        }
    }

    private boolean parseLangNameAlertString(ParseLangData data, String language) {
        if (TextUtils.isEmpty(data.langnamealert))
            return false;

        String[] langSetting = null;
        if (language.equalsIgnoreCase(KMiscUtils.LANG_EN)) {
            langSetting = new String[1];
            langSetting[0] = KMiscUtils.LANG_EN;
        } else if (language.equalsIgnoreCase(KMiscUtils.LANG_TW)) {
            langSetting = new String[2];
            langSetting[0] = KMiscUtils.LANG_TW;
            langSetting[1] = KMiscUtils.LANG_CN;
        } else {
            langSetting = new String[2];
            langSetting[0] = mLanguage;
            langSetting[1] = KMiscUtils.LANG_EN;
        }

        String[] parseResult = null;
        for (String lang : langSetting) {
            if (TextUtils.isEmpty(lang))
                continue;

            int pos = data.langnamealert.indexOf(lang);
            if (pos == -1)
                continue;

            int pos2 = data.langnamealert.indexOf(':', pos);
            if (pos2 == -1)
                continue;

            parseResult = parseShowInfoContent(data.langnamealert, pos2+1, 2);

            if (parseResult != null)
                break;
        }

        if (parseResult != null) {
            data.nameid      = parseResult[0];
            data.alertinfoid = parseResult[1];
        }
        return true;
    }

    private String[] parseShowInfoContent(String str, int start, int targetCount) {
        String[] result = new String[targetCount];
        int len = str.length();
        int cnt = 0;
        char c;
        StringBuilder sb = new StringBuilder(10);
        boolean isStop = false;
        for (int i = start;
             i < len && cnt < targetCount && !isStop;
             ++i) {
            c = str.charAt(i);
            switch (c) {
                case ',':
                case '|':
                    result[cnt++] = sb.toString();
                    sb.delete(0, sb.length());
                    if (c == '|') {
                        isStop = true;
                    }
                    break;
                default:
                    sb.append(c);
            }
        }
        if (cnt < targetCount){
            result[cnt++] = sb.toString();
        }
        return result;
    }

    private HashMap<String, String> getShowInfoNames(SQLiteDatabase db, ArrayList<ParseLangData> parseDatas) {
        HashMap<String, String> result = new HashMap<>();
        HashSet<String> ids = new HashSet<>();
        for(ParseLangData data : parseDatas) {
            if (!TextUtils.isEmpty(data.nameid)) {
                ids.add(data.nameid);
            }
        }

        if (ids.isEmpty())
            return result;

        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum  = 0;
        //String sql = "select _id,name from langqueryname where _id in ";

        String[] selection = new String[]{PkgQueryHfLangQueryNameTable._ID, PkgQueryHfLangQueryNameTable.NAME};
        String sql = SqlUtil.appendSqlString(PkgQueryHfLangQueryNameTable.TABLE_NAME,selection) +" where " + PkgQueryHfLangQueryNameTable._ID +" in ";

        String r;
        Cursor cursorSign = null;
        while ((r = SqlUtil.collectionToSQLInStringIncreasing(
                ids, pageSize, pageNum++)) != null) {
            try {
                cursorSign = db.rawQuery(sql + r, null);
                if (cursorSign == null || cursorSign.getCount() == 0) {
                    continue;
                }

                while (cursorSign.moveToNext()) {
                    int key = cursorSign.getInt(0);
                    String value = cursorSign.getString(1);
                    result.put(String.valueOf(key), value);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorSign != null) {
                    cursorSign.close();
                    cursorSign = null;
                }
            }
        }
        return result;
    }

    private HashMap<String, String> getShowInfoAlertInfos(SQLiteDatabase db, ArrayList<ParseLangData> parseDatas) {
        HashMap<String, String> result = new HashMap<String, String>();
        HashSet<String> ids = new HashSet<String>();
        for(ParseLangData data : parseDatas) {
            if (!TextUtils.isEmpty(data.alertinfoid)) {
                ids.add(data.alertinfoid);
            }
        }

        if (ids.isEmpty())
            return result;

        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum  = 0;
        //String sql = "select _id,alert from langqueryalert where _id in ";

        String[] selection = new String[]{LangqueryalertTable._ID, LangqueryalertTable.ALERT};
        String sql = SqlUtil.appendSqlString(LangqueryalertTable.TABLE_NAME,selection) +" where " + LangqueryalertTable._ID +" in ";

        String r;
        Cursor cursorSign = null;
        while ((r = SqlUtil.collectionToSQLInStringIncreasing(
                ids, pageSize, pageNum++)) != null) {
            try {
                NLog.d(TAG, "KResudialRegularLocalQuery getShowInfoAlertInfos exec sql = %s", sql + r);
                cursorSign = db.rawQuery(sql + r, null);
                if (cursorSign == null || cursorSign.getCount() == 0) {
                    continue;
                }

                while (cursorSign.moveToNext()) {
                    int key = cursorSign.getInt(0);
                    String value = cursorSign.getString(1);
                    result.put(String.valueOf(key), value);
                }

            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorSign != null) {
                    cursorSign.close();
                    cursorSign = null;
                }
            }
        }
        return result;
    }
}
