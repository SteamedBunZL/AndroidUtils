package com.clean.spaceplus.cleansdk.boost.util;

/**
 * @author zengtao.kuang
 * @Description: 初始化Ini,升级ini 管理器,单实例的</p>
 * clearprocess.doFilter 是一个ini格式，添加时候别重复
 * @date 2016/4/6 15:56
 * @copyright TCL-MIG
 */

import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.clean.spaceplus.cleansdk.util.IniResolver;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IniManager {

    public static final int SKIP_BYTE_COUNT = 2;

    public static final int PROCESS_UNCHECKED = 1;
    public static final int PROCESS_FILTERED = 2;
    public static final int PROCESS_UNCHECKED_WHEN_SCREENOFF = 3;
    public static final int PROCESS_FLEXIBLE_WHITE_LIST = 4;
    public static final int PROCESS_NECESSARY_APP = 6;

    public static final int CPU_WHITELIST = 1;
    public static final int CPU_SYSBALCK = 2;

    public static final String PROCESS_SECTIONNAME = "process";
    public static final String ENCODING = "utf-8";

    public static final String FLEXIBLE_SECTIONNAME = "flexible";
    public static final String CPU_SECTIONNAME = "cpu";


    private static IniManager sInstance;


    private IniResolver mResolver = new IniResolver();

    public synchronized static IniManager getInstance(){
        if (sInstance == null) {
            sInstance = new IniManager();
        }
        return sInstance;
    }

    private IniManager(){
        initResovler();
    }

    public void updated(){
        synchronized (mResolver) {
            initResovler();
        }
    }

    // get cpu section values
    public int getCpuValue(String pkgName) {
        synchronized (mResolver) {
            String flag = mResolver.getValue(CPU_SECTIONNAME, pkgName);
            return parseString2Int(flag);
        }
    }

    /**
     * 获取flexible节点下内容
     * */
    public int getFlexibleValue(String pkgname) {
        synchronized (mResolver) {
            String flag = mResolver.getValue(FLEXIBLE_SECTIONNAME, pkgname);
            return parseString2Int(flag);
        }
    }

    /**
     * 获取process节点下内容
     * */
    public int getIniMark(String pkgname){
        synchronized (mResolver) {
            String flag = mResolver.getValue(PROCESS_SECTIONNAME, pkgname);
            return parseString2Int(flag);
        }
    }

/*	public String getIniMarkStr(String pkgname){
		synchronized (mResolver) {
			return mResolver.getValue(Env.PROCESS_SECTIONNAME, pkgname);
		}
	}*/

    private int parseString2Int(String flag){
        try {
            return Integer.parseInt(flag);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return 0;
    }

    private void initResovler() {

        //FIXME
//        String strFilePath = Environment.getExternalStorageDirectory().getPath()+File.pathSeparator+ "junkprocess_en_1.0.0.filter";
//        if (TextUtils.isEmpty(strFilePath)) {
//            return;
//        }

        InputStream inputStream = null;
        ByteArrayInputStream byteArrayIS = null;
        try {
            //FIXME
            inputStream  = new FileInputStream(SpaceApplication.getInstance().getContext().getDatabasePath("junkprocess_en_1.0.2.filter"));
            int len = inputStream.available() - SKIP_BYTE_COUNT;
            if (len > 0) {
                byte[] content = new byte[len];
                inputStream.skip(SKIP_BYTE_COUNT);
                int nReadCount = inputStream.read(content);
                if (nReadCount == len) {
                    for (int nIdx = 0; nIdx < len; nIdx++) {
                        content[nIdx] = (byte) (~(content[nIdx] ^ (byte) 0x8F));
                    }
                    byteArrayIS = new ByteArrayInputStream(content);
                    mResolver.load((new InputStreamReader(byteArrayIS, ENCODING)));
                }
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            IOUtils.closeSilently(inputStream);
            IOUtils.closeSilently(byteArrayIS);
        }
    }

}
