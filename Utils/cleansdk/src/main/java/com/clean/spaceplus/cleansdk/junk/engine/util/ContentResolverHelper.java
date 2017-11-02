package com.clean.spaceplus.cleansdk.junk.engine.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;

import com.clean.spaceplus.cleansdk.junk.engine.ProgressCtrl;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.ArrayList;

/**
 * @author Jerry
 * @Description:ContentResolver 的辅助类。因为query接口没有提供超时参数。使用此辅助类可以实现超时控制功能
 * @date 2016/5/11 10:55
 * @copyright TCL-MIG
 */
public class ContentResolverHelper {
    private ContentResolver mCtx;
    public ContentResolverHelper () {
        this(SpaceApplication.getInstance().getContext().getContentResolver());
    }

    public ContentResolverHelper( ContentResolver contentResolver ) {
        mCtx = contentResolver;
    }

    /**
     * 执行任务
     * @param runnable 执行器
     * @param ctrl 控制器。用来控制是否终止查询
     * @param nTimeout_ms 超时限制。 单位(毫秒)，大于0有效。小于等于0表示没有超时限制
     */
    private void runTask(Runnable runnable, ProgressCtrl ctrl, long nTimeout_ms ) {
        // 不开启超时控制
        if ( ctrl == null && nTimeout_ms <= 0 ) {
            runnable.run();
            return;
        }
        //开启超时控制
        Thread thread = new Thread( runnable, "ContentResolverHelper.runTask" );
        thread.start();
        long nUnitWaitTime_ms = 100; //每次休眠单位
        long nWaitTimes = nTimeout_ms/nUnitWaitTime_ms;
        if ( nTimeout_ms % nUnitWaitTime_ms > 0 ) {
            nWaitTimes += 1;
        }
        try {
            while( true ) {
                thread.join( nUnitWaitTime_ms);
                if ( !thread.isAlive() ) {
                    return;
                }
                if ( ctrl != null && ctrl.isStop() ) {
                    return;
                }
                if ( nTimeout_ms > 0 ) {
                    nWaitTimes --;
                    if ( nWaitTimes <= 0 ) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * ContentResolver 的query功能
     * @param uri 同 ContentResolver.query
     * @param projection 同 ContentResolver.query
     * @param selection 同 ContentResolver.query
     * @param selectionArgs 同 ContentResolver.query
     * @param sortOrder 同 ContentResolver.query
     * @param ctrl 控制器。用来控制是否终止查询
     * @param nTimeout_ms 超时限制。 单位(毫秒)，大于0有效。小于等于0表示没有超时限制
     * @return  同 ContentResolver.query
     */
    public Cursor query(final Uri uri, final String[] projection,
                        final String selection, final String[] selectionArgs, final String sortOrder,
                        ProgressCtrl ctrl, final long nTimeout_ms ) {
        if ( null == mCtx ) {
            return null;
        }

        final ArrayList<Cursor> cursors = new ArrayList<>(1);
        runTask( new Runnable() {
            @Override
            public void run() {
                long nStartTime = SystemClock.uptimeMillis();
                try {
                    Cursor cursor = mCtx.query(uri, projection, selection, selectionArgs, sortOrder);
                    cursors.add(cursor);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                long nUseTime = SystemClock.uptimeMillis()-nStartTime;
                if ( nTimeout_ms > 0 && nUseTime > nTimeout_ms ) {
                    NLog.d( "CRHpr", "Query takes too long. useTime:"+nUseTime+" limit:"+nTimeout_ms );
                }
            }
        }, ctrl, nTimeout_ms );
        if ( !cursors.isEmpty() ) {
            return cursors.get(0);
        }
        return null;
    }
}
