package com.tcl.zhanglong.utils.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tcl.zhanglong.testlib.HelloClass;
import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.Utils.DebugLog;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class MainActivity extends BaseActivity implements View.OnClickListener{

//    public static final Uri CONTENT_URI = Uri.parse("content://com.android.partnerbrowsercustomizations/homepage");
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.chrome.browser/history");
//    public static final Uri CONTENT_URI_1 = Uri.parse("content://com.google.android.apps.chrome.browser-contract/history");

    private Button button2;

    private Button button3;

    private PriorityBlockingQueue<String> queue;

    private Dispatcher dispatcher;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        queue = new PriorityBlockingQueue<>();

        dispatcher = new Dispatcher();


//        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
////        setSupportActionBar(toolbar);
//
//        toolbar.setTitle("MainActivity");
//
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(MainActivity.this,"TEST",Toast.LENGTH_SHORT);
//                return true;
//            }
//        });
//        toolbar.inflateMenu(R.menu.menu_main);
////
//        new Thread(){
//            @Override
//            public void run() {
////                clearHistory(getContentResolver());
//                List<String> list = getVisitedHistory(getContentResolver());
//
//                DebugLog.w("list is %s",list);
//
//
//            }
//        }.start();

//        List<String> list = testException();


    }


    private List testException(){
        List<String> list = new ArrayList<>();

        try {
            list.add("Hello");
            list.get(0);
            DebugLog.e("正常走");
        } catch (Exception e) {
            DebugLog.e("走了上面");
            e.printStackTrace();
        }finally {
            DebugLog.e("走了finallys");
        }
        DebugLog.e("走了下面");
        return list;
    }

    private void testObject(){
        List<Entry> list = new ArrayList<>();
        Entry entry =null;
        int i= 1;
        while(i<=3){
            entry = new Entry();
            entry.name = "zhangsan" + i;
            i++;
            list.add(entry);
        }
        DebugLog.e("new List %s",list.toString());
    }

    private static void clearHistory(ContentResolver cr){
        cr.delete(CONTENT_URI,null,null);
    }

    class Entry{
        public String name;


        @Override
        public String toString() {
            return "name : " + name;
        }
    }


    private static final List<String> getVisitedHistory(ContentResolver cr) {

        Cursor c = null;
        List<String> list = new ArrayList<>();
        try {
            String[] projection = new String[] {
                    "url"
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

    private void shortkeyTest(){

        //Surround with cmd + alt + T
            try {
                int i = 10;
                int j = 3;
            } catch (Exception e) {
                e.printStackTrace();
            }

        //Unwrap shift + cmd + fn + delete

        int i = 10;
        int j = 3;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button2:
//                if (dispatcher!=null)
//                    dispatcher.start();
                testException();
                break;
            case R.id.button3:
//                if (dispatcher!=null)
//                    dispatcher.quit();
                testObject();
                break;
        }
    }

    class Dispatcher extends Thread{

        private volatile boolean mQuit = false;



        public void quit() {
            interrupt();
            mQuit = true;
        }


        @Override
        public void run() {

            setName("Dispatcher");

            String str;
            while(true){
                try {
                    str = queue.take();
                } catch (InterruptedException e) {
                    if (mQuit){
                        return;
                    }

                    continue;
                }

            }
        }


    }
}
