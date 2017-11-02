// RootKeeper.aidl
package com.clean.spaceplus.cleansdk.junk.engine.aidl;

// Declare any non-default types here with import statements

interface RootKeeper {

    String convertRootCacheCleanCloudPath(String rootPath,String path,String pkgName);

    List<String> convertRootCacheCleanCloudPathREG(String rootPath,String path,String pkgName);
}
