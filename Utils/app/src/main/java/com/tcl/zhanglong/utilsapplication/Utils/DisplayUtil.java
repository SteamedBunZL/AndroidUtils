package com.tcl.zhanglong.utilsapplication.Utils;

import android.content.Context;

/**
 * dp、sp转px的工具类
 * Created by zhanglong on 16/8/29.
 */
public class DisplayUtil {

    /**
     * 将px值转为dip或dp值，保证尺寸在小不变
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context,float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale+0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context,float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue*scale+0.5f);
    }

    /**
     * 将px转成sp保证文字大小不变
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context,float pxValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue/fontScale+0.5f);
    }

    /**
     * 将sp转为px保证文字大小不变
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context,float spValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue*fontScale+0.5f);
    }
}
