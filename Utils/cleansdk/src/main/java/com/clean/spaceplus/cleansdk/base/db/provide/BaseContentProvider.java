package com.clean.spaceplus.cleansdk.base.db.provide;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;

import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/24 10:12
 * @copyright TCL-MIG
 */
public abstract class BaseContentProvider extends ContentProvider {
    public static final String TAG = BaseContentProvider.class.getSimpleName();
    protected static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {
        NLog.d(TAG, "BaseContentProvider onCreate "+ this);
        return true;
}

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


    private SQLiteDatabase mDatabase;
    protected synchronized SQLiteDatabase getDatabase(){
        if(mDatabase == null){
            NLog.d(TAG, "BaseContentProvider 初始化db = %s",mDatabase );
            DBHelper mHelper = new DBHelper(getContext(),getDBName(),getDBVersion());
            mDatabase = mHelper.getWritableDatabase();
            enableDbWriteAheadLogging(mDatabase);
        }
        return mDatabase;
    }

    protected void enableDbWriteAheadLogging(SQLiteDatabase database){
        if (database != null && Build.VERSION.SDK_INT >= 11){
            database.enableWriteAheadLogging();
        }
    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, int version) {
            super(context, name, null, version);
            NLog.d(TAG, "BaseContentProvider new  DBHelper");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            NLog.d(TAG, "BaseContentProvider DBHelper onCreate");
            createAllTables(getFactory());
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }




    public  void createAllTables(TableFactory factory){
        NLog.d(TAG, "BaseContentProvider createAllTables factory = "+factory);
        if (factory == null){
            return;
        }
        TableHelper<?>[] mTableHelpers = factory.createAllTableHelpers();
        if(CollectionUtils.isEmpty(mTableHelpers)){
            return;
        }

        List<String> sqls = new ArrayList<>();

        for(TableHelper<?> tableHelper : mTableHelpers){
            Collection<String> s = tableHelper.getCreateTableSqls();
            NLog.d(TAG, "BaseContentProvider createAllTables = %s", s);
            if (!CollectionUtils.isEmpty(s))
                sqls.addAll(s);
        }
    }


    public abstract String getDBName();
    public abstract int getDBVersion();
    public abstract String getAuthorities();
    public abstract TableFactory getFactory();


}
