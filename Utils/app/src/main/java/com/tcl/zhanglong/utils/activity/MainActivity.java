package com.tcl.zhanglong.utils.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.steve.commonlib.DebugLog;
import com.steve.utils.ReflectUtils;
import com.tcl.security.cloudengine.CloudEngine;
import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.notification.AnotherColorEngine;
import com.tcl.zhanglong.utils.notification.NotificationColorEngine;
import com.tcl.zhanglong.utils.notification.NotificationManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.ACTION_VIEW;
import static com.steve.utils.ReflectUtils.dumpClass;
import static com.steve.utils.ReflectUtils.invokeMethod;


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

    //CleanManagerImpl cleanMgr = new CleanManagerImpl(new ArrayList<JunkGroupTitle>());



    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        CloudEngine.init(this,"sfefwfwf",null);
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

    /**
     * 验证try catch finally return 的流程
     * @return
     */
    private List verifyException(){
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

    /**
     * 验证反射工具类功能
     */
    private void verifyReflectUtils(){
        //反射内部类
        DebugLog.w("-----------%s------------","VerifyRelectUtils");
        DebugLog.d("%s", dumpClass("com.steve.utils.Outer$Inner"));
        Object obj = ReflectUtils.newInstance("com.steve.utils.Outer$Inner",ReflectUtils.newInstance("com.steve.utils.Outer"));
        DebugLog.d("%s",ReflectUtils.getField(obj,"innerField"));
        DebugLog.d("%s", invokeMethod(obj,"innerMethod"));


        //反射内部静态类
        Class<?> clazz = ReflectUtils.getClazz("com.steve.utils.Outer$StaticInner");
        DebugLog.d("%s",ReflectUtils.getStaticField(clazz,"innerStaticField"));
        Object obj2 = ReflectUtils.newInstance("com.steve.utils.Outer$StaticInner");
        DebugLog.d("%s",ReflectUtils.getField(obj2,"innerField"));
        DebugLog.d("%s", invokeMethod(obj2,"innerMethod"));
        DebugLog.d("%s",ReflectUtils.invokeStaticMethod(clazz,"innerStaticMethod"));
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
                //getTime2(textview);
                //verifyReflectUtils();
                //testNotificationBar(this);
                //intenttest.setClassName("com.ehawk.antivirus.applock.wifi","com.tcl.security.SplashActivity");
                //performGoNext("com.baidu.input");
                NotificationManager manager = new NotificationManager(this,new NotificationColorEngine());
                manager.testNotification(this);
                break;
            case R.id.button3:
                //getAndroidId();
                //Log.e("","===ZL lan : " + getLang() + ", country : " + getArea() + ", STR : " + Locale.getDefault().toString());
                //testListFiles();
                //Intent intent = new Intent(this,SecurityViewTestActivity.class);
                //startActivity(intent);

                testHashSet();
                break;
            case R.id.button4:
                //changeLan();
                verifyException();
                break;
        }
    }

    private void testHashSet(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        //list.add(2);
        //list.add(3);
        list.add(4);
        list.add(5);
        DebugLog.w("List : %s",list);
        HashSet<Integer> set = new HashSet<>();
        for(Integer i:list){
            set.add(i);
        }
        DebugLog.w("Set : %s",set);
        for(Integer i:set){
            DebugLog.e("Every Set : %d",i);
        }
    }

    private String getAndroidId(){
        String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        DebugLog.e("Android Id %s",ANDROID_ID);
        return ANDROID_ID;
    }

//    private void changeLan(){
//        IActivityManager iActMag = ActivityManagerNative.getDefault();
//        try {
//            Configuration config = iActMag.getConfiguration();
//            config.locale = Locale.CHINESE;
//            // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
//            // 会重新调用 onCreate();
//            iActMag.updateConfiguration(config);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }



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


    private void testNotificationBar(Context context){
        PackageManager pm = getPackageManager();
        //ReflectUtils.invokeMethod(service,"expandNotificationsPanel");
        //DebugLog.w("ss : %s",msg);
    }

    public static int index = 1;

    void performGoNext(String packageName) {
        index = 1;
        Intent intent_install = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.applications.InstalledAppDetailsTop");
        intent_install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent_install.setComponent(cm);
        intent_install.setData(Uri.parse(packageName));
        intent_install.setAction(ACTION_VIEW);
        startActivity(intent_install);
    }



}
