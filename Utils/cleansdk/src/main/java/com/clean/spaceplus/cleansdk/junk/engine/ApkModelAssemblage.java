package com.clean.spaceplus.cleansdk.junk.engine;

import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;

import java.io.File;

/**
 * @author zengtao.kuang
 * @Description: apk信息对象存取集合接口
 * @date 2016/5/10 19:51
 * @copyright TCL-MIG
 */
public interface ApkModelAssemblage {

    /*
	 * 存入一个apk信息对象
	 */
    boolean putOneApkModel(File apkFile, APKModel apkModel);
}
