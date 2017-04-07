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
import android.widget.Toast;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.Utils.DebugLog;
import com.tcl.zhanglong.utils.Utils.ThreadPoolUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.button2)
    Button button2;

    @BindView(R.id.button3)
    Button button3;

    @BindView(R.id.button4)
    Button button4;



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
                for(int i = 0;i<10;i++){
                    ThreadPoolUtil.getIns().getThreadPoolExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DebugLog.e("One Thread is running");
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
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



}
