package com.steve.utils;

import com.steve.commonlib.DebugLog;

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　>      <　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　⌒　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃  护码神兽
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * <p>
 * Created by Steve on 17/8/14.
 * <p>
 * ━━━━━━感觉萌萌哒━━━━━━
 */

class Outer {
    /**
     *  普通内部类
     */
    class Inner {
        private String innerField = "inner Field";

        private Inner(){
            DebugLog.i("I'am Inner construction without args");
        }

        private Inner(String innerField){
            this.innerField = innerField;
            DebugLog.i("I'am Inner construction with args "+ this.innerField);
        }

        private void innerMethod(){
            DebugLog.i("I'am inner method");
        }
    }

    /**
     * 静态内部类
     */
    static class StaticInner {

        private String innerField = "StaticInner Field";
        private static String innerStaticField = "StaticInner static Field";

        private StaticInner(){
            DebugLog.i("I'am StaticInner construction without args");
        }

        private StaticInner(String innerField){
            this.innerField = innerField;
            DebugLog.i("I'am StaticInner construction with args "+ this.innerField);
        }

        private void innerMethod(){
            DebugLog.i("I'am StaticInner method");
        }

        private static void innerStaticMethod(){
            DebugLog.i("I'am StaticInner static method");
        }
    }
}