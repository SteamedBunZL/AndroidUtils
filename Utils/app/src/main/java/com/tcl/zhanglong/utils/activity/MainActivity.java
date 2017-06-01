package com.tcl.zhanglong.utils.activity;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.Utils.DebugLog;
import com.tcl.zhanglong.utils.Utils.ThreadPoolUtil;
import com.tcl.zhanglong.utils.binderpool.ScanActivity;
import com.tcl.zhanglong.utils.notification.AnotherColorEngine;
import com.tcl.zhanglong.utils.notification.NotificationColorEngine;
import com.tcl.zhanglong.utils.notification.NotificationManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tcl.zhanglong.utils.notification.AnotherColorEngine.getNotificationColor;


public class MainActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.button2)
    Button button2;

    @BindView(R.id.button3)
    Button button3;

    @BindView(R.id.button4)
    Button button4;

    @BindView(R.id.textview)
    TextView textview;

    NotificationManager manager = new NotificationManager(this,new NotificationColorEngine());



    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }



    @Override
    protected void initView() {
        ButterKnife.bind(this);
        button3.setTextColor(0xffe6e6e6);
        int color = AnotherColorEngine.getNotificationColor(this);
        DebugLog.d("zl color %h",color);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }


    private List testException(){
        List<String> list = new ArrayList<>();

        try {
            DebugLog.e("正常走");
            //list.get(0).equals("sss");
            //return null;
        } catch (Exception e) {
            DebugLog.e("走了上面");
            e.printStackTrace();
            DebugLog.w("return");
            return null;
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


    class Entry{

        public String name;

        @Override
        public String toString() {
            return "name : " + name;
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button2:
//                Toast.makeText(this,"Start Function",Toast.LENGTH_SHORT).show();
//                Intent intent  = new Intent(this,FunctionListActivity.class);
//                startActivity(intent);
//                for(int i = 0;i<10;i++){
//                    ThreadPoolUtil.getIns().getThreadPoolExecutor().execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                DebugLog.e("One Thread is running");
//                                Thread.sleep(5000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }


                //ScanActivity.startScanActivity(this);

//                manager.testRemoteViewNotification(this);

                getTime2(textview);
                break;
            case R.id.button3:
                //getAndroidId();
                //Log.e("","===ZL lan : " + getLang() + ", country : " + getArea() + ", STR : " + Locale.getDefault().toString());
                //testListFiles();
                break;
            case R.id.button4:
                //changeLan();
                testException();
                break;
        }
    }

    private String getAndroidId(){
        String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        DebugLog.e("Android Id %s",ANDROID_ID);
        return ANDROID_ID;
    }

    private void changeLan(){
        IActivityManager iActMag = ActivityManagerNative.getDefault();
        try {
            Configuration config = iActMag.getConfiguration();
            config.locale = Locale.CHINESE;
            // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
            // 会重新调用 onCreate();
            iActMag.updateConfiguration(config);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    public static void testA(){
        testB();
    }

    public static void testB(){
        testc();
    }

    public static void testc(){
        throw new IllegalStateException("test");
    }


    /**
     * 获取当前系统语言
     * @return
     */
    public static String getLang(){
            Locale l = Locale.getDefault();
            return l.getLanguage();
    }

    /**
     * 获取国家
     * @return
     */
    public static String getArea(){
            Locale l = Locale.getDefault();
            return l.getCountry();
    }

    public void testListFiles(){
        new Thread(){
            @Override
            public void run() {
                getAllFiles(new File(Environment.getExternalStorageDirectory().getPath()));
            }
        }.start();
    }

    private void getAllFiles(File root){
        String root_path = root.getPath();
        String fileNames[] = root.list();
        if(fileNames != null){
            File file = null;
            for (String f : fileNames){
                DebugLog.d("fileName : %s",root_path + "/" + f);
                file = new File(root_path + "/" + f);
                if (file.isDirectory()){
                    getAllFiles(file);
                }else{

                }
            }
        }
    }


    public String getTime1(){
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ee = dff.format(new Date(System.currentTimeMillis()));
        return ee;
    }

    public void getTime2(TextView textView){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String rt = sdf.format(calendar.getTime());
        textView.setText("day : " + day + " , hour : " + hour + "\n" + "time : " + rt);
    }

    public String getTime3(){
        String local = "GMT+8";
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(local));
        cal.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        String date = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        String time = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        System.out.println("TimeTest.method3() date="+date+",time="+time);
        return date + " " + time;
    }



}
