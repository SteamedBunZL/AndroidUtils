package com.tcl.security.virusengine.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Message;

/**
 * Create this class to solve the problem that the Message.obtain() crashed when sdk <= 19
 * 注意:以下Message获取方式均在SDK版本小等于19时不可以使用 {@link Message#sendToTarget()} 方法发送消息
 * Created by Steve on 16/10/8.
 */

public class HandlerUtil {

    /**
     * Safe method to get Message
     * @return
     */
    public static Message obtain(){
        try {
            if (Build.VERSION.SDK_INT >19)
                return Message.obtain();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message();
    }


    public static Message obtainMessage(Handler handler,int what,int arg1,int arg2){
        try {
            if (Build.VERSION.SDK_INT>19)
                return Message.obtain(handler, what, arg1, arg2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        return msg;
    }

    public static Message obtainMessage(Handler handler,int what,Object obj){
        try {
            if (Build.VERSION.SDK_INT>19)
                return Message.obtain(handler, what, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        return msg;
    }

    public static Message obtainMessage(Handler handler,int what){
        try {
            if (Build.VERSION.SDK_INT>19)
                return Message.obtain(handler, what);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.what = what;
        return msg;
    }

    public static Message obtainMessage(Handler handler,int what,int arg1,int arg2,Object obj){
        try {
            if (Build.VERSION.SDK_INT>19)
                return Message.obtain(handler, what, arg1, arg2,obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        return msg;
    }


}
