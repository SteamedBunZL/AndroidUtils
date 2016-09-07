package com.tcl.zhanglong.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tcl.zhanglong.utils.Utils.DebugLog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    public static final Uri CONTENT_URI = Uri.parse("content://com.android.partnerbrowsercustomizations/homepage");
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.chrome.browser/history");
//    public static final Uri CONTENT_URI_1 = Uri.parse("content://com.google.android.apps.chrome.browser-contract/history");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(){
            @Override
            public void run() {
//                clearHistory(getContentResolver());
                List<String> list = getVisitedHistory(getContentResolver());

                DebugLog.w("list is %s",list);


            }
        }.start();


    }

    private static void clearHistory(ContentResolver cr){
        cr.delete(CONTENT_URI,null,null);
    }


    private static final List<String> getVisitedHistory(ContentResolver cr) {

        Cursor c = null;
        List<String> list = new ArrayList<>();
        try {
            String[] projection = new String[] {
                    "title"
            };
            c = cr.query(CONTENT_URI, null, null, null, null);
            if (c == null) return list;
            DebugLog.w("History urls size %d",c.getCount());
            while (c.moveToNext()) {
                list.add(c.getString(0));
            }
        } catch (IllegalStateException e) {
            Log.e("DebugLog", "getVisitedHistory", e);
        } finally {
            if (c != null) c.close();
        }
        return list;
    }
}
