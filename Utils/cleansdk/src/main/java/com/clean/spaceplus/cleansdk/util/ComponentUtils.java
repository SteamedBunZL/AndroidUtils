package com.clean.spaceplus.cleansdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.animation.Animation;

import java.lang.reflect.Method;

/**
 * @author zengtao.kuang
 * @Description: 组件工具
 * @date 2016/4/6 16:40
 * @copyright TCL-MIG
 */
public final class ComponentUtils {

    // 必须都使用此方法打开外部activity,避免外部activity不存在而造成崩溃，
    public static boolean startActivity(Context context, Intent intent) {
        boolean bResult = true;
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            bResult = false;
        }
        return bResult;
    }


    // 关闭activity切换时的过渡动画，只能在startActivity()和finish()之后调用
    public static void cancelActivityTransition(Context context) {
        if (Build.VERSION.SDK_INT >= 5) {
            Method overridePendingTransitionMethod = null;
            try {
                overridePendingTransitionMethod = context.getClass().getMethod("overridePendingTransition", new Class[] { int.class, int.class });
                if (null == overridePendingTransitionMethod) {
                    return;
                }

                overridePendingTransitionMethod.invoke(context, new Object[] { Integer.valueOf(Animation.INFINITE), Integer.valueOf(Animation.INFINITE) });
            } catch (Exception e) {
            }
        }
    }


    public static boolean checkIsFinishing(Context context){
        if(context != null && context instanceof Activity){
            if(((Activity)context).isFinishing()){
                return true;
            }
        }
        return false;
    }
}
