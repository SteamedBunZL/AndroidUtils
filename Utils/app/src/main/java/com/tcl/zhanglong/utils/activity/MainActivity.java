package com.tcl.zhanglong.utils.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.steve.commonlib.DebugLog;
import com.steve.utils.ReflectUtils;
import com.tcl.security.cloudengine.CloudEngine;
import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.Utils.VibratorUtils;
import com.tcl.zhanglong.utils.View.wechat_address_book.WeChatActivity;
import com.tcl.zhanglong.utils.View.wechat_emoji_rank.WeChatEmojiActivity;
import com.tcl.zhanglong.utils.jni.JNI;
import com.tcl.zhanglong.utils.notification.AnotherColorEngine;
import com.tcl.zhanglong.utils.notification.BigRemoteViews;
import com.tcl.zhanglong.utils.notification.NotificationColorEngine;
import com.tcl.zhanglong.utils.notification.NotificationManager;
import com.tcl.zhanglong.utils.notification.SmallRemoteViews;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

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


    private MediaSessionCompat mSession;



    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        setUpMediaSession();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mSession.setActive(true);
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


                //测试通知栏点击和删除时收到的通知
                //NotificationManager manager = new NotificationManager(this,new NotificationColorEngine());
                //manager.testNotification(this);

                //测试修改HashSet Arraylist中修改对象后 remove有没有生效
                //testSet();
                //testList();

                //jni
                //String ss = new JNI().sayHello();

                //testPlusPlus();
                //testArray();

                //startActivity(new Intent(MainActivity.this, WeChatActivity.class));
                //VibratorUtils.getInstacce().startVibrate(45,MainActivity.this);

                //android.app.NotificationManager nm = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //nm.notify(1001,buildNotification());
                //sendBigRemoteViews();

                invokeTimer();
                break;
            case R.id.button3:
                //getAndroidId();
                //Log.e("","===ZL lan : " + getLang() + ", country : " + getArea() + ", STR : " + Locale.getDefault().toString());
                //testListFiles();
                //Intent intent = new Intent(this,SecurityViewTestActivity.class);
                //startActivity(intent);

                //testHashSet();
                startActivity(new Intent(MainActivity.this, WeChatEmojiActivity.class));
                break;
            case R.id.button4:
                //changeLan();
                verifyException();
                break;
        }
    }


    private void testPlusPlus(){
        for(int i = 0;i<10;++i){
            DebugLog.d("i = %d",i);
        }
    }

    private void testArray(){
        ArrayList<Person> list = new ArrayList<>();
        list.add(null);
        DebugLog.d("list size %d",list.size());
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



    void testSet(){
        Person p1 = new Person("张一","男",23);
        Person p2 = new Person("张二","女",35);
        Person p3 = new Person("张三","男",40);

        Set<Person> list = new HashSet<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);

        DebugLog.w("set before hashcode %d",p3.hashCode());
        p3.setAge(2);
        p3.setName("李二");
        DebugLog.w("set after hashcode %d",p3.hashCode());

        list.remove(p3);

        DebugLog.w("set remove list %s",list);

        list.add(p3);

        DebugLog.w("set add list %s",list);



    }

    void testList(){
        Person p1 = new Person("张一","男",23);
        Person p2 = new Person("张二","女",35);
        Person p3 = new Person("张三","男",40);

        List<Person> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);

        p3.setAge(3);
        list.remove(p3);

        DebugLog.w("list remove list %s",list);

        list.add(p3);

        DebugLog.w("list add list %s",list);



    }


    private Notification buildNotification() {
        final String albumName = "周杰伦";
        final String artistName = "周杰伦";
        final boolean isPlaying = false;
        String text = TextUtils.isEmpty(albumName)
                ? artistName : artistName + " - " + albumName;

        int playButtonResId = isPlaying
                ? R.drawable.ic_pause_white_36dp : R.drawable.ic_play_white_36dp;

        //Intent nowPlayingIntent = NavigationUtils.getNowPlayingIntent(this);
        //PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap artwork = null;
        //artwork = ImageLoader.getInstance().loadImageSync(TimberUtils.getAlbumArtUri(getAlbumId()).toString());

//        if (artwork == null) {
//            artwork = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_empty_music2);
//        }

        artwork = BitmapFactory.decodeResource(getResources(),R.drawable.ic_empty_music2);


        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(artwork)
                .setContentIntent(null)

                .setContentTitle("Test")
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .addAction(R.drawable.ic_skip_previous_white_36dp,
                        "",
                        null)
                .addAction(playButtonResId, "",
                        null)
                .addAction(R.drawable.ic_skip_next_white_36dp,
                        "",
                        null);



        if (isJellyBeanMR1()) {
            builder.setShowWhen(false);
        }
        if (isLollipop()) {
            builder.setVisibility(android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC);
            NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle()
                    .setMediaSession(mSession.getSessionToken());
                    //.setMediaSession(new MediaSessionCompat(MainActivity.this,"MediaSession",new ComponentName(MainActivity.this,Intent.ACTION_MEDIA_BUTTON),null).getSessionToken())
                    //.setShowActionsInCompactView(0, 1, 2, 3);
            builder.setStyle(style);
        }
        //if (artwork != null && isLollipop())
        //    builder.setColor(Palette.from(artwork).generate().getVibrantColor(Color.parseColor("#403f4d")));
        //builder.setColor(getColor(android.R.color.white));
        Notification n = builder.build();

//        if (PreferencesUtility.getInstance(this).getXPosedTrackselectorEnabled()) {
//            addXTrackSelector(n);
//        }

        return n;
    }

    public static boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    private void setUpMediaSession(){
        if (isLollipop()){
            mSession = new MediaSessionCompat(this,"Music");
            mSession.setCallback(new MediaSessionCompat.Callback() {

            });
            mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            updateMediaSession();
        }

    }


    private void updateMediaSession(){

        int playState = PlaybackStateCompat.STATE_PLAYING;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(playState, 0, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                    .build());
        }
    }


    private void sendBigRemoteViews(){
        BigRemoteViews brv = new BigRemoteViews(this);
        RemoteViews rv = brv.getRemoteViews();

        SmallRemoteViews srv = new SmallRemoteViews(this);
        RemoteViews rv2 = srv.getRemoteViews();

        android.support.v4.app.NotificationCompat.Builder  builder= new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.kugou_small)
                .setContentTitle("酷狗")
                .setContentText("歌手")
                .setCustomBigContentView(rv)
                .setContent(rv2);

        android.app.NotificationManager nm = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1000,builder.build());

    }

    private Timer timer;

    private void invokeTimer(){
        if (timer!=null){
            timer.cancel();
            timer = null;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DebugLog.d("fuck you baby");
            }
        },0,3000);
    }



}
