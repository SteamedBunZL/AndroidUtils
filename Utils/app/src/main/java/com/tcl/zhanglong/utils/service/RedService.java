package com.tcl.zhanglong.utils.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.utils.activity.MainActivity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import static android.R.id.list;
import static android.content.Intent.ACTION_VIEW;
import static com.tcl.zhanglong.utils.activity.MainActivity.index;

/**
 * Created by Steve on 17/1/23.
 */

public class RedService extends AccessibilityService{

    public static final String CLASSNAME_UI_WECHAT_CHAT = "com.tencent.mm.ui.LauncherUI";

    public static final String CLASSNAME_UI_WECHAT_RED = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        //DebugLog.w("eventType : %s",Integer.toHexString(eventType));
        String className = event.getClassName().toString();
        switch (eventType){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                DebugLog.d("Window State Change Top : %s",className);

//                if (className.equals(CLASSNAME_UI_WECHAT_CHAT)){
//                    //开始抢红包
//                    getPacket();
//                }else if (className.equals(CLASSNAME_UI_WECHAT_RED)){
//                    //打开红包
//                    openPacket();
//                }else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")){
//                    close();
//                }
                AccessibilityNodeInfo info = event.getSource();
                if (className.equals("com.android.settings.applications.InstalledAppDetailsTop")){
                    if (MainActivity.index == 1)
                        performForceStopById(info);
                    else
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                } else if (className.equals("android.app.AlertDialog")) {
                    performConfigById(info);
                }
                break;
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                DebugLog.d("Type Scroll Top : %s",className);
//                //if (className.equals(CLASSNAME_UI_WECHAT_CHAT)){
//                    //开始抢红包
//                    getPacket();
//                //}
//                break;
        }
    }

    private void close(){

    }

    @Override
    public void onInterrupt() {

    }

    @SuppressLint("NewApi")
    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycle(rootNode);
    }

    public void test_recycle(AccessibilityNodeInfo info){
        DebugLog.w("AccessibilityNodeInfo %s",info.toString());
        if (info.getChildCount() == 0) {
            if(info.getText() != null){
                if("领取红包".equals(info.getText().toString())){
                    //这里有一个问题需要注意，就是需要找到一个可以点击的View
//                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    AccessibilityNodeInfo parent = info.getParent();
//                    while(parent != null){
//                        if(parent.isClickable()){
//                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                            break;
//                        }
//                        parent = parent.getParent();
//                    }
                    test(info);

                }
            }

        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    test_recycle(info.getChild(i));
                }
            }
        }
    }

    /**
     * 打印一个节点的结构
     * @param info
     */
    @SuppressLint("NewApi")
    public void recycle(AccessibilityNodeInfo info) {
        //DebugLog.w("AccessibilityNodeInfo %s",info.toString());
        if (info.getChildCount() == 0) {
            if(info.getText() != null){
                if("领取红包".equals(info.getText().toString())){
                    //这里有一个问题需要注意，就是需要找到一个可以点击的View
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    AccessibilityNodeInfo parent = info.getParent();
                    while(parent != null){
                        if(parent.isClickable()){
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }

                }
            }

        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle(info.getChild(i));
                }
            }
        }
    }


    @SuppressLint("NewApi")
    private void openPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        openRecycle(nodeInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openRecycle(AccessibilityNodeInfo info){
        DebugLog.w("AccessibilityNodeInfo %s",info.toString());
        //开按钮
        if (info != null) {
            List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/be_");
            for (AccessibilityNodeInfo n : list) {
                DebugLog.e("AccessibilityNodeInfo %s",n.toString());
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            if (list==null||list.isEmpty()){
                List<AccessibilityNodeInfo> xlist = info.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bed");
                for (AccessibilityNodeInfo n : xlist) {
                    DebugLog.e("AccessibilityNodeInfo %s",n.toString());
                    n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }

        //关闭按钮
//        if (info != null) {
//            List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bed");
//            for (AccessibilityNodeInfo n : list) {
//                DebugLog.e("AccessibilityNodeInfo %s",n.toString());
//                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//        }

    }


    public void test(AccessibilityNodeInfo info){
        try {
            DebugLog.d("HashCode %s",info.hashCode());
            DebugLog.w("调用了一次");
            Class clazz = info.getClass();
//            Method[] methods = clazz.getDeclaredMethods();
//            for (Method method:methods){
//                DebugLog.d("methods %s",method.toString());
//            }
            Method setSealed = clazz.getDeclaredMethod("setSealed",boolean.class);
            setSealed.setAccessible(true);
            setSealed.invoke(info,false);
            info.setText("sjlfjsljflsjf");
            DebugLog.d("Info Text %s",info.getText());
        } catch (Exception e) {
            DebugLog.e("异常 %s",e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void closeDetail() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        openRecycle(nodeInfo);
    }

    String stopText;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    boolean performForceStopById(AccessibilityNodeInfo info){
        if (index != 1)
            return false;

        List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByViewId("com.android.settings:id/right_button");
        for(AccessibilityNodeInfo subInfo:list){
            if (subInfo.getClassName().equals(Button.class.getName())&&subInfo.isEnabled()){
                stopText = (String) subInfo.getText();
                //perform click
                subInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                index--;
                return true;
            }
        }
        return false;
    }

    Locale getSystemLocale(){
        return Locale.getDefault();
    }

    //android:id/button1
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    boolean performConfigById(AccessibilityNodeInfo info){
        List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByViewId("android:id/button1");
        for(AccessibilityNodeInfo subInfo:list){
            if (subInfo.getClassName().equals(Button.class.getName())&&subInfo.isEnabled()){
                String text = (String) subInfo.getText();
                DebugLog.t("MyAccessibility").w("Locale=%s,Stop Text=%s,Config Text=%s",getSystemLocale(),stopText,text);
                //perform click
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                return true;
            }
        }
        return false;
    }



}
