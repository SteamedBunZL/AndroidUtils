//package com.clean.spaceplus.cleansdk.base.utils.so;
//
///**
// * @author liangni
// * @Description:
// * @date 2016/4/23 17:12
// * @copyright TCL-MIG
// */
//public class LibLoader {
//    private static LibLoader mInst = new LibLoader();
//
//    public static LibLoader getInstance() {
//        return mInst;
//    }
//
//    private LibLoader() {
//    }
//
//
////    //system core
////    public boolean isSysCoreLoaded() {
////        return mLoadedSysCore;
////    }
////
////    public void loadLibarySyscore(String fileName) {
////        if (mLoadedSysCore && (null == mSysCore || mSysCore.isLibraryOk())) {
////            // 已成功加载过
////            return;
////        }
////
////        mLoadedSysCore = false;
////
////        boolean keepUtils = true;
////        mSysCore = new LibLoadUtils(fileName);
////
////        try {
////            if (!mSysCore.load()) {
////                System.loadLibrary(fileName);
////                keepUtils = false;
////            }
////            mLoadedSysCore = true;
////        }catch (UnsatisfiedLinkError e) {
////            e.printStackTrace();
////        } catch (Exception e)
////        {
////            e.printStackTrace();
////        }
////
////        mSysCore.close();
////
////        if (!keepUtils) {
////            mSysCore = null;
////        }
////    }
////
////
////    //lzma
////    public boolean isLzmaLoaded() {
////        return mLoadedLzma;
////    }
////
////    public void loadLibaryLzma(String fileName) {
////        if (mLoadedLzma && (null == mLzmaCore || mLzmaCore.isLibraryOk())) {
////            // 已成功加载过
////            return;
////        }
////
////        mLoadedLzma = false;
////
////        boolean keepUtils = true;
////        mLzmaCore = new LibLoadUtils(fileName);
////
////        try {
////            if (!mLzmaCore.load()) {
////                System.loadLibrary(fileName);
////                keepUtils = false;
////            }
////            mLoadedLzma = true;
////        }catch (UnsatisfiedLinkError e) {
////            e.printStackTrace();
////        }catch ( Exception e)
////        {
////            e.printStackTrace();
////        }
////
////        mLzmaCore.close();
////
////        if (!keepUtils) {
////            mLzmaCore = null;
////        }
////    }
//
//
//    //
//    //@REMARK
//    // 优先加载本地目录、之后再执行默认加载
//    //
//    public String loadLibrary(String fileName){
//
//        if (mUtilsLoaded && (null == mUtils || mUtils.isLibraryOk())) {
//            // 已成功加载过
//            return libString;
//        }
//
//        mUtilsLoaded = false;
//
//        boolean keepUtils = true;
//        mUtils = new LibLoadUtils(fileName);
//        if (!mUtils.load()) {
//            System.loadLibrary(fileName);
//            if(SoLoader.LIB_NAME.equals(fileName)){
//                libString = fileName;
//            }
//            keepUtils = false;
//        }
//
//        if(keepUtils && SoLoader.LIB_NAME.equals(fileName)){
//            libString = mUtils.getLibFullPath();
//        }
//
//        mUtilsLoaded = true;
//
//        mUtils.close();
//
//        if (!keepUtils) {
//            mUtils = null;
//        }
//
//        return libString;
//    }
//
//    private String libString = null;
//
//    private LibLoadUtils mUtils = null;
//    private boolean mUtilsLoaded = false;
//
////    private LibLoadUtils mSysCore = null;
////    private volatile boolean mLoadedSysCore = false;
////
////    private LibLoadUtils mLzmaCore = null;
////    private volatile boolean mLoadedLzma = false;
//
//}
