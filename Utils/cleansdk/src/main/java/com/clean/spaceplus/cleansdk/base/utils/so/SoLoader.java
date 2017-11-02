//package com.clean.spaceplus.cleansdk.base.utils.so;
//
///**
// * @author liangni
// * @Description:动态库载入工具
// * @date 2016/4/23 17:10
// * @copyright TCL-MIG
// */
//public class SoLoader {
//
//    public static final String 	LIB_NAME 	= "kcmutil";
//
////    private static Object mMutex = new Object();
//    /*DCL不安全，必须加上volatile保证可见性*/
////    private volatile static boolean mLoaded = false;
//
//    public static boolean doLoad(boolean u){
//          return false;
////        if (mLoaded) {
////            return true;
////        }
////
////        boolean loaded = false;
////        synchronized (mMutex) {
////            if (mLoaded) {
////                return true;
////            }
////
////            try {
////                LibLoader.getInstance().loadLibrary(LIB_NAME);
////                loaded = true;
////            } catch(Exception e) {
////                loaded = false;
////            } catch (Error e) {
////                loaded = false;
////            }
////            mLoaded = loaded;
////        }
////
////
////        return loaded;
//    }
//}
