package com.tcl.zhanglong.utils.Utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Created by Steve on 16/10/9.
 */

public class MeasureUtil {

    /**
     * 获取屏幕宽高不准确,不包括虚拟按键宽高 单位像素
     * getScreenSize()[0] width
     * getScreenSize()[1] height
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int[] array = new int[2];
        array[0] = dm.widthPixels;
        array[1] = dm.heightPixels;
        return array;
    }

    /**
     * 获取屏幕宽高,准确,包括虚拟按键
     * @param context
     * @return
     */
    public static int[] getScreenSize2(Context context){
        int[] array = new int[2];
        Point point = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            windowManager.getDefaultDisplay().getRealSize(point);
        }else{
            windowManager.getDefaultDisplay().getSize(point);
        }
        //屏幕宽度
        array[0] = point.y;
        //屏幕高度
        array[1] = point.x;
        return array;
    }

    /**
     * SP 转 DX
     * @param context
     * @param sp
     * @return
     */
    public static float sp2dx(Context context,int sp) {
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return size;
    }

    /**
     * DP 转 DX
     * @param context
     * @param dp
     * @return
     */
    public static float dp2dx(Context context,int dp) {
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return size;
    }
}
