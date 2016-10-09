package com.tcl.zhanglong.utils.Utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by Steve on 16/10/9.
 */

public class MeasureUtil {

    /**
     * 获取屏幕宽高 单位像素
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
