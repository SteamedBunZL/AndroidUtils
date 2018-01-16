package com.tcl.zhanglong.utils.Utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Steve on 2017/12/12.
 */

public class VibratorUtils {

    private Vibrator vibrator;
    private static class VibrateInstance {
        public static VibratorUtils mInstance = new VibratorUtils();
    }

    private VibratorUtils() {
    }

    public static VibratorUtils getInstacce() {
        return VibrateInstance.mInstance;
    }

    public void startVibrate(int during,Context context){
        if(vibrator == null){
            vibrator = (Vibrator)getService(context,Context.VIBRATOR_SERVICE);
        }
        long [] pattern = {100,45};
        vibrator.vibrate(pattern,-1);
    }

    static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }


//    //震动milliseconds毫秒
    public void vibrate(Context context) {
        if(vibrator == null){
            vibrator = (Vibrator)getService(context,Context.VIBRATOR_SERVICE);
        }
        if (vibrator == null || !vibrator.hasVibrator())
            return;
        vibrator.vibrate(2000);
    }


//
//
//    //以pattern[]方式震动
//    public static void vibrate(Context context, long[] pattern,int repeat){
//        Vibrator vib = (Vibrator) getService(context,Service.VIBRATOR_SERVICE);
//        if (vib == null || !vib.hasVibrator())
//            return;
//        vib.vibrate(pattern,repeat);
//    }
//
//
//    //取消震动
//    public static void virateCancle(Context context){
//        Vibrator vib = (Vibrator) getService(context,Service.VIBRATOR_SERVICE);
//        vib.cancel();
//    }


}
